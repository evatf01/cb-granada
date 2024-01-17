package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PartidoService {

    @Autowired
    PartidoRepo partidoRepo;

    @Autowired
    TicketRepo ticketRepo;

    public Partido addPartido(final Partido partido) {
        return partidoRepo.save(partido);
    }

    public List<Partido> getPartdios() {
        return partidoRepo.findAll();
    }



    public Optional<Partido> getPartidoById(final UUID id) {
        return partidoRepo.findById(id);
    }


    public void removePartido (UUID id) {
        final Partido partido = partidoRepo.findById(id).orElseThrow(() ->
                new IllegalStateException("Employee not exist with id: " + id));
        partidoRepo.delete(partido);
    }

    public List<Ticket> getTickets() {
        return ticketRepo.findAll();
    }
}
