
package com.tollfinder.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TollPlazaMatch(
        @JsonProperty("name") String tollName,

        Double latitude,

        Double longitude,

        @JsonProperty("distanceFromSource") Double distanceFromSourceKm,

        @JsonIgnore Double distanceFromRouteKm) {
}
