package figuritas.album.services;

import figuritas.album.model.Album;
import figuritas.album.model.Sticker;
import figuritas.album.repositories.AlbumRepository;
import figuritas.album.repositories.StickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private StickerRepository stickerRepository;

    public Album crearAlbum(Album album) {
        return albumRepository.save(album);
    }

    public Album cargarAlbum(Long albumId, List<Sticker> stickers) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado con id: " + albumId));
        for (Sticker sticker : stickers) {
            sticker.setAlbum(album);
        }
        stickerRepository.saveAll(stickers);
        return albumRepository.save(album);
    }
}
