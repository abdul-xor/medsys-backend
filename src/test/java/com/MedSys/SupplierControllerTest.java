package com.MedSys;
import com.MedSys.controller.SupplierController;
import com.MedSys.entity.Order;
import com.MedSys.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SupplierControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private SupplierController controller;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);
        order.setStatus("Initiated");
        order.setQuantity(5);
        order.setOrderDate(Date.valueOf(LocalDate.now()));
    }

    // ===========================
    // 1️⃣ GET /api/supplier/orders
    // ===========================

    @Test
    void getAllOrders_Success() {
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        ResponseEntity<List<Order>> response = controller.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllOrders_EmptyList() {
        when(orderService.getAllOrders()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Order>> response = controller.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllOrders_NullOrderInsideList() {
        List<Order> list = new ArrayList<>();
        list.add(null);

        when(orderService.getAllOrders()).thenReturn(list);

        ResponseEntity<List<Order>> response = controller.getAllOrders();

        assertNull(response.getBody().get(0));
    }

    @Test
    void getAllOrders_ServiceThrowsException() {
        when(orderService.getAllOrders())
                .thenThrow(new RuntimeException("DB Error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.getAllOrders());

        assertEquals("DB Error", ex.getMessage());
    }

    @Test
    void getAllOrders_MultipleOrders() {
        Order o2 = new Order();
        o2.setId(2L);
        o2.setStatus("Delivered");

        when(orderService.getAllOrders()).thenReturn(List.of(order, o2));

        ResponseEntity<List<Order>> response = controller.getAllOrders();

        assertEquals(2, response.getBody().size());
    }

    // ======================================
    // 2️⃣ PUT /api/supplier/order/update/{id}
    // ======================================

    @Test
    void updateOrderStatus_Success() {
        when(orderService.updateOrderStatus(1L, "Delivered"))
                .thenReturn(order);

        ResponseEntity<Order> response =
                controller.updateOrderStatus(1L, "Delivered");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
    }

    @Test
    void updateOrderStatus_OrderNotFound() {
        when(orderService.updateOrderStatus(99L, "Delivered"))
                .thenReturn(null);

        ResponseEntity<Order> response =
                controller.updateOrderStatus(99L, "Delivered");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateOrderStatus_ServiceThrowsException() {
        when(orderService.updateOrderStatus(any(), any()))
                .thenThrow(new RuntimeException("Update failed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.updateOrderStatus(1L, "Delivered"));

        assertEquals("Update failed", ex.getMessage());
    }

    @Test
    void updateOrderStatus_NullStatus() {
        when(orderService.updateOrderStatus(1L, null))
                .thenReturn(order);

        ResponseEntity<Order> response =
                controller.updateOrderStatus(1L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateOrderStatus_EmptyStatus() {
        when(orderService.updateOrderStatus(1L, ""))
                .thenReturn(order);

        ResponseEntity<Order> response =
                controller.updateOrderStatus(1L, "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
