package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.AssignSeatCategoryRequest;
import com.surnekev.ticketing.dto.CreateSeatCategoryRequest;
import com.surnekev.ticketing.dto.SeatCategoryDto;
import com.surnekev.ticketing.dto.SeatCategoryUpdateRequest;
import com.surnekev.ticketing.dto.SeatPriceOverrideRequest;
import com.surnekev.ticketing.dto.SeatTableAssignmentDto;
import com.surnekev.ticketing.service.SeatAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/seat-config")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class SeatAdminController {

    private final SeatAdminService seatAdminService;

    @GetMapping("/categories")
    public List<SeatCategoryDto> listCategories() {
        return seatAdminService.listCategories();
    }

    @PostMapping("/categories")
    public SeatCategoryDto createCategory(@RequestBody @Valid CreateSeatCategoryRequest request) {
        return seatAdminService.createCategory(request);
    }

    @PutMapping("/categories/{id}")
    public SeatCategoryDto updateCategory(@PathVariable Long id,
                                          @RequestBody @Valid SeatCategoryUpdateRequest request) {
        return seatAdminService.updateCategory(id, request);
    }

    @GetMapping("/tables")
    public List<SeatTableAssignmentDto> listTables(@RequestParam(value = "concertId", required = false) Long concertId) {
        return seatAdminService.listTableAssignments(concertId);
    }

    @PostMapping("/tables/assign-category")
    public void assignCategory(@RequestBody @Valid AssignSeatCategoryRequest request) {
        seatAdminService.assignCategoryToTables(request);
    }

    @PostMapping("/tables/override-price")
    public void overridePrice(@RequestBody @Valid SeatPriceOverrideRequest request) {
        seatAdminService.applyPriceOverride(request);
    }
}

