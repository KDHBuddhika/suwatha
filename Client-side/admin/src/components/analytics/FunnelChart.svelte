<script lang="ts">
  import { onMount } from 'svelte';
  import { Chart, registerables } from 'chart.js';
  import ChartDataLabels from 'chartjs-plugin-datalabels';

  // Register Chart.js components and the datalabels plugin
  Chart.register(...registerables, ChartDataLabels);

  let canvas: HTMLCanvasElement;
  let chart: Chart;

  const funnelData = [
    { stage: 'Initial Contact', value: 1000, color: '#3b82f6' },
    { stage: 'Profile Created', value: 850, color: '#2563eb' },
    { stage: 'Therapist Matched', value: 720, color: '#1d4ed8' },
    { stage: 'First Session Scheduled', value: 650, color: '#1e40af' },
    { stage: 'Session Completed', value: 580, color: '#1e3a8a' }
  ];

  // Calculate metrics dynamically
  const initialValue = funnelData[0]?.value || 0;
  const finalValue = funnelData[funnelData.length - 1]?.value || 0;
  const conversionRate = initialValue > 0 ? (finalValue / initialValue * 100).toFixed(0) : 0;
  const dropOffRate = 100 - Number(conversionRate);

  onMount(() => {
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: funnelData.map(item => item.stage),
        datasets: [{
          data: funnelData.map(item => item.value),
          backgroundColor: funnelData.map(item => item.color),
          borderRadius: 4,
          borderSkipped: false,
          barPercentage: 0.7, // Makes bars a bit slimmer
          categoryPercentage: 0.8
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          // Configuration for chartjs-plugin-datalabels
          datalabels: {
            anchor: 'end',
            align: 'right',
            offset: 8,
            color: '#1e293b',
            font: {
              size: 12,
              weight: '600',
              family: 'Inter, sans-serif'
            },
            formatter: (value, context) => {
              const prevValue = context.chart.data.datasets[0].data[context.dataIndex - 1] as number | undefined;
              if (prevValue === undefined) {
                return `${value.toLocaleString()}`; // First item
              }
              const conversion = ((value / prevValue) * 100).toFixed(0);
              return `${value.toLocaleString()} (${conversion}%)`;
            }
          },
          tooltip: {
            enabled: false // Disable tooltip as datalabels are now used
          }
        },
        scales: {
          x: {
            beginAtZero: true,
            grid: {
              color: '#f1f5f9'
            },
            ticks: {
              display: false // Hide ticks as labels provide the data
            },
            border: {
              display: false // Hide axis line
            }
          },
          y: {
            grid: {
              display: false
            },
            border: {
              display: false
            },
            ticks: {
               font: {
                size: 12,
                weight: '500',
                family: 'Inter, sans-serif'
              },
              color: '#475569'
            }
          }
        },
        animation: {
          duration: 1500,
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

<div class="funnel-chart-container">
  <div class="chart-wrapper">
    <canvas bind:this={canvas}></canvas>
  </div>
  
  <div class="funnel-metrics">
    <div class="metric-card">
      <div class="metric-value">{conversionRate}%</div>
      <div class="metric-label">Overall Conversion</div>
    </div>
    <div class="metric-card">
      <div class="metric-value">{dropOffRate}%</div>
      <div class="metric-label">Total Drop-off</div>
    </div>
    <div class="metric-card">
      <div class="metric-value">3.2 days</div>
      <div class="metric-label">Avg. Time to Convert</div>
    </div>
  </div>
</div>

<style>
  .funnel-chart-container {
    display: flex;
    flex-direction: column;
    width: 100%;
    /* Let the component's height be determined by its content */
    height: auto; 
  }

  .chart-wrapper {
    position: relative;
    /* Use aspect-ratio for a responsive chart height */
    aspect-ratio: 16 / 10;
    min-height: 320px;
    width: 100%;
  }

  .funnel-metrics {
    display: grid;
    /* Responsive grid that wraps on smaller screens */
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 1.25rem;
    padding-top: 1.5rem;
    margin-top: 1rem;
    border-top: 1px solid #e2e8f0;
  }

  .metric-card {
    text-align: center;
    padding: 1rem;
    background: #f8fafc;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
  }

  .metric-value {
    font-size: 1.75rem;
    font-weight: 700;
    color: #1e40af;
    margin-bottom: 0.25rem;
  }

  .metric-label {
    font-size: 0.8rem;
    color: #64748b;
    font-weight: 500;
  }

  @media (max-width: 768px) {
    .chart-wrapper {
      min-height: 280px;
    }
    
    .metric-value {
      font-size: 1.5rem;
    }
  }
</style>