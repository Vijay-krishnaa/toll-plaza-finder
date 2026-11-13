
package com.tollfinder.service;

import com.tollfinder.dto.TollPlazaMatch;
import com.tollfinder.dto.TollPlazaRequest;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class TollPlazaService {

  private final MapplsRoutingService routingService;
  private final TollPlazaRouteMatcher routeMatcher;

  public TollPlazaService(
      MapplsRoutingService routingService,
      TollPlazaRouteMatcher routeMatcher) {

    this.routingService = routingService;
    this.routeMatcher = routeMatcher;
  }

  @Cacheable(value = "tollPlazas", key = "#request.sourcePincode() + '-' + #request.destinationPincode()")
  public List<TollPlazaMatch> findTollPlazas(TollPlazaRequest request) {

    String source = request.sourcePincode();
    String destination = request.destinationPincode();

    // Same source and destination
    if (source.equals(destination)) {
      return List.of();
    }

    // Get route from Mappls
    var routeResponse = routingService.getRoute(source, destination);

    // Find toll plazas along the route
    return routeMatcher.findTollsAlongRoute(routeResponse);
  }
}
