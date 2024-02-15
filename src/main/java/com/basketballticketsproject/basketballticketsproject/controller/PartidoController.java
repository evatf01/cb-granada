package com.basketballticketsproject.basketballticketsproject.controller;


import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.service.FileStorageService;
import com.basketballticketsproject.basketballticketsproject.service.PartidoService;
import com.basketballticketsproject.basketballticketsproject.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cbgranada-api/v1")
@Slf4j
public class PartidoController {

    @Autowired
    PartidoService partidoService;
    @Autowired
    TicketService ticketService;
    @Autowired 
    FileStorageService fileStorageService;


    //añadir un partido sin pdf
    @PostMapping("/addPartido")
    public Partido addPartido(@RequestBody Partido partido) {
        return partidoService.addPartido(partido);
    }

    @PostMapping(value= "/subirPartido", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    public String crearPartido(@RequestPart("partido") String partidoStr, @RequestPart("entradasPdf") MultipartFile entradasPdf) throws IOException{
        Partido partido = null;
        System.out.println(partidoStr);
        System.out.println(entradasPdf.getOriginalFilename());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); //Permite que transforme las fechas
            partido = mapper.readValue(partidoStr, Partido.class);
        } catch (IOException err) {
            System.out.println("======== Error crearPartido =============");
            System.out.println(err.toString());
        }
        if(partido != null) {           
            File entradas =  new File(entradasPdf.getOriginalFilename());
            try (OutputStream os = new FileOutputStream(entradas)) {
                os.write(entradasPdf.getBytes());
            }
            fileStorageService.storeFile(entradas, partido);
            entradas.delete();
            
            return "done";
        } 
        else 
            return "No se pudo leer el partido";      
    }

    //borrar un partido
    @DeleteMapping("/borrarPartido/{id}")
    public void borrarPartidoById(@PathVariable Long id) {
        partidoService.removePartido(id);
    }

    @PutMapping("/modificarPartido/{id}")
    public  ResponseEntity<Partido> modificarPartido (@PathVariable Long id, @RequestBody Partido partidoNuevo) {
        final Partido partido = partidoService.modificarPartido(id, partidoNuevo);
        if (ObjectUtils.isEmpty(partido)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(partido, HttpStatus.OK);
    }



    //obtener todos los partidos
    @GetMapping("/getPartidos")
    public List<Partido> getPartidos(){
        return partidoService.getPartidos();
    }

    //obtener entradas


    //obtener un partido en especifico
    @GetMapping("/getPartido/{partidoId}")
    public ResponseEntity<Partido> getPartidoById(@PathVariable Long partidoId) {
        final Partido partidoById = partidoService.getPartidoById(partidoId);
        if (ObjectUtils.isNotEmpty(partidoById)) {
            return new ResponseEntity<>(partidoById, HttpStatus.OK);
        }
        return new ResponseEntity<>(partidoById,HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getProximosPartidos")
    public  ResponseEntity<Set<Partido>> getProximosPartdios() {
        Set<Partido> partidos = partidoService.getProximosPartdios();
        if (CollectionUtils.isNotEmpty(partidos)) {
            return new ResponseEntity<>(partidos, HttpStatus.OK);
        }
        return new ResponseEntity<>(partidos,HttpStatus.NO_CONTENT);
    }

    //dada la id del partido, obtener los usuarios de ese partido
    @GetMapping("/getUsuariosPartido/{partidoId}")
    public ResponseEntity<Set<Usuario>> getUsuariosSorteo(@PathVariable Long partidoId) {
        final Set<Usuario> usuarios =  ticketService.getUsuariosSorteo(partidoId);
        if (CollectionUtils.isNotEmpty(usuarios)) {
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        }
        return new ResponseEntity<>(usuarios,HttpStatus.NO_CONTENT);
    }

    //obtener el número de usuarios apuntados a un partido
    @GetMapping("/getNumeroUsuariosPartido/{partidoId}")
    public ResponseEntity<Integer> getNumeroUsuariosPartido(@PathVariable Long partidoId) {
        final int numUsuarios =  ticketService.getNumeroUsuariosPartido(partidoId);

        return new ResponseEntity<>(numUsuarios,  HttpStatus.OK);
    }

    //guardar usurio que se apunte al partido, pasandole su id y la id del partido
    @PostMapping("/saveUsuarioPartido/{userID}/{partidoId}")
    public ResponseEntity<Boolean> saveUsuarioSorteo(@PathVariable Long userID, @PathVariable Long partidoId) {
        if (ticketService.saveUsuarioPartido(userID, partidoId)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
    }


    //quitar del partido a la persona que se quiera desinscribirse
    @DeleteMapping("/deleteUsuarioFromPartido/{userID}/{partidoId}")
    public void deleteUsuarioFromSorteo(@PathVariable Long userID, @PathVariable Long partidoId) {
        ticketService.deleteUsuarioFromPartido(userID, partidoId);
    }

    @GetMapping("/getMisPartidosIds/{userId}")
    public  ResponseEntity<Set<Long>> getMisPartidosIds(@PathVariable Long userId) {
        Set<Long> partidosIds = partidoService.getMisPartidosIds(userId);
        if (!partidosIds.isEmpty()) {
            return new ResponseEntity<>(partidosIds, HttpStatus.OK);
        }
        return new ResponseEntity<>(partidosIds,HttpStatus.NO_CONTENT);
    }
}
