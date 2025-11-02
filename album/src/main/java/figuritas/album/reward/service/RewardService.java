package figuritas.album.reward.service;
import figuritas.album.album.model.Album;
import figuritas.album.album.repository.AlbumRepository;
import figuritas.album.reward.model.Reward;
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
import org.springframework.stereotype.Service;

@Service
public class RewardService {
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    UserRewardRepository userRewardRepository;
    @Autowired
    StickerRepository stickerRepository;
    @Autowired
    UserStickerRepository userStickerRepository;
    @Autowired
    RewardRepository rewardRepository;
    @Autowired
    NotificationService notificationService;
    public Reward crearPremio(Reward reward) {
        Long albumId = reward.getAlbum().getId();
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));
        if (rewardRepository.existsByAlbumId(albumId)) {
            throw new IllegalStateException("El álbum ya tiene un premio asociado.");
        }
        reward.setAlbum(album);
        return rewardRepository.save(reward);
    }

    /* 
    @Transactional
    public UserReward reclamarPremio(Long usuarioId, Long albumId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));
        Reward reward = rewardRepository.findByAlbumId(albumId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró un premio para el álbum con ID: " + albumId));

        if (userRewardRepository.existsByUsuarioAndAlbum(usuario, album)) {
            throw new IllegalStateException("El usuario ya ha reclamado el premio para este álbum.");
        }
        long totalFiguritasAlbum = stickerRepository.countByAlbum(album);
        long totalFiguritasUsuario = userStickerRepository.countByUserAndAlbum(usuario, album);

        if (totalFiguritasAlbum == 0 || totalFiguritasAlbum > totalFiguritasUsuario) {
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
        return userRewardRepository.save(newUserReward);
    }*/

    public Iterable<Reward> listarPremios() {
        return rewardRepository.findAll();
    }

    public Iterable<UserReward> listarPremiosReclamados(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        return userRewardRepository.findByUsuario(usuario);
    }
}
