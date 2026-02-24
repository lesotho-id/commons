package io.mosip.kernel.emailnotification.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WhatsappRequestDto {

    @NotBlank
    private String recipient;

    @NotBlank
    private String message;

}
