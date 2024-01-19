package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface PartidoRepo extends JpaRepository<Partido, Long> {

    @Query(value = "SELECT * FROM partido order by fecha_partido ASC", nativeQuery = true)
    Set<Partido> getFechasSortAsc();

    Partido findByFechaPartido(LocalDateTime fecha);

    @Query(value = "SELECT * FROM partido where ?1 < fecha_partido", nativeQuery = true)
    Set<Partido> findPartidosDesdeFechaActual(LocalDateTime parsedDate);


    Optional<Partido> findByTickets(Ticket ticket);
}
