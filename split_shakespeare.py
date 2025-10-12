import os
import re
import requests

# Download de complete werken van Shakespeare (Project Gutenberg)
URL = "https://www.gutenberg.org/files/100/100-0.txt"
OUTPUT_DIR = "../data/shakespeare"

os.makedirs(OUTPUT_DIR, exist_ok=True)
print("📥 Downloaden van Shakespeare's complete werken...")
text = requests.get(URL).text

# Herken titels: beginnen met THE of A, volledig in hoofdletters
title_pattern = re.compile(
    r"^(THE|A)\s+[A-Z ,'\-\n]+(?:OF\s+[A-Z ,'\-\n]+)?$",
    re.MULTILINE
)

matches = [m for m in title_pattern.finditer(text) if 10 < len(m.group(0)) < 100]

print(f"📖 Gevonden toneelstukken/gedichten: {len(matches)}")

for i, match in enumerate(matches):
    start = match.start()
    end = matches[i + 1].start() if i + 1 < len(matches) else len(text)

    title = match.group(0).strip().replace("\n", " ")
    safe_title = re.sub(r"[^A-Za-z0-9]+", "_", title)
    filename = safe_title[:80] + ".txt"  # max 80 tekens

    piece = text[start:end].strip()
    if len(piece) < 2000:
        # Te korte stukken overslaan
        continue

    with open(os.path.join(OUTPUT_DIR, filename), "w", encoding="utf-8") as f:
        f.write(piece)

    print(f"✅ {filename}")

print(f"\n📚 Klaar! Toneelstukken staan in {OUTPUT_DIR}")
