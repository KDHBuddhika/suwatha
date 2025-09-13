import { writable } from 'svelte/store';
import { push } from 'svelte-spa-router';

export interface User {
  id: string;
  email: string;
  name: string;
  role: 'doctor' | 'patient';
  specialization?: string;
  licenseNumber?: string;
  profilePictureUrl?: string; 
}

export interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  loading: boolean;
  token: string | null;
}

const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
  loading: false,
  token: null,
};

export const authStore = writable<AuthState>(initialState);

export const authActions = {
  login: async (email: string, password: string): Promise<boolean> => {
    authStore.update(state => ({ ...state, loading: true }));

    try {
      const response = await fetch('http://localhost:8090/api/auth/doctor-login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const data = await response.json();
        const { token, therapist } = data;

        const user: User = {
          id: therapist.id,
          email: therapist.email,
          name: therapist.name,
          role: 'doctor',
          specialization: therapist.specializations.map((s: any) => s.name).join(', '),
          licenseNumber: therapist.licenseNumber,
          profilePictureUrl: therapist.profilePictureUrl,
        };

        authStore.update(state => ({
          ...state,
          isAuthenticated: true,
          user,
          token,
          loading: false,
        }));

        localStorage.setItem('manosara_auth', JSON.stringify({
          user,
          token,
          timestamp: Date.now(),
        }));

        return true;
      } else {
        authStore.update(state => ({ ...state, loading: false }));
        return false;
      }
    } catch (error) {
      console.error('Login failed:', error);
      authStore.update(state => ({ ...state, loading: false }));
      return false;
    }
  },

  logout: () => {
    authStore.set(initialState);
    localStorage.removeItem('manosara_auth');
    push('/doctor-login');
  },

  checkAuth: () => {
    const stored = localStorage.getItem('manosara_auth');
    if (stored) {
      try {
        const authData = JSON.parse(stored);
        authStore.update(state => ({
          ...state,
          isAuthenticated: true,
          user: authData.user,
          token: authData.token,
        }));
      } catch (error) {
        localStorage.removeItem('manosara_auth');
      }
    }
  },
};

authStore.subscribe(state => {
  if (!state.isAuthenticated) {
    localStorage.removeItem('manosara_auth');
  }
});