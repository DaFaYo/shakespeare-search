package nl.demo.shakespeare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayDto {

    private Long id;

    @NotBlank(message = "Title mag niet leeg zijn")
    @Size(max = 255, message = "Title mag maximaal 255 tekens bevatten")
    private String title;

    private String text;
}
