package figuritas.album.services;

import figuritas.album.model.Usuario;
import figuritas.album.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    public void crearUsuario(Usuario usuario){
        usuarioRepository.save(usuario);
    }
    public List<Usuario> obtenerUsuarios(){
        return usuarioRepository.findAll();
    }
}
