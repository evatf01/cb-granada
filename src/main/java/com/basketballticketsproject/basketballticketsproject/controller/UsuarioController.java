package com.basketballticketsproject.basketballticketsproject.controller;

import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;



@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cbgranada-api/v1")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    //encontrar user por nombre
    @GetMapping("/userName/{name}")
    public ResponseEntity<Usuario> getUserByName(@PathVariable String name) {
        final Usuario user = usuarioService.getUsuarioByName(name);
        if (ObjectUtils.isEmpty(user)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //encontrar user por email
    @GetMapping("/userEmail/{email}")
    public  ResponseEntity<Usuario> getUserByEmail(@PathVariable String email) {
        final Usuario user = usuarioService.getUsuarioByEmail(email);
        if (ObjectUtils.isEmpty(user)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    //añadir un usuario
    @PostMapping("/addUser")
    public ResponseEntity<Usuario> addUsuario(@RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.saveUsuario(usuario), HttpStatus.CREATED);
    }

    //login
    @PostMapping(path = "/login")
    public ResponseEntity<Map<String,String>> loginEmployee(@RequestBody Usuario usuario) {
        final Map<String,String> login = usuarioService.loginEmployee(usuario);
        return ResponseEntity.ok(login);
    }


    //obtener todos los usuarios
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<Usuario>> getAllUsers() {
        final List<Usuario> allUsers = usuarioService.getAllUsers();
        if (allUsers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    //modificar un usuario dada su id
    @PutMapping("/modificarUsuario/{id}")
    public  ResponseEntity<Usuario> modificarUsuario (@PathVariable Long id, @RequestBody Usuario usuarioNuevo) {
        final Usuario user = usuarioService.modificarUsuario(id, usuarioNuevo);
        if (ObjectUtils.isEmpty(user)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    //borrar un usuario dada su id
    @DeleteMapping("/borrarUsuario/{id}")
    public void borrarUsuario(@PathVariable Long id) {
        usuarioService.borrarUsuario(id);
    }


}
