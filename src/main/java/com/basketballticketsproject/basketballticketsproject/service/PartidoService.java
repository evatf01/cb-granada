package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

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



    public Partido getPartidoById(final UUID id) {
        return partidoRepo.findById(id).orElse(null);
    }


    public void removePartido (UUID id) {
        final Partido partido = partidoRepo.findById(id).orElseThrow(() ->
                new IllegalStateException("Employee not exist with id: " + id));
        partidoRepo.delete(partido);
    }

    public List<Ticket> getTickets() {
        return ticketRepo.findAll();
    }

    public Set<Partido> getProximosPartdios() {
        Set<Partido> fechasSortAsc = partidoRepo.getFechasSortAsc();
        fechasSortAsc.forEach(System.out::println);
        return fechasSortAsc;
    }
}
