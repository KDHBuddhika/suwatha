<script lang="ts">
  import { onMount } from 'svelte';
  import { Chart, registerables } from 'chart.js';

  Chart.register(...registerables);

  let canvas: HTMLCanvasElement;
  let chart: Chart;

  // Simulate cohort data for different therapist groups
  const cohortData = [
    {
      label: 'Q1 2024 Cohort (15 therapists)',
      data: [0, 2.1, 3.8, 5.2, 6.5, 7.3, 7.8, 8.1, 8.3, 8.4, 8.5, 8.5],
      borderColor: '#3b82f6',
      backgroundColor: '#3b82f620'
    },
    {
      label: 'Q2 2024 Cohort (12 therapists)',
      data: [0, 1.8, 3.2, 4.8, 6.1, 7.0, 7.6, 7.9, 8.2, 8.4, 8.5, 8.6],
      borderColor: '#10b981',
      backgroundColor: '#10b98120'
    },
    {
      label: 'Q3 2024 Cohort (18 therapists)',
      data: [0, 2.3, 4.1, 5.5, 6.8, 7.5, 8.0, 8.3, 8.5, 8.6, 8.7, 8.8],
      borderColor: '#f59e0b',
      backgroundColor: '#f59e0b20'
    },
    {
      label: 'Q4 2024 Cohort (10 therapists)',
      data: [0, 1.5, 2.8, 4.2, 5.5, 6.3, 6.9, 7.2, 7.5, 7.7, 7.9, 8.0],
      borderColor: '#ef4444',
      backgroundColor: '#ef444420'
    },
    {
      label: 'Q1 2025 Cohort (8 therapists)',
      data: [0, 2.0, 3.5, 4.9, 6.2, 7.1, 7.7, 8.0, null, null, null, null],
      borderColor: '#8b5cf6',
      backgroundColor: '#8b5cf620'
    }
  ];

  onMount(() => {
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Month 0', 'Month 1', 'Month 2', 'Month 3', 'Month 4', 'Month 5',
                'Month 6', 'Month 7', 'Month 8', 'Month 9', 'Month 10', 'Month 11'],
        datasets: cohortData.map(cohort => ({
          label: cohort.label,
          data: cohort.data,
          borderColor: cohort.borderColor,
          backgroundColor: cohort.backgroundColor,
          borderWidth: 2.5, // Slightly refined for better visuals
          fill: true,
          tension: 0.4,
          pointRadius: 4, // Adjusted for a cleaner look
          pointHoverRadius: 7,
          pointBackgroundColor: cohort.borderColor,
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2
        }))
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          intersect: false,
          mode: 'index'
        },
        plugins: {
          legend: {
            position: 'bottom',
            align: 'start', // Aligns legend items to the left
            labels: {
              padding: 20,
              usePointStyle: true,
              boxWidth: 8, // Controls the size of the point style
              font: {
                size: 12,
                family: 'Inter, sans-serif'
              }
            }
          },
          tooltip: {
            backgroundColor: '#1e293b', // Darker tooltip
            titleFont: { size: 14, weight: 'bold' },
            bodyFont: { size: 12 },
            padding: 12,
            cornerRadius: 8,
            callbacks: {
              title: function(context) {
                return context[0].label;
              },
              label: function(context) {
                const value = context.parsed.y;
                // Simplified dataset label for tooltip
                const cohortLabel = context.dataset.label.split(' (')[0];
                return value !== null ? `${cohortLabel}: ${value} avg sessions/week` : 'No data';
              }
            }
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Months Since Joining',
              font: {
                size: 12,
                family: 'Inter, sans-serif',
                weight: '600'
              },
              color: '#64748b'
            },
            grid: {
              display: false // Cleaner look without vertical grid lines
            },
            ticks: {
              color: '#64748b'
            }
          },
          y: {
            title: {
              display: true,
              text: 'Average Sessions per Week',
              font: {
                size: 12,
                family: 'Inter, sans-serif',
                weight: '600'
              },
               color: '#64748b'
            },
            beginAtZero: true,
            max: 10,
            grid: {
              color: '#e2e8f0' // Softer grid line color
            },
             ticks: {
              color: '#64748b'
            }
          }
        },
        animation: {
          duration: 1500, // Slightly faster animation
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

<div class="cohort-chart-container">
  <div class="chart-wrapper">
    <canvas bind:this={canvas}></canvas>
  </div>
  
  <div class="cohort-insights">
    <div class="insight-cards">
      <div class="insight-card">
        <div class="insight-metric">6.2</div>
        <div class="insight-label">Avg Ramp-up Time</div>
        <div class="insight-unit">months to 8+ sessions/week</div>
      </div>
      <div class="insight-card">
        <div class="insight-metric">85%</div>
        <div class="insight-label">Retention Rate</div>
        <div class="insight-unit">after 12 months</div>
      </div>
      <div class="insight-card">
        <div class="insight-metric">23%</div>
        <div class="insight-label">Performance Variance</div>
        <div class="insight-unit">between cohorts</div>
      </div>
    </div>
  </div>
</div>

<style>
  /* Use a more specific container name to avoid conflicts */
  .cohort-chart-container {
    display: flex;
    flex-direction: column;
    width: 100%; /* Ensure it takes full width of its parent */
    height: auto; /* Let content determine the height */
  }

  .chart-wrapper {
    position: relative;
    /* Use aspect-ratio for responsive height instead of a fixed height */
    aspect-ratio: 16 / 9;
    min-height: 350px; /* Set a minimum height for smaller screens */
    width: 100%;
  }

  .cohort-insights {
    padding-top: 1.5rem; /* Increased padding for better separation */
    margin-top: 1.5rem; /* Add margin to separate from the chart */
    border-top: 1px solid #e2e8f0;
  }

  .insight-cards {
    display: grid;
    /* Use auto-fit for better responsiveness on different screen sizes */
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 1.5rem; /* Increased gap */
  }

  .insight-card {
    text-align: center;
    padding: 1.25rem;
    background: #f8fafc;
    border-radius: 12px; /* Larger radius */
    border: 1px solid #e2e8f0;
    transition: transform 0.2s ease-out, box-shadow 0.2s ease-out;
  }

  .insight-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  }

  .insight-metric {
    font-size: 2rem; /* Larger metric font */
    font-weight: 700;
    color: #2563eb;
    margin-bottom: 0.25rem;
  }

  .insight-label {
    font-size: 0.875rem;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 0.25rem;
  }

  .insight-unit {
    font-size: 0.75rem;
    color: #64748b;
  }

  @media (max-width: 768px) {
    .chart-wrapper {
      /* Adjust min-height for smaller screens */
      min-height: 300px; 
    }

    .insight-cards {
      gap: 1rem;
    }

    .insight-metric {
      font-size: 1.75rem;
    }
  }
</style>