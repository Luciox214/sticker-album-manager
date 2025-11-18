package figuritas.album.userSticker.state;

import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;

public class EnColeccion implements IEstadoUserSticker {
    @Override
    public void enTrade(UserSticker context) {
        System.out.println("Figurita puesta en intercambio");
    }

    @Override
    public void enColeccion(UserSticker context) {
        throw new IllegalStateException("La figurita ya está en la colección.");
    }

    @Override
    public UserStickerEstado getEstadoEnum() {
        return UserStickerEstado.EN_COLECCION;
    }
}
