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
public class ElectroStoreService {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private PedidoRepository pedidoRepository;

	public List<Producto> listarProductos() {
		return productoRepository.findAll();
	}

	public Producto obtenerProductoPorId(Long id) {
		return productoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Producto no encontrado con el ID: " + id));
	}

	public Producto guardarProducto(Producto producto) {
		return productoRepository.save(producto);
	}

	public void eliminarProducto(Long id) {
		if (!productoRepository.existsById(id)) {
			throw new RuntimeException("Producto no encontrado con el ID: " + id);
		}
		productoRepository.deleteById(id);
	}

	@Transactional
	public Pedido crearPedido(PedidoRequestDTO requestDTO) {

		Pedido pedido = new Pedido();
		pedido.setCliente(requestDTO.getCliente());
		pedido.setFechaCompra(LocalDateTime.now());
		pedido.setEstadoPedido("PENDIENTE");
		pedido.setMetodoPago(requestDTO.getMetodoPago());

		BigDecimal montoTotalCalculado = BigDecimal.ZERO;
		List<DetallePedido> detallesEntity = new ArrayList<>();

		if (requestDTO.getItems() != null && !requestDTO.getItems().isEmpty()) {
			for (ItemPedidoRequestDTO item : requestDTO.getItems()) {
				Producto producto = productoRepository.findById(item.getProductoId())
						.orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + item.getProductoId()));

				if (producto.getStock() < item.getCantidad()) {
					throw new RuntimeException("Stock insuficiente para: " + producto.getNombre() + ". Disponible: "
							+ producto.getStock());
				}

				producto.setStock(producto.getStock() - item.getCantidad());
				productoRepository.save(producto);

				// CORREGIDO: Usamos getPrecioVenta() en lugar de getPrecio()
				BigDecimal precioUnitario = producto.getPrecioVenta();
				BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
				montoTotalCalculado = montoTotalCalculado.add(subtotal);

				DetallePedido detalle = new DetallePedido();
				detalle.setPedido(pedido);
				detalle.setProducto(producto);
				detalle.setCantidad(item.getCantidad());
				detalle.setPrecioUnitario(precioUnitario);
				detalle.setSubtotal(subtotal);

				detallesEntity.add(detalle);
			}
		}

		pedido.setMontoTotal(montoTotalCalculado);
		pedido.setDetalles(detallesEntity);

		return pedidoRepository.save(pedido);
	}
}