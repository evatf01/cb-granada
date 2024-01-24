package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

@Service
@Slf4j
public class PartidoService {

    @Autowired
    PartidoRepo partidoRepo;

    @Autowired
    TicketRepo ticketRepo;

    @Autowired
    UsuarioRepo usuarioRepo;

    public Partido addPartido(final Partido partido) {
        return partidoRepo.save(partido);
    }

    public List<Partido> getPartidos() {
        return partidoRepo.findAll();
    }



    public Partido getPartidoById(final Long id) {
        return partidoRepo.findById(id).orElse(null);
    }


    public void removePartido (Long id) {
        final Partido partido = partidoRepo.findById(id).orElseThrow(() ->
                new IllegalStateException("Partido no exite con Id: " + id));
        Optional<Set<Ticket>> ticketsPartido = ticketRepo.findByPartido(partido);

        if (ticketsPartido.isPresent()) {
            for (Ticket entrada : ticketsPartido.get()) {
                ticketRepo.delete(entrada);
            }
            partido.setTickets(null);
        }
        partidoRepo.delete(partido);
    }

    public Set<Partido> getProximosPartdios() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String text = date.format(formatters);
        LocalDateTime parsedDate = LocalDateTime.parse(text, formatters);

        //obtener proximos partidos desde fecha actual de los que hay entradas disponibles
        // return partidoRepo.findPartidosDesdeFechaActual(parsedDate).stream().filter(Partido::isStockEntradas)
        //         .collect(Collectors.toSet());
        return partidoRepo.findPartidosDesdeFechaActual(parsedDate);
    }


    //obtener los partidos de un usuario
    // public Set<Map<String, String>> getMisPartidos(Long userID) {
    //     Set<Map<String, String>> setPartidoMap = new HashSet<>();
    //     Optional<Usuario> usuario = usuarioRepo.findById(userID);
    //    // final Set<Partido> partidosUsuario = new HashSet<>();
    //     Set<Partido> partidos = new HashSet<>();
    //     if (usuario.isPresent()) {
    //         partidos = usuario.get().getTickets().stream().filter(Objects::nonNull).map(Ticket::getPartido).collect(
    //                 Collectors.toSet());
    //         for (Partido partido : partidos) {
    //             Map<String, String> partidoMap = new HashMap<>();
    //             partidoMap.put("partidoId", String.valueOf(partido.getId()));
    //             partidoMap.put("partidoNombre", partido.getNombrePartido());
    //             partidoMap.put("partidoFecha", String.valueOf(partido.getFechaPartido()));
    //             setPartidoMap.add(partidoMap);
    //         }
    //     }
    //     return setPartidoMap;
    // }
    public Set<Long> getMisPartidosIds(Long userId) {
        Set<Long> partidosIds = partidoRepo.getMisPartidosIds(userId);
        return partidosIds;
    }
}
