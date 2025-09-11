<script lang="ts">
  import { authStore, authActions } from '../../stores/auth';
  import { Bell, Search, Menu } from 'lucide-svelte';
  import NotificationModal from './NotificationModal.svelte';

  // --- STATE ---
  let searchQuery = '';
  let showNotifications = false;
  let selectedNotification = null;

  // --- MOCK DATA ---
  const notifications = [
    { 
      id: 1, 
      message: 'New appointment request from John Smith', 
      time: '5 min ago', 
      unread: true,
      details: 'John Smith has requested a video session for tomorrow at 3:00 PM. Please review their profile and confirm or deny the appointment.'
    },
    { 
      id: 2, 
      message: 'Session completed with Emily Davis', 
      time: '1 hour ago', 
      unread: true,
      details: 'Your 45-minute chat session with Emily Davis has ended. A session summary is now pending your review.'
    },
    { 
      id: 3, 
      message: 'Weekly report is ready for review', 
      time: '2 hours ago', 
      unread: false,
      details: 'Your performance and patient engagement report for the week of Jan 20-26 is available in the Reports section.'
    }
  ];

  // --- FUNCTIONS ---
  function toggleNotifications() {
    showNotifications = !showNotifications;
  }

  function viewNotification(notification) {
    selectedNotification = notification;
    showNotifications = false;
  }

  function handleCloseModal() {
    selectedNotification = null;
  }

  // NOTE: A logout function can be triggered from a dropdown menu
  // that could be associated with clicking the user-profile div.
  // For simplicity, the direct logout button has been removed as per the style restoration.
</script>

<!-- Render the modal here if a notification is selected -->
{#if selectedNotification}
  <NotificationModal 
    notification={selectedNotification}
    on:close={handleCloseModal}
  />
{/if}

<header class="dashboard-header">
  <div class="header-left">
    <button class="mobile-menu-btn">
      <Menu size={24} />
    </button>
    <div class="search-container">
      <Search size={20} class="search-icon" />
      <input 
        type="text" 
        placeholder="Search patients, appointments..." 
        bind:value={searchQuery}
        class="search-input"
      />
    </div>
  </div>

  <div class="header-right">
    <div class="notifications-container">
      <button class="notification-btn" on:click={toggleNotifications}>
        <Bell size={20} />
        <span class="notification-badge">2</span>
      </button>
      
      {#if showNotifications}
        <div class="notifications-dropdown">
          <div class="notifications-header">
            <h3>Notifications</h3>
            <button class="mark-all-read">Mark all read</button>
          </div>
          <div class="notifications-list">
            {#each notifications as notification}
              <!-- svelte-ignore a11y-click-events-have-key-events -->
              <div 
                class="notification-item" 
                class:unread={notification.unread} 
                on:click={() => viewNotification(notification)}
              >
                <p class="notification-message">{notification.message}</p>
                <span class="notification-time">{notification.time}</span>
              </div>
            {/each}
          </div>
        </div>
      {/if}
    </div>

    <!-- THIS IS THE CORRECTED USER PROFILE SECTION -->
    <div class="user-profile">
      <div class="user-avatar">
        <!-- You can make this image dynamic later if needed -->
        <img src="https://images.pexels.com/photos/5327580/pexels-photo-5327580.jpeg?auto=compress&cs=tinysrgb&w=100&h=100&fit=crop" alt="Therapist Avatar" />
      </div>
      <div class="user-info">
        <!-- Only the therapist's name is displayed now -->
        <span class="user-name">{$authStore.user?.name || 'Therapist'}</span>
      </div>
    </div>
  </div>
</header>

<style>
  /* Styles are restored to your original provided version */
  .dashboard-header {
    height: 80px;
    background: white;
    border-bottom: 1px solid #E5E7EB;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 2rem;
    position: sticky;
    top: 0;
    z-index: 50;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .mobile-menu-btn {
    display: none; /* Hidden by default, shown in media query */
    background: none;
    border: none;
    color: #6B7280;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 0.5rem;
  }

  .search-container {
    position: relative;
    width: 400px;
  }

  .search-input {
    width: 80%;
    padding: 0.75rem 1rem 0.75rem 3rem;
    border: 2px solid #E5E7EB;
    border-radius: 0.5rem;
    font-size: 0.9rem;
    transition: all 0.2s ease;
  }

  .search-input:focus {
    outline: none;
    border-color: #4F46E5;
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  }

  .search-icon {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #9CA3AF;
    pointer-events: none;
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 1.5rem;
  }

  .notifications-container {
    position: relative;
  }

  .notification-btn {
    position: relative;
    background: none;
    border: none;
    color: #6B7280;
    cursor: pointer;
    padding: 0.75rem;
    border-radius: 0.5rem;
    transition: all 0.2s ease;
  }
  
  .notification-btn:hover {
    background: #F3F4F6;
    color: #374151;
  }

  .notification-badge {
    position: absolute;
    top: 0.25rem;
    right: 0.25rem;
    background: #EF4444;
    color: white;
    font-size: 0.75rem;
    font-weight: 600;
    padding: 0.125rem 0.375rem;
    border-radius: 0.75rem;
    min-width: 1.25rem;
    text-align: center;
  }

  .notifications-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    width: 350px;
    background: white;
    border: 1px solid #E5E7EB;
    border-radius: 0.75rem;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    z-index: 100;
    margin-top: 0.5rem;
  }

  .notifications-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #E5E7EB;
  }

  .notifications-header h3 {
    margin: 0;
    font-size: 1rem;
    font-weight: 600;
    color: #374151;
  }

  .mark-all-read {
    background: none;
    border: none;
    color: #4F46E5;
    font-size: 0.875rem;
    cursor: pointer;
  }

  .notifications-list {
    max-height: 300px;
    overflow-y: auto;
  }

  .notification-item {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #F3F4F6;
    transition: background 0.2s ease;
    cursor: pointer;
  }
  
  .notification-item:hover {
    background: #F9FAFB;
  }

  .notification-item.unread {
    background: #EEF2FF;
  }
  
  .notification-message {
    margin: 0 0 0.25rem 0;
    font-size: 0.875rem;
    color: #374151;
    line-height: 1.4;
  }
  
  .notification-time {
    font-size: 0.75rem;
    color: #9CA3AF;
  }

  .user-profile {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.5rem;
    border-radius: 0.75rem;
    transition: background 0.2s ease;
    cursor: pointer;
  }

  .user-profile:hover {
    background: #F3F4F6;
  }

  .user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
  }

  .user-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .user-info {
    display: flex;
    flex-direction: column;
    justify-content: center; /* This helps center the text vertically */
  }

  .user-name {
    font-size: 0.875rem;
    font-weight: 600;
    color: #374151;
  }
  
  /* Media Queries for responsiveness */
  @media (max-width: 1024px) {
    .mobile-menu-btn {
      display: block;
    }

    .search-container {
      width: 250px;
    }
  }

  @media (max-width: 768px) {
    .dashboard-header {
      padding: 0 1rem;
    }

    .search-container {
      width: 200px;
    }

    .user-info {
      display: none;
    }

    .notifications-dropdown {
      width: 300px;
      right: -50px;
    }
  }
</style>