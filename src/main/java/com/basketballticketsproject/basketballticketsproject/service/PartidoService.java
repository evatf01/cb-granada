package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

@Service
@Slf4j
public class PartidoService {

    @Autowired
    PartidoRepo partidoRepo;

    @Autowired
    TicketRepo ticketRepo;
    @Autowired
    UsuarioRepo userRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

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
        Set<Ticket> partidoTickets = partido.getTickets();
        if (ticketsPartido.isPresent()) {
            for (Ticket entrada : ticketsPartido.get()) {
                Usuario user = entrada.getUsuario();
                Partido match = entrada.getPartido();
                if(user != null) {
                    user.getTickets().remove(entrada);
                    userRepo.save(user);
                }
                if(match !=null ){
                    match.getTickets().remove(entrada);
                    partidoRepo.save(match);
                }
                ticketRepo.delete(entrada);
            }
        }
        partido.setTickets(null);
        partidoRepo.delete(partido);
    }

    public Set<Partido> getProximosPartdios() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String text = date.format(formatters);
        LocalDateTime parsedDate = LocalDateTime.parse(text, formatters);

        return partidoRepo.findPartidosDesdeFechaActual(parsedDate);
    }


    public Set<Long> getMisPartidosIds(Long userId) {
        return partidoRepo.getMisPartidosIds(userId);
    }

    public Partido modificarPartido(Long id, Partido partidoNuevo) {
        final Partido updatePartido = partidoRepo.findById(id).orElseThrow(() -> new IllegalStateException("Partido no existe con Id: " + id));
        updatePartido.setEquipoVisitante(partidoNuevo.getEquipoVisitante());
        updatePartido.setFechaPartido(partidoNuevo.getFechaPartido());
        updatePartido.setFechaPublicacion(partidoNuevo.getFechaPublicacion());
        return partidoRepo.save(updatePartido);
    }
}
