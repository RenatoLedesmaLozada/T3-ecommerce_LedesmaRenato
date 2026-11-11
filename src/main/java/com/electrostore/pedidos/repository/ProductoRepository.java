package com.electrostore.pedidos.repository;

import com.electrostore.pedidos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

	// Ejecuta la NamedQuery definida en la entidad
	List<Producto> findByCategoria(String categoria);

	// Consulta JPQL con Inyección Segura (Parámetros nombrados)
	@Query("SELECT p FROM Producto p WHERE p.marca = :marca AND p.precio <= :precioMax")
	List<Producto> buscarPorMarcaYPrecioMaximo(@Param("marca") String marca, @Param("precioMax") BigDecimal precioMax);
}