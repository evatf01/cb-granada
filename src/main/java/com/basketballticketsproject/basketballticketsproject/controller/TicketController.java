package com.basketballticketsproject.basketballticketsproject.controller;

import com.basketballticketsproject.basketballticketsproject.dao.Pdf;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.service.FileStorageService;
import com.basketballticketsproject.basketballticketsproject.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cbgranada-api/v1")
@Slf4j
public class TicketController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TicketService ticketService;

    //metodo para añadir un partido con el formulario del front
    @PostMapping("/crearPartidoConEntradas")
    public UUID uploadFile(@RequestBody Pdf pdf) throws IOException, ParseException {
        return fileStorageService.storeFile(fileStorageService.getFileBase(pdf.getFile()), pdf.getTituloPartido(), pdf.getFechaPartido());

    }


    //metodo para añadir un partido, junto con su pdf de entradas DESDE POSTMAN
    @PostMapping("/subirPdf/{nombrePartido}/{fechaPartido}")
    public UUID uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String nombrePartido,
                          @PathVariable String fechaPartido) throws IOException, ParseException {
        final File convFile =  new File(Objects.requireNonNull(file.getOriginalFilename()));
        return  fileStorageService.storeFile(convFile, nombrePartido, fechaPartido);

    }

    //metodo para obtener una entrada con su nombre
    @GetMapping("/getEntrada/{nombreEntrada}")
    public ResponseEntity<byte[]> getImage(@PathVariable String nombreEntrada) throws UnsupportedEncodingException {
        byte[] imageData = fileStorageService.getFileByNumber(nombreEntrada);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(imageData);
    }


    //enviar entrada al usuario
    @GetMapping("/enviarEntrada/{userID}/{partidoId}")
    public ResponseEntity<byte[]> enviarEntrada(@PathVariable UUID userID, @PathVariable UUID partidoId) throws UnsupportedEncodingException {
        final byte[] entrada = ticketService.enviarEntrada(userID, partidoId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(entrada);
    }


/*
    @GetMapping("/entradasSobrantes/{fecha}")
    public ResponseEntity<byte[]> entradasSobrantes(@PathVariable String fecha) throws IOException {
        byte[] bytes = sorteoService.obtenerEntradasSobrantes(fecha);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(bytes);

    }


 */


    @GetMapping("/getEntradasNoAsignadas/{fecha}")
    public List<Ticket> getEntradasNoAsignadas(@PathVariable String fecha) {
        return  ticketService.getEntradasNoAsignadas(fecha);
    }


}