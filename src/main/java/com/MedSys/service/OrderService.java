package com.MedSys.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MedSys.entity.Equipment;
import com.MedSys.entity.Order;
import com.MedSys.repository.EquipmentRepository;
import com.MedSys.repository.OrderRepository;


@Service
public class OrderService {
	
	@Autowired
	EquipmentRepository equipmentRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	
	public Order placeOrder(Long equipmentId, Order order) 
    {
        Equipment equipment=equipmentRepository.findById(equipmentId).orElse(null);
        order.setEquipment(equipment);
        //order.setOrderDate(new Date());
        order.setOrderDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
        order.setStatus("Initiated");
        return orderRepository.save(order);
    }


	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}
	
	
	 public Order updateOrderStatus(Long orderId, String newStatus) {
	        Order orderOptional = orderRepository.findById(orderId).orElse(null);
	        if(orderOptional!=null){
	          orderOptional.setStatus(newStatus);
	          return orderRepository.save(orderOptional); 
	        }
	        return null;
	  }

}
