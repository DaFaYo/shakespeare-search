package nl.demo.shakespeare.repository;

import nl.demo.shakespeare.model.Play;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayRepository extends JpaRepository<Play, Long> {



    @Query(
            value = "SELECT * FROM plays WHERE LOWER(text) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true
    )
    List<Play> searchByText(@Param("keyword") String keyword);
    Optional<Play> findByTitle(String title);
}
