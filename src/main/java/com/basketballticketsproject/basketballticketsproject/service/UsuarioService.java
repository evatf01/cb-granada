package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.dao.LoginUser;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.PASSWORD_REGEX;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public Usuario getUsuarioByName(String name) {
        return usuarioRepo.findByNombre(name);
    }

    public Optional<Usuario> getUsuarioByEmail(String email) {
        return usuarioRepo.findByEmail(email);
    }

    public Usuario saveUsuario(final Usuario usuario) {
        if(StringUtils.isEmpty(usuario.getNombre()) || StringUtils.isEmpty(usuario.getApellidos()) || StringUtils.isEmpty(
                usuario.getEmail()) || StringUtils.isEmpty(usuario.getPassword())) {
            throw new ResponseMessage("Inserte todos los datos para el registro");
        }
        if (PASSWORD_PATTERN.matcher(usuario.getPassword()).matches()) {
            usuario.setPassword(this.passwordEncoder.encode(usuario.getPassword()));
        } else {
            throw new ResponseMessage("Contraseña no válida. La contraseña tiene que tener: un numero, sin espacios, minuscula, mayuscula, un caracter epecial, y minimo 8 caracteres");
        }
        return usuarioRepo.save(usuario);
    }

    public List<Usuario> getAllUsers(){
        return usuarioRepo.findAll();
    }

    public List<Usuario> getAllUsersLimit(){
        return usuarioRepo.findAllUsersLimit();
    }

    public Usuario modificarUsuario(final Long id, final Usuario usuarioNuevo) {
        final Usuario updateUser = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuario no existe con Id: " + id));
        updateUser.setEmail(usuarioNuevo.getEmail());
        updateUser.setNombre(usuarioNuevo.getNombre());
        updateUser.setApellidos(usuarioNuevo.getApellidos());
        if (usuarioNuevo.getPassword() != null && PASSWORD_PATTERN.matcher(usuarioNuevo.getPassword()).matches()) {
            updateUser.setPassword(this.passwordEncoder.encode(usuarioNuevo.getPassword()));
        }
        return usuarioRepo.save(updateUser);
    }


    public void borrarUsuario(Long id) {
        Usuario deleteUser = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuario no existe con Id: " + id));

        Optional<Set<Ticket>> ticketSet = ticketRepo.findByUsuario(deleteUser);
        if (ticketSet.isPresent()) {
            for (Ticket entrada : ticketSet.get()) {
                entrada.setUsuario(null);
                entrada.setEntregada(false);
                ticketRepo.save(entrada);
            }
            deleteUser.setTickets(null);
        }
        usuarioRepo.delete(deleteUser);
    }

    public LoginUser loginUser(Usuario loginUser) {
        Optional<Usuario> user = usuarioRepo.findByEmail(loginUser.getEmail());
        if (user.isPresent()) {
            String password = loginUser.getPassword();
            String encodedPassword = user.get().getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Usuario> employee = usuarioRepo.findOneByEmailAndPassword(loginUser.getEmail(), encodedPassword);
                if (employee.isPresent()) {
                    return setUserLogin(employee.get());
                } else {
                    throw  new ResponseMessage("Fallo en el login");
                }
            } else {
                throw  new ResponseMessage("La contraseña no coincide");
            }
        }else {
            throw new ResponseMessage("El email no existe");
        }
    }

    private static LoginUser setUserLogin(Usuario usuario) {
        return LoginUser.builder().user_id(usuario.getUser_id()).nombre(usuario.getNombre()).apellidos(usuario.getApellidos())
                .email(usuario.getEmail()).isAdmin(usuario.is_admin()).build();
    }


    public Usuario getUsuarioById(Long id) {
        return usuarioRepo.findById(id).orElse(null);
    }

    public boolean checkPassword(Usuario usuario, String password) {
        log.info("password: " + password);
        log.info("usuario: " + usuario.getPassword());
        return passwordEncoder.matches(password, usuario.getPassword());

    }

    public int getHistorialPartidosUsuarioNumerico(Long id) {
        int numeroPartidos = 0;
        Usuario usuario = usuarioRepo.findById(id).orElse(null);
        if (ObjectUtils.isNotEmpty(usuario) && ObjectUtils.isNotEmpty(usuario.getTickets())){
            numeroPartidos=  usuario.getTickets().size();
        }
        return numeroPartidos;
    }
}
