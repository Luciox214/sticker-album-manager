package figuritas.album.userSticker.repository;

import figuritas.album.album.model.Album;
import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {
    @Query("SELECT COUNT(DISTINCT us.sticker.id) FROM UserSticker us WHERE us.usuario = :usuario AND us.sticker.album = :album")
    long countByUserAndAlbum(@Param("usuario") Usuario usuario, @Param("album") Album album);

}
