package figuritas.album.userSticker.repository;

import figuritas.album.album.model.Album;
import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;
import figuritas.album.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {
    @Query("SELECT COUNT(DISTINCT us.sticker.id) FROM UserSticker us WHERE us.usuario = :usuario AND us.sticker.album = :album")
    long countByUserAndAlbum(@Param("usuario") Usuario usuario, @Param("album") Album album);
    @Query("SELECT us.sticker.id FROM UserSticker us WHERE us.usuario = :usuario AND us.sticker.album = :album AND us.estadoDB = :estado GROUP BY us.sticker.id HAVING COUNT(us) > 1")
    List<Long> findDuplicateStickerIdsByUsuarioAndAlbumAndEstado(@Param("usuario") Usuario usuario,
                                                                 @Param("album") Album album,
                                                                 @Param("estado") UserStickerEstado estado);

    @Query("SELECT us FROM UserSticker us WHERE us.usuario = :usuario AND us.sticker.id IN :stickerIds AND us.estadoDB = :estado")
    List<UserSticker> findByUsuarioAndStickerIdsAndEstado(@Param("usuario") Usuario usuario,
                                                          @Param("stickerIds") List<Long> stickerIds,
                                                          @Param("estado") UserStickerEstado estado);

    @Query("SELECT us FROM UserSticker us WHERE us.usuario = :usuario AND us.sticker.album = :album")
    List<UserSticker> findByUsuarioAndAlbum(@Param("usuario") Usuario usuario, @Param("album") Album album);

    @Query("SELECT us FROM UserSticker us WHERE us.usuario.id = :usuarioId AND us.sticker.album.id = :albumId")
    List<UserSticker> findAllByUsuarioIdAndAlbumId(@Param("usuarioId") Long usuarioId, @Param("albumId") Long albumId);

    long countByUsuarioAndStickerAlbum(Usuario usuario, Album album);
}
