document.addEventListener("DOMContentLoaded", () => {
  const queryInput = document.getElementById("query");
  const fuzzyCheckbox = document.getElementById("fuzzy");
  const exactCheckbox = document.getElementById("exact");
  const resultsDiv = document.getElementById("results");

  const btnDocs = document.getElementById("searchDocuments");
  const btnDb = document.getElementById("searchDatabase");

  btnDocs.addEventListener("click", () => search("documents"));
  btnDb.addEventListener("click", () => search("database"));

  async function search(type) {
    const q = queryInput.value.trim();
    if (!q) {
      resultsDiv.innerHTML = "<p>⚠️ Voer een zoekterm in.</p>";
      return;
    }

    const fuzzy = fuzzyCheckbox.checked;
    const exact = exactCheckbox.checked;

    let url = `/api/${type}/search?q=${encodeURIComponent(q)}`;
    if (type === "documents") {
      url += `&fuzzy=${fuzzy}&exact=${exact}`;
    }

    // ⏳ Toon een loading-indicator
    resultsDiv.innerHTML = `
      <div class="loading">
        <div class="spinner"></div>
        <p>Bezig met zoeken...</p>
      </div>
    `;

    try {
      const res = await fetch(url);
      if (!res.ok) throw new Error("Serverfout: " + res.status);

      const data = await res.json();
      renderResults(data, type);
    } catch (err) {
      console.error(err);
      resultsDiv.innerHTML = `<p style="color:red">❌ Fout bij ophalen resultaten: ${err.message}</p>`;
    }
  }

  function renderResults(data, type) {
    if (!data || data.length === 0) {
      resultsDiv.innerHTML = "<p>Geen resultaten gevonden.</p>";
      return;
    }

    resultsDiv.innerHTML = data
      .map((item) => {
        if (type === "database") {
          return `
            <div class="result">
              <h3>${item.title}</h3>
              <p>${item.text}</p>
            </div>`;
        } else {
          return `
            <div class="result">
              <h3>${item.title || "(geen titel)"}</h3>
              <p>${item.content || item.text || ""}</p>
            </div>`;
        }
      })
      .join("");
  }
});
