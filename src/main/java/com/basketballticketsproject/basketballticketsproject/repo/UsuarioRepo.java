package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

    Usuario findByNombre(String name);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findOneByEmailAndPassword(String email, String password);

    Optional<Usuario> findByTickets(Ticket ticket);

    @Query(value = "SELECT * FROM usuario LIMIT 10", nativeQuery = true)
    List<Usuario> findAllUsersLimit();
}
