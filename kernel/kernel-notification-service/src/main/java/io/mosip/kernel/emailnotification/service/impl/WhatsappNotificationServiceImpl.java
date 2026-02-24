package io.mosip.kernel.emailnotification.service.impl;

import io.mosip.kernel.core.notification.exception.InvalidNumberException;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.emailnotification.dto.WhatsappResponseDto;
import io.mosip.kernel.emailnotification.dto.WhatsappRequestDto;
import io.mosip.kernel.emailnotification.service.WhatsappNotificationService;
import io.mosip.kernel.emailnotification.util.MultipartInputStreamFileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Service
public class WhatsappNotificationServiceImpl implements WhatsappNotificationService {
    Logger LOGGER = LoggerFactory.getLogger(WhatsappNotificationServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${mosip.kernel.whatsapp.api-key:7d86f4333c232a15d4f90cfbaf0e0e8b1f9dea409b10f449abeef3948786c860}")
    private String apiKey;

    @Value("${mosip.kernel.whatsapp.message-url:url}")
    private String url;

    @Value("${mosip.kernel.whatsapp.country.code:91}")
    private String countryCode;

    @Value("${mosip.kernel.whatsapp.number.length:10}")
    int numberLength;

    @Override
    public WhatsappResponseDto sendWhatsappNotification(String recipient, String message, MultipartFile [] files) {
        LOGGER.info("whatsapp number: " +recipient);
        LOGGER.info("whatsapp message: "+message);
        validateInput(recipient);
        try {
            String whatsappMobileNo = recipient.startsWith(countryCode) ? recipient : countryCode + recipient;
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("x-api-key", apiKey);
            ResponseEntity<WhatsappResponseDto> response;
            LOGGER.info("Files received count: {}", (files == null ? "null" : files.length));
            if (files != null && files.length>0) {
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("recipient", whatsappMobileNo);
                body.add("caption", message != null ? message : "");

                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        try {
                            body.add("file", new MultipartInputStreamFileResource(file));
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to process file", e);
                        }
                    }
                }
                HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
                response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        WhatsappResponseDto.class
                );
            } else {
                headers.setContentType(MediaType.APPLICATION_JSON);
                WhatsappRequestDto request = new WhatsappRequestDto();
                request.setRecipient(whatsappMobileNo);
                request.setMessage(message);
                HttpEntity<WhatsappRequestDto> entity =
                        new HttpEntity<>(request, headers);
                response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        WhatsappResponseDto.class
                );
            }
            WhatsappResponseDto body = response.getBody();

            if (body != null && body.getMessage() == null) {
                body.setMessage(body.getCaption());
            }
            return body;

        }catch (HttpClientErrorException | HttpServerErrorException ex) {
            WhatsappResponseDto error = new WhatsappResponseDto();
            error.setSuccess(false);
            error.setRecipient(recipient);
            error.setMessage(ex.getResponseBodyAsString());
            return error;

        } catch (Exception ex) {
            WhatsappResponseDto error = new WhatsappResponseDto();
            error.setSuccess(false);
            error.setRecipient(recipient);
            error.setMessage(ex.getMessage());
            return error;
        }
    }
    private void validateInput(String contactNumber) {
        if (contactNumber == null ||
                !StringUtils.isNumeric(contactNumber) ||
                contactNumber.length() != numberLength) {

            throw new InvalidNumberException(
                    "INVALID_WHATSAPP_NUMBER",
                    "Invalid whatsapp number"
            );
        }
    }
}

