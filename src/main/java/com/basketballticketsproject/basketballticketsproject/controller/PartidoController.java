package com.basketballticketsproject.basketballticketsproject.controller;


import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.service.PartidoService;
import com.basketballticketsproject.basketballticketsproject.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cbgranada-api/v1")
@Slf4j
public class PartidoController {

    @Autowired
    PartidoService partidoService;

    @Autowired
    private TicketService ticketService;


    //añadir un partido sin pdf
    @PostMapping("/addPartido")
    public Partido addPartido(@RequestBody Partido partido) {
        return partidoService.addPartido(partido);
    }

    //borrar un partido
    @DeleteMapping("/borrarPartido/{id}")
    public void borrarPartidoById(@PathVariable Long id) {
        partidoService.removePartido(id);
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

    //guardar usurio que se apunte al partido, pasandole su id y la id del partido
    @PostMapping("/saveUsuarioPartido/{userID}/{partidoId}")
    public ResponseEntity<Boolean> saveUsuarioSorteo(@PathVariable Long userID, @PathVariable Long partidoId) {
        if (ticketService.saveUsuarioSorteo(userID, partidoId)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
    }


    //quitar del partido a la persona que se quiera desinscribirse
    @DeleteMapping("/deleteUsuarioFromPartido/{userID}/{partidoId}")
    public void deleteUsuarioFromSorteo(@PathVariable Long userID, @PathVariable Long partidoId) {
        ticketService.deleteUsuarioFromSorteo(userID, partidoId);
    }


    @GetMapping("/getMisPartidos/{userId}")
    public  ResponseEntity<Set<Map<String, String>>> getMisPartidos(@PathVariable Long userId) {
        Set<Map<String, String>> partidos = partidoService.getMisPartidos(userId);
        if (CollectionUtils.isNotEmpty(partidos)) {
            return new ResponseEntity<>(partidos, HttpStatus.OK);
        }
        return new ResponseEntity<>(partidos,HttpStatus.NO_CONTENT);
    }

}
