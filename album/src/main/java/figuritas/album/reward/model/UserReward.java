package figuritas.album.reward.model;
import figuritas.album.album.model.Album;
import figuritas.album.reward.state.IEstadoUserReward;
import figuritas.album.reward.state.NoReclamado;
import figuritas.album.reward.state.Reclamado;
import figuritas.album.reward.state.RewardStateEnum;
import figuritas.album.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_rewards")
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private RewardStateEnum estadoDB;

    @Transient
    private IEstadoUserReward estado;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @PostLoad
    private void inicializarEstado() {
        if (estadoDB == RewardStateEnum.RECLAMADO) {
            this.estado = new Reclamado();
        } else {
            this.estado = new NoReclamado();
        }
    }
    public void reclamar() {
        this.estado.reclamar(this);
    }
    public void cambiarEstado(IEstadoUserReward nuevoEstado) {
        this.estado = nuevoEstado;
        this.estadoDB = nuevoEstado.getEstadoEnum();

    }
}