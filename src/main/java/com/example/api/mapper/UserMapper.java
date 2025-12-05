package com.example.api.mapper;

import com.example.api.dto.UserDto;
import com.example.model.usuarios;

public class UserMapper {

    // =======================
    //     ENTITY → DTO
    // =======================
    public static UserDto toDto(usuarios u) {
        if (u == null) return null;

        UserDto d = new UserDto();

        d.setId(u.getId());
        d.setNombre(u.getNombre());
        d.setApellido(u.getApellido());
        d.setEmail(u.getEmail());
        d.setUserType(u.getUserType());
        d.setEspecialidades(u.getEspecialidades());
        d.setExperiencia(u.getExperiencia()); // Integer → Integer ✔

        return d;
    }

    // =======================
    //     DTO → ENTITY
    // =======================
    public static usuarios toEntity(UserDto d) {
        if (d == null) return null;

        usuarios u = new usuarios();

        u.setId(d.getId());
        u.setNombre(d.getNombre());
        u.setApellido(d.getApellido());
        u.setEmail(d.getEmail());
        u.setUserType(d.getUserType());
        u.setEspecialidades(d.getEspecialidades());
        u.setExperiencia(d.getExperiencia()); // Integer ✔

        return u;
    }

    // =======================
    //    COPY (UPDATE)
    // =======================
    public static void copy(UserDto d, usuarios u) {
        if (d == null || u == null) return;

        u.setNombre(d.getNombre());
        u.setApellido(d.getApellido());
        u.setEmail(d.getEmail());
        u.setUserType(d.getUserType());
        u.setEspecialidades(d.getEspecialidades());
        u.setExperiencia(d.getExperiencia()); // Integer ✔
    }
}
