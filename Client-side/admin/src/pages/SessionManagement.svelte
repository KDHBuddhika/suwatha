<script lang="ts">
  import { onMount } from 'svelte';
  import { sessionsStore, type Session } from '../stores/sessions';
  import { Search, Filter, Calendar, Clock, Star, Eye, Edit, Trash2, Plus, Download, RefreshCw } from 'lucide-svelte';
  
  // ========================================================================
  // STATE FOR API AND UI
  // ========================================================================

  // --- API Parameters ---
  let searchTerm = '';
  let communicationTypeFilter = 'all';
  let statusFilter = 'all';
  let sortColumn = 'startTime';
  let sortDirection: 'asc' | 'desc' = 'desc';
  let currentPage = 0;

  // --- Loading State ---
  let isFetching = false;
  let initialLoad = true;

  // --- NEW: State for Session Statistics ---
  interface SessionStats {
    totalSessions: number;
    activeNow: number;
    completed: number;
    scheduled: number;
  }
  let sessionStats: SessionStats | null = null;

  // --- Modal State ---
  let showSessionModal = false;
  let selectedSession: Session | null = null;
  let showDeleteModal = false;
  let sessionToDelete: Session | null = null;
  
  // --- Static Dropdown Data ---
  const communicationTypes = [
    { value: 'all', label: 'All Types' },
    { value: 'VIDEO', label: 'Video Call' },
    { value: 'AUDIO', label: 'Audio Call' },
    { value: 'CHAT', label: 'Chat' },
    { value: 'IN-PERSON', label: 'In-Person' }
  ];

  const statusOptions = [
    { value: 'all', label: 'All Statuses' },
    { value: 'SCHEDULED', label: 'Scheduled' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'COMPLETED', label: 'Completed' },
    { value: 'CANCELLED', label: 'Cancelled' },
    { value: 'NO_SHOW', label: 'No Show' }
  ];
  
  // ========================================================================
  // DATA MANAGEMENT & REACTIVITY
  // ========================================================================

  $: sessions = $sessionsStore.content;
  $: pagination = $sessionsStore;

  // --- Core function to fetch session list ---
  async function loadSessions() {
    isFetching = true;
    const sort = `${sortColumn},${sortDirection}`;
    
    await sessionsStore.fetchSessions({
      page: currentPage, searchTerm, status: statusFilter,
      type: communicationTypeFilter, sort
    });

    isFetching = false;
    initialLoad = false;
  }

  // --- NEW: Function to fetch statistics ---
  async function loadStats() {
    // You will need to implement this endpoint in your backend
    // For now, this will fail gracefully if the endpoint doesn't exist.
    try {
      const response = await fetch('http://localhost:8090/api/management/sessions/stats');
      if (!response.ok) throw new Error('Stats endpoint not found');
      sessionStats = await response.json();
    } catch (e) {
      console.error("Could not load session stats:", e);
      // Set default stats so the UI doesn't break
      sessionStats = { totalSessions: pagination.totalElements, activeNow: 0, completed: 0, scheduled: 0 };
    }
  }

  // --- CORRECTED: Simplified reactive logic ---
  onMount(() => {
    loadSessions();
    loadStats();
  });

  let debounceTimer: number;
  // This single block now handles all filter/sort changes
  $: {
    const _ = { searchTerm, communicationTypeFilter, statusFilter, sortColumn, sortDirection };
    
    // Don't run on the very first render, onMount handles that
    if (!initialLoad) {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        currentPage = 0; // Reset to page 0
        loadSessions();
        loadStats(); // Reload stats with the new filters
      }, 350);
    }
  }
  
  // This block handles page changes instantly
  $: if (!initialLoad && currentPage !== $sessionsStore.number) {
    const _ = currentPage;
    loadSessions();
  }

  // ========================================================================
  // EVENT HANDLERS & HELPERS (functions below are mostly unchanged)
  // ========================================================================
  
  function handleSort(column: string) {
    if (sortColumn === column) {
      sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      sortColumn = column;
      sortDirection = 'desc';
    }
  }

  async function handleDelete() {
    if (sessionToDelete) {
      await sessionsStore.delete(sessionToDelete.sessionId);
      showDeleteModal = false;
      sessionToDelete = null;
      loadSessions(); 
      loadStats();
    }
  }
  // ... (confirmDelete, cancelDelete, viewSession, closeSessionModal, exportSessions functions are unchanged)
  function confirmDelete(session: Session) { sessionToDelete = session; showDeleteModal = true; }
  function cancelDelete() { showDeleteModal = false; sessionToDelete = null; }
  function viewSession(session: Session) { selectedSession = session; showSessionModal = true; }
  function closeSessionModal() { showSessionModal = false; selectedSession = null; }
  function exportSessions() {
    const csvContent = [
      ['Session ID', 'Type', 'Start Time', 'End Time', 'Status', 'Patient Handle', 'Therapist', 'Rating', 'Duration'],
      ...sessions.map(s => [
        `SES-${s.sessionId}`, s.communicationType, s.startTime || '', s.endTime || '', s.status,
        s.patientHandle, s.therapistName, s.rating ?? '', s.durationInMinutes ?? ''
      ])
    ].map(row => row.join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `sessions-page-${currentPage + 1}-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  }
  function getStatusClass(status: string) {
    switch (status?.toLowerCase()) {
      case 'scheduled': return 'status-scheduled';
      case 'active': return 'status-active';
      case 'completed': return 'status-completed';
      case 'cancelled': return 'status-cancelled';
      case 'no_show': return 'status-no-show';
      default: return 'status-scheduled';
    }
  }
  function getCommunicationTypeIcon(type: string) {
    switch (type?.toLowerCase()) {
      case 'video': return '📹'; case 'audio': return '🎧'; case 'chat': return '💬';
      case 'in-person': return '🏢'; case 'in_person': return '🏢';
      default: return '📞';
    }
  }
  function formatDateTime(dateTime: string | null) {
    if (!dateTime) return 'N/A';
    return dateTime;
  }
  function renderStars(rating: number | null) {
    if (rating === null || rating === undefined) return 'Not rated';
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }

    $: activeCount = sessions.filter(s => s.status.toUpperCase() === 'ACTIVE').length;
  $: completedCount = sessions.filter(s => s.status.toUpperCase() === 'COMPLETED').length;
  $: scheduledCount = sessions.filter(s => s.status.toUpperCase() === 'SCHEDULED').length;
</script>

<div class="session-management">
  <div class="page-header">
    <div class="header-content">
      <h1 class="page-title">Session Management</h1>
      <p class="page-subtitle">Monitor and manage therapy sessions</p>
    </div>
    <div class="header-actions">
      <button class="action-btn export" on:click={exportSessions}>
        <Download size={20} />
        Export Page
      </button>
      <button class="action-btn refresh" on:click={loadSessions} disabled={isFetching}>
        <span class:rotating={isFetching}>
          <RefreshCw size={20} />
        </span>
        Refresh
      </button>
    </div>
  </div>

    <!-- NEW/RESTORED: Stat Boxes Section -->
  <div class="sessions-stats">
    {#if sessionStats}
      <div class="stat-card">
        <span class="stat-value">{sessionStats.totalSessions}</span>
        <span class="stat-label">Total Sessions</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{activeCount}</span>
        <span class="stat-label">Active Now</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{completedCount}</span>
        <span class="stat-label">Completed</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{scheduledCount}</span>
        <span class="stat-label">Scheduled</span>
      </div>
    {:else}
      <!-- Loading state for stats -->
      {#each { length: 4 } as _}
        <div class="stat-card placeholder">
          <span class="stat-value">-</span>
          <span class="stat-label">Loading...</span>
        </div>
      {/each}
    {/if}
  </div>

  <div class="filters-section">
    <div class="search-box">
      <Search class="search-icon" size={20} />
      <input
        type="text"
        placeholder="Search sessions..."
        bind:value={searchTerm}
        class="search-input"
      />
    </div>
    
    <div class="filter-group">
      <Filter size={16} />
      <select bind:value={communicationTypeFilter} class="filter-select">
        {#each communicationTypes as type}
          <option value={type.value}>{type.label}</option>
        {/each}
      </select>
      
      <select bind:value={statusFilter} class="filter-select">
        {#each statusOptions as status}
          <option value={status.value}>{status.label}</option>
        {/each}
      </select>
    </div>
  </div>

  <div class="table-container">
    <div class="table-wrapper">
      <table class="sessions-table">
        <thead>
          <tr>
            <th class="sortable" class:sorted={sortColumn === 'sessionId'} on:click={() => handleSort('sessionId')}>
              Session ID
              <span class="sort-indicator">
                {#if sortColumn === 'sessionId'}{sortDirection === 'asc' ? '↑' : '↓'}{:else}↕{/if}
              </span>
            </th>
            <th>Type</th>
            <th class="sortable" class:sorted={sortColumn === 'startTime'} on:click={() => handleSort('startTime')}>
              Start Time
              <span class="sort-indicator">
                {#if sortColumn === 'startTime'}{sortDirection === 'asc' ? '↑' : '↓'}{:else}↕{/if}
              </span>
            </th>
            <th>End Time</th>
            <th>Status</th>
            <th>Patient Handle</th>
            <th>Therapist</th>
            <th class="sortable" class:sorted={sortColumn === 'rating'} on:click={() => handleSort('rating')}>
              Rating
              <span class="sort-indicator">
                {#if sortColumn === 'rating'}{sortDirection === 'asc' ? '↑' : '↓'}{:else}↕{/if}
              </span>
            </th>
            <th>Duration</th>
            <th>Actions</th>
          </tr>
        </thead>
        
        {#if initialLoad}
          <tbody>
            <tr>
              <td colspan="10" class="loading-state">
                <span class="rotating">
                  <RefreshCw size={24} />
                </span>
                <span>Loading sessions...</span>
              </td>
            </tr>
          </tbody>
        {:else}
          <tbody class:fetching={isFetching}>
            {#if sessions.length > 0}
              {#each sessions as session (session.sessionId)}
                <tr>
                  <td class="session-id">SES-{session.sessionId}</td>
                  <td class="communication-type">
                    <span class="type-badge" title={session.communicationType}>
                      <span class="type-icon">{getCommunicationTypeIcon(session.communicationType)}</span>
                      <span class="type-text">{session.communicationType}</span>
                    </span>
                  </td>
                  <td>{formatDateTime(session.startTime)}</td>
                  <td>{formatDateTime(session.endTime)}</td>
                  <td>
                    <span class="status-badge {getStatusClass(session.status)}">
                      <span class="status-dot"></span>
                      {session.status}
                    </span>
                  </td>
                  <td class="patient-id">{session.patientHandle}</td>
                  <td class="therapist-name">{session.therapistName}</td>
                  <td class="rating">
                    {#if session.rating}
                      <span class="stars">{renderStars(session.rating)}</span>
                    {:else}
                      <span class="no-rating">N/A</span>
                    {/if}
                  </td>
                  <td class="duration">{session.durationInMinutes ?? 'N/A'} min</td>
                  <td class="actions">
                    <button class="action-btn view" title="View Details" on:click={() => viewSession(session)}>
                      <Eye size={16} />
                    </button>
                    <button class="action-btn delete" title="Delete Session" on:click={() => confirmDelete(session)}>
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              {/each}
            {:else}
              <tr>
                <td colspan="10">
                  <div class="empty-state">
                    <Calendar size={48} />
                    <h3>No Sessions Found</h3>
                    <p>No sessions match your current search and filter criteria.</p>
                  </div>
                </td>
              </tr>
            {/if}
          </tbody>
        {/if}
      </table>
    </div>

    <div class="pagination-controls">
      <span class="total-count">
        Total Sessions: <strong>{pagination.totalElements}</strong>
      </span>
      <div class="page-switcher">
        <button class="page-btn" on:click={() => currentPage--} disabled={pagination.first || isFetching}>
          &larr; Previous
        </button>
        <span class="page-indicator">
          Page <strong>{pagination.number + 1}</strong> of <strong>{pagination.totalPages}</strong>
        </span>
        <button class="page-btn" on:click={() => currentPage++} disabled={pagination.last || isFetching}>
          Next &rarr;
        </button>
      </div>
    </div>
  </div>
</div>

<!-- MODALS -->
{#if showSessionModal && selectedSession}
  <div class="modal-overlay" on:click={closeSessionModal}>
    <div class="modal session-modal" on:click|stopPropagation>
      <div class="modal-header">
        <h3>Session Details (SES-{selectedSession.sessionId})</h3>
        <button class="close-btn" on:click={closeSessionModal}>×</button>
      </div>
      <div class="modal-content">
        <div class="session-details">
          <div class="detail-row"><span class="detail-label">Status:</span><span class="status-badge {getStatusClass(selectedSession.status)}"><span class="status-dot"></span>{selectedSession.status}</span></div>
          <div class="detail-row"><span class="detail-label">Patient Handle:</span><span class="detail-value">{selectedSession.patientHandle}</span></div>
          <div class="detail-row"><span class="detail-label">Therapist:</span><span class="detail-value">{selectedSession.therapistName}</span></div>
          <div class="detail-row"><span class="detail-label">Start Time:</span><span class="detail-value">{formatDateTime(selectedSession.startTime)}</span></div>
          <div class="detail-row"><span class="detail-label">End Time:</span><span class="detail-value">{formatDateTime(selectedSession.endTime)}</span></div>
          <div class="detail-row"><span class="detail-label">Duration:</span><span class="detail-value">{selectedSession.durationInMinutes ?? 'N/A'} minutes</span></div>
          <div class="detail-row"><span class="detail-label">Rating:</span><span class="detail-value">{renderStars(selectedSession.rating)}</span></div>
        </div>
      </div>
    </div>
  </div>
{/if}

{#if showDeleteModal}
  <div class="modal-overlay" on:click={cancelDelete}>
    <div class="modal delete-modal" on:click|stopPropagation>
      <h3>Confirm Deletion</h3>
      <p>Are you sure you want to delete session <strong>SES-{sessionToDelete?.sessionId}</strong>?</p>
      <p class="warning">This action cannot be undone.</p>
      <div class="modal-actions">
        <button class="btn-cancel" on:click={cancelDelete}>Cancel</button>
        <button class="btn-delete" on:click={handleDelete}>Delete Session</button>
      </div>
    </div>
  </div>
{/if}

<style>
  :root {
    --primary-color: #2563eb;
    --primary-color-dark: #1d4ed8;
    --primary-color-light: #eff6ff;
    --primary-color-text: #1e40af;
    
    --success-color: #16a34a;
    --success-color-light: #f0fdf4;
    --success-color-text: #15803d;

    --danger-color: #dc2626;
    --danger-color-light: #fee2e2;
    --danger-color-text: #991b1b;
    
    --warning-color: #f59e0b;
    --warning-color-light: #fef3c7;
    --warning-color-text: #b45309;

    --active-color: #22c55e;
    --active-color-light: #dcfce7;
    --active-color-text: #166534;

    --border-color: #e5e7eb;
    --bg-color-light: #f9fafb;
    --text-color-primary: #111827;
    --text-color-secondary: #4b5563;
    --text-color-muted: #6b7280;
    --card-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
    --card-border-radius: 0.75rem; /* 12px */
  }

  /* === Page Layout & Typography === */
  .session-management {
    padding: 2rem;
    max-width: 1600px;
    margin: 0 auto;
    background-color: var(--bg-color-light);
    font-family: 'Inter', sans-serif;
    margin-left: 280px; /* Adjust based on sidebar width */
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 2rem;
  }

  .page-title {
    font-size: 1.875rem; /* 30px */
    font-weight: 700;
    color: var(--text-color-primary);
    margin: 0 0 0.25rem 0;
  }

  .page-subtitle {
    color: var(--text-color-secondary);
    font-size: 1rem;
    margin: 0;
  }

  /* === Header Actions === */
  .header-actions {
    display: flex;
    gap: 1rem;
  }

  .header-actions .action-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.625rem 1rem; /* 10px 16px */
    border: none;
    border-radius: 0.5rem; /* 8px */
    font-weight: 600;
    font-size: 0.875rem;
    cursor: pointer;
    transition: background-color 0.2s, box-shadow 0.2s, transform 0.1s;
    box-shadow: var(--card-shadow);
  }
  
  .header-actions .action-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 6px -1px rgba(0,0,0,.1), 0 2px 4px -2px rgba(0,0,0,.1);
  }

  .header-actions .action-btn.export {
    background: var(--success-color);
    color: white;
  }

  .header-actions .action-btn.export:hover {
    background: #059669;
  }

  .header-actions .action-btn.refresh {
    background: white;
    color: var(--text-color-secondary);
    border: 1px solid var(--border-color);
  }

  .header-actions .action-btn.refresh:hover {
    background: #f9fafb;
    border-color: #d1d5db;
  }

  /* === Filters & Search Section === */
  .filters-section {
    display: flex;
    gap: 1.5rem;
    margin-bottom: 2rem;
    align-items: center;
    background-color: white;
    padding: 1rem;
    border-radius: var(--card-border-radius);
    border: 1px solid var(--border-color);
  }

  .search-box {
    position: relative;
    flex-grow: 1;
  }

  .search-icon {
    position: absolute;
    left: 0.75rem;
    top: 50%;
    transform: translateY(-50%);
    color: #9ca3af;
    pointer-events: none;
  }

  .search-input {
    width: 70%;
    padding: 0.625rem 1rem 0.625rem 2.5rem;
    border: 1px solid var(--border-color);
    border-radius: 0.5rem;
    font-size: 0.875rem;
    transition: border-color 0.2s, box-shadow 0.2s;
  }

  .search-input:focus {
    outline: none;
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
  }

  .filter-group {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: var(--text-color-muted);
  }

  .filter-select {
    padding: 0.625rem 1rem;
    border: 1px solid var(--border-color);
    border-radius: 0.5rem;
    background: white;
    cursor: pointer;
    min-width: 150px;
    font-size: 0.875rem;
  }

  /* === Stats Cards === */
  .sessions-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  .stat-card {
    background: white;
    border-radius: var(--card-border-radius);
    padding: 1.5rem;
    box-shadow: var(--card-shadow);
    border: 1px solid var(--border-color);
  }

  .stat-value {
    display: block;
    font-size: 2.25rem;
    font-weight: 700;
    color: var(--text-color-primary);
    margin-bottom: 0.5rem;
  }

  .stat-label {
    color: var(--text-color-secondary);
    font-size: 0.875rem;
    font-weight: 500;
  }

  /* === Table Styling === */
  .table-container {
    background: white;
    border-radius: var(--card-border-radius);
    box-shadow: var(--card-shadow);
    border: 1px solid var(--border-color);
    overflow: hidden;
  }

  .table-wrapper {
    overflow-x: auto;
  }

  .sessions-table {
    width: 100%;
    border-collapse: collapse;
    min-width: 1200px;
  }

  .sessions-table th {
    background: var(--bg-color-light);
    padding: 0.75rem 1.5rem;
    text-align: left;
    font-size: 0.75rem;
    font-weight: 600;
    color: var(--text-color-muted);
    border-bottom: 1px solid var(--border-color);
    text-transform: uppercase;
    letter-spacing: 0.05em;
    white-space: nowrap;
  }

  .sessions-table th.sortable {
    cursor: pointer;
    transition: background-color 0.2s, color 0.2s;
  }

  .sessions-table th.sortable:hover {
    background: #f3f4f6;
    color: var(--text-color-primary);
  }

  .sessions-table th.sorted {
    background: var(--primary-color-light);
    color: var(--primary-color-dark);
  }

  .sort-indicator {
    margin-left: 0.5rem;
    opacity: 0.7;
  }

  .sessions-table td {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid var(--border-color);
    font-size: 0.875rem;
    color: var(--text-color-secondary);
    vertical-align: middle;
  }

  .sessions-table tr:last-child td {
      border-bottom: none;
  }

  .sessions-table tr:hover {
    background: #f9fafb;
  }

  /* Table Cell Specific Styles */
  .session-id {
    font-weight: 600;
    color: var(--primary-color);
  }

  .type-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.25rem 0.75rem;
    background: #f3f4f6;
    border-radius: 9999px;
    font-size: 0.75rem;
    font-weight: 500;
    text-transform: capitalize;
    color: #374151;
  }

  .status-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.25rem 0.75rem;
    border-radius: 9999px;
    font-size: 0.75rem;
    font-weight: 500;
    text-transform: capitalize;
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .status-scheduled { background: var(--primary-color-light); color: var(--primary-color-text); }
  .status-scheduled .status-dot { background: var(--primary-color); }

  .status-active { background: var(--active-color-light); color: var(--active-color-text); }
  .status-active .status-dot { background: var(--active-color); animation: pulse 2s infinite; }
  @keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }

  .status-completed { background: var(--success-color-light); color: var(--success-color-text); }
  .status-completed .status-dot { background: var(--success-color); }
  
  .status-cancelled { background: var(--warning-color-light); color: var(--warning-color-text); }
  .status-cancelled .status-dot { background: var(--warning-color); }

  .status-no-show { background: var(--danger-color-light); color: var(--danger-color-text); }
  .status-no-show .status-dot { background: var(--danger-color); }

  .patient-name { font-weight: 600; color: var(--text-color-primary); }
  .patient-id { font-size: 0.75rem; color: var(--text-color-muted); margin-top: 0.125rem; }
  .therapist-name { font-weight: 500; color: #374151; }
  .stars { color: #f59e0b; }
  .no-rating { color: #9ca3af; font-size: 0.75rem; }
  
  /* === Action Buttons in Table === */
  .actions { display: flex; gap: 0.5rem; }
  .actions .action-btn {
    display: grid;
    place-items: center;
    width: 32px;
    height: 32px;
    border: none;
    border-radius: 50%;
    cursor: pointer;
    transition: background-color 0.2s, transform 0.1s;
  }
  .actions .action-btn:hover { transform: scale(1.1); }

  .actions .action-btn.view { background: var(--primary-color-light); color: var(--primary-color); }
  .actions .action-btn.view:hover { background: #dbeafe; }
  .actions .action-btn.delete { background: var(--danger-color-light); color: var(--danger-color); }
  .actions .action-btn.delete:hover { background: #fecaca; }

  /* === Empty State === */
  .empty-state {
    text-align: center;
    padding: 4rem 2rem;
    color: var(--text-color-muted);
    background: white;
    border: 1px dashed var(--border-color);
    border-radius: var(--card-border-radius);
    margin-top: 2rem;
  }
  .empty-state h3 { font-size: 1.25rem; color: var(--text-color-primary); margin: 1rem 0 0.5rem 0; }


  /* Pagination */
  .pagination-controls {
    display: flex; justify-content: space-between; align-items: center;
    padding: 1rem 1.25rem; background-color: #f9fafb;
    flex-wrap: wrap; gap: 1rem;
  }
  .total-count, .page-indicator { font-size: 0.875rem; color: #4b5563; }
  .page-switcher { display: flex; align-items: center; gap: 0.5rem; }
  .page-btn {
    padding: 0.5rem 1rem; border: 1px solid #d1d5db;
    border-radius: 6px; background-color: white; cursor: pointer;
  }
  .page-btn:disabled { cursor: not-allowed; opacity: 0.5; }

  /* === Modal Styling === */
  .modal-overlay {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(17, 24, 39, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    backdrop-filter: blur(4px);
  }

  .modal {
    background: white;
    border-radius: 0.75rem;
    width: 90%;
    box-shadow: 0 20px 25px -5px rgba(0,0,0,.1), 0 8px 10px -6px rgba(0,0,0,.1);
    max-height: 90vh;
    display: flex;
    flex-direction: column;
  }

  .session-modal { max-width: 600px; }
  .delete-modal { max-width: 420px; text-align: center; padding: 2rem; }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid var(--border-color);
  }
  .modal-header h3 { margin: 0; color: var(--text-color-primary); }

  .close-btn {
    background: none; border: none; font-size: 1.5rem; cursor: pointer;
    color: var(--text-color-muted); padding: 0.5rem; line-height: 1;
  }

  .modal-content { padding: 1.5rem; overflow-y: auto; }
  .session-details { display: flex; flex-direction: column; gap: 0.75rem; }
  .detail-row {
    display: grid;
    grid-template-columns: 140px 1fr;
    align-items: center;
    padding: 0.5rem 0;
  }
  .detail-label { font-weight: 500; color: var(--text-color-secondary); }
  .detail-value { color: var(--text-color-primary); }

  /* Delete Modal Specific */
  .delete-modal h3 { font-size: 1.25rem; margin: 0 0 0.5rem; color: var(--text-color-primary); }
  .delete-modal p { margin: 0 0 0.5rem; color: var(--text-color-secondary); }
  .warning { color: var(--danger-color-text); font-weight: 500; }
  .modal-actions { display: flex; gap: 1rem; justify-content: center; margin-top: 1.5rem; }
  .modal-actions button {
    padding: 0.625rem 1.5rem;
    border-radius: 0.5rem;
    font-weight: 600;
    cursor: pointer;
    border: 1px solid transparent;
  }
  .btn-cancel { background: white; color: #374151; border-color: var(--border-color); }
  .btn-delete { background: var(--danger-color); color: white; }

  /* === Responsive Design === */
  @media (max-width: 768px) {
    .session-management { padding: 1rem; }
    .page-header, .filters-section { flex-direction: column; gap: 1rem; align-items: stretch; }
    .sessions-stats { grid-template-columns: 1fr 1fr; }
    .modal { width: 95%; margin: 1rem; }
  }
</style>