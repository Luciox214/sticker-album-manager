package figuritas.album.controllers;

import figuritas.album.model.Usuario;
import figuritas.album.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/usuario")
public class UsuarioController {
    @Autowired
    UsuarioService usuarioService;
    @PostMapping
    public void crearUsuario(@RequestBody Usuario usuario){
        usuarioService.crearUsuario(usuario);
    }
    @GetMapping
    public List<Usuario> obtenerUsuarios(){
        return usuarioService.obtenerUsuarios();
    }
}
