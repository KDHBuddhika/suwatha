// src/stores/auth.ts
import { writable } from 'svelte/store';

interface AuthState {
    token: string | null;
    user: { name: string } | null;
    isLoading: boolean;
    error: string | null;
    isAuthenticated: boolean; // Add this missing property
}

const createAuthStore = () => {
    const { subscribe, set, update } = writable<AuthState>({
        token: null,
        user: null,
        isLoading: false,
        error: null,
        isAuthenticated: false // Initialize as false
    });

    return {
        subscribe,
        set,
        update,
        login: async (email: string, password: string): Promise<boolean> => {
            update(state => ({ ...state, isLoading: true, error: null }));
            
            try {
                const response = await fetch('http://localhost:8090/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password }),
                    credentials: 'include'
                });

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.message || `Login failed with status ${response.status}`);
                }

                const data = await response.json();
                
                // Store the token
                localStorage.setItem('authToken', data.token);
                
                // Extract user info from email or response
                const userName = data.user?.name || data.name || email.split('@')[0] || 'User';
                
                set({ 
                    token: data.token, 
                    user: { name: userName },
                    isLoading: false, 
                    error: null,
                    isAuthenticated: true
                });
                return true;
                
            } catch (error) {
                console.error('Login error:', error);
                update(state => ({
                    ...state,
                    isLoading: false,
                    error: error instanceof Error ? error.message : 'Login failed',
                    isAuthenticated: false
                }));
                return false;
            }
        },
        logout: () => {
            localStorage.removeItem('authToken');
            set({ 
                token: null, 
                user: null, 
                isLoading: false, 
                error: null,
                isAuthenticated: false
            });
        },
        initialize: () => {
            const token = localStorage.getItem('authToken');
            if (token) {
                // Verify token is still valid (optional)
                // In production, you might want to verify with backend
                try {
                    // Basic JWT payload extraction (optional)
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    const isExpired = payload.exp && payload.exp * 1000 < Date.now();
                    
                    if (isExpired) {
                        localStorage.removeItem('authToken');
                        set({ 
                            token: null, 
                            user: null, 
                            isLoading: false, 
                            error: null,
                            isAuthenticated: false
                        });
                        return;
                    }
                    
                    // Extract user info from token
                    const userName = payload.sub?.split('@')[0] || 'User';
                    
                    set({ 
                        token, 
                        user: { name: userName },
                        isLoading: false, 
                        error: null,
                        isAuthenticated: true
                    });
                } catch (error) {
                    // If token parsing fails, treat as invalid
                    localStorage.removeItem('authToken');
                    set({ 
                        token: null, 
                        user: null, 
                        isLoading: false, 
                        error: null,
                        isAuthenticated: false
                    });
                }
            } else {
                set({ 
                    token: null, 
                    user: null, 
                    isLoading: false, 
                    error: null,
                    isAuthenticated: false
                });
            }
        }
    };
};

export const authStore = createAuthStore();
authStore.initialize();