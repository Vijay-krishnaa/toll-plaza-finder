
package com.tollfinder.service;

import com.tollfinder.dto.TollPlazaMatch;
import com.tollfinder.entity.TollPlaza;
import com.tollfinder.repository.TollPlazaRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TollPlazaRouteMatcher {

  private static final double EARTH_RADIUS_KM = 6371.0;
  private static final double ROUTE_CORRIDOR_KM = 1.0;
  private static final double DUPLICATE_DISTANCE_KM = 0.5;

  private final TollPlazaRepository tollPlazaRepository;

  public TollPlazaRouteMatcher(TollPlazaRepository tollPlazaRepository) {
    this.tollPlazaRepository = tollPlazaRepository;
  }

  public List<TollPlazaMatch> findTollsAlongRoute(JsonNode routeResponse) {

    JsonNode routes = routeResponse.path("routes");

    if (!routes.isArray() || routes.isEmpty()) {
      throw new IllegalArgumentException("No route found");
    }

    JsonNode coordinates = routes.get(0)
        .path("geometry")
        .path("coordinates");

    if (!coordinates.isArray() || coordinates.size() < 2) {
      throw new IllegalArgumentException("Route geometry is missing");
    }

    List<double[]> routePoints = new ArrayList<>();

    for (JsonNode point : coordinates) {
      routePoints.add(new double[] {
          point.get(0).asDouble(),
          point.get(1).asDouble()
      });
    }

    List<TollPlazaMatch> matches = new ArrayList<>();

    List<TollPlaza> tollPlazas = tollPlazaRepository.findAll();

    // Store already matched tolls to avoid duplicates.
    Map<String, List<TollPlazaMatch>> seenTolls = new HashMap<>();

    for (TollPlaza toll : tollPlazas) {

      if (toll.getLatitude() == null ||
          toll.getLongitude() == null ||
          toll.getTollName() == null ||
          toll.getTollName().isBlank()) {
        continue;
      }

      double bestDistance = Double.MAX_VALUE;
      double distanceFromSource = 0;
      double cumulativeDistance = 0;

      for (int i = 0; i < routePoints.size() - 1; i++) {

        double[] start = routePoints.get(i);
        double[] end = routePoints.get(i + 1);

        double segmentLength = haversine(
            start[1], start[0],
            end[1], end[0]);

        double[] projection = projectPoint(
            toll.getLatitude(),
            toll.getLongitude(),
            start,
            end);

        double distance = haversine(
            toll.getLatitude(),
            toll.getLongitude(),
            projection[1],
            projection[0]);

        if (distance < bestDistance) {
          bestDistance = distance;

          distanceFromSource = cumulativeDistance
              + (projection[2] * segmentLength);
        }

        cumulativeDistance += segmentLength;
      }

      if (bestDistance <= ROUTE_CORRIDOR_KM) {

        TollPlazaMatch match = new TollPlazaMatch(
            toll.getTollName(),
            toll.getLatitude(),
            toll.getLongitude(),
            Math.round(distanceFromSource * 100.0) / 100.0,
            Math.round(bestDistance * 1000.0) / 1000.0);

        String normalizedName = normalizeName(toll.getTollName());

        List<TollPlazaMatch> sameNameTolls = seenTolls.computeIfAbsent(
            normalizedName,
            key -> new ArrayList<>());

        boolean duplicate = false;

        for (TollPlazaMatch existing : sameNameTolls) {

          double distance = haversine(
              match.latitude(),
              match.longitude(),
              existing.latitude(),
              existing.longitude());

          if (distance <= DUPLICATE_DISTANCE_KM) {
            duplicate = true;
            break;
          }
        }

        if (!duplicate) {
          sameNameTolls.add(match);
          matches.add(match);
        }
      }
    }

    matches.sort(
        Comparator.comparingDouble(
            TollPlazaMatch::distanceFromSourceKm));

    return matches;
  }

  // Removes spaces, hyphens and special characters from toll names.
  private String normalizeName(String name) {
    return name.toLowerCase().replaceAll("[^a-z0-9]", "");
  }

  // Finds the nearest point on a route segment.
  // Returns longitude, latitude and position (0 to 1).
  private double[] projectPoint(
      double pointLat,
      double pointLon,
      double[] start,
      double[] end) {

    double averageLat = Math.toRadians(
        (start[1] + end[1]) / 2.0);

    double scaleX = Math.cos(averageLat);

    double ax = start[0] * scaleX;
    double ay = start[1];

    double bx = end[0] * scaleX;
    double by = end[1];

    double px = pointLon * scaleX;
    double py = pointLat;

    double dx = bx - ax;
    double dy = by - ay;

    double lengthSquared = dx * dx + dy * dy;

    double t = 0;

    if (lengthSquared > 0) {
      t = ((px - ax) * dx + (py - ay) * dy)
          / lengthSquared;
    }

    t = Math.max(0, Math.min(1, t));

    double longitude = start[0]
        + t * (end[0] - start[0]);

    double latitude = start[1]
        + t * (end[1] - start[1]);

    return new double[] { longitude, latitude, t };
  }

  // Calculates distance between two geographic coordinates.
  private double haversine(
      double lat1,
      double lon1,
      double lat2,
      double lon2) {

    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
        + Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);

    double c = 2 * Math.atan2(
        Math.sqrt(a),
        Math.sqrt(1 - a));

    return EARTH_RADIUS_KM * c;
  }
}
