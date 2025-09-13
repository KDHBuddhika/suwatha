<script lang="ts">
  import { onMount } from 'svelte';
  import { authStore } from '../../stores/auth'; // Adjust path if needed
  import { Video, MessageSquare, Star, ChevronLeft, ChevronRight, Search } from 'lucide-svelte';

  // --- COMPONENT STATE ---
  let sessions: any[] = [];
  let isLoading = true; // Start in loading state
  let error: string | null = null;

  // --- FILTER & SEARCH STATE ---
  let searchQuery = '';
  let searchTerm = '';
  let statusFilter = 'ALL';
  let typeFilter = 'ALL';
  
  // --- PAGINATION STATE ---
  let currentPage = 1;
  const itemsPerPage = 10;
  let totalPages = 1;

  // --- API FETCHING LOGIC ---
  async function fetchSessions(page: number, status: string, type: string, query: string) {
    isLoading = true;
    error = null;

    let authState;
    const unsubscribe = authStore.subscribe(s => { authState = s; });
    unsubscribe();

    if (!authState?.token) {
      error = "Authentication failed. Please log in again.";
      isLoading = false;
      return;
    }

    const params = new URLSearchParams({
      page: String(page - 1), // Backend is 0-indexed
      size: String(itemsPerPage),
    });
    if (status !== 'ALL') params.append('status', status);
    if (type !== 'ALL') params.append('type', type);
    if (query) params.append('q', query);
    
    const API_URL = `http://localhost:8090/api/doctor/sessions?${params.toString()}`;

    try {
      const response = await fetch(API_URL, {
        headers: { 'Authorization': `Bearer ${authState.token}` }
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch sessions. Server responded with status ${response.status}.`);
      }
      
      const data = await response.json();
      sessions = data.content;
      totalPages = data.totalPages > 0 ? data.totalPages : 1;
      
    } catch (err) {
      error = err.message;
      sessions = []; // Clear previous results on error
    } finally {
      isLoading = false; // THIS IS THE CRITICAL LINE
    }
  }

  // --- SIMPLIFIED REACTIVE LOGIC ---
  // This block now triggers the fetch whenever a dependency changes.
  // It also handles the initial load automatically.
  $: fetchSessions(currentPage, statusFilter, typeFilter, searchTerm);
  
  // --- DEBOUNCING FOR SEARCH INPUT ---
  let debounceTimer: ReturnType<typeof setTimeout>;
  function handleSearchInput() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      currentPage = 1; // Reset to first page on new search
      searchTerm = searchQuery;
    }, 400); // Wait 400ms after user stops typing
  }

  function handleSearchClick() {
    clearTimeout(debounceTimer);
    currentPage = 1;
    searchTerm = searchQuery;
  }

  // When filters change, reset to the first page
  $: { statusFilter, typeFilter; if (currentPage !== 1) currentPage = 1; }

  // --- EVENT HANDLERS ---
  function goToPage(page: number) {
    if (page < 1 || page > totalPages || page === currentPage) return;
    currentPage = page;
  }
</script>

<!-- The HTML remains exactly the same -->
<div class="page-container">
  <div class="page-header">
    <h1>Session History</h1>
    <p>Review details from all your past sessions.</p>
  </div>

  <div class="filter-controls">
    <div class="search-group">
      <input 
        type="search" 
        placeholder="Search by Patient ID..." 
        bind:value={searchQuery}
        on:input={handleSearchInput}
        on:keydown={(e) => { if (e.key === 'Enter') handleSearchClick(); }}
      />
      <button class="search-btn" on:click={handleSearchClick} title="Search">
        <Search size={18} />
      </button>
    </div>
    <div class="filter-group">
      <div class="select-wrapper">
        <select bind:value={statusFilter}>
          <option value="ALL">All Statuses</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>
      <div class="select-wrapper">
        <select bind:value={typeFilter}>
          <option value="ALL">All Types</option>
          <option value="VIDEO">Video</option>
          <option value="CHAT">Chat</option>
        </select>
      </div>
    </div>
  </div>

  <div class="table-container">
    <table>
      <thead>
        <tr>
          <th>Type</th><th>Patient</th><th>Date</th><th>Duration</th><th>Status</th><th>Rating</th><th class="actions-header">Actions</th>
        </tr>
      </thead>
      <tbody>
        {#if isLoading}
          <tr><td colspan="7" class="loading-cell">Loading session data...</td></tr>
        {:else if error}
          <tr><td colspan="7" class="empty-cell error">{error}</td></tr>
        {:else if sessions.length === 0}
          <tr><td colspan="7" class="empty-cell">No sessions match your filters.</td></tr>
        {:else}
          {#each sessions as session (session.sessionId)}
            <tr>
              <td><div class="icon-cell">{#if session.communicationType === 'VIDEO'}<Video size={18} /> Video{:else}<MessageSquare size={18} /> Chat{/if}</div></td>
              <td>{session.patientHandle}</td>
              <td>{session.startTime.split(',')[0]}</td>
              <td>{session.durationInMinutes ? `${session.durationInMinutes} min` : 'N/A'}</td>
              <td><span class="status-badge" class:completed={session.status === 'COMPLETED'} class:cancelled={session.status === 'CANCELLED'}>{session.status}</span></td>
              <td><div class="rating-cell">{#if session.rating}{#each Array(5) as _, i}<span class="star" class:filled={i < session.rating}><Star size={16} /></span>{/each}{:else}<span class="no-rating">N/A</span>{/if}</div></td>
              <td><button class="action-btn">View Summary</button></td>
            </tr>
          {/each}
        {/if}
      </tbody>
    </table>
  </div>

  <div class="pagination-controls">
    <button class="page-btn" disabled={currentPage === 1 || isLoading} on:click={() => goToPage(currentPage - 1)}><ChevronLeft size={16} /><span>Previous</span></button>
    <div class="page-numbers"><span>Page {currentPage} of {totalPages}</span></div>
    <button class="page-btn" disabled={currentPage === totalPages || isLoading} on:click={() => goToPage(currentPage + 1)}><span>Next</span><ChevronRight size={16} /></button>
  </div>
</div>

<!-- The <style> section remains exactly the same -->
<style>
  .page-container { display: flex; flex-direction: column; gap: 1.5rem; }
  .page-header h1 { font-size: 1.75rem; font-weight: 600; color: #1F2937; margin: 0; }
  .page-header p { font-size: 1rem; color: #6B7280; margin-top: 0.25rem; }
  .filter-controls { display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; gap: 1rem; padding: 1rem; background-color: white; border: 1px solid #E5E7EB; border-radius: 0.75rem; }
  .search-group { position: relative; flex-grow: 1; min-width: 250px; max-width: 400px; }
  .search-group input { width: 100%; padding: 0.625rem 3rem 0.625rem 1rem; border: 1px solid #D1D5DB; border-radius: 0.5rem; font-size: 0.9rem; transition: border-color 0.2s, box-shadow 0.2s; }
  .search-group input:focus { outline: none; border-color: #4F46E5; box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1); }
  .search-btn { position: absolute; right: 0.5rem; top: 50%; transform: translateY(-50%); background: none; border: none; color: #6B7280; cursor: pointer; padding: 0.25rem; }
  .filter-group { display: flex; gap: 1rem; }
  .select-wrapper { position: relative; }
  .select-wrapper select { -webkit-appearance: none; appearance: none; background-color: #F9FAFB; border: 1px solid #D1D5DB; border-radius: 0.5rem; padding: 0.625rem 2.5rem 0.625rem 1rem; font-size: 0.9rem; color: #374151; cursor: pointer; }
  .select-wrapper::after { content: '▼'; font-size: 0.625rem; color: #9CA3AF; position: absolute; right: 1rem; top: 50%; transform: translateY(-50%); pointer-events: none; }
  .table-container { background-color: white; border: 1px solid #E5E7EB; border-radius: 0.75rem; overflow-x: auto; }
  table { width: 100%; border-collapse: collapse; min-width: 800px; }
  th, td { padding: 1rem 1.25rem; text-align: left; font-size: 0.875rem; white-space: nowrap; }
  thead { background-color: #F9FAFB; }
  th { font-weight: 600; color: #6B7280; text-transform: uppercase; letter-spacing: 0.05em; }
  tbody tr { border-top: 1px solid #E5E7EB; }
  tbody tr:hover { background-color: #F9FAFB; }
  .loading-cell, .empty-cell { text-align: center; padding: 3rem; color: #6B7280; font-style: italic; font-size: 1rem; }
  .empty-cell.error { color: #DC2626; font-weight: 500; font-style: normal; }
  .icon-cell { display: flex; align-items: center; gap: 0.5rem; font-weight: 500; color: #374151; }
  .status-badge { padding: 0.25rem 0.75rem; border-radius: 1rem; font-weight: 600; font-size: 0.75rem; text-transform: capitalize; }
  .status-badge.completed { background-color: #DEF7EC; color: #03543F; }
  .status-badge.cancelled { background-color: #FDE8E8; color: #9B1C1C; }
  .rating-cell { display: flex; align-items: center; gap: 0.125rem; }
  .star { color: #D1D5DB; }
  .star.filled { color: #F59E0B; }
  .no-rating { color: #9CA3AF; font-size: 0.875rem; }
  .actions-header, tbody td:last-child { text-align: right; }
  .action-btn { background-color: #EEF2FF; color: #4338CA; border: none; border-radius: 0.375rem; padding: 0.5rem 1rem; font-weight: 500; cursor: pointer; transition: background-color 0.2s ease; }
  .action-btn:hover { background-color: #E0E7FF; }
  .pagination-controls { display: flex; justify-content: space-between; align-items: center; padding: 1rem; background-color: white; border: 1px solid #E5E7EB; border-radius: 0.75rem; margin-top: 1.5rem; }
  .page-btn { display: flex; align-items: center; gap: 0.25rem; background-color: white; border: 1px solid #D1D5DB; color: #374151; padding: 0.5rem 1rem; border-radius: 0.5rem; font-weight: 500; cursor: pointer; transition: background-color 0.2s ease; }
  .page-btn:disabled { color: #9CA3AF; cursor: not-allowed; background-color: #F9FAFB; }
  .page-btn:not(:disabled):hover { background-color: #F3F4F6; }
  .page-numbers { color: #6B7280; font-size: 0.875rem; }
</style>