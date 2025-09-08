import { writable } from 'svelte/store';

export interface User {
  id: string;
  email: string;
  name: string;
  role: 'doctor' | 'patient';
  specialization?: string;
  licenseNumber?: string;
}

export interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  loading: boolean;
}

const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
  loading: false
};

export const authStore = writable<AuthState>(initialState);

// Demo credentials
const DEMO_CREDENTIALS = {
  email: 'doctor@manosara.com',
  password: 'demo123'
};

const DEMO_DOCTOR: User = {
  id: 'doc_001',
  email: 'doctor@manosara.com',
  name: 'Dr. Sarah Johnson',
  role: 'doctor',
  specialization: 'Clinical Psychology',
  licenseNumber: 'PSY-12345'
};

export const authActions = {
  login: async (email: string, password: string): Promise<boolean> => {
    authStore.update(state => ({ ...state, loading: true }));
    
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    if (email === DEMO_CREDENTIALS.email && password === DEMO_CREDENTIALS.password) {
      authStore.update(state => ({
        ...state,
        isAuthenticated: true,
        user: DEMO_DOCTOR,
        loading: false
      }));
      return true;
    } else {
      authStore.update(state => ({ ...state, loading: false }));
      return false;
    }
  },

  logout: () => {
    authStore.update(state => ({
      ...state,
      isAuthenticated: false,
      user: null,
      loading: false
    }));
  },

  checkAuth: () => {
    // In a real app, this would check for stored tokens
    const stored = localStorage.getItem('manosara_auth');
    if (stored) {
      try {
        const authData = JSON.parse(stored);
        authStore.update(state => ({
          ...state,
          isAuthenticated: true,
          user: authData.user
        }));
      } catch (error) {
        localStorage.removeItem('manosara_auth');
      }
    }
  }
};

// Subscribe to auth changes and persist to localStorage
authStore.subscribe(state => {
  if (state.isAuthenticated && state.user) {
    localStorage.setItem('manosara_auth', JSON.stringify({
      user: state.user,
      timestamp: Date.now()
    }));
  } else {
    localStorage.removeItem('manosara_auth');
  }
});