package Outpatient.example.Intership_Backend.Advices;


import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
//add
@Getter
@Setter
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String>subErrors;
}
