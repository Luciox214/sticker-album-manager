package figuritas.album.album.service;

import figuritas.album.album.repository.AlbumRepository;
import figuritas.album.reward.model.Reward;
import figuritas.album.reward.model.UserReward;
import figuritas.album.reward.repository.RewardRepository;
import figuritas.album.reward.repository.UserRewardRepository;
import figuritas.album.reward.state.IObserver;
import figuritas.album.reward.state.NoReclamado;
import figuritas.album.reward.state.Reclamado;
import figuritas.album.album.model.Album;
import figuritas.album.sticker.model.Sticker;
import figuritas.album.sticker.repository.StickerRepository;
import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.repository.UserStickerRepository;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class AlbumService {
    @Autowired
    UserStickerRepository userStickerRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private AlbumRepository albumRepository;
    private IObserver observer; 

    @Autowired
    private StickerRepository stickerRepository;
    
    @Autowired
    private UserRewardRepository userRewardRepository;
    @Autowired
    private RewardRepository rewardRepository;


    public Album crearAlbum(Album album) {
        album.setTotalFiguritas(0);
        return albumRepository.save(album);
    }

    public Album cargarAlbum(Long albumId, List<Sticker> stickers) {
        Album album = albumRepository.findById(albumId)
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
        albumRepository.deleteById(id);
    }

    public List<Album> obtenerAlbums() {
        return albumRepository.findAll();
    }

    @Transactional()
    public List<UserSticker> obtenerFiguritasRepetidas(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + userId));

        List<Long> stickerIdDuplicados = userStickerRepository.findDuplicateStickerIdsByUsuario(usuario);

        if (stickerIdDuplicados.isEmpty()) {
            return Collections.emptyList();
        }
        return userStickerRepository.findByUsuarioAndStickerIds(usuario, stickerIdDuplicados);
    }
    // solo calcula el porcentaje de album completo 
    public double obtenerPorcentajeAlbumCompleto(Long usuarioId, Long albumId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));
        long totalFiguritasAlbum = album.getTotalFiguritas();
        if (totalFiguritasAlbum == 0) {
            return 0.0;
        }
        long figuritasUnicasUsuario = userStickerRepository.countByUserAndAlbum(usuario, album);
        return ((double) figuritasUnicasUsuario / totalFiguritasAlbum) * 100;
    }

    @Transactional()
    public List<Sticker> obtenerFiguritasFaltantes(Long usuarioId, Long albumId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado con ID: " + albumId));

        List<Sticker> albumStickers = stickerRepository.findByAlbumId(albumId);

        List<Long> stickerIds = albumStickers.stream()
                .map(Sticker::getId)
                .toList();

        if (stickerIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserSticker> usuarioStickers = userStickerRepository.findByUsuarioAndStickerIds(
                usuario,
                stickerIds);

        List<Long> usuarioStickerIds = usuarioStickers.stream()
                .map(us -> us.getSticker().getId())
                .distinct()
                .toList();

        return albumStickers.stream()
                .filter(sticker -> !usuarioStickerIds.contains(sticker.getId()))
                .toList();
    }
    

    // Verifica si el album esta completo y crea reward + notificacion. 
    @Transactional
    public boolean verificarYCrearRewardSiCorresponde(Long usuarioId, Long albumId) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    Album album = albumRepository.findById(albumId)
            .orElseThrow(() -> new EntityNotFoundException("Álbum no encontrado"));

    long total = album.getTotalFiguritas();
    long unicas = userStickerRepository.countByUserAndAlbum(usuario, album);

    if (total > 0 && unicas == total) {
        crearRewardYNotificar(usuario, album); 
        return true;
    }
    return false;
    }

    @Transactional
    private void crearRewardYNotificar(Usuario usuario, Album album) {

        Reward reward = rewardRepository.findByAlbumId(album.getId())
                .orElseGet(() -> {
                    Reward r = new Reward();
                    r.setAlbum(album);
                    r.setTipo("ALBUM_COMPLETO"); // usa tu campo real (tipo/código)
                    return rewardRepository.save(r);
                });

        boolean yaExiste = userRewardRepository.existsByUsuarioAndAlbum(usuario, album);
        if (yaExiste) return;

        UserReward nuevoUserReward = new UserReward();
        nuevoUserReward.setUsuario(usuario);
        nuevoUserReward.setAlbum(album);
        nuevoUserReward.setReward(reward);
        nuevoUserReward.cambiarEstado(new NoReclamado());
        if(this.observer != null) {
            this.observer.actualizar(nuevoUserReward);   
        }     
        userRewardRepository.save(nuevoUserReward);
        
    }
}