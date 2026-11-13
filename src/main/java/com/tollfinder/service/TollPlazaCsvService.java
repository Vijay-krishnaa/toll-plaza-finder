
package com.tollfinder.service;

import com.tollfinder.entity.TollPlaza;
import com.tollfinder.repository.TollPlazaRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TollPlazaCsvService implements CommandLineRunner {

  private final TollPlazaRepository tollPlazaRepository;

  public TollPlazaCsvService(TollPlazaRepository tollPlazaRepository) {
    this.tollPlazaRepository = tollPlazaRepository;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {

    // Avoid importing the same data every time the app starts
    if (tollPlazaRepository.count() > 0) {
      System.out.println("Toll plaza data already exists. Skipping import.");
      return;
    }

    Map<String, TollPlaza> uniqueTolls = new LinkedHashMap<>();

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new ClassPathResource("toll_plaza_india.csv")
                .getInputStream(),
            StandardCharsets.UTF_8))) {

      String line;
      boolean header = true;

      while ((line = reader.readLine()) != null) {

        if (header) {
          header = false;
          continue;
        }

        String[] columns = line.split(",", -1);

        if (columns.length < 4) {
          continue;
        }

        try {
          double longitude = Double.parseDouble(columns[0].trim());
          double latitude = Double.parseDouble(columns[1].trim());

          String tollName = columns[2].trim();
          String geoState = columns[3].trim();

          if (tollName.isBlank()) {
            continue;
          }

          String key = tollName.toLowerCase()
              + "|" + latitude + "|" + longitude;

          uniqueTolls.putIfAbsent(
              key,
              new TollPlaza(tollName, latitude, longitude, geoState));

        } catch (NumberFormatException exception) {
          System.out.println("Skipping invalid CSV row: " + line);
        }
      }
    }

    tollPlazaRepository.saveAll(uniqueTolls.values());

    System.out.println("CSV import completed!");
    System.out.println("Unique toll plazas imported: "
        + uniqueTolls.size());
  }
}
