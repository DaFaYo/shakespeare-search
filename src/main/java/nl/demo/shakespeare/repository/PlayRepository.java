package nl.demo.shakespeare.repository;

import nl.demo.shakespeare.model.Play;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayRepository extends JpaRepository<Play, Long> {
    List<Play> findByTextContainingIgnoreCase(String keyword);
}
