package figuritas.album.userSticker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import figuritas.album.sticker.model.Sticker;
import figuritas.album.userSticker.state.EnColeccion;
import figuritas.album.userSticker.state.EnTrade;
import figuritas.album.userSticker.state.IEstadoUserSticker;
import figuritas.album.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserSticker {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sticker_id", nullable = false)
    private Sticker sticker;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private UserStickerEstado estadoDB;

    @Transient
    private IEstadoUserSticker estado;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PostLoad
    private void inicializarEstado() {
        if (estadoDB == UserStickerEstado.EN_TRADE) {
            this.estado = new EnTrade();
        } else {
            this.estado = new EnColeccion();
        }
    }

    @PrePersist
    private void prePersist() {
        if (this.estadoDB == null) {
            this.cambiarEstado(new EnColeccion());
        }
    }
    
    public void cambiarEstado(IEstadoUserSticker nuevoEstado) {
        this.estado = nuevoEstado;
        this.estadoDB = nuevoEstado.getEstadoEnum();
    }
}
