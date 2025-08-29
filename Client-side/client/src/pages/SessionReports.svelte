<script lang="ts">
  import { onMount } from 'svelte';
  import { reportsStore } from '../stores/reports';
  import type { SessionReport } from '../stores/reports';

  let filteredReports: SessionReport[] = [];
  let searchTerm = '';
  let selectedMonth = '';
  let selectedCity = '';
  let selectedIllness = '';
  let sortBy = 'date';
  let sortOrder: 'asc' | 'desc' = 'desc';
  let showModal = false;
  let selectedReport: SessionReport | null = null;
  let showSummary = false;

  // Get unique values for filters
  let months: string[] = [];
  let cities: string[] = [];
  let illnesses: string[] = [];

  onMount(() => {
    reportsStore.subscribe(value => {
      filteredReports = value;
      
      // Extract unique values for filters
      months = [...new Set(value.map(r => r.date.substring(0, 7)))].sort().reverse();
      cities = [...new Set(value.map(r => r.city))].sort();
      illnesses = [...new Set(value.map(r => r.illness))].sort();
      
      applyFilters();
    });
  });

  function applyFilters() {
    reportsStore.subscribe(allReports => {
      let filtered = allReports;

      // Apply search filter
      if (searchTerm) {
        filtered = filtered.filter(report =>
          report.sessionId.toLowerCase().includes(searchTerm.toLowerCase()) ||
          report.patientId.toLowerCase().includes(searchTerm.toLowerCase()) ||
          report.therapistName.toLowerCase().includes(searchTerm.toLowerCase()) ||
          report.illness.toLowerCase().includes(searchTerm.toLowerCase()) ||
          report.city.toLowerCase().includes(searchTerm.toLowerCase()) ||
          report.therapistMessage.toLowerCase().includes(searchTerm.toLowerCase())
        );
      }

      // Apply month filter
      if (selectedMonth) {
        filtered = filtered.filter(report => report.date.startsWith(selectedMonth));
      }

      // Apply city filter
      if (selectedCity) {
        filtered = filtered.filter(report => report.city === selectedCity);
      }

      // Apply illness filter
      if (selectedIllness) {
        filtered = filtered.filter(report => report.illness === selectedIllness);
      }

      // Apply sorting
      filtered.sort((a, b) => {
        let aValue, bValue;
        
        switch (sortBy) {
          case 'sessionId':
            aValue = a.sessionId;
            bValue = b.sessionId;
            break;
          case 'date':
            aValue = new Date(a.date);
            bValue = new Date(b.date);
            break;
          case 'therapistName':
            aValue = a.therapistName;
            bValue = b.therapistName;
            break;
          case 'city':
            aValue = a.city;
            bValue = b.city;
            break;
          default:
            aValue = a.date;
            bValue = b.date;
        }

        if (aValue < bValue) return sortOrder === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortOrder === 'asc' ? 1 : -1;
        return 0;
      });

      filteredReports = filtered;
    });
  }

  function handleSort(column: string) {
    if (sortBy === column) {
      sortOrder = sortOrder === 'asc' ? 'desc' : 'asc';
    } else {
      sortBy = column;
      sortOrder = 'asc';
    }
    applyFilters();
  }

  function viewReport(report: SessionReport) {
    selectedReport = report;
    showModal = true;
  }

  function closeModal() {
    showModal = false;
    selectedReport = null;
  }

  function exportToCSV() {
    const headers = ['Session ID', 'Patient ID', 'Therapist Name', 'Date', 'Illness', 'City', 'Therapist Message', 'Outcome'];
    const csvContent = [
      headers.join(','),
      ...filteredReports.map(report => [
        report.sessionId,
        report.patientId,
        report.therapistName,
        report.date,
        report.illness,
        report.city,
        `"${report.therapistMessage.replace(/"/g, '""')}"`,
        report.outcome
      ].join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `session-reports-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  function clearFilters() {
    searchTerm = '';
    selectedMonth = '';
    selectedCity = '';
    selectedIllness = '';
    applyFilters();
  }

  function getIllnessColor(illness: string): string {
    const colors: { [key: string]: string } = {
      'Anxiety Disorder': 'bg-blue-100 text-blue-800',
      'Depression': 'bg-purple-100 text-purple-800',
      'PTSD': 'bg-red-100 text-red-800',
      'Bipolar Disorder': 'bg-yellow-100 text-yellow-800',
      'OCD': 'bg-green-100 text-green-800',
      'Panic Disorder': 'bg-orange-100 text-orange-800',
      'Social Anxiety': 'bg-indigo-100 text-indigo-800',
      'Eating Disorder': 'bg-pink-100 text-pink-800'
    };
    return colors[illness] || 'bg-gray-100 text-gray-800';
  }

  function getOutcomeColor(outcome: string): string {
    const colors: { [key: string]: string } = {
      'Positive Progress': 'bg-green-100 text-green-800',
      'Stable': 'bg-blue-100 text-blue-800',
      'Needs Follow-up': 'bg-yellow-100 text-yellow-800',
      'Referred': 'bg-purple-100 text-purple-800',
      'Improved': 'bg-emerald-100 text-emerald-800'
    };
    return colors[outcome] || 'bg-gray-100 text-gray-800';
  }

  // Statistics
  $: totalReports = filteredReports.length;
  $: uniquePatients = new Set(filteredReports.map(r => r.patientId)).size;
  $: activeTherapists = new Set(filteredReports.map(r => r.therapistName)).size;
  $: avgDuration = filteredReports.reduce((sum, r) => sum + r.duration, 0) / filteredReports.length || 0;
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
        <div class="flex items-center">
          <div class="p-2 bg-blue-100 rounded-lg">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600">Total Reports</p>
            <p class="text-2xl font-bold text-gray-900">{totalReports}</p>
          </div>
        </div>
      </div>

      <div class="bg-white p-6 rounded-lg shadow-sm border">
        <div class="flex items-center">
          <div class="p-2 bg-green-100 rounded-lg">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600">Unique Patients</p>
            <p class="text-2xl font-bold text-gray-900">{uniquePatients}</p>
          </div>
        </div>
      </div>

      <div class="bg-white p-6 rounded-lg shadow-sm border">
        <div class="flex items-center">
          <div class="p-2 bg-purple-100 rounded-lg">
            <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600">Active Therapists</p>
            <p class="text-2xl font-bold text-gray-900">{activeTherapists}</p>
          </div>
        </div>
      </div>

      <div class="bg-white p-6 rounded-lg shadow-sm border">
        <div class="flex items-center">
          <div class="p-2 bg-orange-100 rounded-lg">
            <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600">Avg Duration</p>
            <p class="text-2xl font-bold text-gray-900">{Math.round(avgDuration)}min</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters and Actions -->
    <div class="bg-white p-6 rounded-lg shadow-sm border mb-6">
      <div class="flex flex-wrap gap-4 items-center justify-between">
        <div class="flex flex-wrap gap-4 items-center">
          <!-- Search -->
          <div class="relative">
            <input
              type="text"
              placeholder="Search reports..."
              bind:value={searchTerm}
              on:input={applyFilters}
              class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <svg class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
            </svg>
          </div>

          <!-- Month Filter -->
          <select bind:value={selectedMonth} on:change={applyFilters} class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500">
            <option value="">All Months</option>
            {#each months as month}
              <option value={month}>{new Date(month + '-01').toLocaleDateString('en-US', { year: 'numeric', month: 'long' })}</option>
            {/each}
          </select>

          <!-- City Filter -->
          <select bind:value={selectedCity} on:change={applyFilters} class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500">
            <option value="">All Cities</option>
            {#each cities as city}
              <option value={city}>{city}</option>
            {/each}
          </select>

          <!-- Illness Filter -->
          <select bind:value={selectedIllness} on:change={applyFilters} class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500">
            <option value="">All Conditions</option>
            {#each illnesses as illness}
              <option value={illness}>{illness}</option>
            {/each}
          </select>

          <button
            on:click={clearFilters}
            class="px-4 py-2 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-lg transition-colors"
          >
            Clear Filters
          </button>
        </div>

        <div class="flex gap-2">
          <button
            on:click={() => showSummary = !showSummary}
            class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors"
          >
            {showSummary ? 'Hide' : 'Show'} Summary
          </button>
          <button
            on:click={exportToCSV}
            class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
          >
            Export CSV
          </button>
        </div>
      </div>
    </div>

    <!-- Summary Report -->
    {#if showSummary}
      <div class="bg-white p-6 rounded-lg shadow-sm border mb-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Summary Report</h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <!-- Illness Breakdown -->
          <div>
            <h4 class="font-medium text-gray-700 mb-2">Condition Breakdown</h4>
            {#each illnesses as illness}
              {@const count = filteredReports.filter(r => r.illness === illness).length}
              {@const percentage = totalReports > 0 ? (count / totalReports * 100).toFixed(1) : 0}
              <div class="flex justify-between items-center py-1">
                <span class="text-sm text-gray-600">{illness}</span>
                <span class="text-sm font-medium">{count} ({percentage}%)</span>
              </div>
            {/each}
          </div>

          <!-- City Distribution -->
          <div>
            <h4 class="font-medium text-gray-700 mb-2">City Distribution</h4>
            {#each cities as city}
              {@const count = filteredReports.filter(r => r.city === city).length}
              {@const percentage = totalReports > 0 ? (count / totalReports * 100).toFixed(1) : 0}
              <div class="flex justify-between items-center py-1">
                <span class="text-sm text-gray-600">{city}</span>
                <span class="text-sm font-medium">{count} ({percentage}%)</span>
              </div>
            {/each}
          </div>

          <!-- Outcome Analysis -->
          <div>
            <h4 class="font-medium text-gray-700 mb-2">Treatment Outcomes</h4>
            {#each [...new Set(filteredReports.map(r => r.outcome))] as outcome}
              {@const count = filteredReports.filter(r => r.outcome === outcome).length}
              {@const percentage = totalReports > 0 ? (count / totalReports * 100).toFixed(1) : 0}
              <div class="flex justify-between items-center py-1">
                <span class="text-sm text-gray-600">{outcome}</span>
                <span class="text-sm font-medium">{count} ({percentage}%)</span>
              </div>
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                  on:click={() => handleSort('sessionId')}>
                <div class="flex items-center">
                  Session ID
                  {#if sortBy === 'sessionId'}
                    <svg class="ml-1 w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      {#if sortOrder === 'asc'}
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
                      {:else}
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                      {/if}
                    </svg>
                  {/if}
                </div>
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Patient ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                  on:click={() => handleSort('therapistName')}>
                <div class="flex items-center">
                  Therapist Name
                  {#if sortBy === 'therapistName'}
                    <svg class="ml-1 w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      {#if sortOrder === 'asc'}
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
                      {:else}
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                      {/if}
                    </svg>
                  {/if}
                </div>
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                  on:click={() => handleSort('date')}>
                <div class="flex items-center">
                  Date
                  {#if sortBy === 'date'}
                    <svg class="ml-1 w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      {#if sortOrder === 'asc'}
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
                      {:else}
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                      {/if}
                    </svg>
                  {/if}
                </div>
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Illness</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                  on:click={() => handleSort('city')}>
                <div class="flex items-center">
                  City
                  {#if sortBy === 'city'}
                    <svg class="ml-1 w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      {#if sortOrder === 'asc'}
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
                      {:else}
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                      {/if}
                    </svg>
                  {/if}
                </div>
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Message</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            {#each filteredReports as report}
              <tr class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{report.sessionId}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{report.patientId}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{report.therapistName}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {new Date(report.date).toLocaleDateString()}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full {getIllnessColor(report.illness)}">
                    {report.illness}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{report.city}</td>
                <td class="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">{report.therapistMessage}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button
                        on:click={() => viewReport(report)}
                        class="btn-view-details"
                    >
                        View Details
                    </button>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>

        {#if filteredReports.length === 0}
          <div class="text-center py-12">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
            </svg>
            <h3 class="mt-2 text-sm font-medium text-gray-900">No reports found</h3>
            <p class="mt-1 text-sm text-gray-500">Try adjusting your search criteria or filters.</p>
          </div>
        {/if}
      </div>
    </div>
  </div>
</div>

<!-- Report Details Modal -->
{#if showModal && selectedReport}
  <div class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
      <div class="mt-3">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-medium text-gray-900">Session Report Details</h3>
          <button
            on:click={closeModal}
            class="text-gray-400 hover:text-gray-600"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
        
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Session ID</label>
              <p class="mt-1 text-sm text-gray-900">{selectedReport.sessionId}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Patient ID</label>
              <p class="mt-1 text-sm text-gray-900">{selectedReport.patientId}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Therapist</label>
              <p class="mt-1 text-sm text-gray-900">{selectedReport.therapistName}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Date</label>
              <p class="mt-1 text-sm text-gray-900">{new Date(selectedReport.date).toLocaleDateString()}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Duration</label>
              <p class="mt-1 text-sm text-gray-900">{selectedReport.duration} minutes</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Session Type</label>
              <p class="mt-1 text-sm text-gray-900">{selectedReport.sessionType}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">City</label>
              <p class="mt-1 text-sm text-gray-900">{selectedReport.city}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Outcome</label>
              <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full {getOutcomeColor(selectedReport.outcome)}">
                {selectedReport.outcome}
              </span>
            </div>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700">Condition</label>
            <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full {getIllnessColor(selectedReport.illness)}">
              {selectedReport.illness}
            </span>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700">Therapist Message</label>
            <p class="mt-1 text-sm text-gray-900 bg-gray-50 p-3 rounded-lg">{selectedReport.therapistMessage}</p>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700">Clinical Notes</label>
            <p class="mt-1 text-sm text-gray-900 bg-gray-50 p-3 rounded-lg">{selectedReport.clinicalNotes}</p>
          </div>
        </div>
        
        <div class="mt-6 flex justify-end">
          <button
            on:click={closeModal}
            class="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-colors"
          >
            Close
          </button>
        </div>
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