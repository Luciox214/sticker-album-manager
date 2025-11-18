package figuritas.album.simulacion.service;

import figuritas.album.sticker.model.Sticker;
import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.album.repository.AlbumRepository;
import figuritas.album.sticker.repository.StickerRepository;
import figuritas.album.userSticker.repository.UserStickerRepository;
import figuritas.album.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.Objects;

@Service
public class SimulacionCompraService {
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    StickerRepository stickerRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    UserStickerRepository userStickerRepository;

    @Transactional
    public List<UserSticker> comprarPaquete(Long userId, Long albumId) {
        Long safeUserId = Objects.requireNonNull(userId, "userId no puede ser nulo");
        Long safeAlbumId = Objects.requireNonNull(albumId, "albumId no puede ser nulo");

        Usuario usuario = usuarioRepository.findById(safeUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        albumRepository.findById(safeAlbumId)
                .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado"));

        List<Sticker> figuritasDisponibles = stickerRepository.findByAlbumId(safeAlbumId);
        if (figuritasDisponibles.isEmpty()) {
            throw new IllegalStateException("No hay figuritas disponibles para este álbum");
        }

        Collections.shuffle(figuritasDisponibles);
        List<Sticker> seleccionadas = figuritasDisponibles.stream()
                .limit(5)
                .toList();

        List<UserSticker> obtenidas = seleccionadas.stream()
                .map(sticker -> {
                    UserSticker us = new UserSticker();
                    us.setUsuario(usuario);
                    us.setSticker(sticker);
                    // Se establece el estado usando el enum para persistencia
                    // El patrón State se inicializará automáticamente con @PostLoad
                    us.setEstadoDB(UserStickerEstado.EN_COLECCION);
                    // Utiliza ponerEnColeccion para asegurar que la figurita esté correctamente
                    // inicializada en la colección mediante el patrón State
                    us.ponerEnColeccion();
                    return us;
                })
                .toList();

        return userStickerRepository
                .saveAll(Objects.requireNonNull(obtenidas, "La lista de figuritas obtenidas no puede ser nula"));
    }

}
