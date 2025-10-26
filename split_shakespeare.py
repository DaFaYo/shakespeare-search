#!/usr/bin/env python3
"""
split_shakespeare.py

Verwacht: het complete Gutenberg-bestand (100-0.txt).
Werking:
 - Lees hele tekst
 - Vind de "Contents" sectie en bouw een lijst van titels (1 titel per regel)
 - Loop vanaf het eerste voorkomen van een titel door de tekst regels
 - Iedere keer dat een regel exact overeenkomt met een titel begin je een nieuw stuk
 - Sla alle stukken op als losse bestanden in OUTPUT_DIR (map wordt gemaakt indien nodig)
"""

import os
import requests

URL = "https://www.gutenberg.org/files/100/100-0.txt"
OUTPUT_DIR = "../data/shakespeare"

def download_text(url: str) -> str:
    print("📥 Downloaden...")
    r = requests.get(url)
    r.raise_for_status()
    return r.text

def extract_titles_from_contents(full_text: str):
    lines = full_text.splitlines()
    # Vind index van de regel met "Contents" (eerste voorkomen, case-insensitive)
    contents_idx = None
    for i, line in enumerate(lines):
        if line.strip().lower() == "contents":
            contents_idx = i
            break
    if contents_idx is None:
        raise RuntimeError("Kon 'Contents' niet vinden in tekst")

    # Verzamel titels: regels na 'Contents' totdat we twee opeenvolgende lege regels tegenkomen
    titles = []
    blank_count = 0
    for j in range(contents_idx + 1, len(lines)):
        line = lines[j].strip()
        if line == "":
            blank_count += 1
            # stop zodra we twee lege regels achter elkaar hebben (einde van de contentslijst)
            if blank_count >= 2:
                break
            else:
                continue
        else:
            blank_count = 0

        # filter: alleen plausibele titelregels opnemen (niet te kort, bevat letters)
        if len(line) >= 3 and any(ch.isalpha() for ch in line):
            titles.append(line)

    # verwijder lege of overbodige items en dupes, behoud volgorde
    seen = set()
    clean_titles = []
    for t in titles:
        tt = t.strip()
        if tt and tt not in seen:
            clean_titles.append(tt)
            seen.add(tt)

    return clean_titles

def build_title_sections(full_text: str, titles: list):
    """
    Itereer over de tekstregels vanaf het eerste voorkomen van een titel.
    Elke keer dat een regel exact voorkomt in 'titles' start je een nieuwe buffer.
    Return: dict title -> text (incl. titelregel)
    """
    lines = full_text.splitlines()
    # vind eerste index waar een titel voorkomt (zodat we niet beginnen met header/voorwoord)
    first_idx = None
    title_set = set(titles)
    for i, line in enumerate(lines):
        if line.strip() in title_set:
            first_idx = i
            break
    if first_idx is None:
        raise RuntimeError("Geen van de titles gevonden in de volledige tekst.")

    sections = {}
    current_title = None
    buffer_lines = []

    for line in lines[first_idx:]:
        stripped = line.strip()
        if stripped in title_set:
            # als we al in een sectie zaten, commit die
            if current_title is not None:
                sections[current_title] = "\n".join(buffer_lines).strip()
            # start nieuwe sectie
            current_title = stripped
            buffer_lines = [line]  # bewaar ook de titelregel zelf
        else:
            if current_title is not None:
                buffer_lines.append(line)

    # commit de laatste sectie
    if current_title is not None and current_title not in sections:
        sections[current_title] = "\n".join(buffer_lines).strip()

    return sections

def safe_filename(title: str, max_len=80):
    # Vervang niet-alfanumerieke tekens door underscores en beperk lengte
    name = "".join(ch if ch.isalnum() else "_" for ch in title)
    name = name.strip("_")
    if len(name) == 0:
        name = "untitled"
    return name[:max_len] + ".txt"

def write_sections_to_files(sections: dict, out_dir: str):
    os.makedirs(out_dir, exist_ok=True)
    count = 0
    for title, content in sections.items():
        # optioneel: skip heel korte stukken
        if len(content) < 200:
            print(f"⏭ Overslaan (te kort): {title}")
            continue
        fname = safe_filename(title)
        path = os.path.join(out_dir, fname)
        with open(path, "w", encoding="utf-8") as f:
            f.write(content)
        count += 1
        print(f"✅ Geschreven: {fname} ({len(content)} tekens)")
    print(f"\n📚 Klaar — {count} bestanden geschreven in {out_dir}")

def main():
    full_text = download_text(URL)
    print("🔎 Extract titles from Contents...")
    titles = extract_titles_from_contents(full_text)
    print(f"Found {len(titles)} titles (example): {titles[:10]}")

    print("🔎 Building sections from titles...")
    sections = build_title_sections(full_text, titles)
    print(f"Built {len(sections)} sections. Writing to files...")
    write_sections_to_files(sections, OUTPUT_DIR)

if __name__ == "__main__":
    main()
