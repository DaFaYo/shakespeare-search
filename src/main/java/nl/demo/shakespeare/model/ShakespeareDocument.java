package nl.demo.shakespeare.model;

import lombok.Data;


@Data
public class ShakespeareDocument {
    private String id;       // uniek ID (ES document ID)
    private String title;    // titel van toneelstuk
    private String text;     // volledige tekst of fragment
    private int lineNumber;  // optioneel: regelnummer
}