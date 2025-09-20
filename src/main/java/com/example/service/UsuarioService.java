package com.example.service;

import com.example.model.usuarios;
import com.example.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registrar un nuevo usuario
     */
    public usuarios registrarUsuario(usuarios usuario) throws Exception {
        try {
            // Verificar si el email ya existe
            if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                throw new Exception("El email ya está registrado");
            }

            // Encriptar la contraseña
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // Guardar el usuario
            usuarios usuarioGuardado = usuarioRepository.save(usuario);
            
            System.out.println("Usuario guardado exitosamente con ID: " + usuarioGuardado.getId());
            return usuarioGuardado;
            
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Verificar credenciales de login - MÉTODO MEJORADO CON LOGGING
     */
    public usuarios verificarCredenciales(String email, String password, String userType) {
        System.out.println("=== VERIFICANDO CREDENCIALES ===");
        System.out.println("Email: " + email);
        System.out.println("Tipo de usuario: " + userType);
        
        Optional<usuarios> usuarioOpt = usuarioRepository.findByEmailAndUserType(email, userType);
        
        if (usuarioOpt.isPresent()) {
            usuarios usuario = usuarioOpt.get();
            System.out.println("Usuario encontrado en BD: " + usuario.getEmail() + " (ID: " + usuario.getId() + ")");
            System.out.println("Tipo de usuario en BD: " + usuario.getUserType());
            
            // Verificar contraseña
            boolean passwordMatch = passwordEncoder.matches(password, usuario.getPassword());
            System.out.println("Contraseña coincide: " + passwordMatch);
            
            if (passwordMatch) {
                System.out.println("=== CREDENCIALES VÁLIDAS ===");
                return usuario;
            } else {
                System.out.println("ERROR: Contraseña incorrecta");
            }
        } else {
            System.out.println("ERROR: Usuario no encontrado con email: " + email + " y tipo: " + userType);
        }
        
        System.out.println("=== CREDENCIALES INVÁLIDAS ===");
        return null;
    }

    /**
     * Buscar usuario por email
     */
    public Optional<usuarios> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Verificar si un email ya existe
     */
    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    /**
     * Listar todos los usuarios
     */
    public List<usuarios> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Buscar usuario por ID
     */
    public usuarios buscarPorId(Long id) {
        Optional<usuarios> usuarioOpt = usuarioRepository.findById(id);
        return usuarioOpt.orElse(null);
    }

    /**
     * Actualizar usuario
     */
    public usuarios actualizarUsuario(usuarios usuario) {
        try {
            // Si se está actualizando la contraseña, encriptarla
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            } else {
                // Si no se proporciona contraseña, mantener la actual
                usuarios usuarioActual = usuarioRepository.findById(usuario.getId()).orElse(null);
                if (usuarioActual != null) {
                    usuario.setPassword(usuarioActual.getPassword());
                }
            }
            
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Eliminar usuario por ID
     */
    public void eliminarUsuario(Long id) {
        try {
            usuarioRepository.deleteById(id);
            System.out.println("Usuario eliminado exitosamente con ID: " + id);
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            throw e;
        }
    }
}