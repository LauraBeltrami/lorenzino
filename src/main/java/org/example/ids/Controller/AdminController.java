package org.example.ids.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.example.ids.DTO.RichiestaApprovazioneDTO;
import org.example.ids.Service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GET: Vedi la lista di tutti quelli da approvare
    @GetMapping("/pending")
    public List<RichiestaApprovazioneDTO> getPendingUsers() {
        return adminService.getUtentiInAttesa();
    }

    // DTO per la richiesta di approvazione
    public record ApprovaReq(@NotNull Long id, @NotBlank String ruolo) {}

    // POST: Approva quello scelto
    @PostMapping("/approve")
    public String approvaUser(@Valid @RequestBody ApprovaReq req) {
        adminService.approvaUtente(req.id(), req.ruolo());
        return req.ruolo() + " con ID " + req.id() + " approvato con successo!";
    }
}