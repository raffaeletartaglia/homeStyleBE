package org.example.homestylebe.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorMessageResponse {
	
	private LocalDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
}
