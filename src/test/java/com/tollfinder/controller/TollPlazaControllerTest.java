
package com.tollfinder.controller;

import com.tollfinder.dto.TollPlazaMatch;
import com.tollfinder.service.TollPlazaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TollPlazaController.class)
@Import(TollPlazaControllerTest.TestCacheConfig.class)
class TollPlazaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TollPlazaService tollPlazaService;

  @TestConfiguration
  static class TestCacheConfig {

    @Bean
    CacheManager cacheManager() {
      return new ConcurrentMapCacheManager("tollPlazas");
    }
  }

  @Test
  void shouldRejectInvalidSourcePincode() throws Exception {

    String request = """
        {
            "sourcePincode": "123",
            "destinationPincode": "411045"
        }
        """;

    mockMvc.perform(post("/api/v1/toll-plazas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("Source pincode must be a valid 6-digit Indian pincode"));

    verifyNoInteractions(tollPlazaService);
  }

  @Test
  void shouldRejectInvalidDestinationPincode() throws Exception {

    String request = """
        {
            "sourcePincode": "560064",
            "destinationPincode": "123"
        }
        """;

    mockMvc.perform(post("/api/v1/toll-plazas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("Destination pincode must be a valid 6-digit Indian pincode"));

    verifyNoInteractions(tollPlazaService);
  }

  @Test
  void shouldRejectMissingSourcePincode() throws Exception {

    String request = """
        {
            "destinationPincode": "411045"
        }
        """;

    mockMvc.perform(post("/api/v1/toll-plazas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("Source pincode is required"));

    verifyNoInteractions(tollPlazaService);
  }

  @Test
  void shouldAcceptValidRequest() throws Exception {

    String request = """
        {
            "sourcePincode": "560064",
            "destinationPincode": "411045"
        }
        """;

    when(tollPlazaService.findTollPlazas(any()))
        .thenReturn(List.<TollPlazaMatch>of());

    mockMvc.perform(post("/api/v1/toll-plazas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(tollPlazaService, times(1)).findTollPlazas(any());
  }
}
