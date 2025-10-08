package figuritas.album.sticker.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import figuritas.album.album.model.Album;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Sticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "album_id")
    private Album album;

}
