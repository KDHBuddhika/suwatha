<script lang="ts">
  export let title: string;
  export let value: string | number;
  export let subtitle: string = '';
  export let icon: any = null;
  export let trend: 'up' | 'down' | 'neutral' = 'neutral';
  export let trendValue: string = '';
</script>

<div class="dashboard-card">
  <div class="card-header">
    <div class="card-info">
      <h3 class="card-title">{title}</h3>
      {#if subtitle}
        <p class="card-subtitle">{subtitle}</p>
      {/if}
    </div>
    {#if icon}
      <div class="card-icon">
        <svelte:component this={icon} size={24} />
      </div>
    {/if}
  </div>
  
  <div class="card-content">
    <div class="card-value">{value}</div>
    {#if trendValue}
      <div class="card-trend" class:trend-up={trend === 'up'} class:trend-down={trend === 'down'}>
        {trendValue}
      </div>
    {/if}
  </div>
</div>

<style>
  .dashboard-card {
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e5e7eb;
    transition: box-shadow 0.2s, transform 0.2s;
  }

  .dashboard-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-1px);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;
  }

  .card-title {
    font-size: 0.875rem;
    font-weight: 600;
    color: #6b7280;
    margin: 0;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .card-subtitle {
    font-size: 0.75rem;
    color: #9ca3af;
    margin: 0.25rem 0 0 0;
  }

  .card-icon {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
  }

  .card-content {
    display: flex;
    align-items: baseline;
    gap: 0.75rem;
  }

  .card-value {
    font-size: 2rem;
    font-weight: bold;
    color: #111827;
    line-height: 1;
  }

  .card-trend {
    font-size: 0.875rem;
    font-weight: 600;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
  }

  .trend-up {
    background: #dcfce7;
    color: #166534;
  }

  .trend-down {
    background: #fee2e2;
    color: #991b1b;
  }

  @media (max-width: 768px) {
    .dashboard-card {
      padding: 1rem;
    }

    .card-value {
      font-size: 1.5rem;
    }
  }
</style>