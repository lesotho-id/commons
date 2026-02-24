package io.mosip.kernel.emailnotification.controller;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.emailnotification.dto.WhatsappResponseDto;
import io.mosip.kernel.emailnotification.service.WhatsappNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "whatsappnotification", description = "Operation related to whatsapp notification")
public class WhatsappNotificationController {
    @Autowired
    WhatsappNotificationService whatsappNotificationService;

    @ResponseFilter
    @Operation(summary = "Endpoint for sending a whatsapp message", description = "Endpoint for sending a whatsapp message", tags = { "whatsappnotification" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success or you may find errors in error array in response"),
            @ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
    @PostMapping(value = "/whatsapp/send", consumes = "multipart/form-data")
    public ResponseWrapper<WhatsappResponseDto> sendWhatsappNotification(
            String recipient,
            String message,
            MultipartFile [] files) {
        ResponseWrapper<WhatsappResponseDto> responseWrapper = new ResponseWrapper<>();
        responseWrapper.setResponse(whatsappNotificationService.sendWhatsappNotification(recipient,
                message,files));
        return responseWrapper;
    }
}

