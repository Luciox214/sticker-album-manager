package figuritas.album.sticker.repository;

import figuritas.album.album.model.Album;
import figuritas.album.sticker.model.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {
    List<Sticker> findByAlbumId(Long albumId);

    long countByAlbum(Album album);
}
