<script lang="ts">
  export let title: string;
  export let value: string | number;
  export let icon: any = null;
  export let subtitle: string = '';
</script>

<div class="kpi-card">
  <div class="kpi-header">
    <div class="kpi-info">
      <h3 class="kpi-title">{title}</h3>
      {#if subtitle}
        <p class="kpi-subtitle">{subtitle}</p>
      {/if}
    </div>
    {#if icon}
      <div class="kpi-icon">
        <svelte:component this={icon} size={24} />
      </div>
    {/if}
  </div>
  
  <div class="kpi-value">{value}</div>
</div>

<style>
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

  .kpi-subtitle {
    font-size: 0.75rem;
    color: #9ca3af;
    margin: 0.25rem 0 0 0;
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

  @media (max-width: 768px) {
    .kpi-card {
      padding: 1rem;
    }

    .kpi-value {
      font-size: 2rem;
    }
  }
</style>