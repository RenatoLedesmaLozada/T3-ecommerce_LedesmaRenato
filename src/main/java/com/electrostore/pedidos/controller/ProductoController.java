package com.electrostore.pedidos.controller;

import com.electrostore.pedidos.entity.Producto;
import com.electrostore.pedidos.service.ElectroStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	@Autowired
	private ElectroStoreService electroStoreService;

	@GetMapping
	public ResponseEntity<List<Producto>> listar() {
		return ResponseEntity.ok(electroStoreService.listarProductos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
		return ResponseEntity.ok(electroStoreService.obtenerProductoPorId(id));
	}

	@PostMapping
	public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
		return new ResponseEntity<>(electroStoreService.guardarProducto(producto), HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		electroStoreService.eliminarProducto(id);
		return ResponseEntity.noContent().build();
	}
}