package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario getUsuarioByName(String name) {
        return usuarioRepo.findByNombre(name);
    }

    public Usuario getUsuarioByEmail(String email) {
        return usuarioRepo.findByEmail(email);
    }

    public Usuario saveUsuario(final Usuario usuario) {
        if(StringUtils.isEmpty(usuario.getNombre()) || StringUtils.isEmpty(usuario.getApellidos()) || StringUtils.isEmpty(
                usuario.getEmail()) || StringUtils.isEmpty(usuario.getPassword())) {
            throw new ResponseMessage("Inserte todos los datos para el registro");
        }
        usuario.setPassword(this.passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepo.save(usuario);
    }

    public List<Usuario> getAllUsers(){
        return usuarioRepo.findAll();
    }

    public Usuario modificarUsuario(final Long id, final Usuario usuarioNuevo) {
        final Usuario updateUser = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuario no existe con Id: " + id));
        updateUser.setEmail(usuarioNuevo.getEmail());
        updateUser.setNombre(usuarioNuevo.getNombre());
        updateUser.setApellidos(usuarioNuevo.getApellidos());
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

    public Map<String, String> loginEmployee(Usuario loginUser) {
        Usuario user = usuarioRepo.findByEmail(loginUser.getEmail());
        if (user != null) {
            String password = loginUser.getPassword();
            String encodedPassword = user.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Usuario> employee = usuarioRepo.findOneByEmailAndPassword(loginUser.getEmail(), encodedPassword);
                if (employee.isPresent()) {
                    return setUserLoginMap(employee.get());
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

    private static Map<String, String> setUserLoginMap(Usuario usuario) {
        final Map<String, String> userLogin = new HashMap<>();
        userLogin.put("userId", String.valueOf(usuario.getUser_id()));
        userLogin.put("userName", String.valueOf(usuario.getNombre()));
        userLogin.put("userApellidos", String.valueOf(usuario.getApellidos()));
        userLogin.put("userEmail", String.valueOf(usuario.getEmail()));
        userLogin.put("isAdmin", String.valueOf(usuario.is_admin()));
        return userLogin;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepo.findByNombre(username);
    }
}
