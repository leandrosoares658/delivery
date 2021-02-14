package com.leandrosoares658.delivery.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandrosoares658.delivery.dto.OrderDTO;
import com.leandrosoares658.delivery.dto.ProductDTO;
import com.leandrosoares658.delivery.entities.Order;
import com.leandrosoares658.delivery.entities.OrderStatus;
import com.leandrosoares658.delivery.entities.Product;
import com.leandrosoares658.delivery.repositories.OrderRepository;
import com.leandrosoares658.delivery.repositories.ProductRepository;


@Service
public class OrderService {
	
	@Autowired
	private OrderRepository repository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Transactional(readOnly = true)
	public List<OrderDTO> findAll() {
		List<Order> list = repository.findOrdersWithProducts();
		return list.stream().map(x -> new OrderDTO(x)).collect(Collectors.toList());
	}
	
	
	@Transactional
	public OrderDTO insert(OrderDTO dto) {
		Order order = new Order(null, dto.getAddress(), dto.getLatitude(), dto.getLongitude(), 
				Instant.now(), OrderStatus.PENDING);
		
		for (ProductDTO p: dto.getProducts()) {
			Product product = productRepository.getOne(p.getId());
			order.getProducts().add(product);
		}
		order = repository.save(order);
		return new OrderDTO(order);
	}
	
	@Transactional
	public OrderDTO sertDelivered(Long Id) {
		Order order = repository.getOne(Id);
		order.setStatus(OrderStatus.DELIVERED);
		order = repository.save(order);
		return new OrderDTO(order);
	}
}
