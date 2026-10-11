// Frontend logic for Graduation Admission Predictor

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('predictionForm');
    const submitBtn = document.getElementById('submitBtn');

    // Input fields
    const greSlider = document.getElementById('greSlider');
    const greInput = document.getElementById('greInput');
    const greDisplay = document.getElementById('greDisplay');

    const toeflSlider = document.getElementById('toeflSlider');
    const toeflInput = document.getElementById('toeflInput');
    const toeflDisplay = document.getElementById('toeflDisplay');

    const cgpaSlider = document.getElementById('cgpaSlider');
    const cgpaInput = document.getElementById('cgpaInput');
    const cgpaDisplay = document.getElementById('cgpaDisplay');

    const researchCheckbox = document.getElementById('researchCheckbox');
    const researchStatusText = document.getElementById('researchStatusText');
    const regionCheckboxes = document.querySelectorAll('input[name="regions"]');
    const selectAllRegionsBtn = document.getElementById('selectAllRegionsBtn');
    const clearRegionsBtn = document.getElementById('clearRegionsBtn');

    const samplePreset = document.getElementById('samplePreset');
    const quickTestBtn = document.getElementById('quickTestBtn');

    const validationAlert = document.getElementById('validationAlert');
    const alertMessage = document.getElementById('alertMessage');
    const closeAlertBtn = document.getElementById('closeAlertBtn');

    const resultsEmptyState = document.getElementById('resultsEmptyState');
    const resultsLoadingState = document.getElementById('resultsLoadingState');
    const resultsList = document.getElementById('resultsList');
    const resultsCountBadge = document.getElementById('resultsCountBadge');
    const resultsSubtext = document.getElementById('resultsSubtext');

    const historyTableBody = document.getElementById('historyTableBody');
    const refreshHistoryBtn = document.getElementById('refreshHistoryBtn');

    // Sync sliders and number fields
    function syncGre(val) {
        val = Math.max(260, Math.min(340, parseInt(val, 10) || 260));
        greSlider.value = val;
        greInput.value = val;
        greDisplay.textContent = val;
    }

    function syncToefl(val) {
        val = Math.max(0, Math.min(120, parseInt(val, 10) || 0));
        toeflSlider.value = val;
        toeflInput.value = val;
        toeflDisplay.textContent = val;
    }

    function syncCgpa(val) {
        val = Math.max(0.0, Math.min(10.0, parseFloat(val) || 0.0));
        val = Math.round(val * 100) / 100;
        cgpaSlider.value = val;
        cgpaInput.value = val.toFixed(2);
        cgpaDisplay.textContent = val.toFixed(2);
    }

    greSlider.addEventListener('input', (e) => syncGre(e.target.value));
    greInput.addEventListener('input', (e) => syncGre(e.target.value));

    toeflSlider.addEventListener('input', (e) => syncToefl(e.target.value));
    toeflInput.addEventListener('input', (e) => syncToefl(e.target.value));

    cgpaSlider.addEventListener('input', (e) => syncCgpa(e.target.value));
    cgpaInput.addEventListener('input', (e) => syncCgpa(e.target.value));

    syncGre(greInput.value);
    syncToefl(toeflInput.value);
    syncCgpa(cgpaInput.value);

    // Research toggle text
    researchCheckbox.addEventListener('change', () => {
        researchStatusText.textContent = researchCheckbox.checked
            ? 'Applicant has research or lab experience'
            : 'No prior research experience';
    });

    // Region shortcuts
    selectAllRegionsBtn.addEventListener('click', () => {
        regionCheckboxes.forEach(cb => cb.checked = true);
    });

    clearRegionsBtn.addEventListener('click', () => {
        regionCheckboxes.forEach(cb => cb.checked = false);
    });

    // Sample presets
    const PRESETS = {
        competitive: {
            gre: 328, toefl: 112, cgpa: 9.15, research: true,
            regions: ['Western Europe', 'Northern Europe', 'Central Europe'],
            predictor: 'weighted'
        },
        balanced: {
            gre: 312, toefl: 98, cgpa: 7.90, research: false,
            regions: ['Western Europe', 'Central Europe', 'Southern Europe'],
            predictor: 'weighted'
        },
        borderline: {
            gre: 298, toefl: 85, cgpa: 7.10, research: true,
            regions: ['Southern Europe', 'Eastern Europe', 'Central Europe'],
            predictor: 'strict'
        }
    };

    function applyPreset(key) {
        const p = PRESETS[key];
        if (!p) return;
        syncGre(p.gre);
        syncToefl(p.toefl);
        syncCgpa(p.cgpa);
        researchCheckbox.checked = p.research;
        researchCheckbox.dispatchEvent(new Event('change'));

        regionCheckboxes.forEach(cb => {
            cb.checked = p.regions.includes(cb.value);
        });

        const radio = document.querySelector(`input[name="predictor"][value="${p.predictor}"]`);
        if (radio) radio.checked = true;
    }

    samplePreset.addEventListener('change', (e) => {
        if (e.target.value) {
            applyPreset(e.target.value);
        }
    });

    if (quickTestBtn) {
        quickTestBtn.addEventListener('click', () => {
            form.dispatchEvent(new Event('submit'));
        });
    }

    // Alerts
    function showAlert(msg) {
        alertMessage.textContent = msg;
        validationAlert.classList.remove('hidden');
    }

    function hideAlert() {
        validationAlert.classList.add('hidden');
    }

    closeAlertBtn.addEventListener('click', hideAlert);

    // Validation
    function validateInputs() {
        const gre = parseInt(greInput.value, 10);
        const toefl = parseInt(toeflInput.value, 10);
        const cgpa = parseFloat(cgpaInput.value);

        if (isNaN(gre) || gre < 260 || gre > 340) {
            showAlert('GRE score must be between 260 and 340.');
            greInput.focus();
            return false;
        }

        if (isNaN(toefl) || toefl < 0 || toefl > 120) {
            showAlert('TOEFL score must be between 0 and 120.');
            toeflInput.focus();
            return false;
        }

        if (isNaN(cgpa) || cgpa < 0.0 || cgpa > 10.0) {
            showAlert('CGPA must be between 0.0 and 10.0.');
            cgpaInput.focus();
            return false;
        }

        const selectedRegions = Array.from(regionCheckboxes)
            .filter(cb => cb.checked)
            .map(cb => cb.value);

        if (selectedRegions.length === 0) {
            showAlert('Please select at least one preferred region.');
            return false;
        }

        hideAlert();
        return {
            gre,
            toefl,
            cgpa,
            research: researchCheckbox.checked,
            regions: selectedRegions,
            predictor: document.querySelector('input[name="predictor"]:checked')?.value || 'weighted'
        };
    }

    // Form submission
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = validateInputs();
        if (!payload) return;

        resultsEmptyState.classList.add('hidden');
        resultsList.classList.add('hidden');
        resultsLoadingState.classList.remove('hidden');
        submitBtn.disabled = true;

        try {
            const response = await fetch('/api/predict', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.error || 'Prediction failed');
            }

            renderResults(data, payload);
            loadHistory();
        } catch (err) {
            showAlert(err.message);
            resultsLoadingState.classList.add('hidden');
            resultsEmptyState.classList.remove('hidden');
        } finally {
            submitBtn.disabled = false;
        }
    });

    // Render result cards
    function renderResults(results, payload) {
        resultsLoadingState.classList.add('hidden');

        if (!results || results.length === 0) {
            resultsList.innerHTML = `
                <div class="empty-state">
                    <p class="empty-title">No universities found</p>
                    <p class="empty-desc">No universities match the selected regions (${payload.regions.join(', ')}).</p>
                </div>
            `;
            resultsList.classList.remove('hidden');
            resultsCountBadge.classList.add('hidden');
            return;
        }

        resultsCountBadge.textContent = `${results.length} Results`;
        resultsCountBadge.classList.remove('hidden');
        resultsSubtext.textContent = `Sorted by highest chance using ${payload.predictor === 'strict' ? 'Strict Minimums' : 'Weighted Score'}.`;

        resultsList.innerHTML = '';
        results.forEach((item, index) => {
            const rank = index + 1;
            const uni = item.university;
            const chance = item.chancePercentage;
            const note = item.weakestFactorNote;

            let chanceClass = 'chance-low';
            let progressClass = 'progress-low';
            if (chance >= 75) {
                chanceClass = 'chance-high';
                progressClass = 'progress-high';
            } else if (chance >= 60) {
                chanceClass = 'chance-good';
                progressClass = 'progress-good';
            } else if (chance >= 40) {
                chanceClass = 'chance-mid';
                progressClass = 'progress-mid';
            }

            const card = document.createElement('div');
            card.className = 'result-card';
            card.innerHTML = `
                <div class="result-card-top">
                    <div>
                        <span class="rank-badge">#${rank}</span>
                        <h3 class="uni-name">${escapeHtml(uni.name)}</h3>
                        <div class="uni-tags">
                            <span>Country: ${escapeHtml(uni.country)}</span>
                            <span>Region: ${escapeHtml(uni.regionName || uni.region || '')}</span>
                        </div>
                    </div>
                    <div>
                        <span class="chance-number ${chanceClass}">${chance.toFixed(1)}%</span>
                        <span class="chance-label">Chance</span>
                    </div>
                </div>

                <div class="progress-bar-container">
                    <div class="progress-bar-fill ${progressClass}" style="width: ${Math.min(100, Math.max(5, chance))}%;"></div>
                </div>

                <div class="thresholds-row">
                    <span>Min GRE: ${uni.minGre}</span>
                    <span>Min TOEFL: ${uni.minToefl}</span>
                    <span>Min CGPA: ${uni.minCgpa.toFixed(1)}</span>
                </div>

                <div class="weakest-factor-box">
                    <span><strong>Note:</strong> ${escapeHtml(note)}</span>
                </div>
            `;

            resultsList.appendChild(card);
        });

        resultsList.classList.remove('hidden');
    }

    // Load history from SQLite
    async function loadHistory() {
        try {
            const response = await fetch('/api/history');
            if (!response.ok) throw new Error('Failed to load history');
            const history = await response.json();
            renderHistory(history);
        } catch (err) {
            console.warn('Could not load history:', err);
            historyTableBody.innerHTML = `
                <tr>
                    <td colspan="9" class="table-empty-cell">Could not load prediction history.</td>
                </tr>
            `;
        }
    }

    function renderHistory(records) {
        if (!records || records.length === 0) {
            historyTableBody.innerHTML = `
                <tr>
                    <td colspan="9" class="table-empty-cell">No predictions yet. Submit a prediction above.</td>
                </tr>
            `;
            return;
        }

        historyTableBody.innerHTML = '';
        records.forEach((rec) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>#${rec.id}</td>
                <td>${formatDate(rec.createdAt)}</td>
                <td>${rec.gre}</td>
                <td>${rec.toefl}</td>
                <td>${rec.cgpa.toFixed(2)}</td>
                <td>${rec.research ? 'Yes' : 'No'}</td>
                <td title="${escapeHtml(rec.regions)}">${truncate(rec.regions, 28)}</td>
                <td>${escapeHtml(rec.topResult)}</td>
                <td>
                    <button type="button" class="rerun-btn" data-record='${JSON.stringify(rec).replace(/'/g, "&apos;")}'>
                        Re-run
                    </button>
                </td>
            `;
            historyTableBody.appendChild(tr);
        });

        document.querySelectorAll('.rerun-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                try {
                    const record = JSON.parse(e.currentTarget.getAttribute('data-record'));
                    reRunPrediction(record);
                } catch (err) {
                    console.error('Error re-running record:', err);
                }
            });
        });
    }

    function reRunPrediction(rec) {
        syncGre(rec.gre);
        syncToefl(rec.toefl);
        syncCgpa(rec.cgpa);
        researchCheckbox.checked = rec.research;
        researchCheckbox.dispatchEvent(new Event('change'));

        const regionList = rec.regions.split(',').map(s => s.trim());
        regionCheckboxes.forEach(cb => {
            cb.checked = regionList.includes(cb.value);
        });

        window.scrollTo({ top: 0, behavior: 'smooth' });
        setTimeout(() => {
            form.dispatchEvent(new Event('submit'));
        }, 200);
    }

    refreshHistoryBtn.addEventListener('click', loadHistory);
    loadHistory();

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function truncate(str, max = 28) {
        if (!str) return '';
        return str.length > max ? str.substring(0, max) + '...' : str;
    }

    function formatDate(raw) {
        if (!raw) return '-';
        try {
            const date = new Date(raw.replace(' ', 'T') + 'Z');
            if (isNaN(date.getTime())) return raw;
            return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ', ' +
                   date.toLocaleDateString([], { month: 'short', day: 'numeric' });
        } catch {
            return raw;
        }
    }
});
