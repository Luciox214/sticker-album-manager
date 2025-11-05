package figuritas.album.usuario.controller;
import figuritas.album.response.MessageResponse;
import figuritas.album.response.ResponseApi;
import figuritas.album.usuario.UsuarioDTO;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/usuarios")
@Tag(name = "Gestión de Usuarios", description = "Administración de usuarios y sus figuritas")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Crear un nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente")
    @PostMapping
    public ResponseEntity<MessageResponse> crearUsuario(@RequestBody Usuario usuario) {
        usuarioService.crearUsuario(usuario);
        return ResponseEntity
                .ok()
                .body(MessageResponse.success("Usuario creado con éxito: " + usuario.getUsername()));
    }

    @Operation(summary = "Listar usuarios", description = "Devuelve la lista completa de usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente")
    @GetMapping
    public ResponseEntity<ResponseApi<List<UsuarioDTO>>> obtenerUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerUsuarios();
        ResponseApi<List<UsuarioDTO>> response = ResponseApi.success(
                "Listado de usuarios obtenido correctamente",
                usuarios
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(MessageResponse.success("Usuario eliminado correctamente con ID: " + id));
    }

}
