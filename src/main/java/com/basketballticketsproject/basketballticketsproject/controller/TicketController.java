package com.basketballticketsproject.basketballticketsproject.controller;

import com.basketballticketsproject.basketballticketsproject.dto.FileInfoDTO;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.service.FileStorageService;
import com.basketballticketsproject.basketballticketsproject.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cbgranada-api/v1")
@Slf4j
@ControllerAdvice
public class TicketController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TicketService ticketService;


    //borrar un usuario dada su id
    @DeleteMapping("/borrarTicket/{id}")
    public void borrarUsuario(@PathVariable Long id) {
        ticketService.borrarTicket(id);
    }


    //metodo para añadir un partido con el formulario del front
    // @PostMapping("/crearPartidoConEntradas")
    // public Long uploadFile(@RequestBody Pdf pdf) throws IOException {
    //     return 0L;//fileStorageService.storeFile(fileStorageService.getFileBase(pdf.getFile1(), pdf.getFile2()), pdf.getTituloPartido(), pdf.getFechaPartido(), pdf.getFechaPublicacion());
    // }


    @GetMapping("/descargarEntrada/{userId}/{partidoId}")
    public ResponseEntity<List<FileInfoDTO>> getTicketPdf(@PathVariable Long userId, @PathVariable Long partidoId) {
        ResponseEntity<List<FileInfoDTO>> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<FileInfoDTO> inputStreamResource= ticketService.getTicketPdf(userId, partidoId);

        if(inputStreamResource != null) {
            response = new ResponseEntity<>(inputStreamResource, HttpStatus.OK);
        }

        return response;
    }

    @GetMapping("/descargarEntradasAdicionales/{userId}/{partidoId}/{numEntradas}")
    public void asignarMasEntradas(@PathVariable Long userId, @PathVariable Long partidoId,
                                                                         @PathVariable int numEntradas) {
        ticketService.asignarMasEntradas(userId, partidoId, numEntradas);

    }



    //enviar entrada al usuario
    // @GetMapping("/enviarEntrada/{userID}/{partidoId}")
    // public ResponseEntity<String> enviarEntrada(@PathVariable Long userID, @PathVariable Long partidoId) throws UnsupportedEncodingException {
    //     final String entrada = ticketService.enviarEntrada(userID, partidoId);
    //     return new ResponseEntity<>(entrada, HttpStatus.OK);
    //    }

    /*
    @GetMapping("/enviarEntrada/{userID}/{partidoId}/{numEntradas}")
    public ResponseEntity<List<String>> enviarEntrada(@PathVariable Long userID, @PathVariable Long partidoId,
                                                      @PathVariable int numEntradas) {
        final List<String> entrada = ticketService.enviarEntrada(userID, partidoId, numEntradas);
        return new ResponseEntity<>(entrada, HttpStatus.OK);
       }


     */

    //metodo para obtener una entrada con su nombre
    // @GetMapping("/getEntrada/{nombreEntrada}")
    // public ResponseEntity<byte[]> getImage(@PathVariable String nombreEntrada) throws UnsupportedEncodingException {
    //     byte[] imageData = fileStorageService.getFileByNumber(nombreEntrada);

    //     return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(imageData);
    // }


    @GetMapping("/entradasSobrantes/{idPartido}")
    public ResponseEntity<Integer> entradasSobrantes(@PathVariable Long idPartido) {
        int numEntradas = ticketService.contadorEntradasRestantes(idPartido);
        return new ResponseEntity<>(numEntradas, HttpStatus.OK);

    }


    @GetMapping("/getEntradasNoAsignadas/{id}")
    public List<Ticket> getEntradasNoAsignadas(@PathVariable Long id) {
        return  ticketService.getEntradasNoAsignadas(id);
    }

    @GetMapping("/getTickets")
    public List<Ticket> getTickets(){
        return ticketService.getTickets();
    }



}