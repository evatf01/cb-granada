package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findById(Long id);

    Ticket findByEntrada(String entrada);

    Optional<Set<Ticket>>  findByUsuario(Usuario usuario);

    Optional<Ticket> findOneByUsuarioAndPartido(Usuario user, Partido partido);

    Optional<Set<Ticket>> findByPartido(Partido partido);

    @Query(value = "SELECT * FROM ticket t where t.usuario_id = ?1 and t.partido_id = ?2", nativeQuery = true)
    Set<Ticket> findTicketUsuario(Long idUser, Long idPartido);

    @Query(value = "SELECT * FROM ticket t where t.entregada = false and t.partido_id = ?1 LIMIT 1", nativeQuery = true)
    Optional<Ticket> findTicketNoEntregado(Long id);

    @Query(value = "SELECT count(*) FROM ticket t where t.partido_id = ?1 AND t.entregada = false ", nativeQuery = true)
    int findEntradasRestantes(long idPartido);

    @Query(value = "SELECT * FROM ticket t where t.entregada = false and t.partido_id = ?1 LIMIT ?2", nativeQuery = true)
    Set<Ticket> findMasTicketsNoEntregados(Long partidoId, int numEntradas);
}
