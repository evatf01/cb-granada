package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Sorteo;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.SorteoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.*;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.NUM_ENTRADAS;

@Service
public class SorteoService {

    @Autowired
    private SorteoRepo sorteoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PartidoRepo partidoRepo;

    @Autowired
    private TicketRepo ticketRepo;

    public Set<Usuario> getUsuariosSorteo(final String fecha) {
        Sorteo sorteo = sorteoRepo.findByFecha(fecha);
        return sorteo.getUsuarios();
    }


    public List<Sorteo> getSorteos() {
        return sorteoRepo.findAll();
    }

    public Sorteo saveUsuarioSorteo(final UUID idUser, final String fecha) {
        final Usuario usuario = usuarioRepo.findById(idUser).orElse(null);
        final Sorteo sorteoFecha = sorteoRepo.findByFecha(fecha);
        if (sorteoFecha.getUsuarios().size() <= NUM_ENTRADAS && !ObjectUtils.isEmpty(sorteoFecha)) {
            if (!ObjectUtils.isEmpty(usuario) && !sorteoFecha.getUsuarios().contains(usuario)) {
                sorteoFecha.getUsuarios().add(usuario);
                //usuario.getSorteo().add(sorteo);
                sorteoRepo.save(sorteoFecha);
            } else {
                throw new ResponseMessage("Ya estas apuntado");
            }
        }
        return sorteoFecha;
    }

    public void deleteUsuarioFromSorteo(final UUID userID, final String fecha) {
        final Usuario usuario = usuarioRepo.findById(userID).orElse(null);

        final Sorteo sorteoFecha = sorteoRepo.findByFecha(fecha);
        if (!ObjectUtils.isEmpty(usuario)) {
            //Se borra al usuario del sorteo
            sorteoFecha.getUsuarios().remove(usuario);
            usuario.getSorteos().remove(sorteoFecha);

            final Optional<Ticket> ticketUsuario = usuario.getTickets().stream().filter(ticket ->
                    ticket.getFecha().equals(fecha)).findFirst();

            if (ticketUsuario.isPresent()) {
                //si el usuario tiene una entrada, se borra y se vuelve a poner entragada a false
                usuario.getTickets().remove(ticketUsuario.get());
                ticketUsuario.get().setUsuario(null);
                ticketUsuario.get().setEntregada(false);
                ticketRepo.save(ticketUsuario.get());
            }
            usuarioRepo.save(usuario);
            sorteoRepo.save(sorteoFecha);
        }
    }

    public byte[] enviarEntrada(final String fecha, final UUID user) {
        final Usuario usuario = usuarioRepo.findById(user).orElse(null);
        final Set<Usuario> usuariosSorteo = this.getUsuariosSorteo(fecha);
        File file;

        //para comprobar si el usuario está inscrito al sorteo
        byte[] entrada;
        if(usuariosSorteo.contains(usuario) && usuario != null) {
            final Partido partido = partidoRepo.findByFechaPartido(fecha);

            //comprobar si ese usuario ya tiene una entrada de ese partido
            final Optional<Ticket> entradaUsuario = usuario.getTickets().stream().filter(ticket ->
                    ticket.getFecha().equals(fecha)).findFirst();

            //filtro para obtener las entradas que no estan entregadas y cojo la primera
            final Optional<Ticket> ticketToSend = partido.getTickets().stream().filter(ticket -> !ticket.isEntregada())
                    .findFirst();
            if (ticketToSend.isPresent() && !entradaUsuario.isPresent()) {
                saveTicketAndUser(ticketToSend.get(), fecha, usuario);
                //descodificar base64 
                entrada = FileStorageService.decodeBase64ToPdf(ticketToSend.get());
            } else {
                throw new ResponseMessage("Ya tienes una entrada de este partido");
            }
        }
        else {
            throw new ResponseMessage("No estas apuntado a este partido");
        }
        return entrada;
    }

    private void saveTicketAndUser(Ticket ticket, String fecha, Usuario usuario) {
        ticket.setEntregada(true);
        ticket.setFecha(fecha);
        ticket.setUsuario(usuario);
        usuario.getTickets().add(ticket);

        ticketRepo.save(ticket);
        usuarioRepo.save(usuario);
    }

/*
    public byte[] obtenerEntradasSobrantes(String fecha){

        Partido partidoFecha = partidoRepo.findByFechaPartido(fecha);
        List<Ticket> ticketStream = partidoFecha.getTickets().stream().filter(partido -> !partido.isEntregada()).toList();

        byte[] bytesPdf = new byte[0];
        for(Ticket ticket : ticketStream) {
            bytesPdf = FileStorageService.decodeBase64ToPdf(ticket);
        }

        return Base64.getUrlDecoder().decode(bytesPdf);
    }

 */

    public List<Ticket> getEntradasNoAsignadas(String fecha) {
        Partido partidoFecha = partidoRepo.findByFechaPartido(fecha);
        return  partidoFecha.getTickets().stream().filter(partido -> !partido.isEntregada()).toList();
    }
}
