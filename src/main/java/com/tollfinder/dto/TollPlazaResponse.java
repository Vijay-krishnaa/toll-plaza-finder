
package com.tollfinder.dto;

import java.util.List;

public record TollPlazaResponse(
    String message,
    List<TollPlazaMatch> tollPlazas) {
}
