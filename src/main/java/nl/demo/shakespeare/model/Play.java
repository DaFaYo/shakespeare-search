package nl.demo.shakespeare.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plays")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Play {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;
}
