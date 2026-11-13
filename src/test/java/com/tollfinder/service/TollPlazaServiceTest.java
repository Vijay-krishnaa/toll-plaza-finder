
package com.tollfinder.service;

import com.tollfinder.dto.TollPlazaMatch;
import com.tollfinder.dto.TollPlazaRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.JsonNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TollPlazaServiceTest {

  @Mock
  private MapplsRoutingService routingService;

  @Mock
  private TollPlazaRouteMatcher routeMatcher;

  @InjectMocks
  private TollPlazaService tollPlazaService;

  @Test
  void shouldReturnEmptyListWhenSourceAndDestinationAreSame() {

    TollPlazaRequest request = new TollPlazaRequest("560064", "560064");

    List<TollPlazaMatch> result = tollPlazaService.findTollPlazas(request);

    assertTrue(result.isEmpty());

    verifyNoInteractions(routingService);
    verifyNoInteractions(routeMatcher);
  }

  @Test
  void shouldReturnEmptyListWhenNoTollPlazasAreFound() {

    TollPlazaRequest request = new TollPlazaRequest("560064", "411045");

    JsonNode routeResponse = mock(JsonNode.class);

    when(routingService.getRoute("560064", "411045"))
        .thenReturn(routeResponse);

    when(routeMatcher.findTollsAlongRoute(routeResponse))
        .thenReturn(List.of());

    List<TollPlazaMatch> result = tollPlazaService.findTollPlazas(request);

    assertTrue(result.isEmpty());

    verify(routingService, times(1))
        .getRoute("560064", "411045");

    verify(routeMatcher, times(1))
        .findTollsAlongRoute(routeResponse);
  }
}
