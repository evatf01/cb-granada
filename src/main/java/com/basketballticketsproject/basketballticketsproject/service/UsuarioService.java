package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

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
        usuario.setPassword(this.passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepo.save(usuario);
    }

    public List<Usuario> getAllUsers(){
        return usuarioRepo.findAll();
    }

    public Usuario modificarUsuario(final UUID id, final Usuario usuarioNuevo) {
        final Usuario updateUser = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("Employee not exist with id: " + id));
        updateUser.setEmail(usuarioNuevo.getEmail());
        updateUser.setNombre(usuarioNuevo.getNombre());
        updateUser.setApellido(usuarioNuevo.getApellido());
        return usuarioRepo.save(updateUser);
    }

    public void borrarUsuario(UUID id) {
        Usuario deleteUser = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("Employee not exist with id: " + id));

        Optional<Ticket> ticket = ticketRepo.findByUsuario(deleteUser);
        if (ticket.isPresent()) {
            ticket.get().setUsuario(null);
            ticketRepo.save(ticket.get());
        }

        deleteUser.setTickets(null);
        usuarioRepo.delete(deleteUser);
    }

    public Usuario loginEmployee(Usuario loginUser) {
        Usuario user = usuarioRepo.findByEmail(loginUser.getEmail());
        if (user != null) {
            String password = loginUser.getPassword();
            String encodedPassword = user.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Usuario> employee = usuarioRepo.findOneByEmailAndPassword(loginUser.getEmail(), encodedPassword);
                if (employee.isPresent()) {
                    return employee.get();
                } else {
                    throw  new ResponseMessage("Login Failed");
                }
            } else {
                throw  new ResponseMessage("password Not Match");
            }
        }else {
            throw new ResponseMessage("Email not exits");
        }
    }
}
