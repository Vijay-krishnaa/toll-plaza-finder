
package com.tollfinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TollPlazaRequest(

    @NotBlank(message = "Source pincode is required") @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Source pincode must be a valid 6-digit Indian pincode") String sourcePincode,

    @NotBlank(message = "Destination pincode is required") @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Destination pincode must be a valid 6-digit Indian pincode") String destinationPincode

) {
}
