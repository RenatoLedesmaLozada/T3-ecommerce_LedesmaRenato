package com.electrostore.pedidos.controller;

import com.electrostore.pedidos.dto.PedidoRequestDTO;
import com.electrostore.pedidos.entity.Pedido;
import com.electrostore.pedidos.service.ElectroStoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	@Autowired
	private ElectroStoreService electroStoreService;

	@PostMapping
	public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody PedidoRequestDTO request) {
		Pedido pedidoCreado = electroStoreService.crearPedido(request);
		return new ResponseEntity<>(pedidoCreado, HttpStatus.CREATED);
	}
}