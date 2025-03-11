// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice
// public class GlobalExceptionHandler {
//     @ExceptionHandler(value = TokenRefreshException.class)
//     public ResponseEntity<?> handleTokenRefreshException(TokenRefreshException ex) {
//         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
//     }
    
//     @ExceptionHandler(value = AccessDeniedException.class)
//     public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
//         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
//     }
// }