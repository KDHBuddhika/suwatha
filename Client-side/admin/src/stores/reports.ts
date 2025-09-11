import { writable, get } from 'svelte/store';
import { authStore } from './auth'; // Assuming you have an auth store for the token

// --- INTERFACES MATCHING THE BACKEND API RESPONSE ---

// For a single report in the main list
export interface SessionReport {
  sessionId: number;
  patientHandle: string;
  therapistName: string;
  date: string;
  illness: string;
  city: string;
  therapistMessage: string | null;
  gender: string | null;
  age: number | null;
  riskAssessment: 'HIGH' | 'MODERATE' | 'LOW' | 'NONE';
}

// For the detailed view of a single report (for the modal)
export interface SessionReportDetails extends SessionReport {
  durationInMinutes: number;
  communicationType: string;
  clinicalNotes: string | null;
}

// For the `pagination` object in the main response
export interface Pagination {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  itemsPerPage: number;
}

// For the `summary` object in the main response
export interface Summary {
  totalReports: number;
  uniquePatients: number;
  activeTherapists: number;
  averageDuration: number;
}

// For the entire response from the main /api/reports endpoint
export interface PaginatedReportsResponse {
  reports: SessionReport[];
  pagination: Pagination;
  summary: Summary;
}

// For the response from the /api/reports/filters endpoint
export interface FilterOptions {
  cities: string[];
  illnesses: string[];
  riskAssessments: string[];
}

// The complete shape of our store's state
interface ReportsStoreState extends PaginatedReportsResponse {
  filterOptions: FilterOptions;
}

// The initial state for our store
const initialState: ReportsStoreState = {
  reports: [],
  pagination: { currentPage: 1, totalPages: 1, totalItems: 0, itemsPerPage: 10 },
  summary: { totalReports: 0, uniquePatients: 0, activeTherapists: 0, averageDuration: 0 },
  filterOptions: { cities: [], illnesses: [], riskAssessments: [] }
};

function createReportsStore() {
  const { subscribe, set, update } = writable<ReportsStoreState>(initialState);

  return {
    subscribe,

    // The main function to fetch the list of reports
    fetchReports: async (params: {
      page?: number; size?: number; sort?: string;
      searchTerm?: string; month?: string; city?: string;
      illness?: string; risk?: string; age?: number | null;
    }) => {
      const token = get(authStore).token;
      const query = new URLSearchParams();
      
      // Build query parameters dynamically
      query.append('page', ((params.page || 1) - 1).toString()); // Backend is likely 0-indexed
      query.append('size', (params.size || 10).toString());
      if (params.sort) query.append('sort', params.sort);
      if (params.searchTerm) query.append('q', params.searchTerm);
      if (params.month) query.append('month', params.month); // e.g., "2025-01"
      if (params.city) query.append('city', params.city);
      if (params.illness) query.append('illness', params.illness);
      if (params.risk) query.append('risk', params.risk);
      if (params.age) query.append('age', params.age.toString());

      try {
        const response = await fetch(`http://localhost:8090/api/reports?${query.toString()}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Failed to fetch reports');
        
        const data: PaginatedReportsResponse = await response.json();
        // The backend gives us page 1, but our internal state might be 0. Let's trust the backend.
        data.pagination.currentPage = data.pagination.currentPage || 1; 
        update(state => ({ ...state, ...data }));

      } catch (error) {
        console.error("Error fetching reports:", error);
      }
    },

    // Fetches the options for the filter dropdowns
    fetchFilterOptions: async () => {
      const token = get(authStore).token;
      try {
        const response = await fetch('http://localhost:8090/api/reports/filters', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Failed to fetch filter options');

        const data: FilterOptions = await response.json();
        update(state => ({ ...state, filterOptions: data }));

      } catch (error) {
        console.error("Error fetching filter options:", error);
      }
    },

    // Fetches the detailed data for a single report (for the modal)
    fetchReportDetails: async (sessionId: number): Promise<SessionReportDetails | null> => {
      const token = get(authStore).token;
      try {
        const response = await fetch(`http://localhost:8090/api/reports/${sessionId}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Failed to fetch report details');

        return await response.json();

      } catch (error) {
        console.error("Error fetching report details:", error);
        return null;
      }
    }
  };
}

export const reportsStore = createReportsStore();