package nl.demo.shakespeare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayUpdateDto {

    private Long id;

    @NotBlank(message = "Title mag niet leeg zijn")
    @Size(max = 255, message = "Title mag maximaal 255 tekens bevatten")
    private String title;

    @NotBlank(message = "Text mag niet leeg zijn")
    private String text;
}
