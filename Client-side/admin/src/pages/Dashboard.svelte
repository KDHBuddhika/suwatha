<script lang="ts">
  import { onMount } from 'svelte';
  import { authStore } from '../stores/auth';
  import KPICard from '../components/KPICard.svelte';
  import StarRating from '../components/StarRating.svelte';
  import PieChart from '../components/PieChart.svelte';
  import BarChart from '../components/BarChart.svelte';
  import DataTable from '../components/DataTable.svelte';
  import { Video, Calendar, UserCheck, Star, Clock } from 'lucide-svelte';
  import { get } from 'svelte/store';


  let currentTime = new Date().toLocaleString();


    function formatTimestamp(isoString: string) {
    if (!isoString) return { formattedDate: '', formattedTime: '' }; // Handle empty or null strings
    const date = new Date(isoString);
    const formattedDate = date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
    const formattedTime = date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: true
    });
    return { formattedDate, formattedTime };
  }

  onMount( () => {
    const timer = setInterval(() => {
      currentTime = new Date().toLocaleString();
    }, 1000);

    // Fetch activity logs on component mount
     fetchActivityLogs();

     fetchTherapists(); 

     fetchDashboardStats();

    fetchIllnessDistribution();

    fetchPeakUsageHourly();

     fetchDailySessionVolume();

    return () => clearInterval(timer);
  });


// ----------------------------------------------------------------------------------------------------------------------------
  $: user = $authStore.user;

  // Sample data for charts
  // const pieChartData = [
  //   { label: 'Anxiety', value: 45, color: '#2563eb' },
  //   { label: 'Depression', value: 30, color: '#7c3aed' },
  //   { label: 'Trauma', value: 15, color: '#dc2626' },
  //   { label: 'Other', value: 10, color: '#059669' }
  // ];

  // Interface for Pie Chart data items
  interface PieChartDataItem {
    label: string;
    value: number;
    color: string;
  }

    // === ADD THE NEW REACTIVE VARIABLES ===
  let pieChartData: PieChartDataItem[] = [];
  let pieChartError = '';

   async function fetchIllnessDistribution() {
    try {
      const token = get(authStore).token;
      const response = await fetch('http://localhost:8090/api/admin/dashboard/charts/illness-distribution', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      pieChartData = await response.json();

    } catch (error) {
      console.error('Error fetching illness distribution data:', error);
      pieChartError = 'Failed to load patient issues data.';
    }
  }


  // ------------------------------------------------------------------------------------------------------------------------------------------

    interface ChartDataItem {
    label: string;
    value: number;
    color?: string;
  }

  // --- State Variables ---
  let hourlyUsageData: ChartDataItem[] = [];        // Data the chart is bound to
  let backendHourlyUsageData: ChartDataItem[] = []; // Stores the API response
  let hourlyUsageError = '';
  let showRealHourlyData = true;                    // The state for our toggle switch

  // --- Demo Data Generator ---
  // This function's only job is to create sample data.
  function generateDemoHourlyData(): ChartDataItem[] {
    return Array.from({ length: 24 }, (_, i) => {
      let sessions = 5; // Base sessions
      if (i >= 14 && i <= 17) sessions += Math.floor(Math.random() * 15) + 10;
      if (i >= 19 && i <= 22) sessions += Math.floor(Math.random() * 12) + 8;
      if (i >= 1 && i <= 6) sessions = Math.floor(Math.random() * 3) + 1;
      return { label: `${i}:00`, value: sessions };
    });
  }

  // --- Data Fetching Function ---
  // This function's ONLY job is to fetch data from the backend
  // and store it. It does NOT decide what the chart shows.
  async function fetchPeakUsageHourly() {
    try {
      const token = get(authStore).token;
      const response = await fetch('http://localhost:8090/api/admin/dashboard/charts/peak-usage-hourly', {
        method: 'GET',
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      backendHourlyUsageData = await response.json();

    } catch (error) {
      console.error('Error fetching peak usage data:', error);
      hourlyUsageError = 'Failed to load peak usage data.';
    }
  }

  // --- Reactive Logic Block ---
  // This block's ONLY job is to watch for changes (to the toggle or the data)
  // and decide what data the chart should display. This is the "brain" of the feature.
  $: {
    // You can open your browser's console (F12) to see this message when you toggle!
    console.log(`Toggle changed! Show real data is now: ${showRealHourlyData}`);

    if (showRealHourlyData) {
      // If the toggle is ON, use the data from the backend.
      hourlyUsageData = backendHourlyUsageData;
    } else {
      // If the toggle is OFF, generate and use fresh demo data.
      hourlyUsageData = generateDemoHourlyData();
    }
  }

  // -----------------------------------------------------------------------------------------------------------------------------------------

    // --- State Variables ---
  let dailySessionData: ChartDataItem[] = [];        // Data the chart is bound to
  let backendDailySessionData: ChartDataItem[] = []; // Stores the API response
  let dailySessionError = '';
  let showRealDailyData = true;                     // The state for our toggle switch

// --- Demo Data Generator ---
  // This is your original logic, now inside a reusable function.
  function generateDemoDailyData(): ChartDataItem[] {
    return Array.from({ length: 14 }, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - (13 - i));
      const sessions = Math.floor(Math.random() * 30) + 40; // Generates higher, more varied numbers for demo
      return { 
        label: date.toLocaleDateString('en-US', { month: 'short', day: '2-digit' }), 
        value: sessions 
      };
    });
  }

  // --- Data Fetching Function ---
  async function fetchDailySessionVolume() {
    try {
      const token = get(authStore).token;
      const response = await fetch('http://localhost:8090/api/admin/dashboard/charts/daily-session-volume', {
        method: 'GET',
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      backendDailySessionData = await response.json();

    } catch (error) {
      console.error('Error fetching daily session volume:', error);
      dailySessionError = 'Failed to load daily session data.';
    }
  }

  // --- Reactive Logic Block ---
  // This block watches for changes and decides what data the chart should display.
  $: {
    if (showRealDailyData) {
      dailySessionData = backendDailySessionData;
    } else {
      dailySessionData = generateDemoDailyData();
    }
  }


  //----------------------------------------------------------------------------------------------------------------------------------------------
  interface DashboardStats {
    activeSessions: number;
    totalSessions: number;
    finishedSessions: number;
    sessionsToday: number;
    therapistsAvailable: number;
    totalTherapists: number;
    busyTherapists: number;
    offlineTherapists: number;
    averageSessionRating: number;
  }

  let dashboardStats: DashboardStats | null = null;
  let statsError = '';

   async function fetchDashboardStats() {
    try {
      const token = get(authStore).token;
      const response = await fetch('http://localhost:8090/api/admin/dashboard/statistics', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      dashboardStats = await response.json();

    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      statsError = 'Failed to load key statistics.';
    }
  }

  //----------------------------------------------------------------------------------------------------------------------------------------------


  // const therapistData = [
  //   { name: 'Dr. Sarah Johnson', email: 'sarah.johnson@suwatha.com', status: 'Available' },
  //   { name: 'Dr. Michael Chen', email: 'michael.chen@suwatha.com', status: 'Busy' },
  //   { name: 'Dr. Emily Rodriguez', email: 'emily.rodriguez@suwatha.com', status: 'Available' },
  //   { name: 'Dr. James Wilson', email: 'james.wilson@suwatha.com', status: 'Offline' },
  //   { name: 'Dr. Lisa Anderson', email: 'lisa.anderson@suwatha.com', status: 'Available' }
  // ];

  // Interface for Therapist data from the backend
  interface Therapist {
    id: number;
    name: string;
    email: string;
    profilePictureUrl: string;
    currentStatus: 'BUSY' | 'OFFLINE' | 'AVAILABLE'; // Use specific statuses for type safety
    specializations: string[];
    active: boolean;
    handSignSpecialist: boolean;
  }

  // Interface for the data you'll pass to the DataTable
  interface TherapistDisplayData {
    name: string;
    email: string;
    status: string;
  }

  let therapistData: TherapistDisplayData[] = [];
  let therapistError = '';

  async function fetchTherapists() {
    try {
      const token = get(authStore).token;
      const response = await fetch('http://localhost:8090/api/doctor/get-all-therapist', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Tell TypeScript what kind of data to expect from the API
      const data: Therapist[] = await response.json();

      // Map the full backend response to the fields you need for the table
      therapistData = data.map(therapist => ({
        name: therapist.name,
        email: therapist.email,
        status: therapist.currentStatus // Rename currentStatus to status
      }));

    } catch (error) {
      console.error('Error fetching therapist data:', error);
      therapistError = 'Failed to load therapist data.';
    }
  }


  // -----------------------------------------------------------------------------------------------------------------------------------------------


  // const activityLogData = [
  //   { id: 'ACT-001', description: 'Dr. Sarah Johnson started a new session with Patient #1247', timestamp: '2025-01-27 13:45:23' },
  //   { id: 'ACT-002', description: 'New therapist Dr. Michael Chen registered to the platform', timestamp: '2025-01-27 13:32:15' },
  //   { id: 'ACT-003', description: 'Session completed between Dr. Emily Rodriguez and Patient #1156', timestamp: '2025-01-27 13:28:47' },
  //   { id: 'ACT-004', description: 'Dr. James Wilson updated their availability status to offline', timestamp: '2025-01-27 13:15:32' },
  //   { id: 'ACT-005', description: 'Patient #1298 scheduled appointment with Dr. Lisa Anderson', timestamp: '2025-01-27 13:08:19' },
  //   { id: 'ACT-006', description: 'Dr. Sarah Johnson completed patient assessment form', timestamp: '2025-01-27 12:55:41' },
  //   { id: 'ACT-007', description: 'System backup completed successfully', timestamp: '2025-01-27 12:30:00' },
  //   { id: 'ACT-008', description: 'Dr. Emily Rodriguez joined video call with Patient #1089', timestamp: '2025-01-27 12:22:18' }
  // ];
  // Remove the hardcoded activityLogData
  // Define interface for activity log data
   interface ActivityLog {
    id: string;
    description: string;
    timestamp?: string; // Make original timestamp optional if not displayed
    date?: string; // Add formatted date
    time?: string; // Add formatted time
  }

  // Initialize with proper type
  let activityLogData: ActivityLog[] = [];
  let activityLogError = '';

  // Function to fetch activity logs from backend
    async function fetchActivityLogs() {
    try {
      const token = get(authStore).token;
      const response = await fetch('http://localhost:8090/api/admin/dashboard/activity-logs/latest', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data: ActivityLog[] = await response.json();
      // Map over the data to format the timestamp
      activityLogData = data.map(log => {
        const { formattedDate, formattedTime } = formatTimestamp(log.timestamp || '');
        return {
          ...log,
          date: formattedDate, // Add new properties for formatted date and time
          time: formattedTime
        };
      });
    } catch (error) {
      console.error('Error fetching activity logs:', error);
      activityLogError = 'Failed to load activity logs. Please try again later.';
    }
  }
  // ---------------------------------------------------------------------------------------------------------------------------------------------
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



<!-- --------------------------------------------------------------------------------------------------------------------------------------------- -->
  <!-- Section 1: KPI Cards -->
  <!-- <div class="kpi-grid">
    <KPICard
      title="Active Sessions Now"
      value="8"
      icon={Video}
    />
    <KPICard
      title="Sessions Today"
      value="74"
      icon={Calendar}
    />
    <KPICard
      title="Therapists Available"
      value="12"
      icon={UserCheck}
    />
    <div class="kpi-card">
      <div class="kpi-header">
        <div class="kpi-info">
          <h3 class="kpi-title">Avg. Patient Rating (30d)</h3>
        </div>
        <div class="kpi-icon">
          <Star size={24} />
        </div>
      </div>
      <div class="kpi-value">
        <StarRating rating={4.6} />
      </div>
    </div>
  </div> -->

  <div class="kpi-grid">
        {#if statsError}
          <div class="error-message">{statsError}</div>
        {:else if dashboardStats}
          <!-- Card 1: Active Sessions -->
          <KPICard
            title="Active Sessions Now"
            value={dashboardStats.activeSessions.toString()}
            icon={Video}
          />
          
          <!-- Card 2: Sessions Today -->
          <KPICard
            title="Sessions Today"
            value={dashboardStats.sessionsToday.toString()}
            icon={Calendar}
          />
          
       
           <!-- Card 3: Active Sessions -->
        <KPICard
          title="Finished Sessions"
          value={dashboardStats.finishedSessions.toString()}
          icon={Video}
        />
          
          <!-- Card 4: Total session -->
        <KPICard
          title="Total Session"
          value={dashboardStats.totalSessions.toString()}
          icon={Video}
        />
          
       
    {:else}
      <!-- Loading State: Show placeholder text while fetching -->
      <KPICard title="Active Sessions Now" value="..." icon={Video} />
      <KPICard title="Sessions Today" value="..." icon={Calendar} />
      <KPICard title="Therapists Available" value="..." icon={UserCheck} />
      <KPICard title="Avg. Patient Rating" value="..." icon={Star} />
    {/if}

  </div>

  <div class="kpi-grid">
      {#if statsError}
        <div class="error-message">{statsError}</div>
      {:else if dashboardStats}
      

           <!-- Card 1: Therapists Available -->
          <KPICard
            title="Therapists Available"
            value={dashboardStats.therapistsAvailable.toString()}
            icon={UserCheck}
          />
        
        <!-- Card 2: Sessions Today -->
        <KPICard
          title="Busy Therapist | Offline Therapist"
          value={dashboardStats.busyTherapists.toString() +"   |   " + dashboardStats.offlineTherapists.toString()}
          icon={Calendar}
        />
        
        <!-- Card 3: Therapists Available -->
        <KPICard
          title="Total Therapist"
          value={dashboardStats.totalTherapists.toString()}
          icon={UserCheck}
        />

      
           <!-- Card 4: Average Rating -->
          <div class="kpi-card">
            <div class="kpi-header">
              <div class="kpi-info">
                <h3 class="kpi-title">Avg. Patient Rating (30d)</h3>
              </div>
              <div class="kpi-icon">
                <Star size={24} />
              </div>
            </div>
            <div class="kpi-value">
              <StarRating rating={dashboardStats.averageSessionRating} />
            </div>
      </div>

  {:else}
    <!-- Loading State: Show placeholder text while fetching -->
    <KPICard title="Active Sessions Now" value="..." icon={Video} />
    <KPICard title="Sessions Today" value="..." icon={Calendar} />
    <KPICard title="Therapists Available" value="..." icon={UserCheck} />
    <KPICard title="Avg. Patient Rating" value="..." icon={Star} />
  {/if}
</div>


<!-- --------------------------------------------------------------------------------------------------------------------------------------------- -->





 <!-- Section 2: Main Charts -->
<div class="charts-grid">
  <div class="chart-container"> <!-- Optional: Add a container for styling -->
    {#if pieChartError}
      <div class="error-message">
        {pieChartError}
      </div>
    {:else if pieChartData.length === 0}
      <div class="loading-message">
        Loading patient issues data...
      </div>
    {:else}
      <PieChart
        title="Patient Issues Breakdown (Last 30 Days)"
        data={pieChartData}
      />
    {/if}
  </div>

  <!-- NEW CONTAINER FOR THE BAR CHART -->
  <div class="chart-container">
    <div class="chart-header">
      <h3 class="chart-title">Peak Platform Usage by Hour of Day</h3>
      <div class="toggle-container">
        <span>Demo</span>
        <label class="switch">
          <input type="checkbox" bind:checked={showRealHourlyData}>
          <span class="slider"></span>
        </label>
        <span>Real</span>
      </div>
    </div>

    {#if hourlyUsageError}
      <div class="error-message">{hourlyUsageError}</div>
    {:else if hourlyUsageData.length === 0}
      <div class="loading-message">Loading usage data...</div>
    {:else}
      <BarChart
        data={hourlyUsageData}
        xAxisLabel="Hour of the Day"
        yAxisLabel="Number of Sessions"
        color="#2563eb"
      />
    {/if}
  </div>
</div>


<!-- --------------------------------------------------------------------------------------------------------------------------------------------- -->

  <!-- Section 3: Data Feeds and Tables -->
  <div class="data-grid">


     <!-- Daily Session Volume Chart Container -->
    <div class="chart-container">
      <div class="chart-header">
        <h3 class="chart-title">Daily Session Volume (Last 14 Days)</h3>
        <div class="toggle-container">
          <span>Demo</span>
          <label class="switch">
            <input type="checkbox" bind:checked={showRealDailyData}>
            <span class="slider"></span>
          </label>
          <span>Real</span>
        </div>
      </div>

      {#if dailySessionError}
        <div class="error-message">{dailySessionError}</div>
      {:else if dailySessionData.length === 0}
        <div class="loading-message">Loading daily session data...</div>
      {:else}
        <BarChart
          data={dailySessionData}
          xAxisLabel="Date"
          yAxisLabel="Total Sessions"
          color="#059669"
        />
      {/if}
    </div>

    
    <!-- <DataTable
      title="Therapist Status Overview"
      headers={['Name', 'Email', 'Status']}
      data={therapistData}
    /> -->
     <div class="data-table-container"> <!-- Optional: Add a container for styling -->
          {#if therapistError}
            <div class="error-message">
              {therapistError}
            </div>
          {:else if therapistData.length === 0}
            <div class="loading-message">
              Loading therapist data...
            </div>
          {:else}
            <DataTable
              title="Therapist Status Overview"
              headers={['Name', 'Email', 'Status']}
              data={therapistData}
            />
          {/if} 
     </div>
  </div>


<!-- --------------------------------------------------------------------------------------------------------------------------------------------- -->
  <!-- Section 4: Recent Activity Log -->
  <!-- <div class="activity-section">
    <DataTable
      title="Recent Activity Log"
      headers={['ID', 'Description', 'Timestamp']}
      data={activityLogData}
    />
  </div> -->

  <!-- Section 4: Recent Activity Log -->
  <div class="activity-section">
    {#if activityLogError}
      <div class="error-message">
        {activityLogError}
      </div>
    {:else if activityLogData.length === 0}
      <div class="loading-message">
        Loading activity logs...
      </div>
    {:else}
      <DataTable
        title="Recent Activity Log"
        headers={['ID', 'Description', 'Date', 'Time']} 
        data={activityLogData.map(log => ({ // Map data to match new headers
          id: log.id,
          description: log.description,
          date: log.date,
          time: log.time
        }))}
      />
    {/if}
  </div>
<!-- --------------------------------------------------------------------------------------------------------------------------------------------- -->

</div>

<style>
  .dashboard-content {
    padding: 2rem;
    max-width: 100%;
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

  .kpi-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
    margin-bottom: 4rem;
  }

  .kpi-card {
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e5e7eb;
    transition: all 0.2s;
    height: 100%;
  }

  .kpi-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  .kpi-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;
  }

  .kpi-title {
    font-size: 0.875rem;
    font-weight: 600;
    color: #6b7280;
    margin: 0;
    line-height: 1.4;
  }

  .kpi-icon {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    flex-shrink: 0;
  }

  .kpi-value {
    font-size: 2.5rem;
    font-weight: bold;
    color: #111827;
    line-height: 1;
  }

  .charts-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 2rem;
    margin-bottom: 4rem;
  }

  .data-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
    margin-bottom: 4rem;
  }


                                                        /* chart hourly usage toggle */
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
  }

  .toggle-container {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.8rem;
    color: #64748b;
  }

  /* Simple CSS for a basic toggle switch */
  .switch {
    position: relative;
    display: inline-block;
    width: 34px;
    height: 20px;
  }
  .switch input {
    opacity: 0;
    width: 0;
    height: 0;
  }
  .slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #ccc;
    transition: .4s;
    border-radius: 20px;
  }
  .slider:before {
    position: absolute;
    content: "";
    height: 14px;
    width: 14px;
    left: 3px;
    bottom: 3px;
    background-color: white;
    transition: .4s;
    border-radius: 50%;
  }
  input:checked + .slider {
    background-color: #2563eb;
  }
  input:checked + .slider:before {
    transform: translateX(14px);
  }

  @media (max-width: 1024px) {
    .charts-grid,
    .data-grid {
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

    .kpi-grid {
      grid-template-columns: 1fr;
    }

    .dashboard-title {
      font-size: 1.5rem;
    }

    .kpi-value {
      font-size: 2rem;
    }
  }
</style>