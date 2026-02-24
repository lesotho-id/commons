package io.mosip.kernel.emailnotification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhatsappResponseDto {

    private boolean success;
    private String recipient;
    private String name;
    private String message;
    private Long timestamp;
    private Boolean fromMe;
    private String caption;
    private String file;
    private String messageId;
}
