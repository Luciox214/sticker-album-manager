package figuritas.album.simulacion.controller;
import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.simulacion.service.SimulacionCompraService;
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
@RequestMapping("api/v1/simulacion")
@Tag(name = "Simulación de Compra", description = "Endpoint para simular compra de paquetes de figuritas")
public class SimulacionController {

    @Autowired
    SimulacionCompraService simulacionCompraService;

    @Operation(
            summary = "Simula la compra de un paquete",
            description = "Devuelve 5 figuritas aleatorias del álbum seleccionado para el usuario especificado"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paquete generado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSticker.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario o álbum no encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en los parámetros de entrada"
            )
    })
    @PostMapping("/comprar/{albumId}")
    public List<UserSticker> simularCompra(
            @Parameter(description = "ID del usuario que realiza la compra", required = true, example = "1")
            @RequestParam Long userId,

            @Parameter(description = "ID del álbum del cual se compran las figuritas", required = true, example = "2")
            @PathVariable Long albumId) {

        return simulacionCompraService.comprarPaquete(userId, albumId);
    }
}