<script lang="ts">
  import { onMount } from 'svelte';
  import { X } from 'lucide-svelte';

  let selectedCity: any | null = null;
  let showCityModal = false;

  const cityData = [
    {
      name: 'New York',
      coordinates: { x: 75, y: 25 },
      sessionVolume: 1247,
      topIllnesses: [
        { name: 'Anxiety Disorder', count: 387, percentage: 31 },
        { name: 'Depression', count: 298, percentage: 24 },
        { name: 'PTSD', count: 186, percentage: 15 },
        { name: 'Bipolar Disorder', count: 149, percentage: 12 },
        { name: 'OCD', count: 124, percentage: 10 },
        { name: 'Other', count: 103, percentage: 8 }
      ]
    },
    {
      name: 'Los Angeles',
      coordinates: { x: 15, y: 45 },
      sessionVolume: 892,
      topIllnesses: [
        { name: 'Depression', count: 267, percentage: 30 },
        { name: 'Anxiety Disorder', count: 232, percentage: 26 },
        { name: 'Eating Disorder', count: 125, percentage: 14 },
        { name: 'Addiction', count: 107, percentage: 12 },
        { name: 'PTSD', count: 89, percentage: 10 },
        { name: 'Other', count: 72, percentage: 8 }
      ]
    },
    {
      name: 'Chicago',
      coordinates: { x: 55, y: 35 },
      sessionVolume: 634,
      topIllnesses: [
        { name: 'Anxiety Disorder', count: 203, percentage: 32 },
        { name: 'Depression', count: 177, percentage: 28 },
        { name: 'Trauma', count: 95, percentage: 15 },
        { name: 'Bipolar Disorder', count: 76, percentage: 12 },
        { name: 'Social Anxiety', count: 57, percentage: 9 },
        { name: 'Other', count: 26, percentage: 4 }
      ]
    },
    {
      name: 'Houston',
      coordinates: { x: 45, y: 65 },
      sessionVolume: 456,
      topIllnesses: [
        { name: 'Depression', count: 137, percentage: 30 },
        { name: 'Anxiety Disorder', count: 118, percentage: 26 },
        { name: 'PTSD', count: 82, percentage: 18 },
        { name: 'Addiction', count: 64, percentage: 14 },
        { name: 'Panic Disorder', count: 32, percentage: 7 },
        { name: 'Other', count: 23, percentage: 5 }
      ]
    },
    {
      name: 'Phoenix',
      coordinates: { x: 25, y: 55 },
      sessionVolume: 298,
      topIllnesses: [
        { name: 'Anxiety Disorder', count: 89, percentage: 30 },
        { name: 'Depression', count: 77, percentage: 26 },
        { name: 'Trauma', count: 54, percentage: 18 },
        { name: 'Bipolar Disorder', count: 42, percentage: 14 },
        { name: 'OCD', count: 24, percentage: 8 },
        { name: 'Other', count: 12, percentage: 4 }
      ]
    },
    {
      name: 'Philadelphia',
      coordinates: { x: 78, y: 32 },
      sessionVolume: 387,
      topIllnesses: [
        { name: 'Depression', count: 124, percentage: 32 },
        { name: 'Anxiety Disorder', count: 101, percentage: 26 },
        { name: 'PTSD', count: 66, percentage: 17 },
        { name: 'Social Anxiety', count: 50, percentage: 13 },
        { name: 'Panic Disorder', count: 31, percentage: 8 },
        { name: 'Other', count: 15, percentage: 4 }
      ]
    }
  ];

  function getMarkerSize(volume: number) {
    const maxVolume = Math.max(...cityData.map(c => c.sessionVolume));
    const minSize = 12;
    const maxSize = 32;
    return minSize + (volume / maxVolume) * (maxSize - minSize);
  }

  function getMarkerColor(volume: number) {
    const maxVolume = Math.max(...cityData.map(c => c.sessionVolume));
    const intensity = volume / maxVolume;
    
    if (intensity > 0.8) return '#dc2626';
    if (intensity > 0.6) return '#f59e0b';
    if (intensity > 0.4) return '#3b82f6';
    return '#22c55e';
  }

  function handleCityClick(city: any) {
    selectedCity = city;
    showCityModal = true;
  }

  function closeCityModal() {
    showCityModal = false;
    selectedCity = null;
  }
</script>

<div class="geospatial-map">
  <div class="map-container">
    <!-- Simplified US Map SVG -->
    <svg class="us-map" viewBox="0 0 100 60" preserveAspectRatio="xMidYMid meet">
      <!-- US Map Outline (simplified) -->
      <path
        d="M10,15 L85,15 L85,20 L90,20 L90,45 L85,45 L85,50 L10,50 L10,45 L5,45 L5,20 L10,20 Z"
        fill="#f1f5f9"
        stroke="#e2e8f0"
        stroke-width="0.5"
      />
      
      <!-- State boundaries (simplified) -->
      <g stroke="#e2e8f0" stroke-width="0.3" fill="none">
        <line x1="25" y1="15" x2="25" y2="50" />
        <line x1="40" y1="15" x2="40" y2="50" />
        <line x1="55" y1="15" x2="55" y2="50" />
        <line x1="70" y1="15" x2="70" y2="50" />
        <line x1="10" y1="30" x2="85" y2="30" />
        <line x1="10" y1="40" x2="85" y2="40" />
      </g>

      <!-- City Markers -->
      {#each cityData as city}
        <g class="city-marker" on:click={() => handleCityClick(city)}>
          <circle
            cx={city.coordinates.x}
            cy={city.coordinates.y}
            r={getMarkerSize(city.sessionVolume) / 4}
            fill={getMarkerColor(city.sessionVolume)}
            stroke="white"
            stroke-width="1"
            class="marker-circle"
          />
          <text
            x={city.coordinates.x}
            y={city.coordinates.y - getMarkerSize(city.sessionVolume) / 4 - 2}
            text-anchor="middle"
            class="city-label"
          >
            {city.name}
          </text>
          <text
            x={city.coordinates.x}
            y={city.coordinates.y + 1}
            text-anchor="middle"
            class="volume-label"
          >
            {city.sessionVolume}
          </text>
        </g>
      {/each}
    </svg>
  </div>

  <div class="map-legend">
    <h4 class="legend-title">Session Volume</h4>
    <div class="legend-items">
      <div class="legend-item">
        <div class="legend-marker" style="background-color: #22c55e; width: 12px; height: 12px;"></div>
        <span>Low (&lt; 400)</span>
      </div>
      <div class="legend-item">
        <div class="legend-marker" style="background-color: #3b82f6; width: 16px; height: 16px;"></div>
        <span>Medium (400-600)</span>
      </div>
      <div class="legend-item">
        <div class="legend-marker" style="background-color: #f59e0b; width: 20px; height: 20px;"></div>
        <span>High (600-900)</span>
      </div>
      <div class="legend-item">
        <div class="legend-marker" style="background-color: #dc2626; width: 24px; height: 24px;"></div>
        <span>Very High (900+)</span>
      </div>
    </div>
  </div>
</div>

<!-- City Details Modal -->
{#if showCityModal && selectedCity}
  <div class="modal-overlay" on:click={closeCityModal}>
    <div class="city-modal" on:click|stopPropagation>
      <div class="modal-header">
        <h3 class="modal-title">{selectedCity.name} - Mental Health Overview</h3>
        <button class="close-btn" on:click={closeCityModal}>
          <X size={20} />
        </button>
      </div>
      
      <div class="modal-content">
        <div class="city-stats">
          <div class="stat-item">
            <span class="stat-label">Total Sessions</span>
            <span class="stat-value">{selectedCity.sessionVolume.toLocaleString()}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">Active Therapists</span>
            <span class="stat-value">{Math.floor(selectedCity.sessionVolume / 50)}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">Avg Sessions/Day</span>
            <span class="stat-value">{Math.floor(selectedCity.sessionVolume / 30)}</span>
          </div>
        </div>

        <div class="illness-breakdown">
          <h4 class="breakdown-title">Top Mental Health Conditions</h4>
          <div class="illness-chart">
            {#each selectedCity.topIllnesses as illness}
              <div class="illness-bar">
                <div class="illness-info">
                  <span class="illness-name">{illness.name}</span>
                  <span class="illness-stats">{illness.count} sessions ({illness.percentage}%)</span>
                </div>
                <div class="bar-container">
                  <div 
                    class="bar-fill" 
                    style="width: {illness.percentage}%; background-color: {getMarkerColor(illness.count * 10)}"
                  ></div>
                </div>
              </div>
            {/each}
          </div>
        </div>
      </div>
    </div>
  </div>
{/if}

<style>
  .geospatial-map {
    height: 100%;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .map-container {
    flex: 1;
    background: #f8fafc;
    border-radius: 12px;
    padding: 2rem;
    border: 1px solid #e2e8f0;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .us-map {
    width: 100%;
    max-width: 800px;
    height: auto;
    cursor: default;
  }

  .city-marker {
    cursor: pointer;
    transition: all 0.2s;
  }

  .city-marker:hover .marker-circle {
    stroke-width: 2;
    filter: brightness(1.1);
  }

  .city-label {
    font-size: 2.5px;
    font-weight: 600;
    fill: #1e293b;
    pointer-events: none;
  }

  .volume-label {
    font-size: 2px;
    font-weight: 500;
    fill: white;
    pointer-events: none;
  }

  .map-legend {
    background: white;
    padding: 1rem;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
  }

  .legend-title {
    font-size: 0.875rem;
    font-weight: 600;
    color: #1e293b;
    margin: 0 0 0.75rem 0;
  }

  .legend-items {
    display: flex;
    gap: 1.5rem;
    flex-wrap: wrap;
  }

  .legend-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.75rem;
    color: #64748b;
  }

  .legend-marker {
    border-radius: 50%;
    border: 1px solid white;
  }

  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }

  .city-modal {
    background: white;
    border-radius: 16px;
    max-width: 600px;
    width: 90%;
    max-height: 80vh;
    overflow-y: auto;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-bottom: 1px solid #e2e8f0;
  }

  .modal-title {
    font-size: 1.25rem;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
  }

  .close-btn {
    background: none;
    border: none;
    color: #64748b;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 6px;
    transition: all 0.2s;
  }

  .close-btn:hover {
    background: #f1f5f9;
    color: #1e293b;
  }

  .modal-content {
    padding: 1.5rem;
  }

  .city-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .stat-item {
    text-align: center;
    padding: 1rem;
    background: #f8fafc;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
  }

  .stat-label {
    display: block;
    font-size: 0.75rem;
    color: #64748b;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 0.5rem;
  }

  .stat-value {
    display: block;
    font-size: 1.5rem;
    font-weight: 700;
    color: #2563eb;
  }

  .illness-breakdown {
    margin-top: 1.5rem;
  }

  .breakdown-title {
    font-size: 1.125rem;
    font-weight: 600;
    color: #1e293b;
    margin: 0 0 1rem 0;
  }

  .illness-chart {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .illness-bar {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .illness-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .illness-name {
    font-weight: 600;
    color: #1e293b;
    font-size: 0.875rem;
  }

  .illness-stats {
    font-size: 0.75rem;
    color: #64748b;
    font-weight: 500;
  }

  .bar-container {
    width: 100%;
    height: 8px;
    background: #f1f5f9;
    border-radius: 4px;
    overflow: hidden;
  }

  .bar-fill {
    height: 100%;
    border-radius: 4px;
    transition: width 0.8s ease-out;
  }

  @media (max-width: 768px) {
    .map-container {
      padding: 1rem;
    }

    .legend-items {
      justify-content: center;
    }

    .city-modal {
      width: 95%;
      margin: 1rem;
    }

    .city-stats {
      grid-template-columns: 1fr;
      gap: 0.75rem;
    }

    .modal-header {
      padding: 1rem;
    }

    .modal-content {
      padding: 1rem;
    }
  }
</style>