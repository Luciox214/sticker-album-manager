package figuritas.album.userSticker.state;

import figuritas.album.userSticker.model.UserSticker;
import figuritas.album.userSticker.model.UserStickerEstado;

public interface IEstadoUserSticker {
    void enTrade(UserSticker context);

    void enColeccion(UserSticker context);

    UserStickerEstado getEstadoEnum();
}
