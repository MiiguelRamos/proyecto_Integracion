package aiss.dailymotionminer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;


 // Manejador centralizado de excepciones de DailymotionMiner.

 // Traduce los errores de las llamadas HTTP salientes (API de Dailymotion y VideoMiner) en respuestas HTTP significativas para el cliente de este adaptador

@ControllerAdvice
public class GlobalExceptionHandler {


     // El recurso solicitado a Dailymotion no existe.
     // Devuelve 404 Not Found.

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleNotFound(HttpClientErrorException.NotFound ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Resource not found in Dailymotion: " + ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


     // Cualquier otro 4xx procedente de una llamada saliente (por ejemplo, 400, 401, 403).
     // Reenvía el código de estado original para que el cliente reciba una respuesta significativa.

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleClientError(HttpClientErrorException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Client error when calling external service: " + ex.getMessage());
        return new ResponseEntity<>(body, ex.getStatusCode());
    }


     // Dailymotion (o VideoMiner) devolvió un 5xx — el servicio externo está caído o roto.
     // Desde el punto de vista del cliente, esto es un Bad Gateway (502).

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleServerError(HttpServerErrorException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Upstream service error: " + ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_GATEWAY);
    }


     // El servicio externo no es accesible (conexión rechazada, fallo de DNS, timeout).
     // Se mapea a 503 Service Unavailable.

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleResourceAccess(ResourceAccessException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Cannot reach external service: " + ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
