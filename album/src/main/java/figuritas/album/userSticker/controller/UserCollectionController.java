package figuritas.album.userSticker.controller;

import figuritas.album.album.model.CollectionProgressDTO;
import figuritas.album.album.model.CollectionRarityDTO;
import figuritas.album.album.service.AlbumService;
import figuritas.album.response.ResponseApi;
import figuritas.album.sticker.model.Sticker;
import figuritas.album.userSticker.model.UserSticker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{usuarioId}/albums/{albumId}/collection")
@Tag(name = "Colección de Usuario", description = "Consultas sobre la colección del usuario para un álbum específico")
public class UserCollectionController {

    @Autowired
    private AlbumService albumService;

    @Operation(summary = "Obtener progreso de colección", description = "Devuelve el estado de completitud del álbum para el usuario.")
    @ApiResponse(responseCode = "200", description = "Progreso obtenido correctamente")
    @GetMapping("/progress")
    public ResponseEntity<ResponseApi<CollectionProgressDTO>> obtenerProgreso(
            @PathVariable Long usuarioId,
            @PathVariable Long albumId) {
        CollectionProgressDTO progreso = albumService.obtenerProgresoAlbum(usuarioId, albumId);
        return ResponseEntity.ok(ResponseApi.success("Progreso obtenido correctamente", progreso));
    }

    @Operation(summary = "Obtener figuritas faltantes", description = "Lista las figuritas que el usuario aún no posee para el álbum.")
    @ApiResponse(responseCode = "200", description = "Listado de faltantes obtenido correctamente")
    @GetMapping("/missing")
    public ResponseEntity<ResponseApi<List<Sticker>>> obtenerFaltantes(
            @PathVariable Long usuarioId,
            @PathVariable Long albumId) {
        List<Sticker> faltantes = albumService.obtenerFiguritasFaltantes(usuarioId, albumId);
        return ResponseEntity.ok(ResponseApi.success("Figuritas faltantes obtenidas correctamente", faltantes));
    }

    @Operation(summary = "Obtener figuritas duplicadas", description = "Devuelve las figuritas que el usuario tiene repetidas en estado de colección para el álbum.")
    @ApiResponse(responseCode = "200", description = "Listado de duplicadas obtenido correctamente")
    @GetMapping("/duplicates")
    public ResponseEntity<ResponseApi<List<UserSticker>>> obtenerDuplicadas(
            @PathVariable Long usuarioId,
            @PathVariable Long albumId) {
        List<UserSticker> duplicadas = albumService.obtenerFiguritasRepetidas(usuarioId, albumId);
        return ResponseEntity.ok(ResponseApi.success("Figuritas duplicadas obtenidas correctamente", duplicadas));
    }

    @Operation(summary = "Colección agrupada por rareza", description = "Devuelve métricas agrupadas por rareza de las figuritas del usuario.")
    @ApiResponse(responseCode = "200", description = "Colección agrupada obtenida correctamente")
    @GetMapping("/rarity")
    public ResponseEntity<ResponseApi<List<CollectionRarityDTO>>> obtenerColeccionPorRareza(
            @PathVariable Long usuarioId,
            @PathVariable Long albumId) {
        List<CollectionRarityDTO> coleccion = albumService.obtenerColeccionPorRareza(albumId, usuarioId);
        return ResponseEntity.ok(ResponseApi.success("Colección agrupada por rareza obtenida correctamente", coleccion));
    }
}
