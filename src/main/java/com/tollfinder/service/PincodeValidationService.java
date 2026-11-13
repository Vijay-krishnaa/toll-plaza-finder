
package com.tollfinder.service;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tollfinder.exception.PincodeApiException;

@Service
public class PincodeValidationService {

  private static final Logger log = LoggerFactory.getLogger(PincodeValidationService.class);

  private static final Pattern PINCODE_PATTERN = Pattern.compile("^[1-9][0-9]{5}$");

  private final RestClient restClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public PincodeValidationService() {

    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

    factory.setConnectTimeout(Duration.ofSeconds(5));
    factory.setReadTimeout(Duration.ofSeconds(10));

    this.restClient = RestClient.builder()
        .baseUrl("https://api.postalpincode.in")
        .requestFactory(factory)
        .defaultHeader(
            HttpHeaders.ACCEPT,
            MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(
            HttpHeaders.USER_AGENT,
            "tollfinder/1.0")
        .build();
  }

  public boolean isValidPincode(String pincode) {

    if (pincode == null) {
      return false;
    }

    String trimmedPincode = pincode.trim();

    if (!PINCODE_PATTERN.matcher(trimmedPincode).matches()) {
      return false;
    }

    String response;

    try {
      response = restClient.get()
          .uri("/pincode/{pincode}", trimmedPincode)
          .retrieve()
          .body(String.class);

    } catch (RestClientException ex) {

      log.error("Postal API call failed for pincode {}",
          trimmedPincode, ex);

      throw new PincodeApiException(
          "Postal API error: " + ex.getMessage());
    }

    if (response == null || response.isBlank()) {
      throw new PincodeApiException(
          "Empty response from Postal API.");
    }

    try {
      JsonNode root = objectMapper.readTree(response);

      return root.isArray()
          && !root.isEmpty()
          && "Success".equals(
              root.get(0).path("Status").asText());

    } catch (IOException ex) {

      log.error("Could not parse Postal API response for pincode {}",
          trimmedPincode, ex);

      throw new PincodeApiException(
          "Unable to process Postal API response.");
    }
  }
}
