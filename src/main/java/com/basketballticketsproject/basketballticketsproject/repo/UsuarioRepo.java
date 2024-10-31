package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

    Usuario findByNombre(String name);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findOneByEmailAndPassword(String email, String password);

    Optional<Usuario> findByTickets(Ticket ticket);

    @Query(value = "SELECT * FROM usuario LIMIT 10", nativeQuery = true)
    List<Usuario> findAllUsersLimit();


    @Query(value = "SELECT count(u.user_id) as partidosAsistidos FROM usuario u JOIN ticket t ON u.user_id = t.usuario_id where u.user_id = ?1 group by u.user_id  ;",
            nativeQuery = true)
   Integer getHistorialPartidos(Long id);

    @Modifying
    @Query(value = "UPDATE usuario set VALIDADO = true where email = ?1", nativeQuery = true)
    int validarEmail(String email);
}
