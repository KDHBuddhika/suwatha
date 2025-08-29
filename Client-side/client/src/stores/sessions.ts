import { writable } from 'svelte/store';

export interface Session {
  id: string;
  communicationType: 'video' | 'audio' | 'chat' | 'in-person';
  startTime: string;
  endTime: string;
  status: 'scheduled' | 'active' | 'completed' | 'cancelled' | 'no-show';
  patientId: string;
  therapistName: string;
  rating: number | null;
  duration?: number; // in minutes
  notes?: string;
  patientName?: string;
}

const initialSessions: Session[] = [
  {
    id: 'SES-001',
    communicationType: 'video',
    startTime: '2025-01-27T14:00:00',
    endTime: '2025-01-27T15:00:00',
    status: 'completed',
    patientId: 'PAT-1247',
    therapistName: 'Dr. Sarah Johnson',
    rating: 5,
    duration: 60,
    notes: 'Great progress on anxiety management techniques',
    patientName: 'John Smith'
  },
  {
    id: 'SES-002',
    communicationType: 'audio',
    startTime: '2025-01-27T15:30:00',
    endTime: '2025-01-27T16:30:00',
    status: 'active',
    patientId: 'PAT-1156',
    therapistName: 'Dr. Michael Chen',
    rating: null,
    duration: 60,
    patientName: 'Emily Davis'
  },
  {
    id: 'SES-003',
    communicationType: 'video',
    startTime: '2025-01-27T16:00:00',
    endTime: '2025-01-27T17:00:00',
    status: 'scheduled',
    patientId: 'PAT-1089',
    therapistName: 'Dr. Emily Rodriguez',
    rating: null,
    duration: 60,
    patientName: 'Michael Brown'
  },
  {
    id: 'SES-004',
    communicationType: 'chat',
    startTime: '2025-01-27T10:00:00',
    endTime: '2025-01-27T11:00:00',
    status: 'completed',
    patientId: 'PAT-1298',
    therapistName: 'Dr. James Wilson',
    rating: 4,
    duration: 60,
    notes: 'Discussed coping strategies for work stress',
    patientName: 'Sarah Wilson'
  },
  {
    id: 'SES-005',
    communicationType: 'in-person',
    startTime: '2025-01-27T09:00:00',
    endTime: '2025-01-27T10:00:00',
    status: 'completed',
    patientId: 'PAT-1345',
    therapistName: 'Dr. Lisa Anderson',
    rating: 5,
    duration: 60,
    notes: 'Family therapy session - excellent communication',
    patientName: 'The Johnson Family'
  },
  {
    id: 'SES-006',
    communicationType: 'video',
    startTime: '2025-01-27T13:00:00',
    endTime: '2025-01-27T14:00:00',
    status: 'cancelled',
    patientId: 'PAT-1456',
    therapistName: 'Dr. Sarah Johnson',
    rating: null,
    duration: 60,
    notes: 'Patient cancelled due to emergency',
    patientName: 'Robert Taylor'
  },
  {
    id: 'SES-007',
    communicationType: 'audio',
    startTime: '2025-01-27T11:00:00',
    endTime: '2025-01-27T12:00:00',
    status: 'no-show',
    patientId: 'PAT-1567',
    therapistName: 'Dr. Michael Chen',
    rating: null,
    duration: 60,
    notes: 'Patient did not attend scheduled session',
    patientName: 'Lisa Martinez'
  },
  {
    id: 'SES-008',
    communicationType: 'video',
    startTime: '2025-01-27T17:00:00',
    endTime: '2025-01-27T18:00:00',
    status: 'scheduled',
    patientId: 'PAT-1678',
    therapistName: 'Dr. Emily Rodriguez',
    rating: null,
    duration: 60,
    patientName: 'David Anderson'
  },
  {
    id: 'SES-009',
    communicationType: 'chat',
    startTime: '2025-01-26T14:00:00',
    endTime: '2025-01-26T15:00:00',
    status: 'completed',
    patientId: 'PAT-1789',
    therapistName: 'Dr. James Wilson',
    rating: 3,
    duration: 60,
    notes: 'Initial consultation - establishing rapport',
    patientName: 'Jennifer Lee'
  },
  {
    id: 'SES-010',
    communicationType: 'in-person',
    startTime: '2025-01-26T16:00:00',
    endTime: '2025-01-26T17:30:00',
    status: 'completed',
    patientId: 'PAT-1890',
    therapistName: 'Dr. Lisa Anderson',
    rating: 5,
    duration: 90,
    notes: 'Extended session for trauma therapy',
    patientName: 'Amanda Clark'
  }
];

function createSessionsStore() {
  const { subscribe, set, update } = writable<Session[]>(initialSessions);

  return {
    subscribe,
    add: (session: Omit<Session, 'id'>) => {
      const newSession: Session = {
        ...session,
        id: `SES-${String(Date.now()).slice(-3).padStart(3, '0')}`
      };
      update(sessions => [...sessions, newSession]);
    },
    update: (id: string, updatedSession: Partial<Session>) => {
      update(sessions => 
        sessions.map(session => 
          session.id === id ? { ...session, ...updatedSession } : session
        )
      );
    },
    delete: (id: string) => {
      update(sessions => sessions.filter(session => session.id !== id));
    },
    getById: (id: string) => {
      let session: Session | undefined;
      subscribe(sessions => {
        session = sessions.find(s => s.id === id);
      })();
      return session;
    }
  };
}

export const sessionsStore = createSessionsStore();