
package com.tollfinder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

  @GetMapping("/")
  public Map<String, String> home() {
    return Map.of(
        "application", "Toll Plaza Finder API",
        "status", "Running",
        "documentation", "/swagger-ui/index.html");
  }
}
