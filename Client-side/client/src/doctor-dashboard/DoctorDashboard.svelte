<script lang="ts">
  import { onMount } from 'svelte';
  import { currentDashboardPage } from './stores/navigation';
  import { authActions } from '../stores/auth';
  
  import DashboardLayout from './layouts/DashboardLayout.svelte';
  import Overview from './pages/Overview.svelte';
  import Patients from './pages/Patients.svelte';
  
  let currentPageValue = 'overview';
  
  onMount(() => {
    // Check authentication and load initial data
    authActions.checkAuth();
    
    // Subscribe to page changes
    currentDashboardPage.subscribe(page => {
      currentPageValue = page;
    });
  });
</script>

<DashboardLayout>
  {#if currentPageValue === 'overview'}
    <Overview />
  {:else if currentPageValue === 'patients'}
    <Patients />
  {:else if currentPageValue === 'appointments'}
    <div class="coming-soon">
      <h2>Appointments</h2>
      <p>This feature is coming soon...</p>
    </div>
  {:else if currentPageValue === 'sessions'}
    <div class="coming-soon">
      <h2>Sessions</h2>
      <p>This feature is coming soon...</p>
    </div>
  {:else if currentPageValue === 'reports'}
    <div class="coming-soon">
      <h2>Reports</h2>
      <p>This feature is coming soon...</p>
    </div>
  {:else if currentPageValue === 'schedule'}
    <div class="coming-soon">
      <h2>Schedule</h2>
      <p>This feature is coming soon...</p>
    </div>
  {:else if currentPageValue === 'analytics'}
    <div class="coming-soon">
      <h2>Analytics</h2>
      <p>This feature is coming soon...</p>
    </div>
  {:else if currentPageValue === 'settings'}
    <div class="coming-soon">
      <h2>Settings</h2>
      <p>This feature is coming soon...</p>
    </div>
  {:else}
    <Overview />
  {/if}
</DashboardLayout>

<style>
  .coming-soon {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 400px;
    background: white;
    border-radius: 1rem;
    border: 1px solid #E5E7EB;
    text-align: center;
  }

  .coming-soon h2 {
    font-size: 2rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 1rem;
  }

  .coming-soon p {
    color: #6B7280;
    font-size: 1.125rem;
  }
</style>