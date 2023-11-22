package com.basketballticketsproject.basketballticketsproject.repo;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PartidoRepo extends JpaRepository<Partido, UUID> {

}
