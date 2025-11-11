package figuritas.album.reward.service;

import figuritas.album.album.model.Album;
import figuritas.album.album.repository.AlbumRepository;
import figuritas.album.reward.model.Reward;
import figuritas.album.reward.model.UserReward;
import figuritas.album.reward.repository.RewardRepository;
import figuritas.album.reward.repository.UserRewardRepository;
import figuritas.album.reward.state.Reclamado;
import figuritas.album.reward.state.RewardStateEnum;
import figuritas.album.sticker.repository.StickerRepository;
import figuritas.album.userSticker.repository.UserStickerRepository;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UserRewardRepository userRewardRepository;
    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private StickerRepository stickerRepository;
    @Mock
    private UserStickerRepository userStickerRepository;

    @InjectMocks
    private RewardService rewardService;

    private Usuario usuario;
    private Album album;
    private Reward reward;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("tester");
        usuario.setEmail("tester@example.com");

        album = new Album();
        album.setId(2L);
        album.setTitulo("Album Test");

        reward = new Reward();
        reward.setId(3L);
        reward.setAlbum(album);
        reward.setTipo("SKIN");
    }

    @Test
    @SuppressWarnings("null")
    void reclamarPremio_persistirConEstadoReclamado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));
        when(rewardRepository.findByAlbumId(2L)).thenReturn(Optional.of(reward));
        when(userRewardRepository.findByUsuarioAndAlbumForUpdate(usuario, album)).thenReturn(Optional.empty());
        when(stickerRepository.countByAlbum(album)).thenReturn(3L);
        when(userStickerRepository.countByUserAndAlbum(usuario, album)).thenReturn(3L);
    when(userRewardRepository.saveAndFlush(argThat(ur -> ur != null))).thenAnswer(invocation -> invocation.getArgument(0));

        UserReward resultado = rewardService.reclamarPremio(1L, 2L);

        assertEquals(RewardStateEnum.RECLAMADO, resultado.getEstadoDB());
        assertThat(resultado.getEstado()).isInstanceOf(Reclamado.class);
        assertThat(resultado.getClaimedAt()).isNotNull();

        ArgumentCaptor<UserReward> captor = ArgumentCaptor.forClass(UserReward.class);
        verify(notificationService).actualizar(captor.capture());
        assertEquals(resultado, captor.getValue());
    }

    @Test
    void reclamarPremio_rechazaDuplicado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));
        when(rewardRepository.findByAlbumId(2L)).thenReturn(Optional.of(reward));
        when(userRewardRepository.findByUsuarioAndAlbumForUpdate(usuario, album)).thenReturn(Optional.of(new UserReward()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> rewardService.reclamarPremio(1L, 2L));

        assertThat(exception.getMessage()).contains("ya ha reclamado");
        verify(notificationService, never()).actualizar(any());
    }

    @Test
    void reclamarPremio_fallaSiAlbumIncompleto() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));
        when(rewardRepository.findByAlbumId(2L)).thenReturn(Optional.of(reward));
        when(userRewardRepository.findByUsuarioAndAlbumForUpdate(usuario, album)).thenReturn(Optional.empty());
        when(stickerRepository.countByAlbum(album)).thenReturn(5L);
        when(userStickerRepository.countByUserAndAlbum(usuario, album)).thenReturn(3L);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> rewardService.reclamarPremio(1L, 2L));

        assertThat(exception.getMessage()).contains("aún no está completo");
    }
}
