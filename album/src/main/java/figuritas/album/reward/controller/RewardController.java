package figuritas.album.reward.controller;
import figuritas.album.reward.model.Reward;
import figuritas.album.reward.model.UserReward;
import figuritas.album.reward.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/premios")
@Tag(name = "Gestión de Premios", description = "Creación y reclamo de premios de álbumes")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @Operation(summary = "Crear un nuevo premio", description = "Registra un nuevo premio en el sistema")
    @ApiResponse(responseCode = "201", description = "Premio creado exitosamente")
    @PostMapping
    public ResponseEntity<String> crearPremio(@RequestBody Reward reward) {
        Reward nuevo = rewardService.crearPremio(reward);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Premio creado con éxito: " + nuevo.getTipo());
    }

    /* 
    @Operation(summary = "Reclamar premio", description = "Permite a un usuario reclamar un premio asociado a su álbum")
    @ApiResponse(responseCode = "200", description = "Premio reclamado exitosamente")
    @PostMapping("/reclamar")
    public ResponseEntity<String> reclamarPremio(
            @RequestParam Long usuarioId,
            @RequestParam Long albumId) {

        UserReward reclamado = rewardService.reclamarPremio(usuarioId, albumId);
        return ResponseEntity.ok("Premio '" + reclamado.getReward().getTipo()    +
                "' reclamado correctamente por el usuario con ID: " + usuarioId);
    }*/

    @Operation(summary = "Listar todos los premios", description = "Devuelve la lista de premios disponibles en el sistema")
    @ApiResponse(responseCode = "200", description = "Premios listados correctamente")
    @GetMapping
    public ResponseEntity<Iterable<Reward>> listarPremios() {
        return ResponseEntity.ok(rewardService.listarPremios());
    }

    @Operation(summary = "Listar premios reclamados de un usuario", description = "Devuelve todos los premios que un usuario ha reclamado")
    @ApiResponse(responseCode = "200", description = "Premios reclamados obtenidos correctamente")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Iterable<UserReward>> listarPremiosReclamados(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(rewardService.listarPremiosReclamados(usuarioId));
    }
}
