package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

@Service
public class PartidoService {

    @Autowired
    PartidoRepo partidoRepo;

    @Autowired
    TicketRepo ticketRepo;

    @Autowired
    UsuarioRepo usuarioRepo;

    public Partido addPartido(final Partido partido) {
        TemporalAccessor temporal = DateTimeFormatter
                .ofPattern(DATE_FORMATTER)
                .parse(partido.getFechaPartido());
        String fecha = DateTimeFormatter.ofPattern(DATE_FORMATTER).format(temporal);
        partido.setFechaPartido(fecha);
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
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String text = date.format(formatters);
        LocalDate parsedDate = LocalDate.parse(text, formatters);
        Set<Partido> partidosDesdeFechaActual = partidoRepo.findPartidosDesdeFechaActual(parsedDate);
        partidosDesdeFechaActual.forEach(System.out::println);
        return partidosDesdeFechaActual;
    }

    public Set<Partido> getMisPartidos(UUID userID) {
        Optional<Usuario> usuario = usuarioRepo.findById(userID);
        final Set<Partido> partidosUsuario = new HashSet<>();
        if (usuario.isPresent()) {
            Set<Ticket> ticketsUsario = usuario.get().getTickets().stream().filter(Objects::nonNull).collect(Collectors.toSet());
            ticketsUsario.forEach(ticket -> partidosUsuario.add(ticket.getPartido()));
        }
        return partidosUsuario;
    }
}
