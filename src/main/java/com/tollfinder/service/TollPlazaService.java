
package com.tollfinder.service;

import com.tollfinder.dto.TollPlazaMatch;
import com.tollfinder.dto.TollPlazaRequest;
import com.tollfinder.dto.TollPlazaResponse;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class TollPlazaService {

  private final MapplsRoutingService routingService;
  private final TollPlazaRouteMatcher routeMatcher;
  private final PincodeValidationService pincodeValidationService;

  public TollPlazaService(
      MapplsRoutingService routingService,
      TollPlazaRouteMatcher routeMatcher,
      PincodeValidationService pincodeValidationService) {

    this.routingService = routingService;
    this.routeMatcher = routeMatcher;
    this.pincodeValidationService = pincodeValidationService;
  }

  @Cacheable(value = "tollPlazas", key = "#request.sourcePincode() + '-' + #request.destinationPincode()")
  public TollPlazaResponse findTollPlazas(TollPlazaRequest request) {

    String source = request.sourcePincode();
    String destination = request.destinationPincode();

    if (source.equals(destination)) {
      throw new IllegalArgumentException(
          "Source and destination PIN codes cannot be the same.");
    }

    if (!pincodeValidationService.isValidPincode(source)) {
      throw new IllegalArgumentException(
          "Invalid source PIN code: " + source);
    }

    if (!pincodeValidationService.isValidPincode(destination)) {
      throw new IllegalArgumentException(
          "Invalid destination PIN code: " + destination);
    }

    var routeResponse = routingService.getRoute(source, destination);

    List<TollPlazaMatch> tolls = routeMatcher.findTollsAlongRoute(routeResponse);

    if (tolls.isEmpty()) {
      return new TollPlazaResponse(
          "No toll plazas found between the given locations.",
          List.of());
    }

    return new TollPlazaResponse(
        "Toll plazas found successfully.",
        tolls);
  }
}
