<script lang="ts">
  import { onMount } from 'svelte';

  const specializations = [
    'Clinical Psychology',
    'CBT',
    'Trauma Therapy',
    'Family Therapy',
    'Addiction Counseling',
    'Child Psychology'
  ];

  const illnesses = [
    'Anxiety',
    'Depression',
    'PTSD',
    'Bipolar',
    'OCD',
    'Panic Disorder',
    'Social Anxiety',
    'Eating Disorder'
  ];

  // Generate heatmap data (specialization vs illness effectiveness)
  const heatmapData = specializations.map(spec => 
    illnesses.map(illness => {
      // Simulate realistic effectiveness ratings
      let rating = 3.0 + Math.random() * 2; // Base 3.0-5.0 range
      
      // Add logical correlations
      if (spec === 'CBT' && (illness === 'Anxiety' || illness === 'Depression')) rating = Math.min(5.0, rating + 0.5);
      if (spec === 'Trauma Therapy' && illness === 'PTSD') rating = Math.min(5.0, rating + 0.8);
      if (spec === 'Family Therapy' && illness === 'Bipolar') rating = Math.min(5.0, rating + 0.3);
      if (spec === 'Child Psychology' && illness === 'ADHD') rating = Math.min(5.0, rating + 0.6);
      
      return {
        specialization: spec,
        illness,
        rating: Math.round(rating * 10) / 10,
        sessionCount: Math.floor(Math.random() * 50) + 10
      };
    })
  ).flat();

  let hoveredCell: any = null;

  function getHeatmapColor(rating: number) {
    // Color scale from red (poor) to green (excellent)
    const intensity = (rating - 3.0) / 2.0; // Normalize to 0-1
    const red = Math.round(255 * (1 - intensity));
    const green = Math.round(255 * intensity);
    return `rgb(${red}, ${green}, 50)`;
  }

  function getTextColor(rating: number) {
    return rating > 4.0 ? 'white' : 'black';
  }

  function handleCellHover(data: any) {
    hoveredCell = data;
  }

  function handleCellLeave() {
    hoveredCell = null;
  }
</script>

<div class="specialization-heatmap">
  <div class="heatmap-container">
    <div class="heatmap-grid">
      <!-- Column headers (illnesses) -->
      <div class="grid-header">
        <div class="corner-cell"></div>
        {#each illnesses as illness}
          <div class="column-header">{illness}</div>
        {/each}
      </div>
      
      <!-- Data rows -->
      {#each specializations as specialization, specIndex}
        <div class="grid-row">
          <div class="row-header">{specialization}</div>
          {#each illnesses as illness, illnessIndex}
            {@const cellData = heatmapData.find(d => d.specialization === specialization && d.illness === illness)}
            <div
              class="heatmap-cell"
              style="background-color: {getHeatmapColor(cellData.rating)}; color: {getTextColor(cellData.rating)}"
              on:mouseenter={() => handleCellHover(cellData)}
              on:mouseleave={handleCellLeave}
            >
              <span class="cell-rating">{cellData.rating}</span>
              <span class="cell-count">({cellData.sessionCount})</span>
            </div>
          {/each}
        </div>
      {/each}
    </div>

    <!-- Tooltip -->
    {#if hoveredCell}
      <div class="tooltip">
        <div class="tooltip-header">
          <strong>{hoveredCell.specialization}</strong> × <strong>{hoveredCell.illness}</strong>
        </div>
        <div class="tooltip-content">
          <div class="tooltip-metric">
            <span class="tooltip-label">Effectiveness Rating:</span>
            <span class="tooltip-value">{hoveredCell.rating}/5.0</span>
          </div>
          <div class="tooltip-metric">
            <span class="tooltip-label">Total Sessions:</span>
            <span class="tooltip-value">{hoveredCell.sessionCount}</span>
          </div>
        </div>
      </div>
    {/if}
  </div>

  <!-- Legend -->
  <div class="heatmap-legend">
    <h4 class="legend-title">Effectiveness Rating</h4>
    <div class="legend-scale">
      <div class="legend-item">
        <div class="legend-color" style="background-color: {getHeatmapColor(3.0)}"></div>
        <span>3.0</span>
      </div>
      <div class="legend-gradient">
        <div class="gradient-bar"></div>
      </div>
      <div class="legend-item">
        <div class="legend-color" style="background-color: {getHeatmapColor(5.0)}"></div>
        <span>5.0</span>
      </div>
    </div>
    <p class="legend-note">Higher ratings indicate better patient outcomes for specific specialization-illness combinations</p>
  </div>
</div>

<style>
  .specialization-heatmap {
    height: 100%;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .heatmap-container {
    flex: 1;
    position: relative;
    overflow: auto;
  }

  .heatmap-grid {
    display: inline-block;
    min-width: 100%;
  }

  .grid-header {
    display: grid;
    grid-template-columns: 150px repeat(8, 100px);
    gap: 1px;
    margin-bottom: 1px;
  }

  .corner-cell {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
  }

  .column-header {
    background: #f8fafc;
    padding: 0.75rem 0.5rem;
    font-size: 0.75rem;
    font-weight: 600;
    color: #475569;
    text-align: center;
    border: 1px solid #e2e8f0;
    writing-mode: vertical-rl;
    text-orientation: mixed;
  }

  .grid-row {
    display: grid;
    grid-template-columns: 150px repeat(8, 100px);
    gap: 1px;
    margin-bottom: 1px;
  }

  .row-header {
    background: #f8fafc;
    padding: 0.75rem;
    font-size: 0.75rem;
    font-weight: 600;
    color: #475569;
    display: flex;
    align-items: center;
    border: 1px solid #e2e8f0;
  }

  .heatmap-cell {
    padding: 0.5rem;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid #e2e8f0;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 0.25rem;
    min-height: 60px;
  }

  .heatmap-cell:hover {
    transform: scale(1.05);
    z-index: 10;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }

  .cell-rating {
    font-weight: 700;
    font-size: 0.875rem;
  }

  .cell-count {
    font-size: 0.625rem;
    opacity: 0.8;
  }

  .tooltip {
    position: absolute;
    background: #1e293b;
    color: white;
    padding: 1rem;
    border-radius: 8px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
    z-index: 1000;
    pointer-events: none;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    min-width: 250px;
  }

  .tooltip-header {
    font-size: 0.875rem;
    margin-bottom: 0.75rem;
    padding-bottom: 0.5rem;
    border-bottom: 1px solid #475569;
  }

  .tooltip-content {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .tooltip-metric {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .tooltip-label {
    font-size: 0.75rem;
    color: #cbd5e1;
  }

  .tooltip-value {
    font-weight: 600;
    color: white;
  }

  .heatmap-legend {
    background: #f8fafc;
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

  .legend-scale {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 0.75rem;
  }

  .legend-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.75rem;
    font-weight: 600;
    color: #475569;
  }

  .legend-color {
    width: 16px;
    height: 16px;
    border-radius: 2px;
    border: 1px solid #e2e8f0;
  }

  .legend-gradient {
    flex: 1;
    height: 16px;
    position: relative;
  }

  .gradient-bar {
    width: 100%;
    height: 100%;
    background: linear-gradient(to right, rgb(255, 50, 50), rgb(255, 255, 50), rgb(50, 255, 50));
    border-radius: 2px;
    border: 1px solid #e2e8f0;
  }

  .legend-note {
    font-size: 0.75rem;
    color: #64748b;
    margin: 0;
    font-style: italic;
  }

  @media (max-width: 768px) {
    .grid-header,
    .grid-row {
      grid-template-columns: 120px repeat(8, 80px);
    }

    .column-header,
    .row-header {
      font-size: 0.625rem;
      padding: 0.5rem 0.25rem;
    }

    .heatmap-cell {
      min-height: 50px;
      padding: 0.25rem;
    }

    .cell-rating {
      font-size: 0.75rem;
    }

    .cell-count {
      font-size: 0.5rem;
    }
  }
</style>