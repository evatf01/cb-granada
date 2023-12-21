package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Sorteo;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.SorteoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import com.basketballticketsproject.basketballticketsproject.utils.EnviarEmailUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

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
                //borrar entrada del usuario
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
        final byte[] entrada;

        //para comprobar si el usuario está inscrito al sorteo
        if(usuariosSorteo.contains(usuario) && usuario != null) {
            final Partido partido = partidoRepo.findByFechaPartido(fecha);

            //comprobar si ese usuario ya tiene una entrada de ese partido
            final Optional<Ticket> entradaUsuario = usuario.getTickets().stream().filter(ticket ->
                    ticket.getFecha().equals(fecha)).findFirst();

            //filtro para obtener las entradas que no estan entregadas y cojo la primera
            final Optional<Ticket> ticketToSend = partido.getTickets().stream().filter(ticket -> !ticket.isEntregada())
                    .findFirst();
            if (ticketToSend.isPresent() && !entradaUsuario.isPresent()) {
                //descodificar base64 en pdf
                ticketToSend.get().setEntregada(true);
                ticketToSend.get().setFecha(fecha);
                System.out.println("ENTRADA "+ ticketToSend.get().getEntrada());
                entrada = EnviarEmailUsuarios.decodeBase64ToPdf(ticketToSend.get());

                ticketToSend.get().setUsuario(usuario);
                usuario.getTickets().add(ticketToSend.get());

                ticketRepo.save(ticketToSend.get());
                usuarioRepo.save(usuario);
            } else {
                throw new ResponseMessage("Ya tienes una entrada de este partido");
            }
        }
        else {
            throw new ResponseMessage("No estas apuntado a este partido");
        }
        return entrada;
    }

    /*
    public List<byte[]> obtenerEntradasSobrantes(String fecha) {
        Partido partidoFecha = partidoRepo.findByFechaPartido(fecha);
        Set<Ticket> ticketStream = partidoFecha.getTickets().stream().filter(partido -> !partido.isEntregada())
                .collect(Collectors.toSet());

        List<byte[]> listaEntradas = new ArrayList<>();
        
        ticketStream.forEach(ticket -> {
            byte[] bytes = EnviarEmailUsuarios.decodeBase64ToPdf(ticket);
            listaEntradas.add(bytes);
        });
        return listaEntradas;
    }

     */

}
