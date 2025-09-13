import { writable } from 'svelte/store';
import { authStore } from './auth'; // Adjust path if necessary

// Interface for a single notification based on your API response
export interface Notification {
  id: number;
  message: string;
  linkUrl: string;
  createdAt: string;
  read: boolean;
}

// Interface for the store's state
export interface NotificationState {
  notifications: Notification[];
  loading: boolean;
  error: string | null;
}

const initialState: NotificationState = {
  notifications: [],
  loading: false,
  error: null,
};

// Create the writable store
export const notificationStore = writable<NotificationState>(initialState);

// Actions to interact with the notification API
export const notificationActions = {
  fetchNotifications: async () => {
    notificationStore.update(state => ({ ...state, loading: true, error: null }));

    let authState;
    const unsubscribe = authStore.subscribe(s => { authState = s; });
    unsubscribe();

    if (!authState?.user?.id || !authState?.token) {
      notificationStore.update(s => ({ ...s, loading: false, error: "Not authenticated" }));
      return;
    }

    try {
      const response = await fetch(`http://localhost:8090/api/doctor/me/notifications/${authState.user.id}`, {
        headers: {
          'Authorization': `Bearer ${authState.token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch notifications.');
      }

      const data: Notification[] = await response.json();
      notificationStore.update(state => ({
        ...state,
        notifications: data,
        loading: false
      }));

    } catch (err) {
      notificationStore.update(state => ({ ...state, loading: false, error: err.message }));
    }
  },

  markAsRead: async (notificationId: number) => {
    let authState;
    const unsubscribe = authStore.subscribe(s => { authState = s; });
    unsubscribe();

    if (!authState?.user?.id || !authState?.token) return;

    // Optimistic UI update: Mark as read immediately in the UI
    notificationStore.update(state => ({
      ...state,
      notifications: state.notifications.map(n => 
        n.id === notificationId ? { ...n, read: true } : n
      )
    }));
    
    // API Call to persist the change
    try {
      const response = await fetch(`http://localhost:8090/api/doctor/notifications/${notificationId}/read/${authState.user.id}`, {
        method: 'PUT', // Assuming PUT or PATCH, adjust if needed
        headers: {
          'Authorization': `Bearer ${authState.token}`
        }
      });

      if (!response.ok) {
        // If API call fails, revert the change in the UI
        throw new Error('Failed to mark notification as read.');
      }
    } catch (err) {
      console.error(err);
      // Revert the optimistic update on failure
      notificationStore.update(state => ({
        ...state,
        notifications: state.notifications.map(n => 
          n.id === notificationId ? { ...n, read: false } : n
        )
      }));
    }
  },
  
  markAllAsRead: async () => {
    // This function will call the markAsRead endpoint for all unread notifications
    let unreadIds: number[] = [];
    notificationStore.subscribe(state => {
      unreadIds = state.notifications.filter(n => !n.read).map(n => n.id);
    })();

    if (unreadIds.length === 0) return;

    // Use Promise.all to send all requests concurrently
    await Promise.all(unreadIds.map(id => notificationActions.markAsRead(id)));
  }
};