package figuritas.album.album.controller;

import figuritas.album.album.model.Album;
import figuritas.album.album.service.AlbumService;
import figuritas.album.response.MessageResponse;
import figuritas.album.response.ResponseApi;
import figuritas.album.sticker.model.Sticker;
import figuritas.album.userSticker.model.UserSticker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
@Tag(name = "Gestión de Álbumes", description = "Administración de álbumes y figuritas")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Operation(summary = "Crear un nuevo álbum", description = "Registra un nuevo álbum en el sistema")
    @ApiResponse(responseCode = "201", description = "Álbum creado exitosamente")
    @PostMapping
    public ResponseEntity<MessageResponse> crearAlbum(@RequestBody Album album) {
        Album nuevo = albumService.crearAlbum(album);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MessageResponse.success("Album creado con éxito: " + nuevo.getTitulo()));
    }

    @Operation(summary = "Cargar figuritas en un álbum", description = "Agrega una lista de figuritas a un álbum existente")
    @ApiResponse(responseCode = "200", description = "Figuritas cargadas correctamente")
    @PostMapping("/{id}/stickers")
    public ResponseEntity<MessageResponse> cargarFiguritas(@PathVariable Long id, @RequestBody List<Sticker> stickers) {
        Album actualizado = albumService.cargarAlbum(id, stickers);
        return ResponseEntity.ok(MessageResponse.success("Se cargaron " + stickers.size() + " figuritas al álbum " + actualizado.getTitulo()));
    }

    @Operation(summary = "Eliminar un álbum", description = "Elimina un álbum por su ID")
    @ApiResponse(responseCode = "200", description = "Álbum eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminarAlbum(@PathVariable Long id) {
        albumService.borrarAlbum(id);
        return ResponseEntity.ok(MessageResponse.success("Álbum eliminado correctamente con ID: " + id));
    }

    @Operation(summary = "Listar todos los álbumes", description = "Devuelve la lista completa de álbumes del sistema")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<ResponseApi<List<Album>>> listarAlbums() {
        List<Album> albums = albumService.obtenerAlbums();
        ResponseApi<List<Album>> response = ResponseApi.success(
                "Listado de álbumes obtenido correctamente",
                albums
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener figuritas repetidas del usuario", description = "Devuelve la lista de figuritas repetidas de un usuario en un álbum")
    @ApiResponse(responseCode = "200", description = "Figuritas repetidas obtenidas correctamente")
    @GetMapping("/repetidas")
    public ResponseEntity<ResponseApi<List<UserSticker>>> obtenerFiguritasRepetidas(
            @RequestParam Long userId) {
        List<UserSticker> repetidas = albumService.obtenerFiguritasRepetidas(userId);
        ResponseApi<List<UserSticker>> response= ResponseApi.success("Figuritas repetidas obtenidas correctamente", repetidas);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Porcentaje de álbum completo", description = "Calcula el porcentaje de figuritas completadas en el álbum del usuario")
    @ApiResponse(responseCode = "200", description = "Porcentaje obtenido exitosamente")
    @GetMapping("/{albumId}/porcentaje")
    public ResponseEntity<MessageResponse> obtenerPorcentajeAlbumCompleto(
            @RequestParam Long userId,
            @PathVariable Long albumId) {

        double porcentaje = albumService.obtenerPorcentajeAlbumCompleto(userId, albumId);
        return ResponseEntity
                .ok(MessageResponse.success("El álbum " + albumId + " del usuario " + userId + " está completo en un " + porcentaje + "%"));
    }

    @Operation(summary = "Verificar y crear recompensa si el álbum está completo",
           description = "Si el usuario completó el álbum, crea el UserReward y notifica (Observer).")
    @ApiResponse(responseCode = "200", description = "Chequeo realizado")
    @PostMapping("/{albumId}/check-completo")
    public ResponseEntity<CheckCompletoResponse> verificarYCrearReward(
            @RequestParam Long userId,
            @PathVariable Long albumId) {

        boolean creado = albumService.verificarYCrearRewardSiCorresponde(userId, albumId);
        String mensaje = creado
            ? "🎉 Álbum completo: recompensa creada y notificada."
            : "Álbum aún no completo. No se creó recompensa.";

        return ResponseEntity.ok(new CheckCompletoResponse(albumId, userId, creado, mensaje));  
    }

/** DTO para la respuesta del check */
public record CheckCompletoResponse(Long albumId, Long userId, boolean recompensaCreada, String mensaje) {}


    @Operation(summary = "Obtener figuritas faltantes del usuario", description = "Devuelve la lista de figuritas que le faltan al usuario para completar el álbum")
    @ApiResponse(responseCode = "200", description = "Figuritas faltantes obtenidas correctamente")
    @GetMapping("/{albumId}/faltantes")
    public ResponseEntity<ResponseApi<List<Sticker>>> obtenerFiguritasFaltantes(
            @RequestParam Long userId,
            @PathVariable Long albumId) {
        List<Sticker> faltantes = albumService.obtenerFiguritasFaltantes(userId, albumId);
        ResponseApi<List<Sticker>> response = ResponseApi.success("Figuritas faltantes obtenidas correctamente", faltantes);

        return ResponseEntity.ok(response);
    }
}
