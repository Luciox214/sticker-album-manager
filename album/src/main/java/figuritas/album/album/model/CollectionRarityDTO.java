package figuritas.album.album.model;

import figuritas.album.sticker.model.Rareza;

/**
 * Snapshot of the user collection grouped by sticker rarity.
 */
public class CollectionRarityDTO {

    private final Rareza rareza;
    private final long total;
    private final long unicas;
    private final long duplicadas;

    public CollectionRarityDTO(Rareza rareza, long total, long unicas, long duplicadas) {
        this.rareza = rareza;
        this.total = total;
        this.unicas = unicas;
        this.duplicadas = duplicadas;
    }

    public Rareza getRareza() {
        return rareza;
    }

    public long getTotal() {
        return total;
    }

    public long getUnicas() {
        return unicas;
    }

    public long getDuplicadas() {
        return duplicadas;
    }
}
