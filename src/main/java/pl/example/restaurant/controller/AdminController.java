package pl.example.restaurant.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.example.restaurant.model.Order;
import pl.example.restaurant.model.Reservation;
import pl.example.restaurant.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/statistics")
    public AdminService.Statistics statistics() {
        return adminService.statisticsToday();
    }

    @GetMapping("/orders/today")
    public List<Order> ordersToday() {
        return adminService.ordersToday();
    }

    @GetMapping("/reservations/today")
    public List<Reservation> reservationsToday() {
        return adminService.reservationsToday();
    }
}
