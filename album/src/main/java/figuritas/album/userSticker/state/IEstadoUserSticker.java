package figuritas.album.userSticker.state;

import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;

public interface IEstadoUserSticker {
    void ponerEnTrade(UserSticker context);

    void ponerEnColeccion(UserSticker context);

    UserStickerEstado getEstadoEnum();
}
