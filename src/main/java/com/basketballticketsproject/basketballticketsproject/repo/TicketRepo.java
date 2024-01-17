package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TicketRepo extends JpaRepository<Ticket, UUID> {

    Optional<Ticket> findById(UUID id);

    Ticket findByEntrada(String entrada);

    Optional<Set<Ticket>>  findByUsuario(Usuario usuario);

    Set<Ticket> findByPartido(Partido partido);


}
