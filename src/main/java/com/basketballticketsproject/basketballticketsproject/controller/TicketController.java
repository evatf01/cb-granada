package com.basketballticketsproject.basketballticketsproject.controller;

import com.basketballticketsproject.basketballticketsproject.dao.Pdf;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.service.FileStorageService;
import com.basketballticketsproject.basketballticketsproject.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

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
    @PostMapping("/crearPartidoConEntradas")
    public Long uploadFile(@RequestBody Pdf pdf) throws IOException {
        return fileStorageService.storeFile(fileStorageService.getFileBase(pdf.getFile1(), pdf.getFile2()), pdf.getTituloPartido(), pdf.getFechaPartido(), pdf.getFechaPublicacion());
    }


    //metodo para añadir un partido, junto con su pdf de entradas DESDE POSTMAN
    @PostMapping("/subirPdf/{nombrePartido}")
    public Long uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String nombrePartido,
                          @RequestParam String fechaPartido,  @RequestParam String fechaPublicacion) throws IOException {
        final File convFile =  new File(Objects.requireNonNull(file.getOriginalFilename()));
        return  fileStorageService.storeFile(convFile, nombrePartido, fechaPartido, fechaPublicacion);

    }

    //enviar entrada al usuario
    @GetMapping("/enviarEntrada/{userID}/{partidoId}/{numEntradas}")
    public ResponseEntity<List<String>> enviarEntrada(@PathVariable Long userID, @PathVariable Long partidoId,
                                                      @PathVariable int numEntradas) {
        final List<String> entrada = ticketService.enviarEntrada(userID, partidoId, numEntradas);
        return new ResponseEntity<>(entrada, HttpStatus.OK);
       }


    //metodo para obtener una entrada con su nombre
    @GetMapping("/getEntrada/{nombreEntrada}")
    public ResponseEntity<byte[]> getImage(@PathVariable String nombreEntrada) {
        byte[] imageData = fileStorageService.getFileByNumber(nombreEntrada);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(imageData);
    }


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