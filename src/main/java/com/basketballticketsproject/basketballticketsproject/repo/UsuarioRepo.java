package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepo extends JpaRepository<Usuario, UUID> {

    @Query(value = "SELECT * FROM Usuario WHERE nombre = ?1")
    Usuario findByNombre(String name);

    @Query(value = "SELECT * FROM Usuario WHERE email = ?1")
    Usuario findByEmail(String email);

    Optional<Usuario> findOneByEmailAndPassword(String email, String password);

}
