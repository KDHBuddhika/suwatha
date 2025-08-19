<script lang="ts">
  import { onMount } from 'svelte';
  import { authStore } from '../stores/auth';
  import DashboardCard from '../components/DashboardCard.svelte';
  import { Users, Calendar, Activity, TrendingUp, Clock, UserCheck } from 'lucide-svelte';

  let currentTime = new Date().toLocaleString();

  onMount(() => {
    const timer = setInterval(() => {
      currentTime = new Date().toLocaleString();
    }, 1000);

    return () => clearInterval(timer);
  });

  $: user = $authStore.user;
</script>

<div class="dashboard-content">
  <div class="dashboard-header">
    <div>
      <h1 class="dashboard-title">Welcome back, {user?.name || 'Doctor'}</h1>
      <p class="dashboard-subtitle">Suwatha Therapy & Counseling Dashboard</p>
    </div>
    <div class="current-time">
      <Clock size={16} />
      {currentTime}
    </div>
  </div>

  <div class="stats-grid">
    <DashboardCard
      title="Total Doctors"
      value="24"
      subtitle="Active therapists"
      icon={Users}
      trend="up"
      trendValue="+2 this week"
    />
    <DashboardCard
      title="Today's Appointments"
      value="18"
      subtitle="Scheduled sessions"
      icon={Calendar}
      trend="neutral"
      trendValue="3 pending"
    />
    <DashboardCard
      title="Active Sessions"
      value="5"
      subtitle="Live counseling"
      icon={Activity}
      trend="up"
      trendValue="2 more than yesterday"
    />
    <DashboardCard
      title="Completion Rate"
      value="94.5%"
      subtitle="Session completion"
      icon={TrendingUp}
      trend="up"
      trendValue="+2.1%"
    />
  </div>

  <div class="dashboard-sections">
    <div class="section">
      <h2 class="section-title">Recent Activity</h2>
      <div class="activity-list">
        <div class="activity-item">
          <div class="activity-icon">
            <UserCheck size={16} />
          </div>
          <div class="activity-content">
            <p class="activity-text">Dr. Sarah completed a session with Patient #1024</p>
            <span class="activity-time">5 minutes ago</span>
          </div>
        </div>
        <div class="activity-item">
          <div class="activity-icon">
            <Calendar size={16} />
          </div>
          <div class="activity-content">
            <p class="activity-text">New appointment scheduled for tomorrow at 2:00 PM</p>
            <span class="activity-time">15 minutes ago</span>
          </div>
        </div>
        <div class="activity-item">
          <div class="activity-icon">
            <Users size={16} />
          </div>
          <div class="activity-content">
            <p class="activity-text">Dr. Michael joined the platform</p>
            <span class="activity-time">1 hour ago</span>
          </div>
        </div>
        <div class="activity-item">
          <div class="activity-icon">
            <Activity size={16} />
          </div>
          <div class="activity-content">
            <p class="activity-text">Live session started in Room #3</p>
            <span class="activity-time">2 hours ago</span>
          </div>
        </div>
      </div>
    </div>

    <div class="section">
      <h2 class="section-title">Quick Actions</h2>
      <div class="quick-actions">
        <button class="action-btn primary">
          <Users size={20} />
          Add New Doctor
        </button>
        <button class="action-btn secondary">
          <Calendar size={20} />
          Schedule Session
        </button>
        <button class="action-btn secondary">
          <Activity size={20} />
          Start Live Session
        </button>
        <button class="action-btn secondary">
          <TrendingUp size={20} />
          View Reports
        </button>
      </div>
    </div>
  </div>
</div>

<style>
  .dashboard-content {
    padding: 2rem;
    max-width: 1200px;
    margin: 0 auto;
    margin-left: 280px;
  }

  .dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 2rem;
  }

  .dashboard-title {
    font-size: 2rem;
    font-weight: bold;
    color: #111827;
    margin: 0 0 0.5rem 0;
  }

  .dashboard-subtitle {
    color: #6b7280;
    font-size: 1rem;
    margin: 0;
  }

  .current-time {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: #f9fafb;
    padding: 0.75rem 1rem;
    border-radius: 8px;
    color: #6b7280;
    font-size: 0.875rem;
    font-weight: 500;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  .dashboard-sections {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 2rem;
  }

  .section {
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e5e7eb;
  }

  .section-title {
    font-size: 1.125rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 1rem 0;
    padding-bottom: 0.75rem;
    border-bottom: 1px solid #e5e7eb;
  }

  .activity-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .activity-item {
    display: flex;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .activity-icon {
    width: 32px;
    height: 32px;
    background: #eff6ff;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #2563eb;
    flex-shrink: 0;
  }

  .activity-content {
    flex: 1;
  }

  .activity-text {
    font-size: 0.875rem;
    color: #111827;
    margin: 0 0 0.25rem 0;
    line-height: 1.4;
  }

  .activity-time {
    font-size: 0.75rem;
    color: #6b7280;
  }

  .quick-actions {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .action-btn {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.875rem 1rem;
    border: none;
    border-radius: 8px;
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    text-align: left;
  }

  .action-btn.primary {
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    color: white;
  }

  .action-btn.primary:hover {
    background: linear-gradient(135deg, #1d4ed8, #2563eb);
    transform: translateY(-1px);
  }

  .action-btn.secondary {
    background: #f9fafb;
    color: #374151;
    border: 1px solid #e5e7eb;
  }

  .action-btn.secondary:hover {
    background: #f3f4f6;
    border-color: #d1d5db;
  }

  @media (max-width: 1024px) {
    .dashboard-sections {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 768px) {
    .dashboard-content {
      padding: 1rem;
    }

    .dashboard-header {
      flex-direction: column;
      gap: 1rem;
    }

    .stats-grid {
      grid-template-columns: 1fr;
    }

    .dashboard-title {
      font-size: 1.5rem;
    }
  }
</style>