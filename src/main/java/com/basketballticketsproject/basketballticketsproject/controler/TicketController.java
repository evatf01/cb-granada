package com.basketballticketsproject.basketballticketsproject.controler;

import com.basketballticketsproject.basketballticketsproject.dao.Pdf;
import com.basketballticketsproject.basketballticketsproject.entity.Partido;
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
import java.util.Objects;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cbgranada-api/v1")
@Slf4j
public class TicketController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TicketService sorteoService;

    //metodo para añadir un partido con el formulario del front
    @PostMapping("/crearPartidoConEntradas")
    public UUID uploadFile(@RequestBody Pdf pdf) throws IOException {
        return fileStorageService.storeFile(fileStorageService.getFileBase(pdf.getFile()), pdf.getTituloPartido(), pdf.getFechaPartido());

    }


    //metodo para añadir un partido, junto con su pdf de entradas DESDE POSTMAN
    @PostMapping("/subirPdf/{nombrePartido}/{fechaPartido}")
    public UUID uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String nombrePartido,
                          @PathVariable String fechaPartido) throws IOException {
        final File convFile =  new File(Objects.requireNonNull(file.getOriginalFilename()));
        return  fileStorageService.storeFile(convFile, nombrePartido, fechaPartido);

    }

    //metodo para obtener una entrada con su nombre
    @GetMapping("/getEntrada/{nombreEntrada}")
    public ResponseEntity<byte[]> getImage(@PathVariable String nombreEntrada) {
        byte[] imageData = fileStorageService.getFileByNumber(nombreEntrada);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(imageData);
    }


    //enviar entrada al usuario
    @GetMapping("/enviarEntrada/{userID}/{partidoId}")
    public ResponseEntity<byte[]> enviarEntrada(@PathVariable UUID userID, @PathVariable UUID partidoId) {
        final byte[] entrada = sorteoService.enviarEntrada(userID, partidoId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(entrada);
    }


/*
    @GetMapping("/entradasSobrantes/{fecha}")
    public ResponseEntity<byte[]> entradasSobrantes(@PathVariable String fecha) throws IOException {
        byte[] bytes = sorteoService.obtenerEntradasSobrantes(fecha);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(bytes);

    }

 */

}