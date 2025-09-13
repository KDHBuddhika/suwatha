<script lang="ts">
  import { onMount } from 'svelte';
  import { push } from 'svelte-spa-router';
  
  // Corrected import paths
  import { authStore } from '../../stores/auth'; 
  import { sessionRequestStore } from '../stores/sessionStore';
  
  import IncomingSessionModal from './IncomingSessionModal.svelte';

  // --- COMPONENT STATE ---
  
  // State for Therapist Status
  let currentStatus = '';
  let isLoadingStatus = true;
  let statusError: string | null = null;
  
  // State for Dashboard Statistics
  let stats = { sessionsToday: 0, sessionsThisWeek: 0, averageRating: 0.0 };
  let isLoadingStats = true;
  let statsError: string | null = null;

  // Generic loading state for update operations
  let isUpdatingStatus = false;

  // Mock data for pending summaries (can be replaced with an API call later)
  const pendingSummaries = [{ id: 1, patientId: 'a1b2c3d4' }, { id: 2, patientId: 'e5f6g7h8' }];

  // --- DATA FETCHING & ACTIONS ---

  onMount(async () => {
    let authState;
    const unsubscribe = authStore.subscribe(s => { authState = s; });
    unsubscribe();

    if (!authState?.user?.id || !authState?.token) {
      statusError = "Authentication error. Cannot load page data.";
      statsError = "Authentication error. Cannot load page data.";
      isLoadingStatus = false;
      isLoadingStats = false;
      return;
    }

    const { token, user } = authState;

    // Use Promise.all to fetch status and stats concurrently
    const [statusResult, statsResult] = await Promise.allSettled([
      // Fetch #1: Get therapist details for status
      fetch(`http://localhost:8090/api/doctor/get-therapists/${user.id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      }),
      // Fetch #2: Get dashboard stats
      fetch(`http://localhost:8090/api/doctor/dashboard-stats`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
    ]);

    // Handle the result of the status fetch
    if (statusResult.status === 'fulfilled' && statusResult.value.ok) {
      const data = await statusResult.value.json();
      currentStatus = data.currentStatus;
    } else {
      statusError = "Failed to fetch your current status.";
    }
    isLoadingStatus = false;

    // Handle the result of the stats fetch
    if (statsResult.status === 'fulfilled' && statsResult.value.ok) {
      stats = await statsResult.value.json();
    } else {
      statsError = "Failed to load dashboard stats.";
    }
    isLoadingStats = false;
  });

  // Function to update the therapist's status
  async function updateStatus(newStatus: 'AVAILABLE' | 'BUSY' | 'OFFLINE') {
    if (newStatus === 'BUSY') {
      statusError = "The 'BUSY' status is set automatically by the system.";
      setTimeout(() => { statusError = null; }, 5000);
      return;
    }
    
    isUpdatingStatus = true;
    statusError = null;
    let authState;
    const unsubscribe = authStore.subscribe(s => { authState = s; });
    unsubscribe();

    try {
      const response = await fetch('http://localhost:8090/api/doctor/update-status', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authState.token}`
        },
        body: JSON.stringify({ newStatus })
      });
      if (!response.ok) throw new Error("Failed to update status.");
      const responseData = await response.json();
      currentStatus = responseData.currentStatus;
    } catch (err) {
      statusError = err.message;
    } finally {
      isUpdatingStatus = false;
    }
  }

 // 1. This function now accepts a session type to simulate different kinds of requests.
  function simulateSessionRequest(sessionType: 'VIDEO' | 'CHAT') {
    sessionRequestStore.set({ 
      patientId: `Patient-${Math.random().toString(36).substring(2, 10)}`, // Random patient ID
      sessionType: 'CHAT'
    });
  }

  // 2. The join handler now checks the session type before navigating.
  function handleJoinSession() {
    if (!$sessionRequestStore) return; // Safety check

    console.log(`Joining ${$sessionRequestStore.sessionType} session with: ${$sessionRequestStore.patientId}`);
    
    if ($sessionRequestStore.sessionType === 'VIDEO') {
      push('/live-video-session');
    } else if ($sessionRequestStore.sessionType === 'CHAT') {
      push('/chat-session'); // Navigate to the new chat page
    }
    
    sessionRequestStore.set(null); // Hide the modal after deciding where to go
  }
  function handleCancelSession() {
    sessionRequestStore.set(null);
  }

  // Reactive message that updates when `currentStatus` changes
  $: statusMessage = {
    AVAILABLE: 'You are online and ready for new sessions.',
    BUSY: 'You are currently in a session.',
    OFFLINE: 'You are offline and will not receive new session requests.'
  }[currentStatus] || 'Checking your status...';
</script>

<!-- Incoming Session Modal -->
{#if $sessionRequestStore}
  <IncomingSessionModal 
    patientId={$sessionRequestStore.patientId}
    sessionType={$sessionRequestStore.sessionType}
    on:join={handleJoinSession}
    on:cancel={handleCancelSession}
  />
{/if}

<div class="page-container" class:modal-active={$sessionRequestStore}>
  <main class="content-area">
    <!-- Status Controller Section -->
    <div class="status-controller">
      <div class="status-options">
        <button class:active={currentStatus === 'AVAILABLE'} on:click={() => updateStatus('AVAILABLE')} disabled={isLoadingStatus || isUpdatingStatus}>AVAILABLE</button>
        <button class:active={currentStatus === 'BUSY'} on:click={() => updateStatus('BUSY')} disabled={isLoadingStatus || isUpdatingStatus}>BUSY</button>
        <button class:active={currentStatus === 'OFFLINE'} on:click={() => updateStatus('OFFLINE')} disabled={isLoadingStatus || isUpdatingStatus}>OFFLINE</button>
      </div>
      {#if isLoadingStatus}
        <p class="status-message">Loading your current status...</p>
      {:else if statusError}
        <p class="status-message error">{statusError}</p>
      {:else}
        <p class="status-message">{statusMessage}</p>
      {/if}
    </div>

    <!-- Demo Button -->
    <div class="demo-controls">
      <button class="demo-button" on:click={simulateSessionRequest}>Simulate Incoming Session Request</button>
    </div>

    <!-- Stats Grid Section -->
    <div class="stats-grid">
      {#if isLoadingStats}
        <div class="stat-card placeholder"><p class="stat-label">Loading stats...</p></div>
        <div class="stat-card placeholder"><p class="stat-label">Loading stats...</p></div>
        <div class="stat-card placeholder"><p class="stat-label">Loading stats...</p></div>
      {:else if statsError}
        <div class="stat-card error-card">
          <p class="stat-label error">{statsError}</p>
        </div>
      {:else}
        <div class="stat-card"><p class="stat-label">Sessions Today</p><h3 class="stat-value">{stats.sessionsToday}</h3></div>
        <div class="stat-card"><p class="stat-label">Sessions This Week</p><h3 class="stat-value">{stats.sessionsThisWeek}</h3></div>
        <div class="stat-card"><p class="stat-label">Average Rating</p><h3 class="stat-value">{stats.averageRating} / 5.0</h3></div>
      {/if}
    </div>

    <!-- Pending Actions Section -->
    <div class="pending-actions">
      <h4>Pending Summaries</h4>
      {#if pendingSummaries.length > 0}
        <ul class="summary-list">
          {#each pendingSummaries as summary}
            <li class="summary-item">
              <span>Session with Patient-{summary.patientId} needs a summary.</span>
              <button class="add-summary-btn">Add Summary</button>
            </li>
          {/each}
        </ul>
      {:else}
        <p class="no-summaries">All caught up! No summaries are pending.</p>
      {/if}
    </div>
  </main>
</div>




<style>


  /* Applies a blur effect to the entire page when a modal is active */
  .modal-active {
    filter: blur(5px);
    pointer-events: none;
    user-select: none;
    transition: filter 0.2s ease-in-out;
  }

  /* Main content container for the page */
  .content-area {
    padding: 2.5rem;
  }

  /* =================================== */
  /* 2. Status Controller               */
  /* =================================== */

  .status-controller {
    text-align: center;
    margin-bottom: 2.5rem;
  }

  /* The container for the status toggle buttons */
  .status-options {
    display: inline-flex;
    background-color: #E5E7EB;
    border-radius: 9999px;
    padding: 0.25rem;
    margin-bottom: 0.75rem;
  }

  /* Individual status buttons */
  .status-options button {
    padding: 0.5rem 1.5rem;
    border: none;
    border-radius: 9999px;
    font-weight: 600;
    cursor: pointer;
    background-color: transparent;
    color: #6B7280;
    transition: all 0.3s;
  }

  .status-options button:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }

  /* Styling for the currently active button */
  .status-options button.active {
    background-color: #FFFFFF;
    box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  }

  /* Specific text colors for each active status */
  .status-options button.active:where(.active) { color: #10B981; /* AVAILABLE - Green */ }
  .status-options button.active[class*="BUSY"] { color: #F59E0B; /* BUSY - Amber */ }
  .status-options button.active[class*="OFFLINE"] { color: #6B7280; /* OFFLINE - Gray */ }

  /* The text message displayed below the status buttons */
  .status-message {
    color: #6B7280;
    font-size: 0.875rem;
    min-height: 20px; /* Prevents layout shifts */
  }

  .status-message.error {
    color: #DC2626;
    font-weight: 500;
  }

  /* =================================== */
  /* 3. Demo Controls                   */
  /* =================================== */

  .demo-controls {
    text-align: center;
    margin-bottom: 2.5rem;
  }

  .demo-button {
    background-color: #DBEAFE;
    color: #1E40AF;
    border: 1px solid #93C5FD;
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    cursor: pointer;
    font-weight: 500;
  }

  /* =================================== */
  /* 4. Statistics Grid                 */
  /* =================================== */

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2.5rem;
  }

  /* Individual card for each statistic */
  .stat-card {
    background-color: #FFFFFF;
    padding: 1.5rem;
    border-radius: 0.75rem;
    border: 1px solid #E5E7EB;
    text-align: center;
  }

  .stat-label {
    margin: 0 0 0.5rem 0;
    color: #6B7280;
    font-size: 0.875rem;
  }

  .stat-value {
    margin: 0;
    font-size: 2.25rem;
    font-weight: 700;
    color: #1F2937;
  }

  /* Styles for loading and error states in the stats grid */
  .stat-card.placeholder {
    display: flex;
    justify-content: center;
    align-items: center;
    color: #9CA3AF;
    background-color: #F9FAFB;
  }

  .stat-card.error-card {
    grid-column: 1 / -1; /* Make error card span full width */
    background-color: #FEF2F2;
    border-color: #FECACA;
  }

  .stat-label.error {
    color: #991B1B;
    font-weight: 500;
  }
  
  /* =================================== */
  /* 5. Pending Actions Section         */
  /* =================================== */

  .pending-actions {
    background-color: #FFFFFF;
    padding: 1.5rem;
    border-radius: 0.75rem;
    border: 1px solid #E5E7EB;
    max-width: 500px;
    margin: 0 auto;
  }

  .pending-actions h4 {
    margin-top: 0;
    margin-bottom: 1rem;
    font-size: 1.125rem;
    color: #1F2937;
  }

  .summary-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .summary-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem 0;
    border-bottom: 1px solid #E5E7EB;
    font-size: 0.875rem;
    color: #374151; /* Corrected typo from #3741Jit */
  }

  .summary-item:last-child {
    border-bottom: none;
  }

  .add-summary-btn {
    background-color: #4F46E5;
    color: #FFFFFF;
    border: none;
    border-radius: 0.375rem;
    padding: 0.5rem 1rem;
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .add-summary-btn:hover {
    background-color: #4338CA;
  }

  /* Message shown when there are no pending summaries */
  .no-summaries {
    color: #6B7280;
    font-size: 0.875rem;
    text-align: center;
    padding: 1rem 0;
  }
</style>