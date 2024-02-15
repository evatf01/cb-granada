package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findById(Long id);

    // Ticket findByEntrada(String entrada);

    Optional<Set<Ticket>>  findByUsuario(Usuario usuario);

    Optional<Ticket> findOneByUsuarioAndPartido(Usuario user, Partido partido);

    @Query(value = "SELECT path FROM ticket t where t.usuario_id = ?1 AND t.partido_id = ?2", nativeQuery = true)
    String findTicketPath(Long userId, Long partidoId);

    Optional<Set<Ticket>> findByPartido(Partido partido);

    @Query(value = "SELECT * FROM ticket t where t.entregada = false and t.partido_id = ?1 LIMIT 1", nativeQuery = true)
    Optional<Ticket> findTicketNoEntregado(Long id);
}
