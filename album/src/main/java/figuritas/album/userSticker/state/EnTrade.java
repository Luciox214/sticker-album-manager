package figuritas.album.userSticker.state;

import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;

public class EnTrade implements IEstadoUserSticker {
    @Override
    public void enTrade(UserSticker context) {
        throw new IllegalStateException("La figurita ya está en un intercambio y no se puede poner en otro.");
    }

    @Override
    public void enColeccion(UserSticker context) {
        System.out.println("Figurita retirada del intercambio y devuelta a la colección.");
    }

    @Override
    public UserStickerEstado getEstadoEnum() {
        return UserStickerEstado.EN_TRADE;
    }
}
