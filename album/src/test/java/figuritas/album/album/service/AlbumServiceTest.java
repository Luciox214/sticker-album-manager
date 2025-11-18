package figuritas.album.album.service;

import figuritas.album.album.model.Album;
import figuritas.album.album.model.CollectionProgressDTO;
import figuritas.album.album.model.CollectionRarityDTO;
import figuritas.album.album.repository.AlbumRepository;
import figuritas.album.sticker.model.Rareza;
import figuritas.album.sticker.model.Sticker;
import figuritas.album.sticker.repository.StickerRepository;
import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;
import figuritas.album.userSticker.repository.UserStickerRepository;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private UserStickerRepository userStickerRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private StickerRepository stickerRepository;

    @InjectMocks
    private AlbumService albumService;

    private Usuario usuario;
    private Album album;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("tester");

        album = new Album();
        album.setId(2L);
        album.setTitulo("Album Test");
        album.setTotalFiguritas(3);
    }

    @Test
    void obtenerProgresoAlbum_retieneDatosCalculados() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));
        when(stickerRepository.countByAlbum(album)).thenReturn(4L);
        when(userStickerRepository.countByUserAndAlbum(usuario, album)).thenReturn(2L);

        CollectionProgressDTO dto = albumService.obtenerProgresoAlbum(1L, 2L);

        assertEquals(2L, dto.getFiguritasUnicasUsuario());
        assertEquals(4L, dto.getTotalFiguritasAlbum());
        assertThat(dto.getPorcentajeCompleto()).isEqualTo(50.0);
    }

    @Test
    void obtenerFiguritasRepetidas_filtraPorAlbumYEstado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));

        when(userStickerRepository.findDuplicateStickerIdsByUsuarioAndAlbumAndEstado(usuario, album, UserStickerEstado.EN_COLECCION))
                .thenReturn(List.of(10L));

        UserSticker repetida = buildUserSticker(10L, Rareza.COMUN);

        when(userStickerRepository.findByUsuarioAndStickerIdsAndEstado(usuario, List.of(10L), UserStickerEstado.EN_COLECCION))
                .thenReturn(List.of(repetida));

        List<UserSticker> resultado = albumService.obtenerFiguritasRepetidas(1L, 2L);

        assertThat(resultado).hasSize(1);
        assertEquals(10L, resultado.getFirst().getSticker().getId());
    }

    @Test
    void obtenerFiguritasFaltantes_devuelveStickersQueNoPoseeElUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));

        Sticker sticker1 = buildSticker(10L, Rareza.COMUN);
        Sticker sticker2 = buildSticker(11L, Rareza.RARA);
        Sticker sticker3 = buildSticker(12L, Rareza.EPICA);

        when(stickerRepository.findByAlbumId(2L)).thenReturn(List.of(sticker1, sticker2, sticker3));
        when(userStickerRepository.findByUsuarioAndAlbum(usuario, album))
                .thenReturn(List.of(buildUserSticker(10L, Rareza.COMUN), buildUserSticker(11L, Rareza.RARA)));

        List<Sticker> faltantes = albumService.obtenerFiguritasFaltantes(1L, 2L);

        assertThat(faltantes).singleElement().extracting(Sticker::getId).isEqualTo(12L);
    }

    @Test
    void obtenerColeccionPorRareza_agruparYOrdenarPorEnum() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));

        List<UserSticker> coleccion = List.of(
                buildUserSticker(10L, Rareza.COMUN),
                buildUserSticker(11L, Rareza.COMUN),
                buildUserSticker(12L, Rareza.RARA)
        );

        when(userStickerRepository.findByUsuarioAndAlbum(usuario, album)).thenReturn(coleccion);

        List<CollectionRarityDTO> resultado = albumService.obtenerColeccionPorRareza(2L, 1L);

        assertThat(resultado).hasSize(2);
        CollectionRarityDTO comunes = resultado.getFirst();
        assertEquals(Rareza.COMUN, comunes.getRareza());
        assertEquals(2L, comunes.getTotal());
        assertEquals(2L, comunes.getUnicas());
        assertEquals(0L, comunes.getDuplicadas());
    }

    private Sticker buildSticker(Long id, Rareza rareza) {
        Sticker sticker = new Sticker();
        sticker.setId(id);
        sticker.setRareza(rareza);
        sticker.setNombre("Sticker " + id);
        sticker.setAlbum(album);
        return sticker;
    }

    private UserSticker buildUserSticker(Long stickerId, Rareza rareza) {
        UserSticker userSticker = new UserSticker();
        userSticker.setUsuario(usuario);
        userSticker.ponerEnColeccion();
        userSticker.setCreatedAt(OffsetDateTime.now());

        Sticker sticker = buildSticker(stickerId, rareza);
        userSticker.setSticker(sticker);
        return userSticker;
    }
}
