package figuritas.album.userSticker.repository;

import figuritas.album.userSticker.model.UserSticker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {
}
