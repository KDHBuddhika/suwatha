<script lang="ts">
  export let type: 'funnel' | 'gauge' | 'table' | 'heatmap' | 'line' | 'map' = 'table';
</script>

<div class="loading-skeleton {type}">
  {#if type === 'funnel'}
    <div class="funnel-skeleton">
      {#each Array(6) as _, i}
        <div class="skeleton-bar" style="width: {100 - i * 12}%; height: 40px;"></div>
      {/each}
    </div>
  {:else if type === 'gauge'}
    <div class="gauge-skeleton">
      <div class="skeleton-circle"></div>
      <div class="skeleton-metrics">
        {#each Array(3) as _}
          <div class="skeleton-metric">
            <div class="skeleton-line short"></div>
            <div class="skeleton-line medium"></div>
          </div>
        {/each}
      </div>
    </div>
  {:else if type === 'table'}
    <div class="table-skeleton">
      <div class="skeleton-header">
        {#each Array(5) as _}
          <div class="skeleton-line medium"></div>
        {/each}
      </div>
      {#each Array(6) as _}
        <div class="skeleton-row">
          {#each Array(5) as _}
            <div class="skeleton-line long"></div>
          {/each}
        </div>
      {/each}
    </div>
  {:else if type === 'heatmap'}
    <div class="heatmap-skeleton">
      {#each Array(6) as _}
        <div class="skeleton-heatmap-row">
          {#each Array(8) as _}
            <div class="skeleton-heatmap-cell"></div>
          {/each}
        </div>
      {/each}
    </div>
  {:else if type === 'line'}
    <div class="line-skeleton">
      <div class="skeleton-chart-area">
        <div class="skeleton-line-chart"></div>
      </div>
      <div class="skeleton-legend">
        {#each Array(4) as _}
          <div class="skeleton-legend-item">
            <div class="skeleton-color-box"></div>
            <div class="skeleton-line medium"></div>
          </div>
        {/each}
      </div>
    </div>
  {:else if type === 'map'}
    <div class="map-skeleton">
      <div class="skeleton-map-area">
        <div class="skeleton-map-outline"></div>
        {#each Array(6) as _, i}
          <div 
            class="skeleton-map-marker" 
            style="left: {20 + i * 12}%; top: {30 + (i % 2) * 20}%;"
          ></div>
        {/each}
      </div>
      <div class="skeleton-map-legend">
        {#each Array(4) as _}
          <div class="skeleton-legend-item">
            <div class="skeleton-color-box"></div>
            <div class="skeleton-line short"></div>
          </div>
        {/each}
      </div>
    </div>
  {/if}
</div>

<style>
  .loading-skeleton {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 2rem;
  }

  .skeleton-line,
  .skeleton-bar,
  .skeleton-circle,
  .skeleton-heatmap-cell,
  .skeleton-color-box,
  .skeleton-map-marker,
  .skeleton-map-outline {
    background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
    background-size: 200% 100%;
    animation: shimmer 2s infinite;
    border-radius: 4px;
  }

  @keyframes shimmer {
    0% {
      background-position: -200% 0;
    }
    100% {
      background-position: 200% 0;
    }
  }

  .skeleton-line.short {
    width: 60px;
    height: 16px;
  }

  .skeleton-line.medium {
    width: 120px;
    height: 16px;
  }

  .skeleton-line.long {
    width: 180px;
    height: 16px;
  }

  /* Funnel Skeleton */
  .funnel-skeleton {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    width: 100%;
    max-width: 400px;
  }

  .skeleton-bar {
    border-radius: 6px;
  }

  /* Gauge Skeleton */
  .gauge-skeleton {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2rem;
  }

  .skeleton-circle {
    width: 200px;
    height: 200px;
    border-radius: 50%;
  }

  .skeleton-metrics {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    width: 200px;
  }

  .skeleton-metric {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    align-items: center;
  }

  /* Table Skeleton */
  .table-skeleton {
    width: 100%;
  }

  .skeleton-header {
    display: flex;
    gap: 1rem;
    margin-bottom: 1rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid #e2e8f0;
  }

  .skeleton-row {
    display: flex;
    gap: 1rem;
    margin-bottom: 0.75rem;
  }

  /* Heatmap Skeleton */
  .heatmap-skeleton {
    display: flex;
    flex-direction: column;
    gap: 1px;
    width: 100%;
  }

  .skeleton-heatmap-row {
    display: flex;
    gap: 1px;
  }

  .skeleton-heatmap-cell {
    width: 60px;
    height: 40px;
    border-radius: 2px;
  }

  /* Line Chart Skeleton */
  .line-skeleton {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .skeleton-chart-area {
    height: 300px;
    position: relative;
  }

  .skeleton-line-chart {
    width: 100%;
    height: 100%;
    border-radius: 8px;
  }

  .skeleton-legend {
    display: flex;
    gap: 1rem;
    justify-content: center;
    flex-wrap: wrap;
  }

  .skeleton-legend-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .skeleton-color-box {
    width: 16px;
    height: 16px;
    border-radius: 2px;
  }

  /* Map Skeleton */
  .map-skeleton {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .skeleton-map-area {
    height: 400px;
    position: relative;
    border-radius: 12px;
    overflow: hidden;
  }

  .skeleton-map-outline {
    width: 100%;
    height: 100%;
    border-radius: 12px;
  }

  .skeleton-map-marker {
    position: absolute;
    width: 20px;
    height: 20px;
    border-radius: 50%;
  }

  .skeleton-map-legend {
    display: flex;
    gap: 1rem;
    justify-content: center;
    flex-wrap: wrap;
  }

  @media (max-width: 768px) {
    .loading-skeleton {
      padding: 1rem;
    }

    .skeleton-circle {
      width: 150px;
      height: 150px;
    }

    .skeleton-metrics {
      width: 150px;
    }

    .skeleton-chart-area {
      height: 250px;
    }

    .skeleton-map-area {
      height: 300px;
    }
  }
</style>