package com.MedSys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MedSys.entity.Order;
import com.MedSys.repository.OrderRepository;
import com.MedSys.service.OrderService;


@RestController
public class SupplierController {
	
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderService orderService;
	
	
	@GetMapping("/api/supplier/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }
	
	 @PutMapping("/api/supplier/order/update/{orderId}")
	 public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestParam String newStatus){
	       return ResponseEntity.status(200).body(orderService.updateOrderStatus(orderId,newStatus));
	  }

}
