package com.example.api.rest;

import com.example.api.dto.UserDto;
import com.example.api.mapper.UserMapper;
import com.example.api.payload.ApiResponse;
import com.example.model.usuarios;
import com.example.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API REST para CRUD de usuarios.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UsuarioService usuarioService;

    // -------------------------------------------------------
    // LISTAR USUARIOS CON FILTROS
    // -------------------------------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> list(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String q) {

        List<usuarios> base = usuarioService.listarUsuarios();

        if (role != null && !role.isBlank()) {
            base = base.stream()
                    .filter(u -> role.equalsIgnoreCase(u.getUserType()))
                    .toList();
        }

        if (q != null && !q.isBlank()) {
            String term = q.toLowerCase();
            base = base.stream().filter(u ->
                    (u.getNombre() != null && u.getNombre().toLowerCase().contains(term)) ||
                            (u.getApellido() != null && u.getApellido().toLowerCase().contains(term)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(term))
            ).toList();
        }

        List<UserDto> dtos = base.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(dtos));
    }

    // -------------------------------------------------------
    // OBTENER USUARIO POR ID
    // -------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> get(@PathVariable Long id) {
        usuarios u = usuarioService.buscarPorId(id);

        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.msg("Usuario no encontrado"));
        }

        return ResponseEntity.ok(ApiResponse.ok(UserMapper.toDto(u)));
    }

    // -------------------------------------------------------
    // CREAR USUARIO
    // -------------------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> create(@Valid @RequestBody UserDto dto) {

        try {
            usuarios ent = UserMapper.toEntity(dto);
            usuarios saved = usuarioService.registrarUsuario(ent);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok(UserMapper.toDto(saved)));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.msg("El correo ya está registrado"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.msg("Error al registrar usuario"));
        }
    }

    // -------------------------------------------------------
    // ACTUALIZAR USUARIO
    // -------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserDto dto) {

        usuarios u = usuarioService.buscarPorId(id);

        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.msg("Usuario no existe"));
        }

        try {
            UserMapper.copy(dto, u);
            usuarios saved = usuarioService.actualizarUsuario(u);

            return ResponseEntity.ok(ApiResponse.ok(UserMapper.toDto(saved)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.msg("No se pudo actualizar el usuario"));
        }
    }

    // -------------------------------------------------------
    // ELIMINAR USUARIO
    // -------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(ApiResponse.msg("Eliminado"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.msg("No se pudo eliminar el usuario"));
        }
    }
}
