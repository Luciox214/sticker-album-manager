package figuritas.album.reward.model;
public record RewardDTO(
        Long id,
        Long albumId,
        String albumTitulo,
        String tipo
) {
}
