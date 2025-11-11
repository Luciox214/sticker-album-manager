package figuritas.album.album.service;

import figuritas.album.album.model.AlbumDTO;
import figuritas.album.album.model.CollectionProgressDTO;
import figuritas.album.album.model.CollectionRarityDTO;
import figuritas.album.album.repository.AlbumRepository;
import figuritas.album.album.model.Album;
import figuritas.album.sticker.model.Sticker;
import figuritas.album.sticker.repository.StickerRepository;
import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;
import figuritas.album.userSticker.repository.UserStickerRepository;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AlbumService {
    @Autowired
    UserStickerRepository userStickerRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private StickerRepository stickerRepository;



    public Album crearAlbum(Album album) {
        album.setTotalFiguritas(0);
        return albumRepository.save(album);
    }

    public Album cargarAlbum(Long albumId, List<Sticker> stickers) {
        Album album = albumRepository.findById(Objects.requireNonNull(albumId, "albumId no puede ser nulo"))
                .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado con id: " + albumId));
        for (Sticker sticker : stickers) {
            sticker.setAlbum(album);
            stickerRepository.save(sticker);
        }
        int total = album.getTotalFiguritas() + stickers.size();
        album.setTotalFiguritas(total);
        return albumRepository.save(album);
    }

    public void borrarAlbum(Long id) {
        albumRepository.deleteById(Objects.requireNonNull(id, "id no puede ser nulo"));
    }

    public List<AlbumDTO> obtenerAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(
                        album -> new AlbumDTO(
                                album.getId(),
                                album.getTitulo(),
                                album.getTotalFiguritas()
                        )
                ).collect(Collectors.toList());
    }

    public List<UserSticker> obtenerFiguritasRepetidas(Long usuarioId, Long albumId) {
        Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        Album album = albumRepository.findById(Objects.requireNonNull(albumId, "albumId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));

        List<Long> stickerIdDuplicados = userStickerRepository
                .findDuplicateStickerIdsByUsuarioAndAlbumAndEstado(usuario, album, UserStickerEstado.EN_COLECCION);

        if (stickerIdDuplicados.isEmpty()) {
            return Collections.emptyList();
        }

        return userStickerRepository
                .findByUsuarioAndStickerIdsAndEstado(usuario, stickerIdDuplicados, UserStickerEstado.EN_COLECCION);
    }
    public CollectionProgressDTO obtenerProgresoAlbum(Long usuarioId, Long albumId) {
        Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        Album album = albumRepository.findById(Objects.requireNonNull(albumId, "albumId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));

        long totalFiguritasAlbum = stickerRepository.countByAlbum(album);
        long figuritasUnicasUsuario = userStickerRepository.countByUserAndAlbum(usuario, album);

        double porcentaje = totalFiguritasAlbum == 0
                ? 0.0
                : ((double) figuritasUnicasUsuario / totalFiguritasAlbum) * 100;

        return new CollectionProgressDTO(album.getId(), usuario.getId(), totalFiguritasAlbum, figuritasUnicasUsuario, porcentaje);
    }

    public List<Sticker> obtenerFiguritasFaltantes(Long usuarioId, Long albumId) {
        Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        Album album = albumRepository.findById(Objects.requireNonNull(albumId, "albumId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));

        List<Sticker> albumStickers = stickerRepository.findByAlbumId(albumId);

        if (albumStickers.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> usuarioStickerIds = userStickerRepository.findByUsuarioAndAlbum(usuario, album).stream()
                .map(us -> us.getSticker().getId())
                .distinct()
                .toList();

        return albumStickers.stream()
                .filter(sticker -> !usuarioStickerIds.contains(sticker.getId()))
                .toList();
    }

    public List<CollectionRarityDTO> obtenerColeccionPorRareza(Long albumId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        Album album = albumRepository.findById(Objects.requireNonNull(albumId, "albumId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));

        List<UserSticker> userStickers = userStickerRepository.findByUsuarioAndAlbum(usuario, album);

        return userStickers.stream()
                .collect(Collectors.groupingBy(userSticker -> userSticker.getSticker().getRareza()))
                .entrySet()
                .stream()
                .map(entry -> {
                    long total = entry.getValue().size();
                    long unicas = entry.getValue().stream()
                            .map(userSticker -> userSticker.getSticker().getId())
                            .distinct()
                            .count();
                    long duplicadas = total - unicas;
                    return new CollectionRarityDTO(entry.getKey(), total, unicas, duplicadas);
                })
                .sorted(Comparator.comparing(dto -> dto.getRareza().ordinal()))
                .toList();
    }

    public Album obtenerAlbumPorId(Long id) {
        return albumRepository.findById(Objects.requireNonNull(id, "id no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Album no encontrado"));

    }
}