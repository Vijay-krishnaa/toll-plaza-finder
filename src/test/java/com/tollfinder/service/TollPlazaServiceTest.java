
package com.tollfinder.service;

import com.tollfinder.dto.TollPlazaRequest;
import com.tollfinder.dto.TollPlazaResponse;

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

  @Mock
  private PincodeValidationService pincodeValidationService;

  @InjectMocks
  private TollPlazaService tollPlazaService;

  @Test
  void shouldThrowExceptionWhenSourceAndDestinationAreSame() {

    TollPlazaRequest request = new TollPlazaRequest("560064", "560064");

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> tollPlazaService.findTollPlazas(request));

    assertEquals(
        "Source and destination PIN codes cannot be the same.",
        exception.getMessage());

    verifyNoInteractions(
        routingService,
        routeMatcher,
        pincodeValidationService);
  }

  @Test
  void shouldReturnMessageWhenNoTollPlazasAreFound() {

    TollPlazaRequest request = new TollPlazaRequest("560064", "411045");

    when(pincodeValidationService.isValidPincode("560064"))
        .thenReturn(true);

    when(pincodeValidationService.isValidPincode("411045"))
        .thenReturn(true);

    JsonNode routeResponse = mock(JsonNode.class);

    when(routingService.getRoute("560064", "411045"))
        .thenReturn(routeResponse);

    when(routeMatcher.findTollsAlongRoute(routeResponse))
        .thenReturn(List.of());

    TollPlazaResponse result = tollPlazaService.findTollPlazas(request);

    assertEquals(
        "No toll plazas found between the given locations.",
        result.message());

    assertTrue(result.tollPlazas().isEmpty());

    verify(pincodeValidationService).isValidPincode("560064");
    verify(pincodeValidationService).isValidPincode("411045");

    verify(routingService).getRoute("560064", "411045");

    verify(routeMatcher).findTollsAlongRoute(routeResponse);
  }

  @Test
  void shouldThrowExceptionWhenSourcePincodeIsInvalid() {

    TollPlazaRequest request = new TollPlazaRequest("123456", "411045");

    when(pincodeValidationService.isValidPincode("123456"))
        .thenReturn(false);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> tollPlazaService.findTollPlazas(request));

    assertEquals(
        "Invalid source PIN code: 123456",
        exception.getMessage());

    verifyNoInteractions(routingService, routeMatcher);
  }

  @Test
  void shouldThrowExceptionWhenDestinationPincodeIsInvalid() {

    TollPlazaRequest request = new TollPlazaRequest("560064", "123456");

    when(pincodeValidationService.isValidPincode("560064"))
        .thenReturn(true);

    when(pincodeValidationService.isValidPincode("123456"))
        .thenReturn(false);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> tollPlazaService.findTollPlazas(request));

    assertEquals(
        "Invalid destination PIN code: 123456",
        exception.getMessage());

    verifyNoInteractions(routingService, routeMatcher);
  }
}
