<script lang="ts">
  import { onMount } from 'svelte';
  import { currentDashboardPage } from './stores/navigation';
  import { authActions, authStore } from '../stores/auth';
  import DashboardLayout from './layouts/DashboardLayout.svelte';
  import Overview from './pages/Overview.svelte';
  import Patients from './pages/Patients.svelte';
  import SessionSummaryForm from './pages/SessionSummaryForm.svelte';
  import ProfileSettings from './pages/ProfileSettings.svelte';
  import Sessions from './pages/Sessions.svelte';
  import { push } from 'svelte-spa-router';

  let currentPageValue = 'overview';

  onMount(() => {
    authActions.checkAuth();
    
    const unsubscribe = authStore.subscribe(state => {
      if (!state.isAuthenticated) {
        push('/doctor-login');
      }
    });

    currentDashboardPage.subscribe(page => {
      currentPageValue = page;
    });

    return unsubscribe;
  });

  // function handleLogout() {
  //   authActions.logout();
  // }
</script>

<DashboardLayout>
  {#if currentPageValue === 'overview'}
    <Overview />
  {:else if currentPageValue === 'patients'}
    <Patients />
  {:else if currentPageValue === 'summary'}
    <SessionSummaryForm />
  {:else if currentPageValue === 'settings'}
    <ProfileSettings />
  {:else if currentPageValue === 'sessions'}
    <Sessions />  
  {:else}
    <div class="coming-soon">
      <h2>{currentPageValue.charAt(0).toUpperCase() + currentPageValue.slice(1)}</h2>
      <p>This feature is coming soon...</p>
    </div>
  {/if}
  
  <!-- <button on:click={handleLogout}>Logout</button> -->
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