package com.example.controller;

import com.example.model.usuarios;
import com.example.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    /**
     * Procesar registro - MÉTODO CORREGIDO
     */
    @PostMapping("/register")
    public String register(@RequestParam String nombre,
                          @RequestParam String apellido,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String userType,
                          @RequestParam(required = false) String telefono,
                          @RequestParam(required = false) String direccion,
                          @RequestParam(required = false) String preferencias_contacto,
                          @RequestParam(required = false) String[] especialidades,
                          @RequestParam(required = false) Integer experiencia,
                          @RequestParam(required = false) Double tarifa_hora,
                          @RequestParam(required = false) String certificaciones,
                          @RequestParam(required = false) String descripcion,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        
        // Validar datos
        List<String> errors = validateRegistration(nombre, apellido, email, password, 
            confirmPassword, userType, especialidades, experiencia, tarifa_hora);
        
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", String.join("<br>", errors));
            return "redirect:/auth/register?type=" + userType;
        }
        
        try {
            // Crear objeto usuario
            usuarios nuevoUsuario = new usuarios();
            nuevoUsuario.setNombre(nombre.trim());
            nuevoUsuario.setApellido(apellido.trim());
            nuevoUsuario.setEmail(email.trim().toLowerCase());
            nuevoUsuario.setPassword(password);
            nuevoUsuario.setUserType(userType);
            nuevoUsuario.setTelefono(telefono);
            nuevoUsuario.setDireccion(direccion);
            nuevoUsuario.setPreferenciasContacto(preferencias_contacto);
            
            // Campos específicos para obreros
            if ("obrero".equals(userType)) {
                if (especialidades != null && especialidades.length > 0) {
                    nuevoUsuario.setEspecialidades(String.join(",", especialidades));
                }
                nuevoUsuario.setExperiencia(experiencia);
                nuevoUsuario.setTarifaHora(tarifa_hora);
                nuevoUsuario.setCertificaciones(certificaciones);
                nuevoUsuario.setDescripcion(descripcion);
            }
            
            // Guardar usuario usando el servicio
            usuarios usuarioGuardado = usuarioService.registrarUsuario(nuevoUsuario);
            
            System.out.println("Usuario registrado exitosamente: " + usuarioGuardado.getEmail() + 
                             " con ID: " + usuarioGuardado.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Registro exitoso. ¡Ya puedes iniciar sesión!");
            return "redirect:/auth/login";
            
        } catch (Exception e) {
            System.err.println("Error en registro: " + e.getMessage());
            e.printStackTrace();
            
            if (e.getMessage().contains("email ya está registrado")) {
                redirectAttributes.addFlashAttribute("error", "El email ya está registrado.");
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "Error al crear la cuenta: " + e.getMessage());
            }
            return "redirect:/auth/register?type=" + userType;
        }
    }
    
    /**
     * Mostrar formulario de login (GET)
     */
    @GetMapping("/login")
    public String showLogin(Model model) {
        return "auth/login";
    }
    
    /**
     * Mostrar formulario de registro (GET)
     */
    @GetMapping("/register")
    public String showRegister(Model model, @RequestParam(required = false) String type) {
        model.addAttribute("userType", type != null ? type : "cliente");
        return "auth/register";
    }

    /**
     * Procesar login - MÉTODO MEJORADO CON MEJOR LOGGING
     */
    @PostMapping("/login")
    public String login(@RequestParam String userType,
                       @RequestParam String email,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        System.out.println("=== INICIO DE LOGIN ===");
        System.out.println("Tipo de usuario: " + userType);
        System.out.println("Email: " + email);
        System.out.println("Contraseña proporcionada: " + (password != null ? "***" : "null"));
        
        // Validaciones básicas
        if (email == null || email.trim().isEmpty() || 
            password == null || password.isEmpty() || 
            userType == null || userType.isEmpty()) {
            System.out.println("ERROR: Campos vacíos");
            redirectAttributes.addFlashAttribute("error", "Por favor, complete todos los campos.");
            return "redirect:/auth/login";
        }
        
        try {
            // Verificar credenciales usando el servicio
            System.out.println("Verificando credenciales...");
            usuarios user = usuarioService.verificarCredenciales(email.trim().toLowerCase(), password, userType);
            
            if (user != null) {
                System.out.println("Usuario encontrado: " + user.getEmail() + " (ID: " + user.getId() + ")");
                
                // Usuario encontrado, guardar datos en sesión
                session.setAttribute("user_id", user.getId());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("user_role", user.getUserType());
                session.setAttribute("nombre", user.getNombre());
                session.setAttribute("apellido", user.getApellido());
                
                System.out.println("Datos de sesión guardados:");
                System.out.println("- user_id: " + session.getAttribute("user_id"));
                System.out.println("- user_role: " + session.getAttribute("user_role"));
                System.out.println("- email: " + session.getAttribute("email"));
                
                // Redirigir según el tipo de usuario
                String redirectUrl = "";
                switch (userType) {
                    case "cliente":
                        session.setAttribute("cliente_id", user.getId());
                        redirectUrl = "redirect:/cliente/dashboard";
                        break;
                    case "obrero":
                        session.setAttribute("obrero_id", user.getId());
                        redirectUrl = "redirect:/obrero/dashboard";
                        break;
                    case "admin":
                        session.setAttribute("admin_id", user.getId());
                        redirectUrl = "redirect:/admin/dashboard";
                        break;
                    default:
                        redirectUrl = "redirect:/dashboard";
                        break;
                }
                
                System.out.println("Redirigiendo a: " + redirectUrl);
                System.out.println("=== LOGIN EXITOSO ===");
                return redirectUrl;
            } else {
                // Usuario no encontrado
                System.out.println("ERROR: Usuario no encontrado o credenciales incorrectas");
                redirectAttributes.addFlashAttribute("error", 
                    "Correo electrónico, contraseña o tipo de usuario incorrectos.");
                return "redirect:/auth/login";
            }
            
        } catch (Exception e) {
            System.err.println("ERROR en login: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error interno del sistema. Por favor, intente más tarde.");
            return "redirect:/auth/login";
        }
    }
    
    // ... resto de métodos (showLogin, showRegister, logout, etc.) se mantienen igual
    
    /**
     * Validar datos de registro
     */
    private List<String> validateRegistration(String nombre, String apellido, String email,
                                            String password, String confirmPassword, String userType,
                                            String[] especialidades, Integer experiencia, Double tarifa_hora) {
        List<String> errors = new ArrayList<>();
        
        if (nombre == null || nombre.trim().isEmpty()) {
            errors.add("El nombre es requerido.");
        }
        
        if (apellido == null || apellido.trim().isEmpty()) {
            errors.add("El apellido es requerido.");
        }
        
        if (email == null || email.trim().isEmpty()) {
            errors.add("El email es requerido.");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("El formato del email no es válido.");
        }
        
        if (password == null || password.isEmpty()) {
            errors.add("La contraseña es requerida.");
        } else if (password.length() < 6) {
            errors.add("La contraseña debe tener al menos 6 caracteres.");
        }
        
        if (!password.equals(confirmPassword)) {
            errors.add("Las contraseñas no coinciden.");
        }
        
        if (userType == null || userType.isEmpty()) {
            errors.add("Debe seleccionar un tipo de usuario.");
        } else if (!Arrays.asList("obrero", "cliente").contains(userType)) {
            errors.add("Tipo de usuario no válido.");
        }
        
        // Validaciones específicas para obreros
        if ("obrero".equals(userType)) {
            if (especialidades == null || especialidades.length == 0) {
                errors.add("Debe seleccionar al menos una especialidad.");
            }
            
            if (experiencia != null && (experiencia < 0)) {
                errors.add("Los años de experiencia deben ser un número válido.");
            }
            
            if (tarifa_hora != null && (tarifa_hora < 0)) {
                errors.add("La tarifa por hora debe ser un número válido.");
            }
        }
        
        return errors;
    }
}