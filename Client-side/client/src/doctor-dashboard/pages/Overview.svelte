<script lang="ts">
  import { onMount } from 'svelte';
  import { doctorStore, doctorActions } from '../../stores/doctor';
  import { Users, Calendar, Clock, TrendingUp, Video, MessageCircle, Phone } from 'lucide-svelte';

  onMount(() => {
    doctorActions.loadDashboardData();
  });

  $: stats = $doctorStore.stats;
  $: todayAppointments = $doctorStore.appointments.filter(apt => 
    apt.date === new Date().toISOString().split('T')[0]
  );
  $: recentPatients = $doctorStore.patients.slice(0, 5);
</script>

<div class="overview-container">
  <div class="page-header">
    <h1>Dashboard Overview</h1>
    <p>Welcome back! Here's what's happening with your practice today.</p>
  </div>

  <!-- Stats Cards -->
  <div class="stats-grid">
    <div class="stat-card">
      <div class="stat-icon patients">
        <Users size={24} />
      </div>
      <div class="stat-content">
        <h3>{stats.totalPatients}</h3>
        <p>Total Patients</p>
        <span class="stat-change positive">+12% this month</span>
      </div>
    </div>

    <div class="stat-card">
      <div class="stat-icon appointments">
        <Calendar size={24} />
      </div>
      <div class="stat-content">
        <h3>{stats.todayAppointments}</h3>
        <p>Today's Appointments</p>
        <span class="stat-change neutral">2 completed</span>
      </div>
    </div>

    <div class="stat-card">
      <div class="stat-icon hours">
        <Clock size={24} />
      </div>
      <div class="stat-content">
        <h3>{stats.weeklyHours}</h3>
        <p>Weekly Hours</p>
        <span class="stat-change positive">+5% vs last week</span>
      </div>
    </div>

    <div class="stat-card">
      <div class="stat-icon sessions">
        <TrendingUp size={24} />
      </div>
      <div class="stat-content">
        <h3>{stats.completedSessions}</h3>
        <p>Completed Sessions</p>
        <span class="stat-change positive">+8% this month</span>
      </div>
    </div>
  </div>

  <div class="content-grid">
    <!-- Today's Appointments -->
    <div class="content-card">
      <div class="card-header">
        <h2>Today's Appointments</h2>
        <span class="appointment-count">{todayAppointments.length} scheduled</span>
      </div>
      <div class="appointments-list">
        {#each todayAppointments as appointment}
          <div class="appointment-item">
            <div class="appointment-time">
              <span class="time">{appointment.time}</span>
              <span class="duration">{appointment.duration}min</span>
            </div>
            <div class="appointment-details">
              <h4>{appointment.patientName}</h4>
              <div class="appointment-type">
                {#if appointment.type === 'video'}
                  <Video size={16} />
                  <span>Video Session</span>
                {:else if appointment.type === 'chat'}
                  <MessageCircle size={16} />
                  <span>Chat Session</span>
                {:else}
                  <Phone size={16} />
                  <span>Special Support</span>
                {/if}
              </div>
            </div>
            <div class="appointment-status">
              <span class="status-badge {appointment.status}">{appointment.status}</span>
            </div>
          </div>
        {/each}
        
        {#if todayAppointments.length === 0}
          <div class="empty-state">
            <Calendar size={48} />
            <p>No appointments scheduled for today</p>
          </div>
        {/if}
      </div>
    </div>

    <!-- Recent Patients -->
    <div class="content-card">
      <div class="card-header">
        <h2>Recent Patients</h2>
        <a href="#patients" class="view-all">View All</a>
      </div>
      <div class="patients-list">
        {#each recentPatients as patient}
          <div class="patient-item">
            <div class="patient-avatar">
              <span>{patient.name.charAt(0)}</span>
            </div>
            <div class="patient-details">
              <h4>{patient.name}</h4>
              <p>Last session: {new Date(patient.lastSession).toLocaleDateString()}</p>
            </div>
            <div class="patient-status">
              <span class="status-dot {patient.status}"></span>
              <span class="session-count">{patient.sessionCount} sessions</span>
            </div>
          </div>
        {/each}
      </div>
    </div>
  </div>

  <!-- Quick Actions -->
  <div class="quick-actions">
    <h2>Quick Actions</h2>
    <div class="actions-grid">
      <button class="action-btn">
        <Calendar size={20} />
        <span>Schedule Appointment</span>
      </button>
      <button class="action-btn">
        <Users size={20} />
        <span>Add New Patient</span>
      </button>
      <button class="action-btn">
        <MessageCircle size={20} />
        <span>Start Chat Session</span>
      </button>
      <button class="action-btn">
        <Video size={20} />
        <span>Start Video Call</span>
      </button>
    </div>
  </div>
</div>

<style>
  .overview-container {
    max-width: 1400px;
    margin: 0 auto;
  }

  .page-header {
    margin-bottom: 2rem;
  }

  .page-header h1 {
    font-size: 2rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
  }

  .page-header p {
    color: #6B7280;
    font-size: 1rem;
    margin: 0;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  .stat-card {
    background: white;
    border-radius: 1rem;
    padding: 1.5rem;
    border: 1px solid #E5E7EB;
    display: flex;
    align-items: center;
    gap: 1rem;
    transition: all 0.2s ease;
  }

  .stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    border-radius: 0.75rem;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
  }

  .stat-icon.patients { background: linear-gradient(135deg, #4F46E5, #7C3AED); }
  .stat-icon.appointments { background: linear-gradient(135deg, #10B981, #059669); }
  .stat-icon.hours { background: linear-gradient(135deg, #F59E0B, #D97706); }
  .stat-icon.sessions { background: linear-gradient(135deg, #EF4444, #DC2626); }

  .stat-content h3 {
    font-size: 1.75rem;
    font-weight: 700;
    color: #374151;
    margin: 0 0 0.25rem 0;
  }

  .stat-content p {
    color: #6B7280;
    font-size: 0.875rem;
    margin: 0 0 0.5rem 0;
  }

  .stat-change {
    font-size: 0.75rem;
    font-weight: 500;
  }

  .stat-change.positive { color: #10B981; }
  .stat-change.negative { color: #EF4444; }
  .stat-change.neutral { color: #6B7280; }

  .content-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
    margin-bottom: 2rem;
  }

  .content-card {
    background: white;
    border-radius: 1rem;
    border: 1px solid #E5E7EB;
    overflow: hidden;
  }

  .card-header {
    padding: 1.5rem;
    border-bottom: 1px solid #E5E7EB;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-header h2 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #374151;
    margin: 0;
  }

  .appointment-count {
    background: #EEF2FF;
    color: #4F46E5;
    padding: 0.25rem 0.75rem;
    border-radius: 1rem;
    font-size: 0.875rem;
    font-weight: 500;
  }

  .view-all {
    color: #4F46E5;
    text-decoration: none;
    font-size: 0.875rem;
    font-weight: 500;
  }

  .appointments-list, .patients-list {
    padding: 0;
  }

  .appointment-item, .patient-item {
    display: flex;
    align-items: center;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #F3F4F6;
    transition: background 0.2s ease;
  }

  .appointment-item:hover, .patient-item:hover {
    background: #F9FAFB;
  }

  .appointment-time {
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 80px;
    margin-right: 1rem;
  }

  .time {
    font-weight: 600;
    color: #374151;
    font-size: 0.875rem;
  }

  .duration {
    font-size: 0.75rem;
    color: #6B7280;
  }

  .appointment-details {
    flex: 1;
  }

  .appointment-details h4 {
    margin: 0 0 0.25rem 0;
    font-size: 0.875rem;
    font-weight: 600;
    color: #374151;
  }

  .appointment-type {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #6B7280;
    font-size: 0.75rem;
  }

  .status-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 1rem;
    font-size: 0.75rem;
    font-weight: 500;
    text-transform: capitalize;
  }

  .status-badge.scheduled {
    background: #FEF3E7;
    color: #92400E;
  }

  .status-badge.completed {
    background: #F0FDF4;
    color: #166534;
  }

  .status-badge.cancelled {
    background: #FEF2F2;
    color: #991B1B;
  }

  .patient-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: linear-gradient(135deg, #4F46E5, #7C3AED);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    margin-right: 1rem;
  }

  .patient-details {
    flex: 1;
  }

  .patient-details h4 {
    margin: 0 0 0.25rem 0;
    font-size: 0.875rem;
    font-weight: 600;
    color: #374151;
  }

  .patient-details p {
    margin: 0;
    font-size: 0.75rem;
    color: #6B7280;
  }

  .patient-status {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .status-dot.active { background: #10B981; }
  .status-dot.inactive { background: #6B7280; }
  .status-dot.pending { background: #F59E0B; }

  .session-count {
    font-size: 0.75rem;
    color: #6B7280;
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 3rem 1.5rem;
    color: #9CA3AF;
    text-align: center;
  }

  .empty-state p {
    margin: 1rem 0 0 0;
    font-size: 0.875rem;
  }

  .quick-actions {
    background: white;
    border-radius: 1rem;
    padding: 1.5rem;
    border: 1px solid #E5E7EB;
  }

  .quick-actions h2 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #374151;
    margin: 0 0 1rem 0;
  }

  .actions-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1rem;
  }

  .action-btn {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 1rem;
    background: #F8FAFC;
    border: 1px solid #E5E7EB;
    border-radius: 0.5rem;
    color: #374151;
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .action-btn:hover {
    background: #4F46E5;
    color: white;
    border-color: #4F46E5;
  }

  @media (max-width: 1024px) {
    .content-grid {
      grid-template-columns: 1fr;
    }

    .stats-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  @media (max-width: 768px) {
    .stats-grid {
      grid-template-columns: 1fr;
    }

    .actions-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }
</style>