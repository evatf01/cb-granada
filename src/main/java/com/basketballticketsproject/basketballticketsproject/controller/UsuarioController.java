package com.basketballticketsproject.basketballticketsproject.controller;

import com.basketballticketsproject.basketballticketsproject.dto.LoginUserDTO;
import com.basketballticketsproject.basketballticketsproject.dto.PartidoResponseDTO;
import com.basketballticketsproject.basketballticketsproject.entity.TokenResponse;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.service.JwtService;
import com.basketballticketsproject.basketballticketsproject.service.UsuarioService;
import com.basketballticketsproject.basketballticketsproject.utils.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.*;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cbgranada-api/v1")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    @Autowired
    private JavaMailSender mailSender;

    //encontrar user por nombre
    @GetMapping("/userName/{name}")
    public ResponseEntity<Usuario> getUserByName(@PathVariable String name) {
        final Usuario user = usuarioService.getUsuarioByName(name);
        if (ObjectUtils.isEmpty(user)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/userById/{id}")
    public ResponseEntity<Usuario> getUserById(@PathVariable Long id) {
        final Usuario user = usuarioService.getUsuarioById(id);
        if (ObjectUtils.isEmpty(user)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //encontrar user por email
    @GetMapping("/userEmail/{email}")
    public  ResponseEntity<Usuario> getUserByEmail(@PathVariable String email) {
        final Optional<Usuario> user = usuarioService.getUsuarioByEmail(email);
        return user.map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    //añadir un usuario
    @PostMapping("/addUser")
    public ResponseEntity<Usuario> addUsuario(@RequestBody Usuario usuario) {

        enviarConfirmacionEmail(usuario);

        return new ResponseEntity<>(usuarioService.saveUsuario(usuario), HttpStatus.CREATED);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody Usuario usuario) {
        final TokenResponse login = usuarioService.loginUser(usuario);
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

    //obtener un grupo de usuarios
    @GetMapping("/getAllUsersLimit")
    public ResponseEntity<List<Usuario>> getAllUsersLimit() {
        final List<Usuario> allUsers = usuarioService.getAllUsersLimit();
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

    @GetMapping("/checkPasswords/{usuario}/{password}")
    public ResponseEntity<Boolean> checkPasswords(@PathVariable Usuario usuario, @PathVariable String password) {
        final boolean check = usuarioService.checkPassword(usuario, password);
        if (check) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
    }

    //obtener el número de partidos que ha ido el usuario
    @GetMapping("/getHistorialPartidosUsuario")
    public ResponseEntity< List<LoginUserDTO>> getHistorialPartidosUsuarioNumerico() {
        final  List<LoginUserDTO> list = usuarioService.getHistorialPartidosUsuarioNumerico();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //obtener los partidos que ha ido el usuario
    @GetMapping("/listarPartidosUsuario/{userId}")
    public  ResponseEntity<List<PartidoResponseDTO>> listarPartidosUsuario(@PathVariable Long userId) {
        List<PartidoResponseDTO> partidosIds = usuarioService.listarPartidosUsuario(userId);
        if (!partidosIds.isEmpty()) {
            return new ResponseEntity<>(partidosIds, HttpStatus.OK);
        }
        return new ResponseEntity<>(partidosIds,HttpStatus.NO_CONTENT);
    }

    // Validacion del correo electronico del usuario
    @GetMapping("/confirmacionEmail/{email}")
    public ResponseEntity<Boolean> confirmacionEmail(@PathVariable String email){
        final boolean check = usuarioService.validarEmail(email);
        if (check) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
    }

    public void enviarConfirmacionEmail(Usuario usuario) {

        SimpleMailMessage email = new SimpleMailMessage();

        email.setFrom("noreply@t-systems.com");
        email.setTo(usuario.getEmail());
        email.setSubject(ASUNTO_VALIDACION);
        email.setText(EMAIL_MENSAJE_VALIDACION + ENLACE_VALIDACION+usuario.getEmail());
        mailSender.send(email);

    }
}
