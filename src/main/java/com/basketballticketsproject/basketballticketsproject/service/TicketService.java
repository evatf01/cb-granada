package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.dto.FileInfoDTO;
import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TicketService {

    private final Path root = Paths.get("cb-granada");

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PartidoRepo partidoRepo;

    @Autowired
    private TicketRepo ticketRepo;


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
                this.asignarEntrada(ticketNoEntregado.get(), usuario);
                //comprobamos si nos quedamos sin entradas
                if (ticketNoEntregado.get().getPartido().getStockEntradas() <= 0 ){
                    return true;
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

    private void asignarEntrada(Ticket ticketNoEntregado, Usuario usuario) {
        ticketNoEntregado.setUsuario(usuario);
        ticketNoEntregado.setEntregada(true);
        usuario.getTickets().add(ticketNoEntregado);
        usuario.setPartidosAsistidos(usuario.getPartidosAsistidos() + 1);
        if (ticketNoEntregado.getPartido().getStockEntradas() > 0){
            ticketNoEntregado.getPartido().setStockEntradas(
                    ticketNoEntregado.getPartido().getStockEntradas() - 1);
        }
        usuarioRepo.save(usuario);
        ticketRepo.save(ticketNoEntregado);
    }

    public void deleteUsuarioFromPartido(final Long userID, final Long partidoId) {
        Set<Ticket> ticketsUsuario = ticketRepo.findTicketUsuario(userID, partidoId);
            if (!CollectionUtils.isEmpty(ticketsUsuario)) {
                for (Ticket ticket : ticketsUsuario){
                    if (ticket.getUsuario().getPartidosAsistidos() > 0 ){
                        ticket.getUsuario().setPartidosAsistidos(ticket.getUsuario().getPartidosAsistidos() -1 );
                    }
                    ticket.getUsuario().getTickets().remove(ticket);
                    usuarioRepo.save(ticket.getUsuario());
                    ticket.setUsuario(null);
                    ticket.setEntregada(false);
                    //en partido nos aseguramos que indica que sigue habiendo entradas
                    ticket.getPartido().setStockEntradas(ticket.getPartido().getStockEntradas() + 1);
                    ticketRepo.save(ticket);
                    partidoRepo.save(ticket.getPartido());
                }
            }
    }

/*
    public List<String> enviarEntrada(final Long userID, final Long partidoId, final int numEntradas) {
        List<String> listEntradas = new ArrayList<>();
        //cuando el usuario pida más entradas, se le pasa por parámetro el número de entradas que pide
        if (numEntradas != 0) {
            Set<Ticket> ticketUsuario = ticketRepo.findMasTicketsNoEntregados(partidoId, numEntradas);
            for (Ticket ticket : ticketUsuario) {
                listEntradas.add(ticket.getPdfBase64());
            }
            return listEntradas;
        }

        final Usuario usuario = usuarioRepo.findById(userID).orElse(null);
        final Partido partido = partidoRepo.findById(partidoId).orElse(null);

        final Set<Usuario> usuariosSorteo = this.getUsuariosSorteo(partidoId);
        //comprobar que el usuario está apuntado al partido
        if (usuariosSorteo.contains(usuario) && usuario != null) {
            ///obtener la entrada de ese usario para ese partido
            final Optional<Ticket> entradaUsario = ticketRepo.findOneByUsuarioAndPartido(usuario, partido);
            entradaUsario.ifPresent(ticket -> listEntradas.add(ticket.getPdfBase64()));
        } else {
            throw new ResponseMessage("No estas apuntado a este partido");
        }
        return listEntradas;
    }
*/



    /*
    public String getTicketPath(Long userId, Long partidoId) {
        return ticketRepo.findTicketPath(userId, partidoId);
    }

     */

    public List<FileInfoDTO> getTicketPdf(Long userId, Long partidoId) {
        List<FileInfoDTO> fileInfoList = new ArrayList<>();
        try{
            List<String> path = ticketRepo.findTicketPath(userId, partidoId);
            for(String s : path) {
                FileInfoDTO fileInfo = new FileInfoDTO();
                FileInputStream fileInputStream = new FileInputStream(s);
                InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);
                fileInfo.setData(inputStreamResource.getInputStream().readAllBytes());
                fileInfoList.add(fileInfo);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return fileInfoList;
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


    public  List<FileInfoDTO> getTicketsAdicionalesPdf(Long userId, Long partidoId, int numEntradas)  {
        List<FileInfoDTO> fileInfoList = new ArrayList<>();
       try{
            List<String> path = ticketRepo.findTicketPath(userId, partidoId);
            for(String s : path) {
                FileInfoDTO fileInfo = new FileInfoDTO();
                FileInputStream fileInputStream = new FileInputStream(s);
                InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);
                fileInfo.setData(inputStreamResource.getInputStream().readAllBytes());
                fileInfoList.add(fileInfo);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return fileInfoList;
    }


    public boolean asignarMasEntradas(Long userId, Long partidoId, int numEntradas) {
        final Usuario usuario = usuarioRepo.findById(userId).orElse(null);
        final Set<Ticket> ticketsNoEntregados = ticketRepo.findMasTicketsNoEntregados(partidoId, numEntradas);

        if (!CollectionUtils.isEmpty(ticketsNoEntregados)) {
            for (Ticket ticket : ticketsNoEntregados) {
                log.info("helooooooo");
                this.asignarEntrada(ticket, usuario);
            }
            return true;
        } else {
            return false;
        }
    }

}
