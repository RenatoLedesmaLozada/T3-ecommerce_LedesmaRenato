package com.electrostore.pedidos.service;

import com.electrostore.pedidos.dto.ItemPedidoRequestDTO;
import com.electrostore.pedidos.dto.PedidoRequestDTO;
import com.electrostore.pedidos.entity.DetallePedido;
import com.electrostore.pedidos.entity.Pedido;
import com.electrostore.pedidos.entity.Producto;
import com.electrostore.pedidos.repository.PedidoRepository;
import com.electrostore.pedidos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Transactional
	public Pedido crearPedido(PedidoRequestDTO requestDTO) {
		// 1. Instanciar la cabecera del pedido
		Pedido pedido = new Pedido();
		pedido.setCliente(requestDTO.getCliente());
		pedido.setFechaCompra(LocalDateTime.now());
		pedido.setEstadoPedido("PENDIENTE");
		pedido.setMetodoPago(requestDTO.getMetodoPago());

		BigDecimal montoTotalCalculado = BigDecimal.ZERO;
		List<DetallePedido> detallesEntity = new ArrayList<>();

		// 2. Recorrer los ítems enviados en la solicitud DTO
		if (requestDTO.getItems() != null) {
			for (ItemPedidoRequestDTO item : requestDTO.getItems()) {
				Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow(
						() -> new RuntimeException("Producto no encontrado con el ID: " + item.getProductoId()));

				// Validar stock disponible (Corregido: sin la 'S' al final)
				if (producto.getStock() < item.getCantidad()) {
					throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre()
							+ ". Stock disponible: " + producto.getStock());
				}

				// Descontar inventario
				producto.setStock(producto.getStock() - item.getCantidad());
				productoRepository.save(producto);

				// Calcular subtotal (precio_venta * cantidad)
				BigDecimal precioUnitario = producto.getPrecioVenta();
				BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
				montoTotalCalculado = montoTotalCalculado.add(subtotal);

				// Crear y enlazar el detalle del pedido
				DetallePedido detalle = new DetallePedido();
				detalle.setPedido(pedido);
				detalle.setProducto(producto);
				detalle.setCantidad(item.getCantidad());
				detalle.setPrecioUnitario(precioUnitario);
				detalle.setSubtotal(subtotal);

				detallesEntity.add(detalle);
			}
		}

		// 3. Guardar el pedido final con su monto y relación de detalles
		pedido.setMontoTotal(montoTotalCalculado);
		pedido.setDetalles(detallesEntity);

		return pedidoRepository.save(pedido);
	}
}