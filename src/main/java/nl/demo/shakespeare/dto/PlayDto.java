package nl.demo.shakespeare.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayDto {

    private Long id;
    private String title;
    private String text;
}
