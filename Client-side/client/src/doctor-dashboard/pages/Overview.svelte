<script lang="ts">
  import Header from '../components/Header.svelte';
  import IncomingSessionModal from './IncomingSessionModal.svelte';
  import { sessionRequestStore } from '../stores/sessionStore'; // 1. Import the session store
    import { push } from 'svelte-spa-router'; // Import the router's push function

  let status = 'AVAILABLE';
  const stats = { sessionsToday: 4, sessionsThisWeek: 28, averageRating: 4.8 };
  const pendingSummaries = [{ id: 1, patientId: 'a1b2c3d4' }, { id: 2, patientId: 'e5f6g7h8' }];

  // 2. This function simulates a real-time event from a server
  function simulateSessionRequest() {
    sessionRequestStore.set({
      patientId: 'Patient-e5f6g7h8',
      sessionType: 'VIDEO'
    });
  }

  // 3. These functions handle the modal events and clear the store to hide it
  function handleJoinSession() {
    console.log("Joining session with:", $sessionRequestStore?.patientId);
      push('/live-video-session');
    sessionRequestStore.set(null); // Hide the modal
  }

  function handleCancelSession() {
    console.log("Cancelling session with:", $sessionRequestStore?.patientId);
    // Navigate to the live video session pagE

    // Clear the store to hide the modal
    sessionRequestStore.set(null); 
  }
</script>

<!-- 4. Conditionally render the modal based on the store's value -->
{#if $sessionRequestStore}
  <IncomingSessionModal 
    patientId={$sessionRequestStore.patientId}
    sessionType={$sessionRequestStore.sessionType}
    on:join={handleJoinSession}
    on:cancel={handleCancelSession}
  />
{/if}

<!-- 5. Apply a class to the container when the modal is active to blur the background -->
<div class="page-container" class:modal-active={$sessionRequestStore}>

  <main class="content-area">
    <div class="status-controller">
      <div class="status-options">
        <button class:active={status === 'AVAILABLE'} on:click={() => status = 'AVAILABLE'}>AVAILABLE</button>
        <button class:active={status === 'BUSY'} on:click={() => status = 'BUSY'}>BUSY</button>
        <button class:active={status === 'OFFLINE'} on:click={() => status = 'OFFLINE'}>OFFLINE</button>
      </div>
      <p class="status-message">You are online and ready for new sessions.</p>
    </div>

    <!-- DEMO BUTTON -->
    <div class="demo-controls">
      <button class="demo-button" on:click={simulateSessionRequest}>
        Simulate Incoming Session Request
      </button>
    </div>

    <div class="stats-grid">
      <div class="stat-card"><p class="stat-label">Sessions Today</p><h3 class="stat-value">{stats.sessionsToday}</h3></div>
      <div class="stat-card"><p class="stat-label">Sessions This Week</p><h3 class="stat-value">{stats.sessionsThisWeek}</h3></div>
      <div class="stat-card"><p class="stat-label">Average Rating</p><h3 class="stat-value">{stats.averageRating} / 5.0</h3></div>
    </div>

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

  /* This new class applies the blur effect when the modal is active */
  .focused-modal-active {
    filter: blur(5px);
    /* Optional: prevent interaction with the background content */
    pointer-events: none;
    user-select: none;
  }

  .dashboard-container {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
    background-color: #F9FAFB;
    min-height: 100vh;
     transition: filter 0.2s ease-in-out;
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 2rem;
    background-color: #FFFFFF;
    border-bottom: 1px solid #E5E7EB;
  }

  .header-left .logo {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }
  
  .platform-name {
    font-size: 1.25rem;
    font-weight: 600;
    color: #1F2937;
  }

  .header-right .user-menu {
    position: relative;
  }

    .header-right {
      display: flex;
      align-items: center;
      gap: 1rem;
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

  .user-button {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: none;
    border: none;
    cursor: pointer;
    font-size: 0.875rem;
  }

  .profile-pic {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
  }
  
  .user-name {
    font-weight: 500;
    color: #374151;
  }

  .dropdown-menu {
    position: absolute;
    right: 0;
    top: 120%;
    background-color: white;
    border: 1px solid #E5E7EB;
    border-radius: 0.5rem;
    box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
    width: 160px;
    z-index: 10;
  }

  .dropdown-menu a {
    display: block;
    padding: 0.75rem 1rem;
    color: #374151;
    text-decoration: none;
    font-size: 0.875rem;
  }

  .dropdown-menu a:hover {
    background-color: #F3F4F6;
  }

  .main-content {
    padding: 2.5rem;
  }

  .status-controller {
    text-align: center;
    margin-bottom: 2.5rem;
  }

  .status-options {
    display: inline-flex;
    background-color: #E5E7EB;
    border-radius: 9999px;
    padding: 0.25rem;
    margin-bottom: 0.75rem;
  }

  .status-options button {
    padding: 0.5rem 1.5rem;
    border: none;
    border-radius: 9999px;
    font-weight: 600;
    cursor: pointer;
    background-color: transparent;
    color: #6B7280;
    transition: background-color 0.3s, color 0.3s;
  }
  
  .status-options button.active {
    background-color: #FFFFFF;
    color: #10B981;
    box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  }

  .status-message {
    color: #6B7280;
    font-size: 0.875rem;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2.5rem;
  }

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

  .pending-actions {
    background-color: #FFFFFF;
    padding: 1.5rem;
    border-radius: 0.75rem;
    border: 1px solid #E5E7EB;
    max-width: 500px;
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
    color: #374151;
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

  .no-summaries {
    color: #6B7280;
    font-size: 0.875rem;
    text-align: center;
    padding: 1rem 0;
  }
</style>