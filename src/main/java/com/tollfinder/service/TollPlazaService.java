
package com.tollfinder.service;

import com.tollfinder.dto.TollPlazaMatch;
import com.tollfinder.dto.TollPlazaRequest;
import com.tollfinder.dto.TollPlazaResponse;
import com.tollfinder.dto.RouteResponse;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import tools.jackson.databind.JsonNode;

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

    JsonNode mapplsRoute = routingService.getRoute(source, destination);

    if (mapplsRoute == null
        || !mapplsRoute.has("routes")
        || !mapplsRoute.path("routes").isArray()
        || mapplsRoute.path("routes").isEmpty()) {

      throw new IllegalArgumentException(
          "No route found between the given PIN codes.");
    }

    double distanceInKm = mapplsRoute
        .path("routes")
        .get(0)
        .path("distance")
        .asDouble() / 1000.0;

    List<TollPlazaMatch> tolls = routeMatcher.findTollsAlongRoute(mapplsRoute);

    RouteResponse route = new RouteResponse(
        source,
        destination,
        distanceInKm);

    return new TollPlazaResponse(route, tolls);
  }
}
