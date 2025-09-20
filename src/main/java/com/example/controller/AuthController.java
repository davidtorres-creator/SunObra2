package com.example.controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

<<<<<<< HEAD
import com.example.model.usuarios;
import com.example.service.UsuarioService;
=======
>>>>>>> 9afebe2d4ab7b8208953e61c1598d3d0d835c896
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.model.User;
import com.example.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
<<<<<<< HEAD
    private UsuarioService usuarioService;
    
    /**
     * Procesar registro - MÉTODO CORREGIDO
=======
    private UserRepository userRepository;
    
    /**
     * Mostrar formulario de login
     */
    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {
        // Verificar si ya está autenticado
        if (isAuthenticated(session)) {
            return redirectToDashboard(session);
        }
        
        // Obtener mensajes de la sesión
        String error = (String) session.getAttribute("auth_error");
        String success = (String) session.getAttribute("auth_success");
        
        if (error != null) {
            model.addAttribute("error", error);
            session.removeAttribute("auth_error");
        }
        
        if (success != null) {
            model.addAttribute("success", success);
            session.removeAttribute("auth_success");
        }
        
        return "auth/login";
    }
    
    /**
     * Procesar login
     */
    @PostMapping("/login")
    public String login(@RequestParam String userType,
                       @RequestParam String email,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        // Validaciones básicas
        if (email == null || email.trim().isEmpty() || 
            password == null || password.isEmpty() || 
            userType == null || userType.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor, complete todos los campos.");
            return "redirect:/auth/login";
        }
        
        // Validar formato de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectAttributes.addFlashAttribute("error", "El formato del email no es válido.");
            return "redirect:/auth/login";
        }
        
        // Validar tipo de usuario
        if (!Arrays.asList("obrero", "cliente", "admin").contains(userType)) {
            redirectAttributes.addFlashAttribute("error", "Tipo de usuario no válido.");
            return "redirect:/auth/login";
        }
        
        try {
            // Verificar credenciales usando el repositorio
            Optional<User> userOpt = userRepository.findByEmailAndPasswordAndRol(email.trim(), password, userType);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Usuario encontrado, guardar datos en sesión
                session.setAttribute("user_id", user.getId());
                session.setAttribute("email", email.trim());
                session.setAttribute("user_role", userType);
                session.setAttribute("nombre", user.getNombre());
                session.setAttribute("apellido", user.getApellido());
                
                // Redirigir según el tipo de usuario
                switch (userType) {
                    case "cliente":
                        session.setAttribute("cliente_id", user.getId());
                        return "redirect:/cliente/dashboard";
                    case "obrero":
                        session.setAttribute("obrero_id", user.getId());
                        return "redirect:/obrero/dashboard";
                    case "admin":
                        session.setAttribute("admin_id", user.getId());
                        return "redirect:/admin/dashboard";
                    default:
                        return "redirect:/dashboard";
                }
            } else {
                // Usuario no encontrado
                redirectAttributes.addFlashAttribute("error", 
                    "Correo electrónico, contraseña o tipo de usuario incorrectos.");
                return "redirect:/auth/login";
            }
            
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                "Error interno del sistema. Por favor, intente más tarde.");
            return "redirect:/auth/login";
        }
    }
    
    /**
     * Mostrar formulario de registro
     */
    @GetMapping("/register")
    public String showRegister(@RequestParam(required = false) String type, 
                          Model model, HttpSession session) {
        // Verificar si ya está autenticado
        if (isAuthenticated(session)) {
            return redirectToDashboard(session);
        }
        
        // Validar el tipo de usuario
        String userType = type;
        if (userType != null && !Arrays.asList("obrero", "cliente").contains(userType)) {
            userType = "";
        }
        
        // Obtener mensajes de la sesión
        String error = (String) session.getAttribute("auth_error");
        String success = (String) session.getAttribute("auth_success");
        
        if (error != null) {
            model.addAttribute("error", error);
            session.removeAttribute("auth_error");
        }
        
        if (success != null) {
            model.addAttribute("success", success);
            session.removeAttribute("auth_success");
        }
        
        model.addAttribute("userType", userType);
        return "auth/register";
    }
    
    /**
     * Procesar registro
>>>>>>> 9afebe2d4ab7b8208953e61c1598d3d0d835c896
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
<<<<<<< HEAD
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
=======
            // Verificar si el email ya existe
            if (userRepository.existsByEmail(email.trim())) {
                redirectAttributes.addFlashAttribute("error", "El email ya está registrado.");
                return "redirect:/auth/register";
            }
            
            // Crear nuevo usuario
            User newUser = new User(nombre.trim(), apellido.trim(), email.trim(), password, userType);
            newUser.setTelefono(telefono != null ? telefono.trim() : "");
            newUser.setDireccion(direccion != null ? direccion.trim() : "");
            
            // Guardar usuario en la base de datos
            User savedUser = userRepository.save(newUser);
            
            if (savedUser != null && savedUser.getId() != null) {
                redirectAttributes.addFlashAttribute("success", 
                    "Registro exitoso. ¡Ya puedes iniciar sesión!");
                return "redirect:/auth/login";
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "Error al crear la cuenta. Por favor, intente nuevamente.");
                return "redirect:/auth/register";
            }
>>>>>>> 9afebe2d4ab7b8208953e61c1598d3d0d835c896
            
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
    
<<<<<<< HEAD
    // ... resto de métodos (showLogin, showRegister, logout, etc.) se mantienen igual
=======
>>>>>>> 9afebe2d4ab7b8208953e61c1598d3d0d835c896
    
    /**
     * Validar datos de registro
     */
    private List<String> validateRegistration(String nombre, String apellido, String email,
                                            String password, String confirmPassword, String userType,
                                            String[] especialidades, Integer experiencia, Double tarifaHora) {
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
            
            if (tarifaHora != null && (tarifaHora < 0)) {
                errors.add("La tarifa por hora debe ser un número válido.");
            }
        }
        
        return errors;
    }
}