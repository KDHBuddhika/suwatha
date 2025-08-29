<script lang="ts">
  import { sessionsStore, type Session } from '../stores/sessions';
  import { Search, Filter, Calendar, Clock, Star, Eye, Edit, Trash2, Plus, Download, RefreshCw } from 'lucide-svelte';

  let searchTerm = '';
  let communicationTypeFilter = 'all';
  let statusFilter = 'all';
  let sortColumn = 'startTime';
  let sortDirection: 'asc' | 'desc' = 'desc';
  let showSessionModal = false;
  let selectedSession: Session | null = null;
  let showDeleteModal = false;
  let sessionToDelete: Session | null = null;

  const communicationTypes = [
    { value: 'all', label: 'All Types' },
    { value: 'video', label: 'Video Call' },
    { value: 'audio', label: 'Audio Call' },
    { value: 'chat', label: 'Chat' },
    { value: 'in-person', label: 'In-Person' }
  ];

  const statusOptions = [
    { value: 'all', label: 'All Status' },
    { value: 'scheduled', label: 'Scheduled' },
    { value: 'active', label: 'Active' },
    { value: 'completed', label: 'Completed' },
    { value: 'cancelled', label: 'Cancelled' },
    { value: 'no-show', label: 'No Show' }
  ];

  $: filteredSessions = $sessionsStore
    .filter(session => {
      const matchesSearch = 
        session.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
        session.therapistName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        session.patientName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        session.patientId.toLowerCase().includes(searchTerm.toLowerCase());
      
      const matchesCommunicationType = communicationTypeFilter === 'all' || session.communicationType === communicationTypeFilter;
      const matchesStatus = statusFilter === 'all' || session.status === statusFilter;
      
      return matchesSearch && matchesCommunicationType && matchesStatus;
    })
    .sort((a, b) => {
      let aVal, bVal;
      
      if (sortColumn === 'startTime' || sortColumn === 'endTime') {
        aVal = new Date(a[sortColumn]).getTime();
        bVal = new Date(b[sortColumn]).getTime();
      } else if (sortColumn === 'id') {
        aVal = parseInt(a.id.split('-')[1]);
        bVal = parseInt(b.id.split('-')[1]);
      } else {
        aVal = a[sortColumn as keyof Session];
        bVal = b[sortColumn as keyof Session];
      }
      
      if (sortDirection === 'asc') {
        return aVal < bVal ? -1 : aVal > bVal ? 1 : 0;
      } else {
        return aVal > bVal ? -1 : aVal < bVal ? 1 : 0;
      }
    });

  function handleSort(column: string) {
    if (sortColumn === column) {
      sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      sortColumn = column;
      sortDirection = 'asc';
    }
  }

  function getStatusClass(status: string) {
    switch (status) {
      case 'scheduled': return 'status-scheduled';
      case 'active': return 'status-active';
      case 'completed': return 'status-completed';
      case 'cancelled': return 'status-cancelled';
      case 'no-show': return 'status-no-show';
      default: return 'status-scheduled';
    }
  }

  function getCommunicationTypeIcon(type: string) {
    switch (type) {
      case 'video': return '📹';
      case 'audio': return '🎧';
      case 'chat': return '💬';
      case 'in-person': return '🏢';
      default: return '📞';
    }
  }

  function formatDateTime(dateTime: string) {
    return new Date(dateTime).toLocaleString('en-US', {
      month: 'short',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }

  function calculateDuration(startTime: string, endTime: string) {
    const start = new Date(startTime);
    const end = new Date(endTime);
    return Math.round((end.getTime() - start.getTime()) / (1000 * 60));
  }

  function viewSession(session: Session) {
    selectedSession = session;
    showSessionModal = true;
  }

  function confirmDelete(session: Session) {
    sessionToDelete = session;
    showDeleteModal = true;
  }

  function handleDelete() {
    if (sessionToDelete) {
      sessionsStore.delete(sessionToDelete.id);
      showDeleteModal = false;
      sessionToDelete = null;
    }
  }

  function cancelDelete() {
    showDeleteModal = false;
    sessionToDelete = null;
  }

  function closeSessionModal() {
    showSessionModal = false;
    selectedSession = null;
  }

  function exportSessions() {
    const csvContent = [
      ['Session ID', 'Communication Type', 'Start Time', 'End Time', 'Status', 'Patient ID', 'Patient Name', 'Therapist', 'Rating', 'Duration (min)'],
      ...filteredSessions.map(session => [
        session.id,
        session.communicationType,
        session.startTime,
        session.endTime,
        session.status,
        session.patientId,
        session.patientName || '',
        session.therapistName,
        session.rating || '',
        session.duration || calculateDuration(session.startTime, session.endTime)
      ])
    ].map(row => row.join(',')).join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `sessions-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  function renderStars(rating: number | null) {
    if (!rating) return 'Not rated';
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }
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
        Export CSV
      </button>
      <button class="action-btn refresh" on:click={() => location.reload()}>
        <RefreshCw size={20} />
        Refresh
      </button>
    </div>
  </div>

  <div class="filters-section">
    <div class="search-box">
      <Search class="search-icon" size={20} />
      <input
        type="text"
        placeholder="Search by session ID, therapist, patient name, or patient ID..."
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

  <div class="sessions-stats">
    <div class="stat-card">
      <span class="stat-value">{filteredSessions.length}</span>
      <span class="stat-label">Total Sessions</span>
    </div>
    <div class="stat-card">
      <span class="stat-value">{filteredSessions.filter(s => s.status === 'active').length}</span>
      <span class="stat-label">Active Now</span>
    </div>
    <div class="stat-card">
      <span class="stat-value">{filteredSessions.filter(s => s.status === 'completed').length}</span>
      <span class="stat-label">Completed</span>
    </div>
    <div class="stat-card">
      <span class="stat-value">{filteredSessions.filter(s => s.status === 'scheduled').length}</span>
      <span class="stat-label">Scheduled</span>
    </div>
  </div>

  <div class="table-container">
    <div class="table-wrapper">
      <table class="sessions-table">
        <thead>
          <tr>
            <th class="sortable" class:sorted={sortColumn === 'id'} on:click={() => handleSort('id')}>
              Session ID
              <span class="sort-indicator">
                {#if sortColumn === 'id'}
                  {sortDirection === 'asc' ? '↑' : '↓'}
                {:else}
                  ↕
                {/if}
              </span>
            </th>
            <th>Communication Type</th>
            <th class="sortable" class:sorted={sortColumn === 'startTime'} on:click={() => handleSort('startTime')}>
              Start Time
              <span class="sort-indicator">
                {#if sortColumn === 'startTime'}
                  {sortDirection === 'asc' ? '↑' : '↓'}
                {:else}
                  ↕
                {/if}
              </span>
            </th>
            <th class="sortable" class:sorted={sortColumn === 'endTime'} on:click={() => handleSort('endTime')}>
              End Time
              <span class="sort-indicator">
                {#if sortColumn === 'endTime'}
                  {sortDirection === 'asc' ? '↑' : '↓'}
                {:else}
                  ↕
                {/if}
              </span>
            </th>
            <th>Status</th>
            <th>Patient</th>
            <th>Therapist</th>
            <th>Rating</th>
            <th>Duration</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {#each filteredSessions as session (session.id)}
            <tr>
              <td class="session-id">{session.id}</td>
              <td class="communication-type">
                <span class="type-badge">
                  <span class="type-icon">{getCommunicationTypeIcon(session.communicationType)}</span>
                  {session.communicationType}
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
              <td class="patient-info">
                <div class="patient-name">{session.patientName || 'Unknown'}</div>
                <div class="patient-id">{session.patientId}</div>
              </td>
              <td class="therapist-name">{session.therapistName}</td>
              <td class="rating">
                {#if session.rating}
                  <span class="stars">{renderStars(session.rating)}</span>
                  <span class="rating-value">({session.rating}/5)</span>
                {:else}
                  <span class="no-rating">Not rated</span>
                {/if}
              </td>
              <td class="duration">
                {session.duration || calculateDuration(session.startTime, session.endTime)} min
              </td>
              <td class="actions">
                <button class="action-btn view" on:click={() => viewSession(session)}>
                  <Eye size={16} />
                </button>
                <button class="action-btn delete" on:click={() => confirmDelete(session)}>
                  <Trash2 size={16} />
                </button>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  </div>

  {#if filteredSessions.length === 0}
    <div class="empty-state">
      <Calendar size={48} />
      <h3>No sessions found</h3>
      <p>No sessions match your current search and filter criteria.</p>
    </div>
  {/if}
</div>

<!-- Session Details Modal -->
{#if showSessionModal && selectedSession}
  <div class="modal-overlay" on:click={closeSessionModal}>
    <div class="modal session-modal" on:click|stopPropagation>
      <div class="modal-header">
        <h3>Session Details</h3>
        <button class="close-btn" on:click={closeSessionModal}>×</button>
      </div>
      <div class="modal-content">
        <div class="session-details">
          <div class="detail-row">
            <span class="detail-label">Session ID:</span>
            <span class="detail-value">{selectedSession.id}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Communication Type:</span>
            <span class="detail-value">
              {getCommunicationTypeIcon(selectedSession.communicationType)} {selectedSession.communicationType}
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Status:</span>
            <span class="status-badge {getStatusClass(selectedSession.status)}">
              <span class="status-dot"></span>
              {selectedSession.status}
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Patient:</span>
            <span class="detail-value">{selectedSession.patientName} ({selectedSession.patientId})</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Therapist:</span>
            <span class="detail-value">{selectedSession.therapistName}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Start Time:</span>
            <span class="detail-value">{new Date(selectedSession.startTime).toLocaleString()}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">End Time:</span>
            <span class="detail-value">{new Date(selectedSession.endTime).toLocaleString()}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Duration:</span>
            <span class="detail-value">{selectedSession.duration || calculateDuration(selectedSession.startTime, selectedSession.endTime)} minutes</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Rating:</span>
            <span class="detail-value">
              {#if selectedSession.rating}
                {renderStars(selectedSession.rating)} ({selectedSession.rating}/5)
              {:else}
                Not rated
              {/if}
            </span>
          </div>
          {#if selectedSession.notes}
            <div class="detail-row notes">
              <span class="detail-label">Notes:</span>
              <span class="detail-value">{selectedSession.notes}</span>
            </div>
          {/if}
        </div>
      </div>
    </div>
  </div>
{/if}

<!-- Delete Confirmation Modal -->
{#if showDeleteModal}
  <div class="modal-overlay" on:click={cancelDelete}>
    <div class="modal delete-modal" on:click|stopPropagation>
      <h3>Confirm Delete</h3>
      <p>Are you sure you want to delete session <strong>{sessionToDelete?.id}</strong>?</p>
      <p class="warning">This action cannot be undone.</p>
      <div class="modal-actions">
        <button class="btn-cancel" on:click={cancelDelete}>Cancel</button>
        <button class="btn-delete" on:click={handleDelete}>Delete</button>
      </div>
    </div>
  </div>
{/if}

<style>
  .session-management {
    padding: 2rem;
    max-width: 1600px;
    margin: 0 auto;
    margin-left: 280px; /* Adjust for sidebar width */
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 2rem;
  }

  .page-title {
    font-size: 2rem;
    font-weight: bold;
    color: #111827;
    margin: 0 0 0.5rem 0;
  }

  .page-subtitle {
    color: #6b7280;
    font-size: 1rem;
    margin: 0;
  }

  .header-actions {
    display: flex;
    gap: 1rem;
  }

  .action-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }

  .action-btn.export {
    background: #059669;
    color: white;
  }

  .action-btn.export:hover {
    background: #047857;
  }

  .action-btn.refresh {
    background: #6b7280;
    color: white;
  }

  .action-btn.refresh:hover {
    background: #4b5563;
  }

  .filters-section {
    display: flex;
    gap: 1rem;
    margin-bottom: 2rem;
    align-items: center;
  }

  .search-box {
    position: relative;
    flex: 1;
    max-width: 500px;
  }

  .search-icon {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #9ca3af;
  }

  .search-input {
    width: 70%;
    padding: 0.75rem 1rem 0.75rem 3rem;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    font-size: 1rem;
    transition: border-color 0.2s;
  }

  .search-input:focus {
    outline: none;
    border-color: #2563eb;
  }

  .filter-group {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #6b7280;
  }

  .filter-select {
    padding: 0.75rem;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    background: white;
    cursor: pointer;
    min-width: 150px;
  }

  .sessions-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .stat-card {
    background: white;
    border-radius: 8px;
    padding: 1.5rem;
    text-align: center;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e5e7eb;
  }

  .stat-value {
    display: block;
    font-size: 2rem;
    font-weight: bold;
    color: #2563eb;
    margin-bottom: 0.5rem;
  }

  .stat-label {
    color: #6b7280;
    font-size: 0.875rem;
    font-weight: 600;
  }

  .table-container {
    background: white;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e5e7eb;
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
    background: #f9fafb;
    padding: 1rem;
    text-align: left;
    font-weight: 600;
    color: #374151;
    border-bottom: 2px solid #e5e7eb;
    font-size: 0.875rem;
    white-space: nowrap;
  }

  .sessions-table th.sortable {
    cursor: pointer;
    user-select: none;
    transition: background-color 0.2s;
  }

  .sessions-table th.sortable:hover {
    background: #f3f4f6;
  }

  .sessions-table th.sorted {
    background: #eff6ff;
    color: #2563eb;
  }

  .sort-indicator {
    margin-left: 0.5rem;
    font-size: 0.75rem;
    opacity: 0.6;
  }

  .sessions-table td {
    padding: 1rem;
    border-bottom: 1px solid #e5e7eb;
    font-size: 0.875rem;
    color: #111827;
    vertical-align: middle;
  }

  .sessions-table tr:hover {
    background: #f9fafb;
  }

  .session-id {
    font-weight: 600;
    color: #2563eb;
  }

  .type-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.25rem 0.75rem;
    background: #f3f4f6;
    border-radius: 16px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: capitalize;
  }

  .type-icon {
    font-size: 1rem;
  }

  .status-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.25rem 0.75rem;
    border-radius: 16px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: capitalize;
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .status-scheduled {
    background: #dbeafe;
    color: #1d4ed8;
  }

  .status-scheduled .status-dot {
    background: #3b82f6;
  }

  .status-active {
    background: #dcfce7;
    color: #166534;
  }

  .status-active .status-dot {
    background: #22c55e;
    animation: pulse 2s infinite;
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  }

  .status-completed {
    background: #f0fdf4;
    color: #15803d;
  }

  .status-completed .status-dot {
    background: #16a34a;
  }

  .status-cancelled {
    background: #fef3c7;
    color: #d97706;
  }

  .status-cancelled .status-dot {
    background: #f59e0b;
  }

  .status-no-show {
    background: #fee2e2;
    color: #991b1b;
  }

  .status-no-show .status-dot {
    background: #dc2626;
  }

  .patient-info {
    min-width: 150px;
  }

  .patient-name {
    font-weight: 600;
    color: #111827;
  }

  .patient-id {
    font-size: 0.75rem;
    color: #6b7280;
    margin-top: 0.25rem;
  }

  .therapist-name {
    font-weight: 500;
    color: #374151;
  }

  .rating {
    min-width: 120px;
  }

  .stars {
    color: #fbbf24;
    font-size: 1rem;
    margin-right: 0.5rem;
  }

  .rating-value {
    font-size: 0.75rem;
    color: #6b7280;
  }

  .no-rating {
    color: #9ca3af;
    font-style: italic;
  }

  .duration {
    font-weight: 500;
    color: #374151;
  }

  .actions {
    display: flex;
    gap: 0.5rem;
  }

  .action-btn.view {
    background: #eff6ff;
    color: #2563eb;
    padding: 0.5rem;
    border-radius: 6px;
  }

  .action-btn.view:hover {
    background: #dbeafe;
  }

  .action-btn.delete {
    background: #fee2e2;
    color: #dc2626;
    padding: 0.5rem;
    border-radius: 6px;
  }

  .action-btn.delete:hover {
    background: #fecaca;
  }

  .empty-state {
    text-align: center;
    padding: 4rem 2rem;
    color: #6b7280;
  }

  .empty-state h3 {
    font-size: 1.5rem;
    color: #374151;
    margin: 1rem 0 0.5rem 0;
  }

  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }

  .modal {
    background: white;
    border-radius: 12px;
    max-width: 600px;
    width: 90%;
    max-height: 80vh;
    overflow-y: auto;
  }

  .session-modal {
    max-width: 700px;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-bottom: 1px solid #e5e7eb;
  }

  .modal-header h3 {
    margin: 0;
    color: #111827;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #6b7280;
    padding: 0.25rem;
  }

  .close-btn:hover {
    color: #374151;
  }

  .modal-content {
    padding: 1.5rem;
  }

  .session-details {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .detail-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem 0;
    border-bottom: 1px solid #f3f4f6;
  }

  .detail-row.notes {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .detail-label {
    font-weight: 600;
    color: #374151;
    min-width: 120px;
  }

  .detail-value {
    color: #111827;
    text-align: right;
  }

  .detail-row.notes .detail-value {
    text-align: left;
    background: #f9fafb;
    padding: 0.75rem;
    border-radius: 6px;
    width: 100%;
  }

  .delete-modal {
    max-width: 400px;
    padding: 2rem;
  }

  .delete-modal h3 {
    margin: 0 0 1rem 0;
    color: #111827;
  }

  .delete-modal p {
    margin: 0 0 0.5rem 0;
    color: #6b7280;
  }

  .warning {
    color: #dc2626;
    font-weight: 600;
  }

  .modal-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    margin-top: 2rem;
  }

  .btn-cancel {
    padding: 0.75rem 1.5rem;
    border: 2px solid #e5e7eb;
    background: white;
    color: #374151;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
  }

  .btn-delete {
    padding: 0.75rem 1.5rem;
    border: none;
    background: #dc2626;
    color: white;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
  }

  @media (max-width: 768px) {
    .session-management {
      padding: 1rem;
    }

    .page-header {
      flex-direction: column;
      gap: 1rem;
    }

    .filters-section {
      flex-direction: column;
      align-items: stretch;
    }

    .search-box {
      max-width: none;
    }

    .sessions-stats {
      grid-template-columns: repeat(2, 1fr);
    }

    .sessions-table {
      font-size: 0.75rem;
    }

    .sessions-table th,
    .sessions-table td {
      padding: 0.5rem;
    }

    .modal {
      width: 95%;
      margin: 1rem;
    }

    .detail-row {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.25rem;
    }

    .detail-value {
      text-align: left !important;
    }
  }
</style>