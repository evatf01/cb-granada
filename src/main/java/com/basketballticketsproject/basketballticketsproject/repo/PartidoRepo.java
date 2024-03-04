package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PartidoRepo extends JpaRepository<Partido, Long> {
 
    Partido findByFechaPartido(LocalDateTime fecha);

    @Query(value = "SELECT * FROM partido where ?1 < fecha_partido && ?1 >= fecha_publicacion ORDER BY fecha_partido", nativeQuery = true)
    Set<Partido> findPartidosDesdeFechaActual(LocalDateTime parsedDate);

    @Query(value = "SELECT partido_id FROM ticket WHERE usuario_id = ?1", nativeQuery = true)
    Set<Long> getMisPartidosIds(Long userId);

    Optional<Partido> findByTickets(Ticket ticket);

    @Query (value="SELECT p.* FROM partido p JOIN ticket t ON p.id = t.partido_id WHERE t.usuario_id = ?1 group by p.id;",
            nativeQuery = true)
    List<Partido> listarPartidosUsuario(Long id);

    @Query(value = "SELECT * FROM partido where ?1 > fecha_partido  ORDER BY fecha_partido", nativeQuery = true)
    Set<Partido> getPartidosAnteriores(LocalDateTime parsedDate);

    @Query(value = "SELECT * FROM partido where ?1 < fecha_partido ORDER BY fecha_partido", nativeQuery = true)
    Set<Partido> findProximosPartidos(LocalDateTime parsedDate);
}
