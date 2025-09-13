<script lang="ts">
  import { onMount } from 'svelte';
  import { authStore } from '../../stores/auth';
  import { notificationStore, notificationActions, type Notification } from '../../stores/notifications'; // Import the new store and actions
  import { Bell, Search, Menu } from 'lucide-svelte';
  import NotificationModal from './NotificationModal.svelte';

  // --- COMPONENT STATE ---
  let searchQuery = '';
  let showNotifications = false;
  let selectedNotification: Notification | null = null;

  // Subscribe to the notification store to get reactive updates
  const { subscribe } = notificationStore;
  const state = subscribe(value => {
    // This subscription keeps the component reactive to store changes
  });
  
  // Create a reactive statement to automatically calculate the unread count whenever notifications change
  $: unreadCount = $notificationStore.notifications.filter(n => !n.read).length;

  // Fetch notifications when the component is first mounted to the DOM
  onMount(() => {
    notificationActions.fetchNotifications();
  });

  // --- FUNCTIONS ---
  function toggleNotifications() {
    showNotifications = !showNotifications;
  }

  // When a notification is clicked, view it in the modal and mark it as read
  function viewNotification(notification: Notification) {
    if (!notification.read) {
      notificationActions.markAsRead(notification.id);
    }
    selectedNotification = notification;
    showNotifications = false; // Close the dropdown when the modal opens
  }
  
  // Handle the "Mark all read" button click
  function handleMarkAllRead() {
    notificationActions.markAllAsRead();
  }

  // Close the notification detail modal
  function handleCloseModal() {
    selectedNotification = null;
  }

  // Helper function to format ISO date strings into a user-friendly "time ago" format
  function formatTimeAgo(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    
    let interval = seconds / 31536000; // years
    if (interval > 1) return Math.floor(interval) + " years ago";
    interval = seconds / 2592000; // months
    if (interval > 1) return Math.floor(interval) + " months ago";
    interval = seconds / 86400; // days
    if (interval > 1) return Math.floor(interval) + " days ago";
    interval = seconds / 3600; // hours
    if (interval > 1) return Math.floor(interval) + " hours ago";
    interval = seconds / 60; // minutes
    if (interval > 1) return Math.floor(interval) + " minutes ago";
    
    return Math.floor(seconds) <= 10 ? "Just now" : Math.floor(seconds) + " seconds ago";
  }
</script>

<!-- The modal is rendered only when a notification is selected -->
{#if selectedNotification}
  <NotificationModal 
    notification={{
      id: selectedNotification.id,
      message: selectedNotification.message,
      unread: selectedNotification.read,
      // Map the API response fields to the props the modal expects
      details: `Associated link: ${selectedNotification.linkUrl}`,
      time: formatTimeAgo(selectedNotification.createdAt)
    }}
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
        <!-- The badge is only shown if there are unread notifications -->
        {#if unreadCount > 0}
          <span class="notification-badge">{unreadCount}</span>
        {/if}
      </button>
      
      {#if showNotifications}
        <div class="notifications-dropdown">
          <div class="notifications-header">
            <h3>Notifications</h3>
            <button 
              class="mark-all-read" 
              on:click={handleMarkAllRead} 
              disabled={unreadCount === 0}>
              Mark all read
            </button>
          </div>
          <div class="notifications-list">
            {#if $notificationStore.loading}
              <div class="notification-item-status">Loading...</div>
            {:else if $notificationStore.error}
              <div class="notification-item-status error">Error: {$notificationStore.error}</div>
            {:else if $notificationStore.notifications.length === 0}
              <div class="notification-item-status">You have no notifications.</div>
            {:else}
              <!-- Loop through notifications from the store -->
              {#each $notificationStore.notifications as notification (notification.id)}
                <div 
                  class="notification-item" 
                  class:unread={!notification.read} 
                  on:click={() => viewNotification(notification)}
                  role="button"
                  tabindex="0"
                  on:keydown={(e) => e.key === 'Enter' && viewNotification(notification)}
                >
                  <p class="notification-message">{notification.message}</p>
                  <span class="notification-time">{formatTimeAgo(notification.createdAt)}</span>
                </div>
              {/each}
            {/if}
          </div>
        </div>
      {/if}
    </div>

    <!-- User Profile Section -->
    <div class="user-profile">
      <div class="user-avatar">
        <img src={`http://localhost:8090/file/${$authStore.user?.profilePictureUrl}`} alt="Therapist Avatar" />
      </div>
      <div class="user-info">
        <span class="user-name">{$authStore.user?.name || 'Therapist'}</span>
      </div>
    </div>
  </div>
</header>

<style>
 /* ==========================================================================
   Base & Header Styles
   ========================================================================== */

.dashboard-header {
  height: 80px;
  background: white;
  border-bottom: 1px solid #E5E7EB;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 3rem;
  position: sticky;
  top: 0;
  z-index: 50;
  margin-left:-10px;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

/* ==========================================================================
   Search Bar
   ========================================================================== */

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

/* ==========================================================================
   Notifications
   ========================================================================== */

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

.mark-all-read:disabled {
  color: #9CA3AF;
  cursor: not-allowed;
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
  font-weight: 500;
}

.notification-message {
  margin: 0 0 0.25rem 0;
  font-size: 0.875rem;
  color: #374151;
  line-height: 1.4;
}

.notification-time {
  font-size: 0.75rem;
  color: #6B7280;
}

.notification-item-status {
  padding: 1.5rem;
  text-align: center;
  color: #6B7280;
}

.notification-item-status.error {
  color: #DC2626;
}

/* ==========================================================================
   User Profile
   ========================================================================== */

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
  justify-content: center;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
}

/* ==========================================================================
   Responsive Styles
   ========================================================================== */

.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  color: #6B7280;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 0.5rem;
}

/* Tablet */
@media (max-width: 1024px) {
  .mobile-menu-btn {
    display: block;
  }

  .search-container {
    width: 250px;
  }
}

/* Mobile */
@media (max-width: 768px) {
  .dashboard-header {
    padding: 0 1rem;
  }

  .search-container {
    display: none;
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