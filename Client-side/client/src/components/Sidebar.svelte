<script lang="ts">
  import { User, Calendar, BarChart3, Settings, LogOut, Home ,Monitor,FileText} from 'lucide-svelte';
  import { authStore } from '../stores/auth';
  import { link } from 'svelte-spa-router';
 

  export let currentPath = '';
  let isCollapsed = false;

  const menuItems = [
    { path: '/dashboard', label: 'Overview', icon: Home },
    { path: '/doctors', label: 'Manage Doctors', icon: User },
    { path: '/sessions', label: 'Session Management', icon: Monitor },
    { path: '/reports', label: 'Session Reports', icon: FileText },
    { path: '/appointments', label: 'Appointments', icon: Calendar },
    { path: '/analytics', label: 'Analytics', icon: BarChart3 },
    { path: '/settings', label: 'Settings', icon: Settings }
  ];

  function handleLogout() {
    authStore.logout();
  }
</script>

<aside class="sidebar" class:collapsed={isCollapsed}>
  <div class="sidebar-header">
    <div class="logo">
      <div class="logo-icon">M</div>
      {#if !isCollapsed}
        <span class="logo-text">Manosara</span>
      {/if}
    </div>
    <button class="collapse-btn" on:click={() => isCollapsed = !isCollapsed}>
      <div class="hamburger"></div>
    </button>
  </div>

  <nav class="sidebar-nav">
    <ul class="nav-list">
      {#each menuItems as item}
        <li class="nav-item">
          <a 
            href={item.path} 
            use:link 
            class="nav-link"
            class:active={currentPath === item.path}
          >
            <svelte:component this={item.icon} size={20} />
            {#if !isCollapsed}
              <span class="nav-label">{item.label}</span>
            {/if}
          </a>
        </li>
      {/each}
    </ul>
  </nav>

  <div class="sidebar-footer">
    <button class="logout-btn" on:click={handleLogout}>
      <LogOut size={20} />
      {#if !isCollapsed}
        <span>Logout</span>
      {/if}
    </button>
  </div>
</aside>

<style>
  .sidebar {
    width: 280px;
    height: 100vh;
    background: linear-gradient(180deg, #1e40af 0%, #2563eb 100%);
    color: white;
    display: flex;
    flex-direction: column;
    transition: width 0.3s ease;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
  }

  .sidebar.collapsed {
    width: 80px;
  }

  .sidebar-header {
    padding: 1.5rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .logo-icon {
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 1.25rem;
  }

  .logo-text {
    font-size: 1.5rem;
    font-weight: bold;
  }

  .collapse-btn {
    background: transparent;
    border: none;
    color: white;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 4px;
    transition: background-color 0.2s;
  }

  .collapse-btn:hover {
    background: rgba(255, 255, 255, 0.1);
  }

  .hamburger {
    width: 20px;
    height: 2px;
    background: white;
    position: relative;
  }

  .hamburger::before,
  .hamburger::after {
    content: '';
    position: absolute;
    width: 20px;
    height: 2px;
    background: white;
    transition: all 0.3s;
  }

  .hamburger::before {
    top: -6px;
  }

  .hamburger::after {
    bottom: -6px;
  }

  .sidebar-nav {
    flex: 1;
    padding: 1rem 0;
  }

  .nav-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .nav-item {
    margin: 0.25rem 0;
  }

  .nav-link {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.75rem 1.5rem;
    color: rgba(255, 255, 255, 0.8);
    text-decoration: none;
    transition: all 0.2s;
    border-left: 3px solid transparent;
  }

  .nav-link:hover {
    background: rgba(255, 255, 255, 0.1);
    color: white;
  }

  .nav-link.active {
    background: rgba(255, 255, 255, 0.15);
    color: white;
    border-left-color: #fbbf24;
  }

  .nav-label {
    font-weight: 500;
  }

  .sidebar-footer {
    padding: 1.5rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }

  .logout-btn {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    width: 100%;
    padding: 0.75rem;
    background: rgba(239, 68, 68, 0.2);
    border: none;
    color: white;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .logout-btn:hover {
    background: rgba(239, 68, 68, 0.3);
  }

  @media (max-width: 768px) {
    .sidebar {
      width: 280px;
      transform: translateX(-100%);
    }

    .sidebar.collapsed {
      transform: translateX(0);
      width: 80px;
    }
  }
</style>