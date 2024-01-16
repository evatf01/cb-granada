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
        Optional<Ticket> ticketToSave = Optional.empty();
        if (partido != null) {
            ticketToSave = partido.getTickets().stream().filter(ticket ->
                    !ticket.isEntregada()).findFirst();
        }
        //busco en la tabla tickets el partido con entregada a false y cojo la primera

        if (ticketToSave.isPresent() && !ObjectUtils.isEmpty(usuario) ) {
            System.out.println("holaaaaa");
            //guardar en la tabla ticket el usuario con la entrada en entregada true
            ticketToSave.get().setUsuario(usuario);
            ticketToSave.get().setEntregada(true);
            usuario.getTickets().add(ticketToSave.get());
            usuarioRepo.save(usuario);
            ticketRepo.save(ticketToSave.get());

            return true;
        } else {
            throw new ResponseMessage("Ya no quedan entradas disponibles");
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

    public byte[] enviarEntrada(final UUID userID, final UUID partidoId) {

       final Usuario usuario = usuarioRepo.findById(userID).orElse(null);
       final Partido partido = partidoRepo.findById(partidoId).orElse(null);


        final Set<Usuario> usuariosSorteo = this.getUsuariosSorteo(partidoId);

        byte[] entrada = new byte[0];

        //para comprobar si el usuario está inscrito al sorteo
        if(usuariosSorteo.contains(usuario) && usuario != null) {

        //obtener la entrada del usuario de ese partido
            final Optional<Ticket> ticketUsuario = usuario.getTickets().stream().filter(ticket ->
                    ticket.getPartido().equals(partido)).findFirst();

            if (ticketUsuario.isPresent()) {
                System.out.println("HOLAAA "+ ticketUsuario.get().getPdfBase64());
                //descodificar base64 en la entrada
                entrada = FileStorageService.decodeBase64ToPdf(ticketUsuario.get());
            }
        }
        else {
            throw new ResponseMessage("No estas apuntado a este partido");
        }

        return entrada;
    }


    /*
    public byte[] obtenerEntradasSobrantes(String fecha){

        final Partido partidoFecha = partidoRepo.findByFechaPartido(fecha);
        final List<Ticket> ticketStream = partidoFecha.getTickets().stream().filter(partido -> !partido.isEntregada()).toList();

        List<byte[]> bytesEntrada = new ArrayList<>();
        List<String> entradas = new ArrayList<>();
        byte[] bytes = new byte[0];
        System.out.println("num entradas: " + ticketStream.size());
        for(Ticket ticket : ticketStream) {
            //bytesPdf =  Base64.getDecoder().decode(ticket.getPdfBase64().getBytes(StandardCharsets.UTF_8));
            bytesEntrada.add(Base64.getDecoder().decode(ticket.getPdfBase64().getBytes()));
            entradas.add(ticket.getPdfBase64());
        }
        String entradasString = StringUtils.join(entradas, "\n");
        bytes =  Base64.getDecoder().decode(entradasString.getBytes(StandardCharsets.UTF_8));
        return bytes;
    }

     */

    public List<Ticket> getEntradasNoAsignadas(String fecha) {
        final Partido partidoFecha = partidoRepo.findByFechaPartido(fecha);
        return  partidoFecha.getTickets().stream().filter(partido -> !partido.isEntregada()).toList();
    }
}
