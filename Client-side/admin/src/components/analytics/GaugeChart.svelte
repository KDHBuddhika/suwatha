<script lang="ts">
  import { onMount } from 'svelte';

  let utilizationRate = 78.5;
  let animatedRate = 0;

  onMount(() => {
    // Animate the gauge
    const duration = 2000;
    const steps = 60;
    const increment = utilizationRate / steps;
    let currentStep = 0;

    const timer = setInterval(() => {
      currentStep++;
      animatedRate = Math.min(increment * currentStep, utilizationRate);
      
      if (currentStep >= steps) {
        clearInterval(timer);
      }
    }, duration / steps);

    return () => clearInterval(timer);
  });

  function getGaugeColor(rate: number) {
    if (rate >= 80) return '#dc2626'; // Red - Over capacity
    if (rate >= 70) return '#f59e0b'; // Orange - High utilization
    if (rate >= 50) return '#22c55e'; // Green - Optimal
    return '#3b82f6'; // Blue - Low utilization
  }

  function getStatusText(rate: number) {
    if (rate >= 80) return 'Over Capacity';
    if (rate >= 70) return 'High Utilization';
    if (rate >= 50) return 'Optimal Range';
    return 'Low Utilization';
  }

  $: gaugeColor = getGaugeColor(animatedRate);
  $: statusText = getStatusText(animatedRate);
  $: circumference = 2 * Math.PI * 90;
  $: strokeDasharray = circumference;
  $: strokeDashoffset = circumference - (animatedRate / 100) * circumference;
</script>

<div class="gauge-chart">
  <div class="gauge-container">
    <svg class="gauge-svg" viewBox="0 0 200 200">
      <!-- Background circle -->
      <circle
        cx="100"
        cy="100"
        r="90"
        fill="none"
        stroke="#e2e8f0"
        stroke-width="12"
      />
      
      <!-- Progress circle -->
      <circle
        cx="100"
        cy="100"
        r="90"
        fill="none"
        stroke={gaugeColor}
        stroke-width="12"
        stroke-linecap="round"
        stroke-dasharray={strokeDasharray}
        stroke-dashoffset={strokeDashoffset}
        transform="rotate(-90 100 100)"
        class="progress-circle"
      />
      
      <!-- Center content -->
      <text x="100" y="95" text-anchor="middle" class="gauge-value">
        {Math.round(animatedRate)}%
      </text>
      <text x="100" y="115" text-anchor="middle" class="gauge-label">
        Utilization
      </text>
    </svg>
  </div>

  <div class="gauge-info">
    <div class="status-indicator" style="background-color: {gaugeColor}">
      <span class="status-text">{statusText}</span>
    </div>
    
    <div class="gauge-metrics">
      <div class="metric-row">
        <span class="metric-label">Active Therapists</span>
        <span class="metric-value">47 / 60</span>
      </div>
      <div class="metric-row">
        <span class="metric-label">Avg. Sessions/Day</span>
        <span class="metric-value">6.2</span>
      </div>
      <div class="metric-row">
        <span class="metric-label">Peak Hours</span>
        <span class="metric-value">2-5 PM</span>
      </div>
    </div>
  </div>
</div>

<style>
  .gauge-chart {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 2rem;
  }

  .gauge-container {
    width: 250px;
    height: 250px;
  }

  .gauge-svg {
    width: 100%;
    height: 100%;
  }

  .progress-circle {
    transition: stroke-dashoffset 0.1s ease-out;
  }

  .gauge-value {
    font-size: 2.5rem;
    font-weight: 700;
    fill: #1e293b;
  }

  .gauge-label {
    font-size: 1rem;
    font-weight: 600;
    fill: #64748b;
  }

  .gauge-info {
    text-align: center;
    width: 100%;
  }

  .status-indicator {
    display: inline-block;
    padding: 0.5rem 1rem;
    border-radius: 20px;
    margin-bottom: 1.5rem;
  }

  .status-text {
    color: white;
    font-weight: 600;
    font-size: 0.875rem;
  }

  .gauge-metrics {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .metric-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.5rem 0;
    border-bottom: 1px solid #f1f5f9;
  }

  .metric-label {
    font-size: 0.875rem;
    color: #64748b;
    font-weight: 500;
  }

  .metric-value {
    font-size: 0.875rem;
    color: #1e293b;
    font-weight: 700;
  }

  @media (max-width: 768px) {
    .gauge-container {
      width: 200px;
      height: 200px;
    }

    .gauge-value {
      font-size: 2rem;
    }
  }
</style>