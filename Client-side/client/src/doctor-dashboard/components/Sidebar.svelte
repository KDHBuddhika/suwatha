<script lang="ts">
  import { currentDashboardPage } from '../stores/navigation';
  import { authActions } from '../../stores/auth';
  import { push } from 'svelte-spa-router';
  import Logo2 from '../../components/Logo2.svelte';
  import { 
    Home, 
    Users, 
    Calendar, 
    MessageSquare, 
    FileText, 
    Settings, 
    LogOut,
    Activity,
    Clock,
    Video,
    ClipboardEdit
  } from 'lucide-svelte';

  const menuItems = [
    { id: 'overview', label: 'Overview', icon: Home },
    { id: 'patients', label: 'Patients', icon: Users },
    { id: 'appointments', label: 'Appointments', icon: Calendar },
    { id: 'sessions', label: 'Sessions', icon: MessageSquare },
    { id: 'reports', label: 'Reports', icon: FileText },
     // A link to the live session page (changes the URL)
    { id: 'live-session', label: 'Live Session', icon: Video, path: '/live-video-session' },
    
    // --- NEW ITEM ADDED HERE ---
    // A link to an internal dashboard page (changes the store)
    { id: 'summary', label: 'Session Summary', icon: ClipboardEdit },
    { id: 'schedule', label: 'Schedule', icon: Clock },
    { id: 'analytics', label: 'Analytics', icon: Activity },
    { id: 'settings', label: 'Settings', icon: Settings }
  ];

  function navigateTo(pageId: string) {
    currentDashboardPage.set(pageId);
  }

  function handleLogout() {
    authActions.logout();
  }
</script>

<aside class="sidebar">
  <div class="sidebar-header">
    <div class="logo">
      <div class="logo-icon "><Logo2/></div>
      <!-- <span class="logo-text">Manosara</span> -->
    </div>
    <div class="doctor-badge">Doctor Portal</div>
  </div>

  <nav class="sidebar-nav">
    <ul class="nav-list">
      {#each menuItems as item}
        <li class="nav-item">
          <button 
            class="nav-link"
            class:active={$currentDashboardPage === item.id}
            on:click={() => navigateTo(item.id)}
          >
            <svelte:component this={item.icon} size={20} />
            <span>{item.label}</span>
          </button>
        </li>
      {/each}
    </ul>
  </nav>

  <div class="sidebar-footer">
    <button class="logout-btn" on:click={handleLogout}>
      <LogOut size={20} />
      <span>Logout</span>
    </button>
  </div>
</aside>

<style>
  .sidebar {
    width: 270px;
    height: 100vh;
    background: white;
    border-right: 1px solid #E5E7EB;
    display: flex;
    flex-direction: column;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 100;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  }

  .sidebar-header {
    padding: 2rem 1.5rem;
    border-bottom: 1px solid #E5E7EB;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    margin-bottom: 1rem;
  }

  .logo-icon {
    font-size: 2rem;
  }

  .logo-text {
    font-size: 1.5rem;
    font-weight: 600;
    color: #374151;
  }

  .doctor-badge {
    background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 1rem;
    font-size: 0.875rem;
    font-weight: 500;
    text-align: center;
  }

  .sidebar-nav {
    flex: 1;
    padding: 1.5rem 0;
    overflow-y: auto;
  }

  .nav-list {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .nav-item {
    margin-bottom: 0.25rem;
  }

  .nav-link {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.875rem 1.5rem;
    background: none;
    border: none;
    color: #6B7280;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    text-align: left;
  }

  .nav-link:hover {
    background: #F3F4F6;
    color: #374151;
  }

  .nav-link.active {
    background: linear-gradient(135deg, #EEF2FF 0%, #E0E7FF 100%);
    color: #4F46E5;
    border-right: 3px solid #4F46E5;
  }

  .sidebar-footer {
    padding: 1.5rem;
    border-top: 1px solid #E5E7EB;
  }

  .logout-btn {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.875rem 1rem;
    background: #FEF2F2;
    border: 1px solid #FECACA;
    border-radius: 0.5rem;
    color: #DC2626;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .logout-btn:hover {
    background: #FEE2E2;
    border-color: #FCA5A5;
  }

  @media (max-width: 1024px) {
    .sidebar {
      transform: translateX(-100%);
      transition: transform 0.3s ease;
    }

    .sidebar.open {
      transform: translateX(0);
    }
  }
</style>