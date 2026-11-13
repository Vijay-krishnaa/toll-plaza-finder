
package com.tollfinder.dto;

public record TollPlazaMatch(
    String tollName,
    Double latitude,
    Double longitude,
    Double distanceFromSourceKm,
    Double distanceFromRouteKm) {
}
