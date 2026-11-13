
package com.tollfinder.controller;

import com.tollfinder.dto.TollPlazaRequest;
import com.tollfinder.dto.TollPlazaResponse;
import com.tollfinder.service.TollPlazaService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/toll-plazas")
@Tag(name = "Toll Plaza Finder", description = "Find toll plazas between two Indian pincodes")
public class TollPlazaController {

  private final TollPlazaService tollPlazaService;

  public TollPlazaController(TollPlazaService tollPlazaService) {
    this.tollPlazaService = tollPlazaService;
  }

  @PostMapping
  @Operation(summary = "Find toll plazas between two pincodes", description = "Returns toll plazas along the driving route.")
  public TollPlazaResponse findTollPlazas(
      @Valid @RequestBody TollPlazaRequest request) {

    return tollPlazaService.findTollPlazas(request);
  }
}
