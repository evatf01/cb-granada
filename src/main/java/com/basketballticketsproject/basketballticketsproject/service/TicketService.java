package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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


    public Boolean saveUsuarioPartido(final Long idUser, final Long idPartido) {
        final Usuario usuario = usuarioRepo.findById(idUser).orElse(null);
        final Partido partido = partidoRepo.findById(idPartido).orElse(null);
        final Optional<Ticket> oneByUsuarioAndPartido = ticketRepo.findOneByUsuarioAndPartido(usuario, partido);
        if (partido != null  && !ObjectUtils.isEmpty(usuario) && !oneByUsuarioAndPartido.isPresent()) {

            //obtener una entrada que no esté asignada
            final Optional<Ticket> ticketNoEntregado = ticketRepo.findTicketNoEntregado(idPartido);

            if (ticketNoEntregado.isPresent() ) {
                //guardar en la tabla ticket el usuario con la entrada en entregada true
                ticketNoEntregado.get().setUsuario(usuario);
                ticketNoEntregado.get().setEntregada(true);
                usuario.getTickets().add(ticketNoEntregado.get());
                ticketNoEntregado.get().getPartido().setStockEntradas(
                        ticketNoEntregado.get().getPartido().getStockEntradas() - 1
                );
                usuarioRepo.save(usuario);
                ticketRepo.save(ticketNoEntregado.get());

                //comprobamos si nos quedamos sin entradas
                if (ticketNoEntregado.get().getPartido().getStockEntradas() <= 0 ){
                    return false;
                }
                return true;
            } else {
                log.info("Ya no quedan entradas disponibles");
                return false;
            }
        } else {
            log.info("Ya estas apuntado o este partido/usuario no existe");
            return false;
        }
    }

    public void deleteUsuarioFromPartido(final Long userID, final Long partidoId) {

        Ticket ticketUsuario = ticketRepo.findTicketUsuario(userID, partidoId).orElse(null);
            //obtener la entrada del usuario de ese partido
            if (ticketUsuario != null) {
                ticketUsuario.getUsuario().getTickets().remove(ticketUsuario);
                usuarioRepo.save(ticketUsuario.getUsuario());
                //se le desasigna la entrada y se vuelve a poner entragada a false
                ticketUsuario.setUsuario(null);
                ticketUsuario.setEntregada(false);
                //en partido nos aseguramos que indica que sigue habiendo entradas
                ticketUsuario.getPartido().setStockEntradas(ticketUsuario.getPartido().getStockEntradas() + 1);
                ticketRepo.save(ticketUsuario);
                partidoRepo.save(ticketUsuario.getPartido());

            }
    }

    public String enviarEntrada(final Long userID, final Long partidoId){

       final Usuario usuario = usuarioRepo.findById(userID).orElse(null);
       final Partido partido = partidoRepo.findById(partidoId).orElse(null);

        final Set<Usuario> usuariosSorteo = this.getUsuariosSorteo(partidoId);

        String pdfBase64 = StringUtils.EMPTY;
        //comprobar que el usuario está apuntado al partido
        if(usuariosSorteo.contains(usuario) && usuario != null) {
            ///obtener la entrada de ese usario para ese partido
            final Optional<Ticket> entradaUsario = ticketRepo.findOneByUsuarioAndPartido(usuario, partido);
            if (entradaUsario.isPresent()) {
                pdfBase64 = entradaUsario.get().getPdfBase64();
            } else {
                throw new ResponseMessage("No se encuentra la entrada para este usuario y este partido");
            }
        }else {
            throw new ResponseMessage("No estas apuntado a este partido");
        }
        return pdfBase64;
    }

    public List<Ticket> getEntradasNoAsignadas(Long id) {
        final Partido partidoFecha = partidoRepo.findById(id).orElseThrow(() ->
                new IllegalStateException("El partido con este Id no existe: " + id));
        return  partidoFecha.getTickets().stream().filter(partido -> !partido.isEntregada()).toList();
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

    public int contadorEntradasRestantes(Long idPartido) {
        return ticketRepo.findEntradasRestantes(idPartido);
    }

    public int getNumeroUsuariosPartido(Long partidoId) {
        return this.getUsuariosSorteo(partidoId).size();
    }
}
