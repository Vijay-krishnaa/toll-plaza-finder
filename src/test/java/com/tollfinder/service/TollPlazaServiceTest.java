
package com.tollfinder.service;

import com.tollfinder.dto.RouteResponse;
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
    void shouldReturnEmptyTollPlazasWhenNoTollsAreFound() {

        TollPlazaRequest request = new TollPlazaRequest("560064", "411045");

        when(pincodeValidationService.isValidPincode("560064"))
                .thenReturn(true);

        when(pincodeValidationService.isValidPincode("411045"))
                .thenReturn(true);

        // Mock Mappls route response
        JsonNode routeResponse = mock(JsonNode.class);
        JsonNode routesNode = mock(JsonNode.class);
        JsonNode firstRoute = mock(JsonNode.class);
        JsonNode distanceNode = mock(JsonNode.class);

        when(routeResponse.has("routes")).thenReturn(true);
        when(routeResponse.path("routes")).thenReturn(routesNode);
        when(routesNode.isArray()).thenReturn(true);
        when(routesNode.isEmpty()).thenReturn(false);
        when(routesNode.get(0)).thenReturn(firstRoute);
        when(firstRoute.path("distance")).thenReturn(distanceNode);
        when(distanceNode.asDouble()).thenReturn(847780.0);

        when(routingService.getRoute("560064", "411045"))
                .thenReturn(routeResponse);

        when(routeMatcher.findTollsAlongRoute(routeResponse))
                .thenReturn(List.of());

        TollPlazaResponse result = tollPlazaService.findTollPlazas(request);

        assertNotNull(result.route());
        assertEquals("560064", result.route().sourcePincode());
        assertEquals("411045", result.route().destinationPincode());
        assertEquals(847.78, result.route().distanceInKm(), 0.01);

        assertNotNull(result.tollPlazas());
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
