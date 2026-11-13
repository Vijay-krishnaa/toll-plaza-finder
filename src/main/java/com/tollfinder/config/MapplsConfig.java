
package com.tollfinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapplsConfig {

  @Value("${mappls.api.key}")
  private String apiKey;

  public String getApiKey() {
    return apiKey;
  }
}
