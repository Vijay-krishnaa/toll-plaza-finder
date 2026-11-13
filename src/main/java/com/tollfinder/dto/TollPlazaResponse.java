
package com.tollfinder.dto;

import java.util.List;

public record TollPlazaResponse(
        RouteResponse route,
        List<TollPlazaMatch> tollPlazas) {
}
