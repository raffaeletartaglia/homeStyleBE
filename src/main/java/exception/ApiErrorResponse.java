package exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorResponse {
    private String codiceErrore;
    private String messaggio;
    private LocalDateTime timestamp;
    private String path;
}
