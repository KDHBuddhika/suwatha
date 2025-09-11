import { writable } from 'svelte/store';
import { get } from 'svelte/store';
import { authStore } from './auth'; // Assuming you have an auth store for the token

// Matches the backend's data for a single session
export interface Session {
  sessionId: number;
  communicationType: string;
  startTime: string;
  endTime: string | null;
  status: string;
  patientHandle: string;
  therapistName: string;
  rating: number | null;
  durationInMinutes: number | null;
  // Add patientName if the backend can provide it, otherwise we leave it out
  // patientName?: string; 
}

// Matches the structure of the entire API response
export interface PaginatedSessionsResponse {
  content: Session[];
  totalPages: number;
  totalElements: number;
  number: number; // This is the current page number (zero-indexed)
  size: number;
  first: boolean;
  last: boolean;
}

// The initial state for our store
const initialSessionState: PaginatedSessionsResponse = {
  content: [],
  totalPages: 0,
  totalElements: 0,
  number: 0,
  size: 10,
  first: true,
  last: true,
};

function createSessionsStore() {
  const { subscribe, set } = writable<PaginatedSessionsResponse>(initialSessionState);

  return {
    subscribe,

    // The main function to fetch data from the API
    fetchSessions: async (params: { 
      page?: number; 
      size?: number; 
      sort?: string; 
      searchTerm?: string;
      status?: string;
      type?: string;
    }) => {
      const token = get(authStore).token;

      // Build query parameters
      const query = new URLSearchParams();
      query.append('page', (params.page || 0).toString());
      query.append('size', (params.size || 10).toString());
      if (params.sort) query.append('sort', params.sort);
      if (params.searchTerm) query.append('q', params.searchTerm); // Assuming backend uses 'q' for search
      if (params.status && params.status !== 'all') query.append('status', params.status.toUpperCase());
      if (params.type && params.type !== 'all') query.append('type', params.type.toUpperCase());
      
      try {
        const response = await fetch(`http://localhost:8090/api/management/sessions?${query.toString()}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (!response.ok) throw new Error(`HTTP Error: ${response.status}`);
        
        const data: PaginatedSessionsResponse = await response.json();
        set(data); // Update the store with the new page of data

      } catch (error) {
        console.error("Failed to fetch sessions:", error);
        // Optionally set an error state in the store
      }
    },

    // Updated delete function
    delete: async (id: number) => {
      const token = get(authStore).token;
      try {
        // IMPORTANT: You need to confirm this is the correct DELETE endpoint
        const response = await fetch(`http://localhost:8090/api/management/sessions/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('Failed to delete session');
        
        // On successful delete, we can either optimistically remove it from the list
        // or just refresh the current page to get the latest data from the server.
        // Refreshing is safer and simpler.
        // We'll trigger this from the component after the delete is successful.

      } catch (error) {
        console.error("Error deleting session:", error);
      }
    }
  };
}

export const sessionsStore = createSessionsStore();