<script lang="ts">
  import { onMount } from 'svelte';
  import { currentPage } from './stores/navigation';
  import { authStore, authActions } from './stores/auth';
  
  import LoadingScreen from './components/LoadingScreen.svelte';
  import MainLayout from './layouts/MainLayout.svelte';
  import ServiceLoadingOverlay from './components/ServiceLoadingOverlay.svelte';
  import DoctorDashboard from './doctor-dashboard/DoctorDashboard.svelte';
  
  import Home from './pages/Home.svelte';
  import About from './pages/About.svelte';
  import Terms from './pages/Terms.svelte';
  import Feedback from './pages/Feedback.svelte';
  import Privacy from './pages/Privacy.svelte';
  import DoctorLogin from './pages/DoctorLogin.svelte';
  import Video from './pages/Video.svelte';
  import Chat from './pages/Chat.svelte';
  import SpecialSupport from './pages/SpecialSupport.svelte';
  
  let isLoading = true;
  let currentPageValue = 'home';
  
  onMount(() => {
    setTimeout(() => {
      isLoading = false;
    }, 6500);
    
    // Check for existing authentication
    authActions.checkAuth();
    
    // Subscribe to page changes
    currentPage.subscribe(page => {
      currentPageValue = page;
    });
  });
</script>

<LoadingScreen bind:isLoading />

{#if !isLoading}
  {#if $authStore.isAuthenticated && currentPageValue === 'doctor-dashboard'}
    <DoctorDashboard />
  {:else}
    <MainLayout>
      {#if currentPageValue === 'home'}
        <Home />
      {:else if currentPageValue === 'about'}
        <About />
      {:else if currentPageValue === 'terms'}
        <Terms />
      {:else if currentPageValue === 'feedback'}
        <Feedback />
      {:else if currentPageValue === 'privacy'}
        <Privacy />
      {:else if currentPageValue === 'doctor-login'}
        <DoctorLogin />
      {:else if currentPageValue === 'video'}
        <Video />
      {:else if currentPageValue === 'chat'}
        <Chat />
      {:else if currentPageValue === 'special-support'}
        <SpecialSupport />
      {:else}
        <Home />
      {/if}
    </MainLayout>
  {/if}
{/if}