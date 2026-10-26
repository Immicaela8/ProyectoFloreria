package com.example.demo.repository;

import com.example.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, int id);
}
