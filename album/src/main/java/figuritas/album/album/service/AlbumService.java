package figuritas.album.album.service;
import figuritas.album.album.repository.AlbumRepository;
import figuritas.album.album.model.Album;
import figuritas.album.sticker.model.Sticker;
import figuritas.album.sticker.repository.StickerRepository;
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
            stickerRepository.save(sticker);
        }
        int total= album.getTotalFiguritas()+stickers.size();
        album.setTotalFiguritas(total);
        return albumRepository.save(album);
    }

    public void borrarAlbum(Long id) {
        albumRepository.deleteById(id);
    }

    public List<Album> obtenerAlbums() {
        return albumRepository.findAll();
    }
}