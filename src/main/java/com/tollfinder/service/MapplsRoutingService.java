
package com.tollfinder.service;

import com.tollfinder.config.MapplsConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class MapplsRoutingService {

  private final MapplsConfig mapplsConfig;
  private final RestClient restClient;

  public MapplsRoutingService(MapplsConfig mapplsConfig) {
    this.mapplsConfig = mapplsConfig;
    this.restClient = RestClient.create();
  }

  public JsonNode getRoute(String source, String destination) {

    System.out.println("Calling Mappls Route API...");

    String locations = source + ";" + destination;

    return restClient.get()
        .uri(uriBuilder -> uriBuilder
            .scheme("https")
            .host("route.mappls.com")
            .path("/route/direction/route_adv/driving/" + locations)
            .queryParam("geometries", "geojson")
            .queryParam("overview", "full")
            .queryParam("access_token", mapplsConfig.getApiKey())
            .build())
        .retrieve()
        .body(JsonNode.class);
  }
}
