package figuritas.album.reward.service;
import figuritas.album.album.model.Album;
import figuritas.album.album.repository.AlbumRepository;
import figuritas.album.reward.model.Reward;
import figuritas.album.reward.model.RewardDTO;
import figuritas.album.reward.model.Sujeto;
import figuritas.album.reward.model.UserReward;
import figuritas.album.reward.repository.RewardRepository;
import figuritas.album.reward.repository.UserRewardRepository;
import figuritas.album.reward.state.NoReclamado;
import figuritas.album.reward.state.Reclamado;
import figuritas.album.sticker.repository.StickerRepository;
import figuritas.album.userSticker.repository.UserStickerRepository;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RewardService {
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    UserRewardRepository userRewardRepository;
    @Autowired
    RewardRepository rewardRepository;
    @Autowired
    NotificationService notificationService;
    @Autowired
    StickerRepository stickerRepository;
    @Autowired
    UserStickerRepository userStickerRepository;

    public Reward crearPremio(Long albumId, String tipo) {
        Long safeAlbumId = Objects.requireNonNull(albumId, "albumId no puede ser nulo");
        Album album = albumRepository.findById(safeAlbumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));
        if (rewardRepository.existsByAlbumId(safeAlbumId)) {
            throw new IllegalStateException("El álbum ya tiene un premio asociado.");
        }
        Reward reward = new Reward();
        reward.setAlbum(album);
        reward.setTipo(tipo);
        return rewardRepository.save(reward);
    }


    @Transactional
    public UserReward reclamarPremio(Long usuarioId, Long albumId) {
        Long safeUsuarioId = Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo");
        Long safeAlbumId = Objects.requireNonNull(albumId, "albumId no puede ser nulo");

        Usuario usuario = usuarioRepository.findById(safeUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        Album album = albumRepository.findById(safeAlbumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));
        Reward reward = rewardRepository.findByAlbumId(safeAlbumId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró un premio para el álbum con ID: " + albumId));

        userRewardRepository.findByUsuarioAndAlbumForUpdate(usuario, album)
                .ifPresent(existing -> {
                    throw new IllegalStateException("El usuario ya ha reclamado el premio para este álbum.");
                });

        long totalFiguritasAlbum = stickerRepository.countByAlbum(album);
        long figuritasUnicasUsuario = userStickerRepository.countByUserAndAlbum(usuario, album);

        if (totalFiguritasAlbum == 0 || figuritasUnicasUsuario < totalFiguritasAlbum) {
            throw new IllegalStateException("El álbum aún no está completo. Faltan figuritas.");
        }

        UserReward newUserReward = new UserReward();
        newUserReward.setUsuario(usuario);
        newUserReward.setAlbum(album);
        newUserReward.setReward(reward);
        newUserReward.cambiarEstado(new NoReclamado());
        newUserReward.reclamar();

        Sujeto sujeto = new Sujeto();
        sujeto.agregarObservador(notificationService);
        sujeto.notificarObservadores(newUserReward);
        newUserReward.cambiarEstado(new Reclamado());

        try {
            return userRewardRepository.saveAndFlush(newUserReward);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("El usuario ya ha reclamado el premio para este álbum.", ex);
        }

    }
    public Iterable<RewardDTO> listarPremios() {

        return rewardRepository.findAll()
                .stream()
                .map(reward -> new RewardDTO(
                        reward.getId(),
                        reward.getAlbum().getId(),
                        reward.getAlbum().getTitulo(),
                        reward.getTipo()
                )).collect(Collectors.toList());
    }

    public Iterable<UserReward> listarPremiosReclamados(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        return userRewardRepository.findByUsuario(usuario);
    }
}
