package com.example.demo.repository;

import com.example.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    boolean existsByClienteId(int clienteId);

    boolean existsByProductoId(int productoId);

    boolean existsByZonaId(int zonaId);

    List<Pedido> findByRepartidorId(int repartidorId);

    long countByClienteId(int clienteId);

    long countByClienteIdAndEstado(int clienteId, String estado);

    long countByClienteIdAndEstadoNot(int clienteId, String estado);

    long countByRepartidorIdAndEstadoIn(int repartidorId, Collection<String> estados);

    long countByEstado(String estado);
}
