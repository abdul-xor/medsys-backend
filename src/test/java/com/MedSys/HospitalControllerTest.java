package com.MedSys;

import com.MedSys.controller.HospitalController;
import com.MedSys.entity.*;
import com.MedSys.entity.Order;
import com.MedSys.repository.HospitalRepository;
import com.MedSys.repository.UserRepository;
import com.MedSys.service.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HospitalControllerTest {

    @Mock
    private HospitalService hospitalService;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EquipmentService equipmentService;

    @Mock
    private MaintenanceService maintenanceService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private HospitalController controller;

    private Hospital hospital;
    private User user;
    private Equipment equipment;
    private Maintenance maintenance;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("City Hospital");
        hospital.setLocation("Bangalore");

        user = new User();
        user.setId(1L);
        user.setUsername("john");

        equipment = new Equipment();
        equipment.setId(1L);

        maintenance = new Maintenance();
        maintenance.setId(1L);

        order = new Order();
        order.setId(1L);
    }

    // ===========================
    // 1️⃣ POST /api/hospital/create
    // ===========================

    @Test
    void createHospital_Success() {
        when(hospitalService.createHospital(hospital)).thenReturn(hospital);

        ResponseEntity<Hospital> response = controller.createHospital(hospital);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(hospital, response.getBody());
    }

    @Test
    void createHospital_NullHospital() {
        when(hospitalService.createHospital(null))
                .thenThrow(new NullPointerException());

        assertThrows(NullPointerException.class,
                () -> controller.createHospital(null));
    }

    @Test
    void createHospital_ServiceException() {
        when(hospitalService.createHospital(any()))
                .thenThrow(new RuntimeException("DB Error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.createHospital(hospital));

        assertEquals("DB Error", ex.getMessage());
    }

    @Test
    void createHospital_EmptyName() {
        hospital.setName(null);
        when(hospitalService.createHospital(any())).thenReturn(hospital);

        ResponseEntity<Hospital> response = controller.createHospital(hospital);
        assertNull(response.getBody().getName());
    }

    @Test
    void createHospital_LocationOnly() {
        hospital.setName(null);
        hospital.setLocation("Delhi");

        when(hospitalService.createHospital(any())).thenReturn(hospital);

        assertEquals("Delhi", controller.createHospital(hospital).getBody().getLocation());
    }

    // ===========================
    // 2️⃣ POST /api/hospital/my/create
    // ===========================

    @Test
    void createMyHospital_Success() {
        when(hospitalService.createHospitalForLoggedInUser(any()))
                .thenReturn(hospital);

        Hospital result = controller.createMyHospital(hospital);

        assertEquals(hospital, result);
    }

    @Test
    void createMyHospital_UserNotFound() {
        when(hospitalService.createHospitalForLoggedInUser(any()))
                .thenThrow(new RuntimeException("User not found"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.createMyHospital(hospital));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void createMyHospital_NullHospital() {
        when(hospitalService.createHospitalForLoggedInUser(null))
                .thenThrow(new NullPointerException());

        assertThrows(NullPointerException.class,
                () -> controller.createMyHospital(null));
    }

    @Test
    void createMyHospital_ServiceError() {
        when(hospitalService.createHospitalForLoggedInUser(any()))
                .thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class,
                () -> controller.createMyHospital(hospital));
    }

    @Test
    void createMyHospital_ValidHospital() {
        hospital.setName("Apollo");
        when(hospitalService.createHospitalForLoggedInUser(any()))
                .thenReturn(hospital);

        assertEquals("Apollo", controller.createMyHospital(hospital).getName());
    }

    // ===========================
    // 3️⃣ GET /api/hospital
    // ===========================

    @Test
    void getAllHospitals_Success() {
        when(hospitalService.getAllHospitals())
                .thenReturn(List.of(hospital));

        ResponseEntity<List<Hospital>> response = controller.getAllHospitals();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllHospitals_EmptyList() {
        when(hospitalService.getAllHospitals())
                .thenReturn(new ArrayList<>());

        assertTrue(controller.getAllHospitals().getBody().isEmpty());
    }

    @Test
    void getAllHospitals_ServiceError() {
        when(hospitalService.getAllHospitals())
                .thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class,
                () -> controller.getAllHospitals());
    }

    @Test
    void getAllHospitals_Multiple() {
        when(hospitalService.getAllHospitals())
                .thenReturn(List.of(hospital, new Hospital()));

        assertEquals(2, controller.getAllHospitals().getBody().size());
    }

    @Test
    void getAllHospitals_NullHospital() {
        List<Hospital> list = new ArrayList<>();
        list.add(null);

        when(hospitalService.getAllHospitals()).thenReturn(list);

        ResponseEntity<List<Hospital>> response = controller.getAllHospitals();

        assertNull(response.getBody().get(0));
    }


    // ===========================
    // 4️⃣ POST /api/hospital/equipment
    // ===========================

    @Test
    void addEquipment_Success() {
        when(equipmentService.addEquipment(1L, equipment))
                .thenReturn(equipment);

        ResponseEntity<Equipment> response =
                controller.addEquipment(1L, equipment);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void addEquipment_InvalidHospital() {
        when(equipmentService.addEquipment(anyLong(), any()))
                .thenThrow(new RuntimeException("Hospital not found"));

        assertThrows(RuntimeException.class,
                () -> controller.addEquipment(1L, equipment));
    }

    @Test
    void addEquipment_NullEquipment() {
        when(equipmentService.addEquipment(anyLong(), isNull()))
                .thenThrow(new NullPointerException());

        assertThrows(NullPointerException.class,
                () -> controller.addEquipment(1L, null));
    }

    @Test
    void addEquipment_ServiceError() {
        when(equipmentService.addEquipment(anyLong(), any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> controller.addEquipment(1L, equipment));
    }

    @Test
    void addEquipment_ValidData() {
        when(equipmentService.addEquipment(anyLong(), any()))
                .thenReturn(equipment);

        assertNotNull(controller.addEquipment(1L, equipment).getBody());
    }

    // ===========================
    // 5️⃣ POST /api/hospital/order
    // ===========================

    @Test
    void placeOrder_Success() {
        when(orderService.placeOrder(1L, order))
                .thenReturn(order);

        ResponseEntity<Order> response =
                controller.placeOrder(1L, order);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void placeOrder_InvalidEquipment() {
        when(orderService.placeOrder(anyLong(), any()))
                .thenThrow(new RuntimeException("Equipment not found"));

        assertThrows(RuntimeException.class,
                () -> controller.placeOrder(1L, order));
    }

    @Test
    void placeOrder_NullOrder() {
        when(orderService.placeOrder(anyLong(), isNull()))
                .thenThrow(new NullPointerException());

        assertThrows(NullPointerException.class,
                () -> controller.placeOrder(1L, null));
    }

    @Test
    void placeOrder_ServiceError() {
        when(orderService.placeOrder(anyLong(), any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> controller.placeOrder(1L, order));
    }

    @Test
    void placeOrder_Valid() {
        when(orderService.placeOrder(anyLong(), any()))
                .thenReturn(order);

        assertNotNull(controller.placeOrder(1L, order).getBody());
    }
}
