package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import jakarta.mail.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TicketService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PartidoRepo partidoRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private FileStorageService fileStorageService;


    public List<Ticket> getTickets() {return ticketRepo.findAll();}

    public Set<Usuario> getUsuariosSorteo(final Long idPartido) {
        final Optional<Partido> partido = partidoRepo.findById(idPartido);
        Set<Usuario> collect = new HashSet<>();
        if (partido.isPresent()) {
            collect = partido.get().getTickets().stream().map(Ticket::getUsuario).filter(Objects::nonNull).collect(Collectors.toSet());
        }
        return collect;
    }


    public Boolean saveUsuarioSorteo(final Long idUser, final Long idPartido) {
        final Usuario usuario = usuarioRepo.findById(idUser).orElse(null);
        final Partido partido = partidoRepo.findById(idPartido).orElse(null);
        final Optional<Ticket> oneByUsuarioAndPartido = ticketRepo.findOneByUsuarioAndPartido(usuario, partido);

        Optional<Ticket> ticketToSave;
        if (partido != null  && !ObjectUtils.isEmpty(usuario) && !oneByUsuarioAndPartido.isPresent()) {
            ticketToSave = partido.getTickets().stream().filter(ticket ->
                    !ticket.isEntregada()).findFirst();
            if (ticketToSave.isPresent() ) {
                //guardar en la tabla ticket el usuario con la entrada en entregada true
                ticketToSave.get().setUsuario(usuario);
                ticketToSave.get().setEntregada(true);
                usuario.getTickets().add(ticketToSave.get());

                usuarioRepo.save(usuario);
                ticketRepo.save(ticketToSave.get());

                return true;
            } else {
                this.setStockEntradasFalse(partido);
                log.info("Ya no quedan entradas disponibles");
                return false;
            }
        } else {
            log.info("Ya estas apuntado o este partido/usuario no existe");
            return false;
        }
    }

    public void deleteUsuarioFromSorteo(final Long userID, final Long partidoId) {
        final Optional<Usuario> usuario = usuarioRepo.findById(userID);
        final Optional<Partido> partido = partidoRepo.findById(partidoId);

        if (usuario.isPresent() && partido.isPresent()) {
            //obtener la entrada del usuario para desasignarsela y volverla a poner como entregada a false
            final Optional<Ticket> ticketUsuario = usuario.get().getTickets().stream().filter(ticket ->
                    ticket.getPartido().equals(partido.get())).findFirst();
            if (ticketUsuario.isPresent()) {
                //si el usuario tiene una entrada, se borra y se vuelve a poner entragada a false
                usuario.get().getTickets().remove(ticketUsuario.get());
                ticketUsuario.get().setUsuario(null);
                ticketUsuario.get().setEntregada(false);
                ticketRepo.save(ticketUsuario.get());
            }
            usuarioRepo.save(usuario.get());
        }
    }

    public byte[] enviarEntrada(final Long userID, final Long partidoId){

       final Usuario usuario = usuarioRepo.findById(userID).orElse(null);
       final Partido partido = partidoRepo.findById(partidoId).orElse(null);

        final Set<Usuario> usuariosSorteo = this.getUsuariosSorteo(partidoId);

        byte[] entrada = new byte[0];
        if(usuariosSorteo.contains(usuario) && usuario != null) {
            final Optional<Ticket> entradaUsario = ticketRepo.findOneByUsuarioAndPartido(usuario, partido);
            if (entradaUsario.isPresent()) {
                entrada = FileStorageService.decodeBase64ToPdf(entradaUsario.get().getPdfBase64());
            }
        }else {
            throw new ResponseMessage("No estas apuntado a este partido");
        }
        return entrada;
    }

    public List<Ticket> getEntradasNoAsignadas(Long id) {
        final Partido partidoFecha = partidoRepo.findById(id).orElseThrow(() ->
                new IllegalStateException("El partido con este Id no existe: " + id));
        return  partidoFecha.getTickets().stream().filter(partido -> !partido.isEntregada()).toList();
    }

    private void setStockEntradasFalse(Partido partido) {
        partido.setStockEntradas(false);
        partidoRepo.save(partido);
    }

    public void borrarTicket(Long id) {
        Ticket ticket = ticketRepo.findById(id).orElseThrow(() ->
                new IllegalStateException("El ticket con este Id no existe: " + id));
        Optional<Usuario> usuario = usuarioRepo.findByTickets(ticket);
        Optional<Partido> partido = partidoRepo.findByTickets(ticket);
        if (usuario.isPresent()) {
            usuario.get().getTickets().remove(ticket);
            usuarioRepo.save(usuario.get());
        }
        if (partido.isPresent()) {
            partido.get().getTickets().remove(ticket);
            partidoRepo.save(partido.get());
        }
        ticket.setUsuario(null);
        ticket.setPartido(null);
        ticketRepo.delete(ticket);
    }
}
