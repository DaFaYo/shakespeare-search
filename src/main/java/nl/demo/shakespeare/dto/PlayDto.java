package nl.demo.shakespeare.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayDto {
    private String title;
    private String text;
}
