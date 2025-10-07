package figuritas.album.album.controller;
import figuritas.album.album.model.Album;
import figuritas.album.album.service.AlbumService;
import figuritas.album.sticker.model.Sticker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/album")
@Tag(name = "Gestión de Álbumes", description = "Endpoints para administrar álbumes y figuritas")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Operation(
            summary = "Crear nuevo álbum",
            description = "Crea un nuevo álbum en el sistema"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Álbum creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Album.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del álbum inválidos"
            )
    })
    @PostMapping
    public Album guardarAlbum(
            @Parameter(description = "Datos del álbum a crear", required = true)
            @RequestBody Album album){
        return albumService.crearAlbum(album);
    }

    @Operation(
            summary = "Cargar figuritas en álbum",
            description = "Agrega una lista de figuritas a un álbum existente"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Figuritas cargadas exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Album.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Álbum no encontrado"
            )
    })
    @PostMapping("/{id}/stickers")
    public Album cargarFiguritas(
            @Parameter(description = "ID del álbum", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Lista de figuritas a agregar", required = true)
            @RequestBody List<Sticker> stickers) {
        return albumService.cargarAlbum(id, stickers);
    }

    @Operation(
            summary = "Eliminar álbum",
            description = "Elimina un álbum del sistema por su ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Álbum eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado")
    })
    @DeleteMapping("/{id}")
    public void borrarAlbum(
            @Parameter(description = "ID del álbum a eliminar", required = true, example = "456")
            @PathVariable("id") Long id){
        albumService.borrarAlbum(id);
    }

    @Operation(
            summary = "Obtener todos los álbumes",
            description = "Retorna la lista completa de todos los álbumes disponibles en el sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de álbumes obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Album.class))
    )
    @GetMapping
    public List<Album> obtenerAlbums(){
        return albumService.obtenerAlbums();
    }

}