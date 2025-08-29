// src/stores/doctors.ts
import { writable } from 'svelte/store';
import { authStore } from './auth';
import { get } from 'svelte/store';

export interface Doctor {
    id: number;
    name: string;
    email: string;
    profilePictureUrl?: string;
    currentStatus: string;
    specializations: string[];
    active: boolean;
    handSignSpecialist: boolean;
}

interface DoctorsState {
    doctors: Doctor[];
    isLoading: boolean;
    error: string | null;
    specializations: {id: number, name: string}[];
}

const createDoctorsStore = () => {
    const { subscribe, set, update } = writable<DoctorsState>({
        doctors: [],
        isLoading: false,
        error: null,
        specializations: []
    });

    const getAuthHeaders = () => {
        const auth = get(authStore);
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${auth.token}`
        };
    };

    return {
        subscribe,
        
        fetchAll: async () => {
            update(state => ({ ...state, isLoading: true, error: null }));
            
            try {
                const response = await fetch('http://localhost:8090/api/doctor/get-all-therapist', {
                    method: 'GET',
                    headers: getAuthHeaders(),
                    credentials: 'include'
                });

                if (!response.ok) {
                    throw new Error(`Failed to fetch doctors: ${response.status}`);
                }

                const doctors = await response.json();
                update(state => ({ 
                    ...state, 
                    doctors: doctors || [], 
                    isLoading: false 
                }));
                
                return doctors;
            } catch (error) {
                console.error('Error fetching doctors:', error);
                update(state => ({
                    ...state,
                    isLoading: false,
                    error: error instanceof Error ? error.message : 'Failed to fetch doctors'
                }));
                return [];
            }
        },

        fetchSpecializations: async () => {
            update(state => ({ ...state, isLoading: true }));
            try {
                const response = await fetch('http://localhost:8090/api/admin/get-specialization', {
                    method: 'GET',
                    headers: getAuthHeaders()
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch specializations');
                }

                const specializations = await response.json();
                update(state => ({
                    ...state,
                    specializations,
                    isLoading: false
                }));
                return specializations;
            } catch (error) {
                console.error('Error fetching specializations:', error);
                update(state => ({
                    ...state,
                    isLoading: false,
                    error: error instanceof Error ? error.message : 'Failed to fetch specializations'
                }));
                return [];
            }
        },


add: async (formData: FormData) => {
    update(state => ({ ...state, isLoading: true, error: null }));
    
    try {
        const token = get(authStore).token;
        
        // Debug: Log FormData contents
        for (const [key, value] of formData.entries()) {
            console.log(key, value instanceof File ? value.name : value);
        }

        const response = await fetch('http://localhost:8090/api/doctor/create-Doctor', {
            method: 'POST',
            body: formData,
            headers: {
                'Authorization': `Bearer ${token}`
                // Note: Don't set Content-Type header - browser will set it with boundary
            }
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Server error: ${response.status}`);
        }

        const newDoctor = await response.json();
        update(state => ({
            ...state,
            doctors: [...state.doctors, newDoctor],
            isLoading: false
        }));
        
        return newDoctor;
    } catch (error) {
        console.error('Error adding doctor:', error);
        update(state => ({
            ...state,
            isLoading: false,
            error: error instanceof Error ? error.message : 'Failed to add doctor'
        }));
        throw error;
    }
},


        clearError: () => {
            update(state => ({ ...state, error: null }));
        },

        delete: async (id: number) => {
        update(state => ({ ...state, isLoading: true, error: null }));
        
        try {
            const token = get(authStore).token;
            const response = await fetch(`http://localhost:8090/api/doctor/delete-therapist/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Failed to delete doctor: ${response.status}`);
            }

            update(state => ({
                ...state,
                doctors: state.doctors.filter(doc => doc.id !== id),
                isLoading: false
            }));
            
            return true;
        } catch (error) {
            console.error('Error deleting doctor:', error);
            update(state => ({
                ...state,
                isLoading: false,
                error: error instanceof Error ? error.message : 'Failed to delete doctor'
            }));
            throw error;
        }
    },
    };
};

export const doctorsStore = createDoctorsStore();