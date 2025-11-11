package figuritas.album.album.model;

/**
 * Represents the completion status of a user's album.
 */
public class CollectionProgressDTO {

    private final Long albumId;
    private final Long usuarioId;
    private final long totalFiguritasAlbum;
    private final long figuritasUnicasUsuario;
    private final double porcentajeCompleto;

    public CollectionProgressDTO(Long albumId,
                                 Long usuarioId,
                                 long totalFiguritasAlbum,
                                 long figuritasUnicasUsuario,
                                 double porcentajeCompleto) {
        this.albumId = albumId;
        this.usuarioId = usuarioId;
        this.totalFiguritasAlbum = totalFiguritasAlbum;
        this.figuritasUnicasUsuario = figuritasUnicasUsuario;
        this.porcentajeCompleto = porcentajeCompleto;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public long getTotalFiguritasAlbum() {
        return totalFiguritasAlbum;
    }

    public long getFiguritasUnicasUsuario() {
        return figuritasUnicasUsuario;
    }

    public double getPorcentajeCompleto() {
        return porcentajeCompleto;
    }
}
