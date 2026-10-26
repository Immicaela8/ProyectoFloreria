package com.example.demo.repository;

import com.example.demo.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromocionRepository extends JpaRepository<Promocion, Integer> {
    List<Promocion> findByProductoId(int productoId);

    List<Promocion> findByActivaTrue();

    Optional<Promocion> findFirstByProductoIdAndActivaTrue(int productoId);
}
