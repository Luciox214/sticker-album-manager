package figuritas.album.reward.repository;

import figuritas.album.album.model.Album;
import figuritas.album.reward.model.UserReward;
import figuritas.album.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import java.util.Optional;
@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    boolean existsByUsuarioAndAlbum(Usuario usuario, Album album);

    Iterable<UserReward> findByUsuario(Usuario usuario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ur FROM UserReward ur WHERE ur.usuario = :usuario AND ur.album = :album")
    Optional<UserReward> findByUsuarioAndAlbumForUpdate(@Param("usuario") Usuario usuario,
                                                        @Param("album") Album album);

}
