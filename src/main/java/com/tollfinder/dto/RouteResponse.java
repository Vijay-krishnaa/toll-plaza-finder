package com.tollfinder.dto;

public record RouteResponse(
    String sourcePincode,
    String destinationPincode,
    Double distanceInKm) {
}