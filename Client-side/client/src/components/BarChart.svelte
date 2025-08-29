<script lang="ts">
  import { onMount } from 'svelte';
  import { Chart, registerables } from 'chart.js';

  Chart.register(...registerables);

  export let title: string = '';
  export let data: { label: string; value: number }[] = [];
  export let xAxisLabel: string = '';
  export let yAxisLabel: string = '';
  export let color: string = '#2563eb';

  let canvas: HTMLCanvasElement;
  let chart: Chart;

  onMount(() => {
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.map(item => item.label),
        datasets: [{
          data: data.map(item => item.value),
          backgroundColor: color + '80',
          borderColor: color,
          borderWidth: 1,
          borderRadius: 4,
          hoverBackgroundColor: color + '60',
          hoverBorderColor: color
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              title: function(context) {
                return context[0].label;
              },
              label: function(context) {
                return `${yAxisLabel}: ${context.parsed.y}`;
              }
            }
          }
        },
        scales: {
          x: {
            title: {
              display: !!xAxisLabel,
              text: xAxisLabel,
              font: {
                size: 12,
                family: 'Inter'
              }
            },
            grid: {
              display: false
            }
          },
          y: {
            title: {
              display: !!yAxisLabel,
              text: yAxisLabel,
              font: {
                size: 12,
                family: 'Inter'
              }
            },
            beginAtZero: true,
            grid: {
              color: '#f3f4f6'
            }
          }
        },
        animation: {
          duration: 1000,
          easing: 'easeOutQuart'
        }
      }
    });

    return () => {
      if (chart) {
        chart.destroy();
      }
    };
  });
</script>

<div class="chart-container">
  {#if title}
    <h3 class="chart-title">{title}</h3>
  {/if}
  <div class="chart-wrapper">
    <canvas bind:this={canvas}></canvas>
  </div>
</div>

<style>
  .chart-container {
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e5e7eb;
    height: 100%;
  }

  .chart-title {
    font-size: 1.125rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 1rem 0;
    text-align: center;
  }

  .chart-wrapper {
    position: relative;
    height: 300px;
  }

  @media (max-width: 768px) {
    .chart-wrapper {
      height: 250px;
    }
  }
</style>