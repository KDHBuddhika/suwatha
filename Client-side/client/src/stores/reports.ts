import { writable } from 'svelte/store';

export interface SessionReport {
  id: string;
  sessionId: string;
  patientId: string;
  therapistName: string;
  date: string;
  illness: string;
  city: string;
  therapistMessage: string;
  patientName?: string;
  duration?: number;
  sessionType?: string;
  outcome?: string;
}

const initialReports: SessionReport[] = [
  {
    id: 'RPT-001',
    sessionId: 'SES-001',
    patientId: 'PAT-1247',
    therapistName: 'Dr. Sarah Johnson',
    date: '2025-01-27',
    illness: 'Anxiety Disorder',
    city: 'New York',
    therapistMessage: 'Patient showed significant improvement in anxiety management techniques. Recommended continued practice of breathing exercises and mindfulness meditation.',
    patientName: 'John Smith',
    duration: 60,
    sessionType: 'Video Call',
    outcome: 'Positive Progress'
  },
  {
    id: 'RPT-002',
    sessionId: 'SES-002',
    patientId: 'PAT-1156',
    therapistName: 'Dr. Michael Chen',
    date: '2025-01-26',
    illness: 'Depression',
    city: 'Los Angeles',
    therapistMessage: 'Patient discussed recent challenges with work-life balance. Introduced cognitive behavioral therapy techniques to address negative thought patterns.',
    patientName: 'Emily Davis',
    duration: 50,
    sessionType: 'In-Person',
    outcome: 'Stable'
  },
  {
    id: 'RPT-003',
    sessionId: 'SES-003',
    patientId: 'PAT-1089',
    therapistName: 'Dr. Emily Rodriguez',
    date: '2025-01-25',
    illness: 'PTSD',
    city: 'Chicago',
    therapistMessage: 'Continued EMDR therapy session. Patient is processing traumatic memories well. Scheduled follow-up session for next week.',
    patientName: 'Michael Brown',
    duration: 75,
    sessionType: 'Video Call',
    outcome: 'Gradual Improvement'
  },
  {
    id: 'RPT-004',
    sessionId: 'SES-004',
    patientId: 'PAT-1298',
    therapistName: 'Dr. James Wilson',
    date: '2025-01-24',
    illness: 'Bipolar Disorder',
    city: 'Houston',
    therapistMessage: 'Patient reported mood stability over the past week. Medication compliance is good. Discussed coping strategies for stress management.',
    patientName: 'Sarah Wilson',
    duration: 45,
    sessionType: 'Audio Call',
    outcome: 'Stable'
  },
  {
    id: 'RPT-005',
    sessionId: 'SES-005',
    patientId: 'PAT-1345',
    therapistName: 'Dr. Lisa Anderson',
    date: '2025-01-23',
    illness: 'Anxiety Disorder',
    city: 'Phoenix',
    therapistMessage: 'Family therapy session focused on communication patterns. All family members participated actively. Homework assigned for conflict resolution.',
    patientName: 'The Johnson Family',
    duration: 90,
    sessionType: 'In-Person',
    outcome: 'Positive Progress'
  },
  {
    id: 'RPT-006',
    sessionId: 'SES-006',
    patientId: 'PAT-1456',
    therapistName: 'Dr. Sarah Johnson',
    date: '2025-01-22',
    illness: 'OCD',
    city: 'Philadelphia',
    therapistMessage: 'Exposure and Response Prevention therapy session. Patient successfully completed exposure exercises. Significant reduction in compulsive behaviors noted.',
    patientName: 'Robert Taylor',
    duration: 60,
    sessionType: 'Video Call',
    outcome: 'Significant Improvement'
  },
  {
    id: 'RPT-007',
    sessionId: 'SES-007',
    patientId: 'PAT-1567',
    therapistName: 'Dr. Michael Chen',
    date: '2025-01-21',
    illness: 'Depression',
    city: 'San Antonio',
    therapistMessage: 'Patient expressed feelings of hopelessness. Implemented safety planning and increased session frequency. Coordinated with psychiatrist for medication review.',
    patientName: 'Lisa Martinez',
    duration: 55,
    sessionType: 'In-Person',
    outcome: 'Requires Monitoring'
  },
  {
    id: 'RPT-008',
    sessionId: 'SES-008',
    patientId: 'PAT-1678',
    therapistName: 'Dr. Emily Rodriguez',
    date: '2025-01-20',
    illness: 'Social Anxiety',
    city: 'San Diego',
    therapistMessage: 'Role-playing exercises for social situations. Patient showed increased confidence in group interactions. Recommended joining support group.',
    patientName: 'David Anderson',
    duration: 50,
    sessionType: 'Video Call',
    outcome: 'Positive Progress'
  },
  {
    id: 'RPT-009',
    sessionId: 'SES-009',
    patientId: 'PAT-1789',
    therapistName: 'Dr. James Wilson',
    date: '2025-01-19',
    illness: 'Panic Disorder',
    city: 'Dallas',
    therapistMessage: 'Initial assessment completed. Patient experiences frequent panic attacks. Started psychoeducation about panic disorder and introduced grounding techniques.',
    patientName: 'Jennifer Lee',
    duration: 60,
    sessionType: 'Audio Call',
    outcome: 'Initial Assessment'
  },
  {
    id: 'RPT-010',
    sessionId: 'SES-010',
    patientId: 'PAT-1890',
    therapistName: 'Dr. Lisa Anderson',
    date: '2025-01-18',
    illness: 'ADHD',
    city: 'Austin',
    therapistMessage: 'Behavioral intervention strategies discussed. Patient and parents educated about ADHD management. Recommended organizational tools and study techniques.',
    patientName: 'Amanda Clark',
    duration: 45,
    sessionType: 'In-Person',
    outcome: 'Educational Session'
  },
  {
    id: 'RPT-011',
    sessionId: 'SES-011',
    patientId: 'PAT-1991',
    therapistName: 'Dr. Sarah Johnson',
    date: '2025-01-17',
    illness: 'Eating Disorder',
    city: 'Jacksonville',
    therapistMessage: 'Nutritional counseling session. Patient showed improved relationship with food. Coordinated care with dietitian and medical team.',
    patientName: 'Maria Rodriguez',
    duration: 60,
    sessionType: 'Video Call',
    outcome: 'Collaborative Care'
  },
  {
    id: 'RPT-012',
    sessionId: 'SES-012',
    patientId: 'PAT-2092',
    therapistName: 'Dr. Michael Chen',
    date: '2025-01-16',
    illness: 'Substance Abuse',
    city: 'Fort Worth',
    therapistMessage: 'Relapse prevention planning. Patient completed 30 days of sobriety. Discussed triggers and coping mechanisms. Connected with AA sponsor.',
    patientName: 'Carlos Gonzalez',
    duration: 50,
    sessionType: 'In-Person',
    outcome: 'Recovery Progress'
  },
  {
    id: 'RPT-013',
    sessionId: 'SES-013',
    patientId: 'PAT-2193',
    therapistName: 'Dr. Emily Rodriguez',
    date: '2025-01-15',
    illness: 'Grief Counseling',
    city: 'Columbus',
    therapistMessage: 'Processing loss of spouse. Patient working through stages of grief. Introduced memory work and meaning-making exercises.',
    patientName: 'Eleanor Thompson',
    duration: 55,
    sessionType: 'Video Call',
    outcome: 'Grief Processing'
  },
  {
    id: 'RPT-014',
    sessionId: 'SES-014',
    patientId: 'PAT-2294',
    therapistName: 'Dr. James Wilson',
    date: '2025-01-14',
    illness: 'Anger Management',
    city: 'Charlotte',
    therapistMessage: 'Anger management techniques practiced. Patient reported fewer outbursts at work. Continued work on emotional regulation and communication skills.',
    patientName: 'Marcus Johnson',
    duration: 45,
    sessionType: 'Audio Call',
    outcome: 'Behavioral Improvement'
  },
  {
    id: 'RPT-015',
    sessionId: 'SES-015',
    patientId: 'PAT-2395',
    therapistName: 'Dr. Lisa Anderson',
    date: '2025-01-13',
    illness: 'Relationship Issues',
    city: 'San Francisco',
    therapistMessage: 'Couples therapy session. Worked on communication patterns and conflict resolution. Both partners showed willingness to implement changes.',
    patientName: 'Alex & Jordan Kim',
    duration: 75,
    sessionType: 'In-Person',
    outcome: 'Relationship Progress'
  }
];

function createReportsStore() {
  const { subscribe, set, update } = writable<SessionReport[]>(initialReports);

  return {
    subscribe,
    add: (report: Omit<SessionReport, 'id'>) => {
      const newReport: SessionReport = {
        ...report,
        id: `RPT-${String(Date.now()).slice(-3).padStart(3, '0')}`
      };
      update(reports => [...reports, newReport]);
    },
    update: (id: string, updatedReport: Partial<SessionReport>) => {
      update(reports => 
        reports.map(report => 
          report.id === id ? { ...report, ...updatedReport } : report
        )
      );
    },
    delete: (id: string) => {
      update(reports => reports.filter(report => report.id !== id));
    },
    getById: (id: string) => {
      let report: SessionReport | undefined;
      subscribe(reports => {
        report = reports.find(r => r.id === id);
      })();
      return report;
    }
  };
}

export const reportsStore = createReportsStore();