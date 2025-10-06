package figuritas.album.controllers;

import figuritas.album.model.Album;
import figuritas.album.model.Sticker;
import figuritas.album.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/album")
public class AlbumController {
    @Autowired
    private AlbumService albumService;
    @PostMapping
    public Album guardarAlbum(@RequestBody Album album){
        return albumService.crearAlbum(album);
    }
    @PostMapping("/{id}/stickers")
    public Album cargarFiguritas(@PathVariable Long id, @RequestBody List<Sticker> stickers) {
        return albumService.cargarAlbum(id, stickers );
    }
}
