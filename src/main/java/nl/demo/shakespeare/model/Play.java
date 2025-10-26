package nl.demo.shakespeare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "plays", uniqueConstraints = {@UniqueConstraint(columnNames = "title")})
@Builder
public class Play {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, unique = true)
    private String title;

    @Lob
    @Basic(fetch = FetchType.LAZY) // voorkomt dat hele tekst altijd geladen wordt
    @Column(nullable = false)
    private String text;
}
