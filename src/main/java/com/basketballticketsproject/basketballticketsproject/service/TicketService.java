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
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
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


    public List<Ticket> getTickets() {
        return ticketRepo.findAll();
    }

    public Set<Usuario> getUsuariosSorteo(final UUID idPartido) {
        final Optional<Partido> partido = partidoRepo.findById(idPartido);
        Set<Usuario> collect = new HashSet<>();
        if (partido.isPresent()) {
            collect = partido.get().getTickets().stream().map(Ticket::getUsuario).filter(Objects::nonNull).collect(Collectors.toSet());
        }
        return collect;
    }



    public Boolean saveUsuarioSorteo(final UUID idUser, final UUID idPartido) {
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
                throw new ResponseMessage("Ya no quedan entradas disponibles");
            }
        } else {
            throw new ResponseMessage("Ya estas apuntado");
        }
    }

    public void deleteUsuarioFromSorteo(final UUID userID, final UUID partidoId) {
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

    public byte[] enviarEntrada(final UUID userID, final UUID partidoId){

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

    public List<Ticket> getEntradasNoAsignadas(String fecha) {
        final Partido partidoFecha = partidoRepo.findByFechaPartido(fecha);
        return  partidoFecha.getTickets().stream().filter(partido -> !partido.isEntregada()).toList();
    }

    private void setStockEntradasFalse(Partido partido) {
        partido.setSotckEntradas(false);
        partidoRepo.save(partido);
    }
}
