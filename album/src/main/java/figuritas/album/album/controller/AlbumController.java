package figuritas.album.album.controller;

import figuritas.album.album.model.Album;
import figuritas.album.album.model.AlbumDTO;
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
    public ResponseEntity<ResponseApi<List<AlbumDTO>>> listarAlbums() {
        List<AlbumDTO> albums = albumService.obtenerAlbums();
        ResponseApi<List<AlbumDTO>> response = ResponseApi.success(
                "Listado de álbumes obtenido correctamente",
                albums
        );
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Obtener un álbum", description = "Obtiene un álbum por su ID")
    @ApiResponse(responseCode = "200", description = "Álbum obtenido correctamente")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseApi<Album>> obtenerAlbum(@PathVariable Long id) {
        Album album= albumService.obtenerAlbumPorId(id);
        ResponseApi<Album> response = ResponseApi.success("Álbum obtenido correctamente",album);
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
    @GetMapping("/porcentaje")
    public ResponseEntity<MessageResponse> obtenerPorcentajeAlbumCompleto(
            @RequestParam Long userId,
            @RequestParam Long albumId) {

        double porcentaje = albumService.obtenerPorcentajeAlbumCompleto(userId, albumId);
        return ResponseEntity
                .ok(MessageResponse.success("El álbum " + albumId + " del usuario " + userId + " está completo en un " + porcentaje + "%"));
    }

    @Operation(summary = "Obtener figuritas faltantes del usuario", description = "Devuelve la lista de figuritas que le faltan al usuario para completar el álbum")
    @ApiResponse(responseCode = "200", description = "Figuritas faltantes obtenidas correctamente")
    @GetMapping("/faltantes")
    public ResponseEntity<ResponseApi<List<Sticker>>> obtenerFiguritasFaltantes(
            @RequestParam Long userId,
            @RequestParam Long albumId) {
        List<Sticker> faltantes = albumService.obtenerFiguritasFaltantes(userId, albumId);
        ResponseApi<List<Sticker>> response = ResponseApi.success("Figuritas faltantes obtenidas correctamente", faltantes);

        return ResponseEntity.ok(response);
    }
}
