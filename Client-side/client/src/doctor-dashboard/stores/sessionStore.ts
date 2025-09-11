import { writable } from 'svelte/store';

// Define the structure of a session request
export interface SessionRequest {
  patientId: string;
  sessionType: 'VIDEO' | 'CHAT';
}

// This store will be null when there is no request, and hold a request object when one arrives.
// Our UI will react to this change.
export const sessionRequestStore = writable<SessionRequest | null>(null);