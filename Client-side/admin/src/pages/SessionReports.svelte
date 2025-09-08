<script lang="ts">
  import { onMount } from 'svelte';
  import { reportsStore, type SessionReport, type SessionReportDetails } from '../stores/reports';

  // --- STATE FOR UI INPUTS ---
  let searchTerm = '';
  let selectedMonth = '';
  let selectedCity = '';
  let selectedIllness = '';
  let selectedRisk = '';
  let ageFilter: number | null = null;
  let sortBy = 'date';
  let sortOrder: 'asc' | 'desc' = 'desc';
  let currentPage = 1;

  // --- STATE FOR UI DISPLAY ---
  let isFetching = false;
  let initialLoad = true;
  let showSummary = false;
  let showModal = false;
  let selectedReport: SessionReportDetails | null = null;

  // --- DERIVED STATE FROM THE STORE ---
  $: reports = $reportsStore.reports;
  $: pagination = $reportsStore.pagination;
  $: summary = $reportsStore.summary;
  $: filterOptions = $reportsStore.filterOptions;
  
  // --- NEW: DERIVED STATE FOR SUMMARY BREAKDOWNS ---
  // These automatically recalculate when the 'reports' data changes
  function createBreakdown(items: SessionReport[], key: keyof SessionReport) {
    if (!items || items.length === 0) return [];
    const counts = new Map<string, number>();
    for (const item of items) {
      const value = item[key] as string;
      if (value) {
        counts.set(value, (counts.get(value) || 0) + 1);
      }
    }
    return Array.from(counts.entries())
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count);
  }

  $: illnessBreakdown = createBreakdown(reports, 'illness');
  $: cityBreakdown = createBreakdown(reports, 'city');
  $: riskBreakdown = createBreakdown(reports, 'riskAssessment');


  // --- DATA FETCHING LOGIC ---
  async function loadReports() {
    isFetching = true;
    const sort = `${sortBy},${sortOrder}`;
    
    await reportsStore.fetchReports({
      page: currentPage, searchTerm, month: selectedMonth, city: selectedCity,
      illness: selectedIllness, risk: selectedRisk, age: ageFilter, sort
    });

    isFetching = false;
    initialLoad = false;
  }

  // --- REACTIVE LOGIC TO AUTOMATE FETCHING ---
  onMount(() => {
    loadReports();
    reportsStore.fetchFilterOptions();
  });

  let debounceTimer: number;
  $: {
    const _ = { searchTerm, selectedMonth, selectedCity, selectedIllness, selectedRisk, ageFilter, sortBy, sortOrder };
    if (!initialLoad) {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        currentPage = 1;
        loadReports();
      }, 350);
    }
  }
  $: if (!initialLoad && currentPage !== pagination.currentPage) {
    const _ = currentPage;
    loadReports();
  }

  // --- EVENT HANDLERS ---
  function handleSort(column: string) {
    if (sortBy === column) {
      sortOrder = sortOrder === 'asc' ? 'desc' : 'asc';
    } else {
      sortBy = column;
      sortOrder = 'desc';
    }
  }

  async function viewReport(report: SessionReport) {
    selectedReport = await reportsStore.fetchReportDetails(report.sessionId);
    if (selectedReport) showModal = true;
  }

  function closeModal() {
    showModal = false;
    selectedReport = null;
  }

  function clearFilters() {
    searchTerm = '';
    selectedMonth = '';
    selectedCity = '';
    selectedIllness = '';
    selectedRisk = '';
    ageFilter = null;
  }

  function exportToCSV() {
    const headers = ['Session ID', 'Patient Handle', 'Therapist', 'Date', 'Illness', 'City', 'Gender', 'Age', 'Risk', 'Message'];
    const csvContent = [
      headers.join(','),
      ...reports.map(r => [
        r.sessionId, r.patientHandle, r.therapistName, r.date, r.illness, r.city,
        r.gender, r.age, r.riskAssessment,
        `"${(r.therapistMessage || '').replace(/"/g, '""')}"`
      ].join(','))
    ].join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `session-reports-page-${currentPage}-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  // --- HELPER FUNCTIONS ---
  function getRiskColor(risk: string) {
    switch (risk?.toUpperCase()) {
      case 'HIGH': return 'bg-red-100 text-red-800';
      case 'MODERATE': return 'bg-yellow-100 text-yellow-800';
      case 'LOW': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }

  function getTagColor(str: string): string {
    if (!str) return 'bg-gray-100 text-gray-800';
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    const colors = [
        'bg-blue-100 text-blue-800', 'bg-purple-100 text-purple-800', 'bg-green-100 text-green-800',
        'bg-orange-100 text-orange-800', 'bg-indigo-100 text-indigo-800', 'bg-pink-100 text-pink-800',
    ];
    return colors[Math.abs(hash % colors.length)];
  }
</script>

<div class="session-reports p-6 bg-gray-50 min-h-screen">
  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">Session Reports</h1>
      <p class="text-gray-600">Comprehensive therapy session reports and analytics</p>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <div class="bg-white p-6 rounded-lg shadow-sm border">
        <p class="text-sm font-medium text-gray-600">Total Reports</p>
        <p class="text-2xl font-bold text-gray-900">{summary.totalReports}</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow-sm border">
        <p class="text-sm font-medium text-gray-600">Unique Patients</p>
        <p class="text-2xl font-bold text-gray-900">{summary.uniquePatients}</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow-sm border">
        <p class="text-sm font-medium text-gray-600">Active Therapists</p>
        <p class="text-2xl font-bold text-gray-900">{summary.activeTherapists}</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow-sm border">
        <p class="text-sm font-medium text-gray-600">Avg Duration</p>
        <p class="text-2xl font-bold text-gray-900">{Math.round(summary.averageDuration || 0)} min</p>
      </div>
    </div>

    <!-- Filters and Actions -->
    <div class="bg-white p-6 rounded-lg shadow-sm border mb-6">
       <div class="flex flex-wrap gap-4 items-center justify-between">
        <div class="flex flex-wrap gap-4 items-center flex-grow">
          <div class="relative flex-grow min-w-[250px]">
            <input type="text" placeholder="Search reports..." bind:value={searchTerm} class="w-25 pl-10 pr-4 py-2 border rounded-lg"/>
            <span class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"><svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg></span>
          </div>
          <select bind:value={selectedCity} class="px-4 py-2 border rounded-lg">
            <option value="">All Cities</option>
            {#each filterOptions.cities as city} <option value={city}>{city}</option> {/each}
          </select>
          <select bind:value={selectedIllness} class="px-4 py-2 border rounded-lg">
            <option value="">All Conditions</option>
            {#each filterOptions.illnesses as illness} <option value={illness}>{illness}</option> {/each}
          </select>
          <select bind:value={selectedRisk} class="px-4 py-2 border rounded-lg">
            <option value="">All Risks</option>
            {#each filterOptions.riskAssessments as risk} <option value={risk}>{risk}</option> {/each}
          </select>
          <input type="number" placeholder="Age" bind:value={ageFilter} class="px-4 py-2 border rounded-lg w-24"/>
          <button on:click={clearFilters} class="px-4 py-2 hover:bg-gray-100 rounded-lg">Clear</button>
        </div>
        <div class="flex gap-2">
          <button on:click={() => showSummary = !showSummary} class="px-4 py-2 bg-purple-600 text-white rounded-lg">{showSummary ? 'Hide' : 'Show'} Summary</button>
          <button on:click={exportToCSV} class="px-4 py-2 bg-green-600 text-white rounded-lg">Export CSV</button>
        </div>
      </div>
    </div>

    <!-- Summary Report -->
    {#if showSummary}
      <div class="bg-white p-6 rounded-lg shadow-sm border mb-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Summary of Filtered Reports ({pagination.totalItems} total)</h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div>
            <h4 class="font-medium text-gray-700 mb-2">Condition Breakdown</h4>
            {#each illnessBreakdown as item}
              {@const percentage = pagination.totalItems > 0 ? (item.count / pagination.totalItems * 100).toFixed(1) : 0}
              <div class="flex justify-between items-center py-1 text-sm"><span class="text-gray-600">{item.name}</span><span class="font-medium">{item.count} ({percentage}%)</span></div>
            {/each}
          </div>
          <div>
            <h4 class="font-medium text-gray-700 mb-2">City Distribution</h4>
            {#each cityBreakdown as item}
              {@const percentage = pagination.totalItems > 0 ? (item.count / pagination.totalItems * 100).toFixed(1) : 0}
              <div class="flex justify-between items-center py-1 text-sm"><span class="text-gray-600">{item.name}</span><span class="font-medium">{item.count} ({percentage}%)</span></div>
            {/each}
          </div>
          <div>
            <h4 class="font-medium text-gray-700 mb-2">Risk Assessment</h4>
            {#each riskBreakdown as item}
               {@const percentage = pagination.totalItems > 0 ? (item.count / pagination.totalItems * 100).toFixed(1) : 0}
              <div class="flex justify-between items-center py-1 text-sm"><span class="text-gray-600">{item.name}</span><span class="font-medium">{item.count} ({percentage}%)</span></div>
            {/each}
          </div>
        </div>
      </div>
    {/if}

    <!-- Reports Table -->
    <div class="bg-white rounded-lg shadow-sm border overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider cursor-pointer" on:click={() => handleSort('sessionId')}>Session ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Patient</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider cursor-pointer" on:click={() => handleSort('therapistName')}>Therapist</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider cursor-pointer" on:click={() => handleSort('date')}>Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Condition</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Risk</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider cursor-pointer" on:click={() => handleSort('city')}>City</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          {#if initialLoad}
            <tbody><tr><td colspan="8" class="text-center py-12">Loading reports...</td></tr></tbody>
          {:else}
            <tbody class="bg-white divide-y divide-gray-200" class:opacity-50={isFetching}>
              {#if reports.length === 0}
                <tr><td colspan="8" class="text-center py-12">No reports found for the selected filters.</td></tr>
              {:else}
                {#each reports as report (report.sessionId)}
                  <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">SES-{report.sessionId}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm">{report.patientHandle}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm">{report.therapistName}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm">{new Date(report.date).toLocaleDateString()}</td>
                    <td class="px-6 py-4 whitespace-nowrap"><span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full {getTagColor(report.illness)}">{report.illness}</span></td>
                    <td class="px-6 py-4 whitespace-nowrap"><span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full {getRiskColor(report.riskAssessment)}">{report.riskAssessment}</span></td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm">{report.city || 'N/A'}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button on:click={() => viewReport(report)} class="text-blue-600 hover:text-blue-900">View</button>
                    </td>
                  </tr>
                {/each}
              {/if}
            </tbody>
          {/if}
        </table>
      </div>
      <div class="p-4 flex items-center justify-between border-t">
        <span class="text-sm text-gray-700">Total Reports: {pagination.totalItems}</span>
        <div class="flex gap-2">
            <button on:click={() => currentPage--} disabled={currentPage <= 1 || isFetching} class="px-4 py-2 border rounded-lg">Previous</button>
            <span class="px-4 py-2">Page {pagination.currentPage} of {pagination.totalPages}</span>
            <button on:click={() => currentPage++} disabled={currentPage >= pagination.totalPages || isFetching} class="px-4 py-2 border rounded-lg">Next</button>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Report Details Modal -->
{#if showModal && selectedReport}
  <div class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50 flex items-center justify-center">
    <div class="relative mx-auto p-6 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white" on:click|stopPropagation>
      <div class="flex items-start justify-between">
          <h3 class="text-lg font-medium text-gray-900">Report Details (SES-{selectedReport.sessionId})</h3>
          <button on:click={closeModal} class="text-gray-400 hover:text-gray-600">&times;</button>
      </div>
      <div class="mt-4 space-y-4 text-sm">
        <div class="grid grid-cols-2 gap-4">
            <div><strong class="block text-gray-500">Patient:</strong> {selectedReport.patientHandle}</div>
            <div><strong class="block text-gray-500">Therapist:</strong> {selectedReport.therapistName}</div>
            <div><strong class="block text-gray-500">Date:</strong> {new Date(selectedReport.date).toLocaleString()}</div>
            <div><strong class="block text-gray-500">Duration:</strong> {selectedReport.durationInMinutes} min</div>
            <div><strong class="block text-gray-500">Type:</strong> {selectedReport.communicationType}</div>
            <div><strong class="block text-gray-500">Condition:</strong> {selectedReport.illness}</div>
            <div><strong class="block text-gray-500">Risk:</strong> {selectedReport.riskAssessment}</div>
        </div>
        <div><strong class="block text-gray-500">Clinical Notes:</strong> <div class="mt-1 p-2 bg-gray-50 rounded">{selectedReport.clinicalNotes || 'N/A'}</div></div>
      </div>
    </div>
  </div>
{/if}







<style>
 /* General Body and Layout */
.session-reports {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    padding: 1.5rem;
    background-color: #F9FAFB;
    min-height: 100vh;
    margin-left: 280PX;
}

.max-w-7xl {
    max-width: 100%;
}

.mx-auto {
    margin-left: auto;
    margin-right: auto;
}

/* Header */
.mb-8 {
    margin-bottom: 2rem;
}

.text-3xl {
    font-size: 1.875rem;
    line-height: 2.25rem;
}

.font-bold {
    font-weight: 700;
}

.text-gray-900 {
    color: #111827;
}

.mb-2 {
    margin-bottom: 0.5rem;
}

.text-gray-600 {
    color: #4B5563;
}

/* Statistics Cards */
.grid {
    display: grid;
}

.grid-cols-1 {
    grid-template-columns: repeat(1, minmax(0, 1fr));
}

.md\:grid-cols-4 {
    grid-template-columns: repeat(4, minmax(0, 1fr));
}

.gap-6 {
    gap: 1.5rem;
}

.bg-white {
    background-color: #FFFFFF;
}

.p-6 {
    padding: 1.5rem;
}

.rounded-lg {
    border-radius: 0.5rem;
}

.shadow-sm {
    box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.border {
    border-width: 1px;
    border-color: #E5E7EB;
}

.flex {
    display: flex;
}

.items-center {
    align-items: center;
}

.p-2 {
    padding: 0.5rem;
}

.bg-blue-100 {
    background-color: #DBEAFE;
}

.text-blue-800 {
    color: #1E40AF;
}

.rounded-lg {
    border-radius: 0.5rem;
}

.w-6 {
    width: 1.5rem;
}

.h-6 {
    height: 1.5rem;
}

.text-blue-600 {
    color: #2563EB;
}

.ml-4 {
    margin-left: 1rem;
}

.text-sm {
    font-size: 0.875rem;
    line-height: 1.25rem;
}

.font-medium {
    font-weight: 500;
}

.text-2xl {
    font-size: 1.5rem;
    line-height: 2rem;
}

/* Filters and Actions */
.mb-6 {
    margin-bottom: 1.5rem;
}

.flex-wrap {
    flex-wrap: wrap;
}

.gap-4 {
    gap: 1rem;
}

.justify-between {
    justify-content: space-between;
}

.relative {
    position: relative;
}

.pl-10 {
    padding-left: 2.5rem;
}

.pr-4 {
    padding-right: 1rem;
}

.py-2 {
    padding-top: 0.5rem;
    padding-bottom: 0.5rem;
}

.border-gray-300 {
    border-color: #D1D5DB;
}

.focus\:ring-2:focus {
    --tw-ring-offset-shadow: var(--tw-ring-inset) 0 0 0 var(--tw-ring-offset-width) var(--tw-ring-offset-color);
    --tw-ring-shadow: var(--tw-ring-inset) 0 0 0 calc(2px + var(--tw-ring-offset-width)) var(--tw-ring-color);
    box-shadow: var(--tw-ring-offset-shadow), var(--tw-ring-shadow), var(--tw-shadow, 0 0 #0000);
}

.focus\:ring-blue-500:focus {
    --tw-ring-color: #3B82F6;
}

.focus\:border-transparent:focus {
    border-color: transparent;
}

.absolute {
    position: absolute;
}

.left-3 {
    left: 0.75rem;
}

.top-2\.5 {
    top: 0.625rem;
}

.h-5 {
    height: 1.25rem;
}

.w-5 {
    width: 1.25rem;
}

.text-gray-400 {
    color: #9CA3AF;
}

select {
    padding: 0.5rem 2.5rem 0.5rem 1rem;
}

.px-4 {
    padding-left: 1rem;
    padding-right: 1rem;
}

.hover\:text-gray-800:hover {
    color: #1F2937;
}

.hover\:bg-gray-100:hover {
    background-color: #F3F4F6;
}

.transition-colors {
    transition-property: color, background-color, border-color, text-decoration-color, fill, stroke;
    transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
    transition-duration: 150ms;
}

.flex.gap-2 {
    gap: 0.5rem;
}

.bg-purple-600 {
    background-color: #9333EA;
}

.text-white {
    color: #FFFFFF;
}

.hover\:bg-purple-700:hover {
    background-color: #7E22CE;
}

.bg-green-600 {
    background-color: #16A34A;
}

.hover\:bg-green-700:hover {
    background-color: #15803D;
}

/* Summary Report */
.text-lg {
    font-size: 1.125rem;
    line-height: 1.75rem;
}

.font-semibold {
    font-weight: 600;
}

.mb-4 {
    margin-bottom: 1rem;
}

.md\:grid-cols-3 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
}

.text-gray-700 {
    color: #374151;
}


  .session-reports {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    padding: 1.5rem;
    background-color: #F9FAFB;
    min-height: 100vh;
    margin-left: 280PX;
}
.py-1 {
    padding-top: 0.25rem;
    padding-bottom: 0.25rem;
}

/* Reports Table */
.overflow-hidden {
    overflow: hidden;
}

.overflow-x-auto {
    overflow-x: auto;
}

.min-w-full {
    min-width: 100%;
}

.divide-y > :not([hidden]) ~ :not([hidden]) {
    --tw-divide-y-reverse: 0;
    border-top-width: calc(1px * calc(1 - var(--tw-divide-y-reverse)));
    border-bottom-width: calc(1px * var(--tw-divide-y-reverse));
}

.divide-gray-200 > :not([hidden]) ~ :not([hidden]) {
    --tw-divide-opacity: 1;
    border-color: rgb(229 231 235 / var(--tw-divide-opacity));
}

.bg-gray-50 {
    background-color: #F9FAFB;
}

.px-6 {
    padding-left: 1.5rem;
    padding-right: 1.5rem;
}

.py-3 {
    padding-top: 0.75rem;
    padding-bottom: 0.75rem;
}

.text-left {
    text-align: left;
}

.text-xs {
    font-size: 0.75rem;
    line-height: 1rem;
}

.text-gray-500 {
    color: #6B7280;
}

.uppercase {
    text-transform: uppercase;
}

.tracking-wider {
    letter-spacing: 0.05em;
}

.cursor-pointer {
    cursor: pointer;
}

.hover\:bg-gray-100:hover {
    background-color: #F3F4F6;
}

.ml-1 {
    margin-left: 0.25rem;
}

.w-4 {
    width: 1rem;
}

.h-4 {
    height: 1rem;
}

tbody.bg-white {
    --tw-bg-opacity: 1;
    background-color: rgb(255 255 255 / var(--tw-bg-opacity));
}

tr.hover\:bg-gray-50:hover {
    --tw-bg-opacity: 1;
    background-color: rgb(249 250 251 / var(--tw-bg-opacity));
}

td.whitespace-nowrap {
    white-space: nowrap;
}

.max-w-xs {
    max-width: 20rem;
}

.truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.text-blue-600 {
    color: #2563EB;
}

.hover\:text-blue-900:hover {
    color: #1E3A8A;
}

.mr-3 {
    margin-right: 0.75rem;
}

/* No Reports Found */
.text-center {
    text-align: center;
}

.py-12 {
    padding-top: 3rem;
    padding-bottom: 3rem;
}

.mx-auto {
    margin-left: auto;
    margin-right: auto;
}

.h-12 {
    height: 3rem;
}

.w-12 {
    width: 3rem;
}

.mt-2 {
    margin-top: 0.5rem;
}

.mt-1 {
    margin-top: 0.25rem;
}

/* Modal */
.fixed {
    position: fixed;
}

.inset-0 {
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
}

.bg-gray-600 {
    background-color: #4B5563;
}

.bg-opacity-50 {
    background-color: rgba(75, 85, 99, 0.5);
}

.overflow-y-auto {
    overflow-y: auto;
}

.h-full {
    height: 100%;
}

.w-full {
    width: 100%;
}

.z-50 {
    z-index: 50;
}

.relative {
    position: relative;
}

.top-20 {
    top: 5rem;
}

.p-5 {
    padding: 1.25rem;
}

.w-11\/12 {
    width: 91.666667%;
}

.md\:w-3\/4 {
    width: 75%;
}

.lg\:w-1\/2 {
    width: 50%;
}

.shadow-lg {
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.rounded-md {
    border-radius: 0.375rem;
}

.mt-3 {
    margin-top: 0.75rem;
}

.space-y-4 > :not([hidden]) ~ :not([hidden]) {
    --tw-space-y-reverse: 0;
    margin-top: calc(1rem * calc(1 - var(--tw-space-y-reverse)));
    margin-bottom: calc(1rem * var(--tw-space-y-reverse));
}

.grid-cols-2 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.block {
    display: block;
}

.mt-1 {
    margin-top: 0.25rem;
}

.bg-gray-50 {
    background-color: #F9FAFB;
}

.p-3 {
    padding: 0.75rem;
}

.mt-6 {
    margin-top: 1.5rem;
}

.justify-end {
    justify-content: flex-end;
}

.bg-gray-300 {
    background-color: #D1D5DB;
}

.hover\:bg-gray-400:hover {
    background-color: #9CA3AF;
}

.btn-view-details {
  /* Shape and Spacing */
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.75rem; /* py-1 px-3 */
  border-radius: 9999px;   /* rounded-full */
  border: 1px solid transparent;

  /* Typography */
  font-size: 0.75rem;     /* text-xs */
  font-weight: 500;       /* font-medium */
  line-height: 1.25;
  white-space: nowrap;

  /* Colors */
  background-color: #DBEAFE; /* blue-100 */
  color: #1D4ED8;           /* blue-700 */

  /* Effects */
  transition: all 0.2s ease-in-out;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

/* Hover State */
.btn-view-details:hover {
  background-color: #BFDBFE; /* blue-200 */
  color: #1E40AF;           /* blue-800 */
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px); /* Subtle lift effect */
}

/* Focus State for Accessibility */
.btn-view-details:focus {
  outline: 2px solid transparent;
  outline-offset: 2px;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.5); /* ring-blue-500 with opacity */
}

/* Active State */
.btn-view-details:active {
    transform: translateY(0);
}


/* Illness and Outcome Badge Colors */
.bg-blue-100 { background-color: #DBEAFE; }
.text-blue-800 { color: #1E40AF; }
.bg-purple-100 { background-color: #F3E8FF; }
.text-purple-800 { color: #6B21A8; }
.bg-red-100 { background-color: #FEE2E2; }
.text-red-800 { color: #991B1B; }
.bg-yellow-100 { background-color: #FEF9C3; }
.text-yellow-800 { color: #854D0E; }
.bg-green-100 { background-color: #DCFCE7; }
.text-green-800 { color: #166534; }
.bg-orange-100 { background-color: #FFEDD5; }
.text-orange-800 { color: #9A3412; }
.bg-indigo-100 { background-color: #E0E7FF; }
.text-indigo-800 { color: #3730A3; }
.bg-pink-100 { background-color: #FCE7F3; }
.text-pink-800 { color: #9D266B; }
.bg-gray-100 { background-color: #F3F4F6; }
.text-gray-800 { color: #1F2937; }
.bg-emerald-100 { background-color: #D1FAE5; }
.text-emerald-800 { color: #065F46; }

/* Additional SVG Styles */
svg {
    display: inline-block;
    vertical-align: middle;
}

path {
    fill: currentColor;
}
</style>