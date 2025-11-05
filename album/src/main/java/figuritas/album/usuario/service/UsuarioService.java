package figuritas.album.usuario.service;
import figuritas.album.usuario.UsuarioDTO;
import figuritas.album.usuario.model.Usuario;
import figuritas.album.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    public void crearUsuario(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public List<UsuarioDTO> obtenerUsuarios(){
        return usuarioRepository.findAll()
                .stream()
                .map(
                        usuario -> new UsuarioDTO(
                                usuario.getId(),
                                usuario.getUsername(),
                                usuario.getNombre(),
                                usuario.getEmail()
                        )
                ).collect(Collectors.toList());
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

}