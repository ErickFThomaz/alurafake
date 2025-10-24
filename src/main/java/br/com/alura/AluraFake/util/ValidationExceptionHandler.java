package br.com.alura.AluraFake.util;

import br.com.alura.AluraFake.util.exception.BadRequestException;
import br.com.alura.AluraFake.util.exception.ConflictException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<ErrorItemDTO>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorItemDTO> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorItemDTO::new)
                .toList();

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessageDTO> handleNotFoundException(ResourceNotFoundException ex) {
        ErrorMessageDTO messageDTO = new ErrorMessageDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageDTO);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessageDTO> handleBadRequestException(BadRequestException ex) {
        ErrorMessageDTO messageDTO = new ErrorMessageDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDTO);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessageDTO> handleConflictException(ConflictException ex) {
        ErrorMessageDTO messageDTO = new ErrorMessageDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageDTO);
    }
}
