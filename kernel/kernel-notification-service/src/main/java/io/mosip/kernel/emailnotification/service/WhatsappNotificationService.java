package io.mosip.kernel.emailnotification.service;

import io.mosip.kernel.emailnotification.dto.WhatsappResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface WhatsappNotificationService {

    WhatsappResponseDto sendWhatsappNotification(String recipient, String message, MultipartFile[] files);
}



