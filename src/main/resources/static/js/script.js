async function doSearch(event, source) {
    event.preventDefault();

    const query = document.querySelector('#query').value.trim();
    const fuzzy = document.querySelector('#fuzzy').checked;
    const exact = document.querySelector('#exact').checked;
    const resultsContainer = document.querySelector('#results');

    if (!query) {
        resultsContainer.innerHTML = '<p>Voer een zoekterm in.</p>';
        return;
    }

    resultsContainer.innerHTML = '<p>Loading...</p>';

    let endpoint;
    if (source === 'documents') {
        endpoint = `/api/documents/search?q=${encodeURIComponent(query)}&fuzzy=${fuzzy}&exact=${exact}`;
    } else if (source === 'database') {
        endpoint = `/api/database/search?q=${encodeURIComponent(query)}&fuzzy=${fuzzy}&exact=${exact}`;
    } else {
        console.error('Onbekende zoekbron');
        return;
    }

    try {
        const response = await fetch(endpoint);
        const results = await response.json();

        if (results.length === 0) {
            resultsContainer.innerHTML = '<p>Geen resultaten gevonden.</p>';
            return;
        }

        resultsContainer.innerHTML = results.map(r => `
            <div class="result">
                <h2>${r.title}</h2>
                ${r.occurrences !== undefined ? `<p><strong>Aantal voorkomens:</strong> ${r.occurrences}</p>` : ''}
                ${r.snippets ? r.snippets.map(s => `<p>${s}</p>`).join('')
                              : (r.highlightedTextSnippets ? r.highlightedTextSnippets.map(s => `<p>${s}</p>`).join('') : '')}
            </div>
        `).join('');

    } catch (error) {
        console.error('Search failed:', error);
        resultsContainer.innerHTML = '<p>Er is een fout opgetreden tijdens het zoeken.</p>';
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelector('#searchDocuments').addEventListener('click', e => doSearch(e, 'documents'));
    document.querySelector('#searchDatabase').addEventListener('click', e => doSearch(e, 'database'));
});
