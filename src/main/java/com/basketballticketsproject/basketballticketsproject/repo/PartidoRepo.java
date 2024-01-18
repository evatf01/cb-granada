package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface PartidoRepo extends JpaRepository<Partido, UUID> {

    @Query(value = "SELECT * FROM partido order by fecha_partido ASC", nativeQuery = true)
    Set<Partido> getFechasSortAsc();

    Partido findByFechaPartido(LocalDate fecha);

    @Query(value = "SELECT * FROM partido where ?1 < fecha_partido", nativeQuery = true)
    Set<Partido> findPartidosDesdeFechaActual(LocalDate parsedDate);
}
