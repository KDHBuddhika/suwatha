<script lang="ts">
  import { onMount } from 'svelte';
  import { Calendar, TrendingUp, Users, MapPin, Activity, AlertTriangle } from 'lucide-svelte';
  import DateRangeFilter from '../components/analytics/DateRangeFilter.svelte';
  import FunnelChart from '../components/analytics/FunnelChart.svelte';
  import GaugeChart from '../components/analytics/GaugeChart.svelte';
  import CorrelationTable from '../components/analytics/CorrelationTable.svelte';
  import SpecializationHeatmap from '../components/analytics/SpecializationHeatmap.svelte';
  import CohortChart from '../components/analytics/CohortChart.svelte';
  import ChurnRiskTable from '../components/analytics/ChurnRiskTable.svelte';
  import GeospatialMap from '../components/analytics/GeospatialMap.svelte';
  import LoadingSkeleton from '../components/analytics/LoadingSkeleton.svelte';

  let activeTab = 'operational';
  let dateRange = { start: '', end: '', preset: 'last30days' };
  let isLoading = true;

  const tabs = [
    { id: 'operational', label: 'Operational Health', icon: Activity },
    { id: 'clinical', label: 'Clinical Impact', icon: TrendingUp },
    { id: 'therapist', label: 'Therapist Analytics', icon: Users },
    { id: 'market', label: 'Market & Demographics', icon: MapPin }
  ];

  onMount(() => {
    // Simulate initial data loading
    setTimeout(() => {
      isLoading = false;
    }, 2000);
  });

  function handleDateRangeChange(event: CustomEvent) {
    dateRange = event.detail;
    isLoading = true;
    
    // Simulate data refresh
    setTimeout(() => {
      isLoading = false;
    }, 1500);
  }

  function setActiveTab(tabId: string) {
    activeTab = tabId;
  }
</script>

<div class="analytics-page">
  <!-- Global Header with Date Range Filter -->
  <div class="analytics-header">
    <div class="header-content">
      <div class="title-section">
        <h1 class="page-title">Manosara Analytics</h1>
        <p class="page-subtitle">Advanced insights for mental health platform performance</p>
      </div>
      <DateRangeFilter on:change={handleDateRangeChange} />
    </div>
  </div>

  <!-- Primary Tabbed Interface -->
  <div class="tabs-container">
    <div class="tabs-header">
      {#each tabs as tab}
        <button
          class="tab-button"
          class:active={activeTab === tab.id}
          on:click={() => setActiveTab(tab.id)}
        >
          <svelte:component this={tab.icon} size={20} />
          {tab.label}
        </button>
      {/each}
    </div>

    <div class="tab-content">
      {#if activeTab === 'operational'}
        <div class="operational-tab">
          <div class="two-column-layout">
            <div class="chart-section">
              <div class="chart-container">
                <h3 class="chart-title">Patient Journey Funnel</h3>
                <p class="chart-subtitle">Connection to completion flow</p>
                {#if isLoading}
                  <LoadingSkeleton type="funnel" />
                {:else}
                  <FunnelChart />
                {/if}
              </div>
            </div>
            <div class="chart-section">
              <div class="chart-container">
                <h3 class="chart-title">Therapist Utilization Rate</h3>
                <p class="chart-subtitle">Real-time capacity monitoring</p>
                {#if isLoading}
                  <LoadingSkeleton type="gauge" />
                {:else}
                  <GaugeChart />
                {/if}
              </div>
            </div>
          </div>
        </div>
      {:else if activeTab === 'clinical'}
        <div class="clinical-tab">
          <div class="two-column-layout">
            <div class="chart-section">
              <div class="chart-container">
                <h3 class="chart-title">Session Metrics vs Patient Risk</h3>
                <p class="chart-subtitle">Correlation analysis for clinical effectiveness</p>
                {#if isLoading}
                  <LoadingSkeleton type="table" />
                {:else}
                  <CorrelationTable />
                {/if}
              </div>
            </div>
            <div class="chart-section">
              <div class="chart-container">
                <h3 class="chart-title">Specialization Effectiveness</h3>
                <p class="chart-subtitle">Therapist skills vs patient outcomes</p>
                {#if isLoading}
                  <LoadingSkeleton type="heatmap" />
                {:else}
                  <SpecializationHeatmap />
                {/if}
              </div>
            </div>
          </div>
        </div>
      {:else if activeTab === 'therapist'}
        <div class="therapist-tab">
          <div class="two-column-layout">
            <div class="chart-section">
              <div class="chart-container">
                <h3 class="chart-title">Therapist Cohort Analysis</h3>
                <p class="chart-subtitle">New therapist ramp-up tracking</p>
                {#if isLoading}
                  <LoadingSkeleton type="line" />
                {:else}
                  <CohortChart />
                {/if}
              </div>
            </div>
            <div class="chart-section">
              <div class="chart-container">
                <h3 class="chart-title">Churn Risk Assessment</h3>
                <p class="chart-subtitle">At-risk therapist identification</p>
                {#if isLoading}
                  <LoadingSkeleton type="table" />
                {:else}
                  <ChurnRiskTable />
                {/if}
              </div>
            </div>
          </div>
        </div>
      {:else if activeTab === 'market'}
        <div class="market-tab">
          <div class="full-width-layout">
            <div class="chart-container">
              <h3 class="chart-title">Geospatial Illness Distribution</h3>
              <p class="chart-subtitle">Session volume and mental health patterns by location</p>
              {#if isLoading}
                <LoadingSkeleton type="map" />
              {:else}
                <GeospatialMap />
              {/if}
            </div>
          </div>
        </div>
      {/if}
    </div>
  </div>
</div>


<style>
  .analytics-page {
    min-height: 100vh;
    background: #f8fafc;
    margin-left: 250px; /* Adjust based on sidebar width */
  }

  .analytics-header {
    background: white;
    border-bottom: 1px solid #e2e8f0;
    padding: 2rem;
    position: sticky;
    top: 0;
    z-index: 100;
  }

  .header-content {
    max-width: 1600px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 2rem;
  }

  .title-section {
    flex: 1;
  }

  .page-title {
    font-size: 2.5rem;
    font-weight: 700;
    color: #1e293b;
    padding-left: 25px;
    margin: 2 0 0.5rem 0;
    background: linear-gradient(135deg, #2563eb, #7c3aed);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .page-subtitle {
    color: #64748b;
    font-size: 1.125rem;
    padding-left: 25px;
    margin: 0;
    font-weight: 500;
  }

  .tabs-container {
    max-width: 1600px;
    margin: 0 auto;
    padding: 0 2rem 2rem;
  }

  .tabs-header {
    display: flex;
    background: white;
    border-radius: 12px 12px 0 0;
    border: 1px solid #e2e8f0;
    border-bottom: none;
    overflow-x: auto;
    margin-top: 2rem;
  }

  .tab-button {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 1.25rem 2rem;
    background: none;
    border: none;
    color: #64748b;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    border-bottom: 3px solid transparent;
    white-space: nowrap;
    position: relative;
  }

  .tab-button:hover {
    color: #2563eb;
    background: #f8fafc;
  }

  .tab-button.active {
    color: #2563eb;
    background: #f8fafc;
    border-bottom-color: #2563eb;
  }

  .tab-button.active::after {
    content: '';
    position: absolute;
    bottom: -1px;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    border-radius: 2px 2px 0 0;
  }

  .tab-content {
    background: white;
    border: 1px solid #e2e8f0;
    border-radius: 0 0 12px 12px;
    min-height: 600px;
  }

  .two-column-layout {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
    padding: 2rem;
  }

  .full-width-layout {
    padding: 2rem;
  }

  .chart-section {
    display: flex;
    flex-direction: column;
  }

  .chart-container {
    background: #ffffff;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e2e8f0;
    height: 100%;
    transition: all 0.3s ease;
  }

  .chart-container:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  .chart-title {
    font-size: 1.25rem;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 0.5rem 0;
  }

  .chart-subtitle {
    color: #64748b;
    font-size: 0.875rem;
    margin: 0 0 1.5rem 0;
    font-weight: 500;
  }

  @media (max-width: 1200px) {
    .two-column-layout {
      grid-template-columns: 1fr;
      gap: 1.5rem;
    }
  }

  @media (max-width: 768px) {
    .analytics-header {
      padding: 1.5rem 1rem;
    }

    .header-content {
      flex-direction: column;
      align-items: stretch;
      gap: 1.5rem;
    }

    .page-title {
      font-size: 2rem;
    }

    .tabs-container {
      padding: 0 1rem 1rem;
    }

    .tab-button {
      padding: 1rem 1.5rem;
      font-size: 0.875rem;
    }

    .two-column-layout,
    .full-width-layout {
      padding: 1.5rem;
    }
  }

  @media (max-width: 480px) {
    .tab-button {
      padding: 0.875rem 1rem;
      flex-direction: column;
      gap: 0.25rem;
      text-align: center;
    }

    .page-title {
      font-size: 1.75rem;
    }
  }
</style>