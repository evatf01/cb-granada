package com.basketballticketsproject.basketballticketsproject.controler;

import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.service.SorteoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cbgranada-api/v1")
public class  SorteoController {

    @Autowired
    private SorteoService sorteoService;

    //dada la fecha del sorteo, obtener los usuarios de ese partido
    @GetMapping("/getUsuariosSorteo/{partidoId}")
    public ResponseEntity<Set<Usuario>> getUsuariosSorteo(@PathVariable UUID partidoId) {
        final Set<Usuario> usuarios =  sorteoService.getUsuariosSorteo(partidoId);
        if (!usuarios.isEmpty()) {
            return new ResponseEntity<>(usuarios,HttpStatus.OK);
        }
        return new ResponseEntity<>(usuarios,HttpStatus.NO_CONTENT);
    }



    //guardar usurio que se apunte al partido, pasandole su id y la fecha del partido
    @PostMapping("/saveUsuarioSorteo/{userID}/{partidoId}")
    public ResponseEntity<Boolean> saveUsuarioSorteo(@PathVariable UUID userID, @PathVariable UUID partidoId) {

        if (sorteoService.saveUsuarioSorteo(userID, partidoId)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
    }

    //quitar del sorteo a la persona que se quiera desinscribirse
    @DeleteMapping("/deleteUsuarioFromSorteo/{userID}/{partidoId}")
    public void deleteUsuarioFromSorteo(@PathVariable UUID userID, @PathVariable UUID partidoId) {
        sorteoService.deleteUsuarioFromSorteo(userID, partidoId);
    }

    @GetMapping("/getEntradasNoAsignadas/{fecha}")
    public List<Ticket> getEntradasNoAsignadas(@PathVariable String fecha) {
        return  sorteoService.getEntradasNoAsignadas(fecha);
    }

}
