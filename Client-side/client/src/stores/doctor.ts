import { writable } from 'svelte/store';

export interface Patient {
  id: string;
  name: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  lastSession: string;
  status: 'active' | 'inactive' | 'pending';
  sessionCount: number;
  notes: string;
}

export interface Appointment {
  id: string;
  patientId: string;
  patientName: string;
  date: string;
  time: string;
  type: 'video' | 'chat' | 'special-support';
  status: 'scheduled' | 'completed' | 'cancelled' | 'in-progress';
  duration: number;
  notes?: string;
}

export interface DashboardStats {
  totalPatients: number;
  todayAppointments: number;
  weeklyHours: number;
  completedSessions: number;
}

export interface DoctorState {
  patients: Patient[];
  appointments: Appointment[];
  stats: DashboardStats;
  loading: boolean;
}

// Demo data
const demoPatients: Patient[] = [
  {
    id: 'pat_001',
    name: 'John Smith',
    email: 'john.smith@email.com',
    phone: '+1 (555) 123-4567',
    dateOfBirth: '1985-03-15',
    lastSession: '2025-01-10',
    status: 'active',
    sessionCount: 12,
    notes: 'Making good progress with anxiety management techniques.'
  },
  {
    id: 'pat_002',
    name: 'Emily Davis',
    email: 'emily.davis@email.com',
    phone: '+1 (555) 234-5678',
    dateOfBirth: '1992-07-22',
    lastSession: '2025-01-08',
    status: 'active',
    sessionCount: 8,
    notes: 'Responding well to cognitive behavioral therapy.'
  },
  {
    id: 'pat_003',
    name: 'Michael Johnson',
    email: 'michael.j@email.com',
    phone: '+1 (555) 345-6789',
    dateOfBirth: '1978-11-03',
    lastSession: '2025-01-05',
    status: 'pending',
    sessionCount: 3,
    notes: 'New patient, initial assessment completed.'
  }
];

const demoAppointments: Appointment[] = [
  {
    id: 'apt_001',
    patientId: 'pat_001',
    patientName: 'John Smith',
    date: '2025-01-15',
    time: '10:00 AM',
    type: 'video',
    status: 'scheduled',
    duration: 60
  },
  {
    id: 'apt_002',
    patientId: 'pat_002',
    patientName: 'Emily Davis',
    date: '2025-01-15',
    time: '2:00 PM',
    type: 'chat',
    status: 'scheduled',
    duration: 45
  },
  {
    id: 'apt_003',
    patientId: 'pat_003',
    patientName: 'Michael Johnson',
    date: '2025-01-16',
    time: '11:00 AM',
    type: 'video',
    status: 'scheduled',
    duration: 60
  }
];

const demoStats: DashboardStats = {
  totalPatients: 45,
  todayAppointments: 6,
  weeklyHours: 32,
  completedSessions: 156
};

const initialState: DoctorState = {
  patients: demoPatients,
  appointments: demoAppointments,
  stats: demoStats,
  loading: false
};

export const doctorStore = writable<DoctorState>(initialState);

export const doctorActions = {
  loadDashboardData: async () => {
    doctorStore.update(state => ({ ...state, loading: true }));
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    doctorStore.update(state => ({
      ...state,
      patients: demoPatients,
      appointments: demoAppointments,
      stats: demoStats,
      loading: false
    }));
  },

  updateAppointmentStatus: (appointmentId: string, status: Appointment['status']) => {
    doctorStore.update(state => ({
      ...state,
      appointments: state.appointments.map(apt => 
        apt.id === appointmentId ? { ...apt, status } : apt
      )
    }));
  },

  addPatientNote: (patientId: string, note: string) => {
    doctorStore.update(state => ({
      ...state,
      patients: state.patients.map(patient => 
        patient.id === patientId ? { ...patient, notes: note } : patient
      )
    }));
  }
};