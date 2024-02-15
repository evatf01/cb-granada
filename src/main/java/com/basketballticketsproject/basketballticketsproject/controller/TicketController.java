package com.basketballticketsproject.basketballticketsproject.controller;

import com.aspose.pdf.internal.imaging.internal.Exceptions.IO.FileNotFoundException;
import com.basketballticketsproject.basketballticketsproject.dao.Pdf;
import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.service.FileStorageService;
import com.basketballticketsproject.basketballticketsproject.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
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
    // @PostMapping("/crearPartidoConEntradas")
    // public Long uploadFile(@RequestBody Pdf pdf) throws IOException {
    //     return 0L;//fileStorageService.storeFile(fileStorageService.getFileBase(pdf.getFile1(), pdf.getFile2()), pdf.getTituloPartido(), pdf.getFechaPartido(), pdf.getFechaPublicacion());
    // }
    
    

    @GetMapping("/descargarEntrada/{userId}/{partidoId}")
    public ResponseEntity<InputStreamResource> getTicketPdf(@PathVariable Long userId, @PathVariable Long partidoId) {    
        ResponseEntity<InputStreamResource> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        InputStreamResource inputStreamResource = ticketService.getTicketPdf(userId, partidoId);       
       
        if(inputStreamResource != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF); 
            response = new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
        } 

        return response;
    }

    //enviar entrada al usuario
    // @GetMapping("/enviarEntrada/{userID}/{partidoId}")
    // public ResponseEntity<String> enviarEntrada(@PathVariable Long userID, @PathVariable Long partidoId) throws UnsupportedEncodingException {
    //     final String entrada = ticketService.enviarEntrada(userID, partidoId);
    //     return new ResponseEntity<>(entrada, HttpStatus.OK);
    //    }


    //metodo para obtener una entrada con su nombre
    // @GetMapping("/getEntrada/{nombreEntrada}")
    // public ResponseEntity<byte[]> getImage(@PathVariable String nombreEntrada) throws UnsupportedEncodingException {
    //     byte[] imageData = fileStorageService.getFileByNumber(nombreEntrada);

    //     return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(imageData);
    // }


/*
    @GetMapping("/entradasSobrantes/{fecha}")
    public ResponseEntity<byte[]> entradasSobrantes(@PathVariable String fecha) throws IOException {
        byte[] bytes = sorteoService.obtenerEntradasSobrantes(fecha);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(bytes);

    }


 */


    @GetMapping("/getEntradasNoAsignadas/{fecha}")
    public List<Ticket> getEntradasNoAsignadas(@PathVariable Long id) {
        return  ticketService.getEntradasNoAsignadas(id);
    }

    @GetMapping("/getTickets")
    public List<Ticket> getTickets(){
        return ticketService.getTickets();
    }


}