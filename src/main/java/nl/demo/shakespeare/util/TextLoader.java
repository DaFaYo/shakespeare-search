package nl.demo.shakespeare.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.model.ShakespeareDocument;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class TextLoader implements CommandLineRunner {

    private final ElasticsearchClient client;

    @Override
    public void run(String... args) throws Exception {
        Path dir = Paths.get("../data/shakespeare");
        if (!Files.exists(dir)) {
            log.warn("Shakespeare data directory does not exist: {}", dir.toAbsolutePath());
            return;
        }

        AtomicInteger counter = new AtomicInteger();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
            for (Path path : stream) {
                String content = Files.readString(path);
                ShakespeareDocument doc = new ShakespeareDocument();
                doc.setId(path.getFileName().toString());
                doc.setTitle(path.getFileName().toString().replace(".txt", ""));
                doc.setText(content);
                doc.setLineNumber(counter.incrementAndGet());

                client.index(i -> i
                        .index("shakespeare")
                        .id(doc.getId())
                        .document(doc)
                );

                log.info("Indexed document: {} ({} characters)", doc.getTitle(), content.length());
            }
        } catch (IOException e) {
            log.error("Error loading Shakespeare texts", e);
        }

        log.info("Finished indexing {} documents", counter.get());
    }
}
