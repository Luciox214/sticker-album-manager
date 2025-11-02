package figuritas.album.reward.repository;

import figuritas.album.album.model.Album;
import figuritas.album.reward.model.UserReward;
import figuritas.album.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import figuritas.album.reward.model.Reward;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    boolean existsByUsuarioAndAlbum(Usuario usuario, Album album);

    Iterable<UserReward> findByUsuario(Usuario usuario);
    
    Optional<UserReward> findByUsuarioAndAlbumAndReward(Usuario usuario, Album album, Reward reward);
}
