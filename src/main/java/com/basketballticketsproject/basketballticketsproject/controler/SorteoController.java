package com.basketballticketsproject.basketballticketsproject.controler;

import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.service.SorteoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cbgranada-api/v1")
public class SorteoController {

    @Autowired
    private SorteoService sorteoService;

    @GetMapping("/getUsuariosSorteo/{fecha}")
    public List<Usuario> getUsuariosSorteo(@PathVariable String fecha) {
        return sorteoService.getUsuariosSorteo(fecha);
    }
}
