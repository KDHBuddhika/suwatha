<script lang="ts">
  import { onMount } from 'svelte';
  import router from 'svelte-spa-router';
  import { authStore, authActions } from './stores/auth';
  
  import LoadingScreen from './components/LoadingScreen.svelte';
  import MainLayout from './layouts/MainLayout.svelte';
  import DoctorDashboard from './doctor-dashboard/DoctorDashboard.svelte';
  import LiveVideoSession from './doctor-dashboard/pages/LiveVideoSession.svelte';
  import ChatSession from './doctor-dashboard/pages/ChatSession.svelte';


  
  import Home from './pages/Home.svelte';
  import About from './pages/About.svelte';
  import Terms from './pages/Terms.svelte';
  import Feedback from './pages/Feedback.svelte';
  import Privacy from './pages/Privacy.svelte';
  import DoctorLogin from './pages/DoctorLogin.svelte';
  import Video from './pages/Video.svelte';
  import Chat from './pages/Chat.svelte';
  import SpecialSupport from './pages/SpecialSupport.svelte';
  // import VideoConsultation from './pages/VideoConsultation.svelte';
  
  let isLoading = true;
  
  // Define routes
  const routes = {
    '/': Home,
    '/about': About,
    '/terms': Terms,
    '/feedback': Feedback,
    '/privacy': Privacy,
    '/doctor-login': DoctorLogin,
    '/video': Video,
    '/chat': Chat,
    '/special-support': SpecialSupport,
    // '/video-consultation': VideoConsultation,
    '/doctor-dashboard': DoctorDashboard,
    '/live-video-session': LiveVideoSession,
    '/chat-session': ChatSession
    
  };
  
  onMount(() => {
    setTimeout(() => {
      isLoading = false;
    }, 10500);
    
    // Check for existing authentication
    authActions.checkAuth();
  });
</script>

<LoadingScreen bind:isLoading />

{#if !isLoading}
  {#if $authStore.isAuthenticated && window.location.pathname === '/doctor-dashboard'}
    <DoctorDashboard />
  {:else}
    <MainLayout>
      <svelte:component this={router} {routes} />
    </MainLayout>
  {/if}
{/if}