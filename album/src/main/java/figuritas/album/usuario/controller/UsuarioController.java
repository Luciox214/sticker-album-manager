package figuritas.album.usuario.controller;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.usuario.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/usuario")
@Tag(name = "Gestión de Usuarios", description = "Endpoints para administrar usuarios del sistema")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Operation(
            summary = "Crear nuevo usuario",
            description = "Registra un nuevo usuario en el sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos o username ya existe")
    })
    @PostMapping
    public void crearUsuario(
            @Parameter(description = "Datos del usuario a crear", required = true)
            @RequestBody Usuario usuario){
        usuarioService.crearUsuario(usuario);
    }

    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Retorna la lista completa de todos los usuarios registrados en el sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class))
    )
    @GetMapping
    public List<Usuario> obtenerUsuarios(){
        return usuarioService.obtenerUsuarios();
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema por su ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public void eliminarUsuario(
            @Parameter(description = "ID del usuario a eliminar", required = true, example = "123")
            @PathVariable Long id){
        usuarioService.eliminarUsuario(id);
    }

    @Service
    public static class UsuarioService {
        @Autowired
        private UsuarioRepository usuarioRepository;
        public void crearUsuario(Usuario usuario){
            usuarioRepository.save(usuario);
        }
        public List<Usuario> obtenerUsuarios(){
            return usuarioRepository.findAll();
        }

        public void eliminarUsuario(Long id) {
            usuarioRepository.deleteById(id);
        }
    }
}