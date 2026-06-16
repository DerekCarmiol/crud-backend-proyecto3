package com.proyecto3.crud.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.proyecto3.crud.entity.Rol;
import com.proyecto3.crud.entity.Usuario;
import com.proyecto3.crud.repository.RolRepository;
import com.proyecto3.crud.repository.UsuarioRepository;

/**
 * Inserta los datos mínimos requeridos al arrancar el proyecto:
 * <ul>
 *   <li>Los dos roles: SUPER-ADMIN-ROLE y USER.</li>
 *   <li>Un usuario SUPER-ADMIN-ROLE y un usuario USER, con contraseña
 *       encriptada (BCrypt).</li>
 * </ul>
 * No se crea ninguna categoría (requisito del enunciado).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    public static final String ROL_SUPER_ADMIN = "SUPER-ADMIN-ROLE";
    public static final String ROL_USER = "USER";

    private static final String EMAIL_SUPER_ADMIN = "superadmin@proyecto3.com";
    private static final String EMAIL_USER = "user@proyecto3.com";
    private static final String PASSWORD_SUPER_ADMIN = "admin123";
    private static final String PASSWORD_USER = "user123";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Rol rolSuperAdmin = obtenerOCrearRol(ROL_SUPER_ADMIN);
        Rol rolUser = obtenerOCrearRol(ROL_USER);

        crearUsuarioSiNoExiste(EMAIL_SUPER_ADMIN, PASSWORD_SUPER_ADMIN, rolSuperAdmin);
        crearUsuarioSiNoExiste(EMAIL_USER, PASSWORD_USER, rolUser);
    }

    private Rol obtenerOCrearRol(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    log.info("Creando rol: {}", nombre);
                    return rolRepository.save(new Rol(nombre));
                });
    }

    private void crearUsuarioSiNoExiste(String email, String passwordPlano, Rol rol) {
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }
        Usuario usuario = new Usuario(email, passwordEncoder.encode(passwordPlano), rol);
        usuarioRepository.save(usuario);
        log.info("Usuario creado: {} (rol {})", email, rol.getNombre());
    }
}
