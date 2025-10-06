package figuritas.album.repositories;

import figuritas.album.model.UserSticker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {
}
