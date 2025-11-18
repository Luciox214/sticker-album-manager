package figuritas.album.userSticker.state;

import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;

public class EnColeccion implements IEstadoUserSticker {
    @Override
    public void ponerEnTrade(UserSticker context) {
        context.cambiarEstado(new EnTrade());
        System.out.println("Figurita puesta en intercambio");
    }

    @Override
    public void ponerEnColeccion(UserSticker context) {
        System.out.println("La figurita ya está en la colección. No se requiere acción.");
    }

    @Override
    public UserStickerEstado getEstadoEnum() {
        return UserStickerEstado.EN_COLECCION;
    }
}
