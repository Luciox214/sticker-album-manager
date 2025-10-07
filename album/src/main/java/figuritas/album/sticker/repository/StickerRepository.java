package figuritas.album.sticker.repository;

import figuritas.album.sticker.model.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
    List<Sticker> findByAlbumId(Long albumId);
}
