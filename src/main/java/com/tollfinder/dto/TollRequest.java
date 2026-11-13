
package com.tollfinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class TollRequest {

  @NotBlank(message = "Source pincode is required")
  @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Source pincode must be a valid 6-digit Indian pincode")
  private String sourcePincode;

  @NotBlank(message = "Destination pincode is required")
  @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Destination pincode must be a valid 6-digit Indian pincode")
  private String destinationPincode;

  public String getSourcePincode() {
    return sourcePincode;
  }

  public void setSourcePincode(String sourcePincode) {
    this.sourcePincode = sourcePincode;
  }

  public String getDestinationPincode() {
    return destinationPincode;
  }

  public void setDestinationPincode(String destinationPincode) {
    this.destinationPincode = destinationPincode;
  }
}
