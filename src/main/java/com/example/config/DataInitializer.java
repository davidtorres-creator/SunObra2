package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.model.User;
import com.example.repository.UserRepository;

/**
 * Componente para inicializar datos de prueba en la base de datos
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existen usuarios
        if (userRepository.count() == 0) {
            System.out.println("Inicializando usuarios de prueba...");
            
            // Crear usuario admin
            User admin = new User("Admin", "Sistema", "admin@sunobra.com", "admin", "admin");
            admin.setTelefono("3138385779");
            admin.setDireccion("Bogotá, Colombia");
            userRepository.save(admin);
            
            // Crear usuario cliente de prueba
            User cliente = new User("Juan", "Pérez", "cliente@sunobra.com", "cliente", "cliente");
            cliente.setTelefono("3138385779");
            cliente.setDireccion("Bogotá, Colombia");
            userRepository.save(cliente);
            
            // Crear usuario obrero de prueba
            User obrero = new User("Carlos", "García", "obrero@sunobra.com", "obrero", "obrero");
            obrero.setTelefono("3138385779");
            obrero.setDireccion("Bogotá, Colombia");
            userRepository.save(obrero);
            
            System.out.println("Usuarios de prueba creados exitosamente:");
            System.out.println("- Admin: admin@sunobra.com / admin");
            System.out.println("- Cliente: cliente@sunobra.com / cliente");
            System.out.println("- Obrero: obrero@sunobra.com / obrero");
        } else {
            System.out.println("La base de datos ya contiene usuarios. Saltando inicialización.");
        }
    }
}
