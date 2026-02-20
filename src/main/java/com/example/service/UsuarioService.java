package com.example.service;

import com.example.model.usuarios;
import com.example.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Locale;
import java.util.Optional;
import java.util.List;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Password por defecto si el archivo no trae contraseña
    private static final String DEFAULT_RAW_PASSWORD = "SunObra123*";

    /**
     * Registrar un nuevo usuario
     */
    public usuarios registrarUsuario(usuarios usuario) throws Exception {
        try {
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                throw new Exception("Email requerido");
            }
            // Verificar si el email ya existe
            if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                throw new Exception("El email ya está registrado");
            }

            // Encriptar la contraseña (si viene nula, se usa la por defecto)
            String raw = (usuario.getPassword() == null || usuario.getPassword().isBlank())
                    ? DEFAULT_RAW_PASSWORD
                    : usuario.getPassword();
            usuario.setPassword(passwordEncoder.encode(raw));

            // Normalizar rol
            if (usuario.getUserType() == null || usuario.getUserType().isBlank()) {
                usuario.setUserType("cliente");
            } else {
                usuario.setUserType(usuario.getUserType().trim().toLowerCase(Locale.ROOT));
            }

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
     * Verificar credenciales de login
     */
    public usuarios verificarCredenciales(String email, String password, String userType) {
        Optional<usuarios> usuarioOpt = usuarioRepository.findByEmailAndUserType(email, userType);
        if (usuarioOpt.isPresent()) {
            usuarios usuario = usuarioOpt.get();
            boolean passwordMatch = passwordEncoder.matches(password, usuario.getPassword());
            if (passwordMatch) return usuario;
        }
        return null;
    }

    public Optional<usuarios> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public List<usuarios> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public usuarios buscarPorId(Long id) {
        Optional<usuarios> usuarioOpt = usuarioRepository.findById(id);
        return usuarioOpt.orElse(null);
    }

    public usuarios actualizarUsuario(usuarios usuario) {
        try {
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            } else {
                usuarios usuarioActual = usuarioRepository.findById(usuario.getId()).orElse(null);
                if (usuarioActual != null) {
                    usuario.setPassword(usuarioActual.getPassword());
                }
            }
            if (usuario.getUserType() != null) {
                usuario.setUserType(usuario.getUserType().trim().toLowerCase(Locale.ROOT));
            }
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarUsuario(Long id) {
        try {
            usuarioRepository.deleteById(id);
            System.out.println("Usuario eliminado exitosamente con ID: " + id);
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            throw e;
        }
    }

    // =========================
    //  CARGA MASIVA (CSV/Excel)
    // =========================

    /**
     * Importa usuarios desde un archivo CSV/XLS/XLSX.
     * Encabezados esperados (en cualquier orden):
     * nombre, apellido, email, userType, especialidades, experiencia, telefono, direccion, password
     *
     * ✅ Ahora retorna ImportResult (insertados + duplicados)
     */
    public ImportResult importarUsuarios(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debe adjuntar un archivo");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Archivo sin nombre");
        }

        String lower = filename.toLowerCase(Locale.ROOT).trim();
        if (lower.endsWith(".csv")) {
            return importarDesdeCsv(file);
        } else if (lower.endsWith(".xls") || lower.endsWith(".xlsx")) {
            return importarDesdeExcel(file);
        } else {
            throw new IllegalArgumentException("Formato no soportado. Use CSV, XLS o XLSX");
        }
    }

    // ------- CSV (mapeo por encabezado) -------
    private ImportResult importarDesdeCsv(MultipartFile file) throws Exception {
        int inserted = 0;
        List<String> duplicates = new ArrayList<>();

        try (InputStream in = file.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            // Encabezado obligatorio
            String header = br.readLine();
            if (header == null || header.isBlank()) {
                throw new IllegalArgumentException("El CSV debe incluir encabezados.");
            }

            String[] cols = header.split(",", -1);
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < cols.length; i++) {
                String key = safe(cols[i]).toLowerCase(Locale.ROOT);
                if (!key.isBlank()) idx.put(key, i);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = line.split(",", -1); // conserva vacíos

                usuarios u = new usuarios();
                u.setNombre(getByName(c, idx, "nombre"));
                u.setApellido(getByName(c, idx, "apellido"));
                String email = getByName(c, idx, "email");
                u.setEmail(email == null ? null : email.trim().toLowerCase(Locale.ROOT));

                String role = getByName(c, idx, "usertype");
                u.setUserType(role == null || role.isBlank() ? "cliente" : role.trim().toLowerCase(Locale.ROOT));

                u.setEspecialidades(getByName(c, idx, "especialidades"));
                u.setExperiencia(parseIntSafe(getByName(c, idx, "experiencia")));
                u.setTelefono(getByName(c, idx, "telefono"));
                u.setDireccion(getByName(c, idx, "direccion"));

                String rawPass = getByName(c, idx, "password");
                u.setPassword((rawPass == null || rawPass.isBlank()) ? DEFAULT_RAW_PASSWORD : rawPass);

                if (u.getEmail() != null && !emailExiste(u.getEmail())) {
                    registrarUsuario(u);
                    inserted++;
                } else if (u.getEmail() != null) {
                    // ✅ Duplicado
                    duplicates.add(u.getEmail());
                }
            }
        }

        return new ImportResult(inserted, duplicates);
    }

    private String getByName(String[] row, Map<String,Integer> idx, String name) {
        Integer i = idx.get(name);
        if (i == null || i < 0 || i >= row.length) return null;
        String v = row[i];
        return v == null ? null : v.trim();
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }

    // ------- Excel (XLS/XLSX) con encabezado -------
    private ImportResult importarDesdeExcel(MultipartFile file) throws Exception {
        int inserted = 0;
        List<String> duplicates = new ArrayList<>();
        DataFormatter fmt = new DataFormatter(Locale.ROOT);

        try (InputStream in = file.getInputStream();
             Workbook wb = WorkbookFactory.create(in)) {

            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new IllegalArgumentException("El Excel está vacío.");
            }

            // Fila 0 = encabezados
            Row header = sheet.getRow(0);
            if (header == null) {
                throw new IllegalArgumentException("El Excel debe incluir encabezados en la primera fila.");
            }

            Map<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < header.getLastCellNum(); i++) {
                String key = safe(fmt.formatCellValue(header.getCell(i))).toLowerCase(Locale.ROOT);
                if (!key.isBlank()) idx.put(key, i);
            }

            // Filas de datos
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                if (isRowEmpty(row)) continue;

                usuarios u = new usuarios();
                u.setNombre(cellByName(row, idx, "nombre", fmt));
                u.setApellido(cellByName(row, idx, "apellido", fmt));
                String email = cellByName(row, idx, "email", fmt);
                u.setEmail(email == null ? null : email.trim().toLowerCase(Locale.ROOT));

                String role = cellByName(row, idx, "usertype", fmt);
                u.setUserType(role == null || role.isBlank() ? "cliente" : role.trim().toLowerCase(Locale.ROOT));

                u.setEspecialidades(cellByName(row, idx, "especialidades", fmt));
                u.setExperiencia(parseIntSafe(cellByName(row, idx, "experiencia", fmt)));
                u.setTelefono(cellByName(row, idx, "telefono", fmt));
                u.setDireccion(cellByName(row, idx, "direccion", fmt));

                String rawPass = cellByName(row, idx, "password", fmt);
                u.setPassword((rawPass == null || rawPass.isBlank()) ? DEFAULT_RAW_PASSWORD : rawPass);

                if (u.getEmail() != null && !emailExiste(u.getEmail())) {
                    registrarUsuario(u);
                    inserted++;
                } else if (u.getEmail() != null) {
                    duplicates.add(u.getEmail());
                }
            }
        }

        return new ImportResult(inserted, duplicates);
    }

    private String cellByName(Row row, Map<String,Integer> idx, String name, DataFormatter fmt) {
        Integer i = idx.get(name);
        if (i == null) return null;
        return trim(fmt.formatCellValue(row.getCell(i)));
    }

    // ===== Helpers =====

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private Integer parseIntSafe(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeRole(String role) {
        if (role == null) return "cliente";
        String r = role.trim().toLowerCase(Locale.ROOT);
        if (r.startsWith("adm")) return "admin";
        if (r.startsWith("obr")) return "obrero";
        if (r.startsWith("cli")) return "cliente";
        // si viene cualquier otro, lo dejamos tal cual en minúscula
        return r;
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        DataFormatter df = new DataFormatter();
        // Revisa las primeras 9 columnas que esperamos
        for (int c = 0; c < 9; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !df.formatCellValue(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }
}
