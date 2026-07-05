package org.example.homestylebe.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Un global exception handler è il punto unico in cui la tua API decide “come” trasformare le eccezioni Java in risposte HTTP standardizzate per il frontend. Serve a non gestire errori dentro ogni controller, ma in un solo posto centrale.
 *
 * Cosa fa nel tuo progetto
 * Con la struttura che hai impostato:
 *
 * Intercetta tutte le eccezioni che estendono BusinessException (quindi tutte le tue custom: EntitaNonTrovataException, ValoreNonValidoException, ecc.).
 * ​
 *
 * Legge da ciascuna eccezione il relativo ErroreCodice, che contiene:
 *
 * il codice simbolico (es. CATEGORIA_NON_TROVATA),
 *
 * il messaggio di default,
 *
 * l’HttpStatus da usare.
 *
 * Costruisce un ApiErrorResponse con:
 *
 * codiceErrore (per il frontend),
 *
 * messaggio (testo leggibile),
 *
 * timestamp,
 *
 * path (URL chiamato).
 *
 * Restituisce un ResponseEntity<ApiErrorResponse> con lo status corretto (404, 400, 409, 500, …).
 *
 * In pratica, qualunque service lanci:
 *
 * java
 * throw new EntitaNonTrovataException(ErroreCodice.CATEGORIA_NON_TROVATA);
 * il global handler:
 *
 * prende l’eccezione,
 *
 * vede ErroreCodice.CATEGORIA_NON_TROVATA → status 404,
 *
 * manda al client JSON tipo:
 *
 * json
 * {
 *   "codiceErrore": "CATEGORIA_NON_TROVATA",
 *   "messaggio": "Categoria non trovata",
 *   "timestamp": "2026-03-15T23:42:00",
 *   "path": "/api/v1/categorie/123"
 * }
 * senza che tu debba gestire l’errore nel controller.
 *
 * Come è fatto tecnicamente
 * Annotazione di classe: @RestControllerAdvice
 *
 * Dice a Spring: “questa classe ascolta tutte le eccezioni dei controller REST”.
 *
 * Metodi annotati con @ExceptionHandler:
 *
 * uno per BusinessException (le tue eccezioni applicative),
 *
 * uno “catch-all” per Exception (errori imprevisti).
 *
 * Perché è utile
 * Coerenza: tutte le API restituiscono errori con la stessa struttura JSON (anche se l’errore nasce in service diversi).
 *
 * Meno duplicazione: niente try/catch nei controller; lanci solo l’eccezione giusta nel service.
 *
 * Chiarezza per il frontend: i codici (CATEGORIA_NON_TROVATA, ORDINE_GIA_ANNULLATO, ecc.) sono stabili e facilmente mappabili lato client.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        ErroreCodice erroreCodice = ex.getErroreCodice();

        log.error("BusinessException - codice: {}, messaggio: {}", erroreCodice.getCodice(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .codiceErrore(erroreCodice.getCodice())
                .messaggio(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(erroreCodice.getHttpStatus())
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Errore non gestito", ex);

        ErroreCodice erroreCodice = ErroreCodice.ERRORE_INTERNO;

        ApiErrorResponse body = ApiErrorResponse.builder()
                .codiceErrore(erroreCodice.getCodice())
                .messaggio(erroreCodice.getMessaggioDiDefault())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(erroreCodice.getHttpStatus())
                .body(body);
    }
}
