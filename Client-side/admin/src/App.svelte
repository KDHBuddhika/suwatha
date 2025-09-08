<script lang="ts">
  import router, { link, push, replace } from 'svelte-spa-router';
  import { authStore } from './stores/auth';
  import Sidebar from './components/Sidebar.svelte';
  import { onMount } from 'svelte';

  // Pages
  import Login from './pages/Login.svelte';
  import Dashboard from './pages/Dashboard.svelte';
  import NotFound from './pages/NotFound.svelte';
  import DoctorManagement from './pages/DoctorManagement.svelte';
  import AddTherapist from './pages/AddTherapist.svelte';
  import ViewDoctor from './pages/ViewDoctor.svelte';
  import EditDoctor from './pages/EditDoctor.svelte';
  import SessionManagement from './pages/SessionManagement.svelte';
  import SessionReports from './pages/SessionReports.svelte';
  import Analytics from './pages/Analytics.svelte';
  let currentPath = '';
  
  // Initialize auth store when app loads
  onMount(() => {
    authStore.initialize();
  });

  // Simple route configuration without guards
  const routes = {
    '/': Login,
    '/login': Login,
    '/dashboard': Dashboard,
    '/appointments': Dashboard, // Placeholder for now
    '/doctors': DoctorManagement,
    '/doctors/add': AddTherapist,
    '/doctors/view/:id': ViewDoctor,
    '/doctors/edit/:id': EditDoctor,
    '/analytics': Analytics, // Placeholder for now
    '/settings': Dashboard, // Placeholder for now
    '/sessions': SessionManagement,
    '/reports': SessionReports,
     
    '*': NotFound
    
  };

  // Handle route changes with guards
  function handleRouteLoaded(event: any) {
    currentPath = event.detail.location;
    
    // Apply route guards after route loads
    const protectedRoutes = ['/dashboard', '/doctors', '/appointments', '/analytics', '/settings', '/doctors/add', '/doctors/view/:id', '/doctors/edit/:id', '/sessions', '/reports'];
    const publicRoutes = ['/', '/login'];
    
    if (protectedRoutes.includes(currentPath) && !$authStore.isAuthenticated) {
      setTimeout(() => replace('/login'), 0);
    } else if (publicRoutes.includes(currentPath) && $authStore.isAuthenticated) {
      setTimeout(() => replace('/dashboard'), 0);
    }
  }

  // Watch for authentication changes
  $: {
    if (typeof window !== 'undefined' && currentPath) {
      const protectedRoutes = ['/dashboard', '/doctors', '/appointments', '/analytics', '/settings', '/doctors/add', '/doctors/view/:id', '/doctors/edit/:id', '/sessions', '/reports'];
      const publicRoutes = ['/', '/login'];
      
      if (protectedRoutes.includes(currentPath) && !$authStore.isAuthenticated) {
        setTimeout(() => replace('/login'), 0);
      } else if (publicRoutes.includes(currentPath) && $authStore.isAuthenticated) {
        setTimeout(() => replace('/dashboard'), 0);
      }
    }
  }
</script>

<main class="app">
  {#if $authStore.isAuthenticated}
    <div class="app-layout">
      <Sidebar {currentPath} />
      <div class="main-content">
        <svelte:component this={router} {routes} on:routeLoaded={handleRouteLoaded} />
      </div>
    </div>
  {:else}
    <svelte:component this={router} {routes} on:routeLoaded={handleRouteLoaded} />
  {/if}
</main>