package com.MedSys;

import com.MedSys.controller.TechnicianController;
import com.MedSys.entity.Maintenance;
import com.MedSys.service.MaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TechnicianControllerTest {

    @Mock
    private MaintenanceService maintenanceService;

    @InjectMocks
    private TechnicianController controller;

    private Maintenance maintenance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        maintenance = new Maintenance();
        maintenance.setId(1L);
        maintenance.setStatus("Scheduled");
    }

    // ===========================
    // 1️⃣ GET ALL MAINTENANCE
    // ===========================

    @Test
    void getAllMaintenance_Success() {
        when(maintenanceService.getAllMaintenance())
                .thenReturn(List.of(maintenance));

        ResponseEntity<List<Maintenance>> response =
                controller.getAllMaintenance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllMaintenance_EmptyList() {
        when(maintenanceService.getAllMaintenance())
                .thenReturn(new ArrayList<>());

        ResponseEntity<List<Maintenance>> response =
                controller.getAllMaintenance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllMaintenance_NullMaintenanceInList() {
        List<Maintenance> list = new ArrayList<>();
        list.add(null);

        when(maintenanceService.getAllMaintenance())
                .thenReturn(list);

        ResponseEntity<List<Maintenance>> response =
                controller.getAllMaintenance();

        assertNull(response.getBody().get(0));
    }

    @Test
    void getAllMaintenance_ServiceThrowsException() {
        when(maintenanceService.getAllMaintenance())
                .thenThrow(new RuntimeException("DB Error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.getAllMaintenance());

        assertEquals("DB Error", ex.getMessage());
    }

    @Test
    void getAllMaintenance_MultipleRecords() {
        Maintenance m2 = new Maintenance();
        m2.setId(2L);

        when(maintenanceService.getAllMaintenance())
                .thenReturn(List.of(maintenance, m2));

        ResponseEntity<List<Maintenance>> response =
                controller.getAllMaintenance();

        assertEquals(2, response.getBody().size());
    }

    // ===========================
    // 2️⃣ UPDATE MAINTENANCE
    // ===========================

    @Test
    void updateMaintenance_Success() {
        when(maintenanceService.updateMaintenance(eq(1L), any()))
                .thenReturn(maintenance);

        ResponseEntity<Maintenance> response =
                controller.updateMaintenance(1L, maintenance);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(maintenance, response.getBody());
    }

    @Test
    void updateMaintenance_NotFound() {
        when(maintenanceService.updateMaintenance(eq(99L), any()))
                .thenReturn(null);

        ResponseEntity<Maintenance> response =
                controller.updateMaintenance(99L, maintenance);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateMaintenance_NullBody() {
        when(maintenanceService.updateMaintenance(eq(1L), isNull()))
                .thenReturn(null);

        ResponseEntity<Maintenance> response =
                controller.updateMaintenance(1L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateMaintenance_ServiceThrowsException() {
        when(maintenanceService.updateMaintenance(any(), any()))
                .thenThrow(new RuntimeException("Update failed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.updateMaintenance(1L, maintenance));

        assertEquals("Update failed", ex.getMessage());
    }

    @Test
    void updateMaintenance_ValidIdDifferentObject() {
        Maintenance updated = new Maintenance();
        updated.setStatus("Completed");

        when(maintenanceService.updateMaintenance(1L, updated))
                .thenReturn(updated);

        ResponseEntity<Maintenance> response =
                controller.updateMaintenance(1L, updated);

        assertEquals("Completed", response.getBody().getStatus());
    }

    // ===========================
    // 3️⃣ DELETE MAINTENANCE
    // ===========================

    @Test
    void deleteMaintenance_Success() {
        doNothing().when(maintenanceService).deleteMaintenance(1L);

        ResponseEntity<Void> response =
                controller.deleteMaintenance(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(maintenanceService, times(1))
                .deleteMaintenance(1L);
    }

    @Test
    void deleteMaintenance_NotFoundStillDeletes() {
        doNothing().when(maintenanceService).deleteMaintenance(99L);

        ResponseEntity<Void> response =
                controller.deleteMaintenance(99L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteMaintenance_ServiceThrowsException() {
        doThrow(new RuntimeException("Delete failed"))
                .when(maintenanceService).deleteMaintenance(1L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.deleteMaintenance(1L));

        assertEquals("Delete failed", ex.getMessage());
    }

    @Test
    void deleteMaintenance_NullId() {
        doNothing().when(maintenanceService).deleteMaintenance(null);

        ResponseEntity<Void> response = controller.deleteMaintenance(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void deleteMaintenance_VerifyMethodCalled() {
        doNothing().when(maintenanceService).deleteMaintenance(1L);

        controller.deleteMaintenance(1L);

        verify(maintenanceService, times(1))
                .deleteMaintenance(1L);
    }
}
