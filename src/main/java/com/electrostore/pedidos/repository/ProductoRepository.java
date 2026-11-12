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

	// 1. Ejecución explícita de la NamedQuery definida en la entidad Producto
	@Query(name = "Producto.findByCategoria")
	List<Producto> findByCategoria(@Param("categoria") String categoria);

	// 2. Consulta JPQL personalizada con Inyección Segura (Parámetros nombrados)
	@Query("SELECT p FROM Producto p WHERE p.marca = :marca AND p.precioVenta <= :precioMax")
	List<Producto> buscarPorMarcaYPrecioMaximo(@Param("marca") String marca, @Param("precioMax") BigDecimal precioMax);
}