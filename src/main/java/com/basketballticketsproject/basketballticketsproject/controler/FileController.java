package com.basketballticketsproject.basketballticketsproject.controler;

import com.basketballticketsproject.basketballticketsproject.dao.Pdf;
import com.basketballticketsproject.basketballticketsproject.service.FileStorageService;
import com.basketballticketsproject.basketballticketsproject.service.SorteoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cbgranada-api/v1")
@Slf4j
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private SorteoService sorteoService;

    //metodo para añadir un partido con el formulario del front
    @PostMapping("/uploadFile")
    public int uploadFile(@RequestBody Pdf pdf) throws IOException {
        return fileStorageService.storeFile(fileStorageService.getFileBase(pdf.getFile()), pdf.getTituloPartido(), pdf.getFechaPartido());

    }


    //metodo para añadir un partido, junto con su pdf de entradas DESDE POSTMAN
    @PostMapping("/subirPdf/{nombrePartido}/{fechaPartido}")
    public int uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String nombrePartido,
                          @PathVariable String fechaPartido) throws IOException {
        final File convFile =  new File(Objects.requireNonNull(file.getOriginalFilename()));
        return  fileStorageService.storeFile(convFile, nombrePartido, fechaPartido);

    }

    //metodo para obtener una entrada con su nombre
    @GetMapping("/getFileByName/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        byte[] imageData = fileStorageService.getFileByNumber(fileName);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(imageData);
    }



    //enviar entrada al usuario
    @GetMapping("/enviarEntrada/{fecha}/{userID}")
    public ResponseEntity<byte[]> enviarEntrada(@PathVariable String fecha, @PathVariable UUID userID) {
        final byte[] entrada = sorteoService.enviarEntrada(fecha, userID);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(entrada);
    }


/*
    @GetMapping("/entradasSobrantes/{fecha}")
    public ResponseEntity<byte[]> entradasSobrantes(@PathVariable String fecha, HttpServletResponse response) throws IOException {
        byte[] bytes = sorteoService.obtenerEntradasSobrantes(fecha);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(bytes);

    }
*/

}