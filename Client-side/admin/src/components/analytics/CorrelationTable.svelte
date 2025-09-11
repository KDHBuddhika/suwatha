<script lang="ts">
  import { ArrowUp, ArrowDown, Minus } from 'lucide-svelte';

  const correlationData = [
    {
      riskLevel: 'High Risk',
      sessionCount: 12.3,
      avgDuration: 68,
      completionRate: 85,
      satisfactionScore: 4.2,
      correlation: 0.78,
      trend: 'up'
    },
    {
      riskLevel: 'Medium Risk',
      sessionCount: 8.7,
      avgDuration: 55,
      completionRate: 92,
      satisfactionScore: 4.5,
      correlation: 0.65,
      trend: 'up'
    },
    {
      riskLevel: 'Low Risk',
      sessionCount: 5.2,
      avgDuration: 45,
      completionRate: 96,
      satisfactionScore: 4.7,
      correlation: 0.42,
      trend: 'neutral'
    },
    {
      riskLevel: 'Crisis',
      sessionCount: 18.5,
      avgDuration: 75,
      completionRate: 78,
      satisfactionScore: 3.9,
      correlation: 0.89,
      trend: 'down'
    }
  ];

  function getRiskLevelColor(riskLevel: string) {
    switch (riskLevel) {
      case 'High Risk': return 'bg-red-100 text-red-800';
      case 'Medium Risk': return 'bg-yellow-100 text-yellow-800';
      case 'Low Risk': return 'bg-green-100 text-green-800';
      case 'Crisis': return 'bg-purple-100 text-purple-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }

  function getCorrelationColor(correlation: number) {
    if (correlation >= 0.7) return 'text-red-600 font-bold';
    if (correlation >= 0.5) return 'text-yellow-600 font-semibold';
    return 'text-green-600 font-medium';
  }

  function getTrendIcon(trend: string) {
    switch (trend) {
      case 'up': return ArrowUp;
      case 'down': return ArrowDown;
      default: return Minus;
    }
  }

  function getTrendColor(trend: string) {
    switch (trend) {
      case 'up': return 'text-green-600';
      case 'down': return 'text-red-600';
      default: return 'text-gray-600';
    }
  }
</script>

<div class="correlation-table">
  <div class="table-wrapper">
    <table class="data-table">
      <thead>
        <tr>
          <th>Risk Level</th>
          <th>Avg Sessions</th>
          <th>Avg Duration (min)</th>
          <th>Completion Rate</th>
          <th>Satisfaction</th>
          <th>Correlation</th>
          <th>Trend</th>
        </tr>
      </thead>
      <tbody>
        {#each correlationData as row}
          <tr>
            <td>
              <span class="risk-badge {getRiskLevelColor(row.riskLevel)}">
                {row.riskLevel}
              </span>
            </td>
            <td class="metric-cell">{row.sessionCount}</td>
            <td class="metric-cell">{row.avgDuration}</td>
            <td class="metric-cell">{row.completionRate}%</td>
            <td class="metric-cell">
              <div class="satisfaction-score">
                <span class="score-value">{row.satisfactionScore}</span>
                <div class="stars">
                  {#each Array(5) as _, i}
                    <span class="star" class:filled={i < Math.floor(row.satisfactionScore)}>★</span>
                  {/each}
                </div>
              </div>
            </td>
            <td class="correlation-cell">
              <span class="correlation-value {getCorrelationColor(row.correlation)}">
                {row.correlation.toFixed(2)}
              </span>
            </td>
            <td class="trend-cell">
              <div class="trend-indicator {getTrendColor(row.trend)}">
                <svelte:component this={getTrendIcon(row.trend)} size={16} />
              </div>
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  </div>

  <div class="insights-panel">
    <h4 class="insights-title">Key Insights</h4>
    <div class="insights-list">
      <div class="insight-item">
        <div class="insight-indicator high-correlation"></div>
        <span>Crisis patients show highest correlation (0.89) between session frequency and outcomes</span>
      </div>
      <div class="insight-item">
        <div class="insight-indicator positive-trend"></div>
        <span>Medium-risk patients demonstrate improving completion rates</span>
      </div>
      <div class="insight-item">
        <div class="insight-indicator optimal-range"></div>
        <span>Low-risk patients maintain highest satisfaction scores (4.7/5)</span>
      </div>
    </div>
  </div>
</div>

<style>
  .correlation-table {
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .table-wrapper {
    flex: 1;
    overflow-x: auto;
    margin-bottom: 1.5rem;
  }

  .data-table {
    width: 100%;
    border-collapse: collapse;
  }

  .data-table th {
    background: #f8fafc;
    padding: 1rem 0.75rem;
    text-align: left;
    font-weight: 600;
    color: #475569;
    border-bottom: 2px solid #e2e8f0;
    font-size: 0.875rem;
    white-space: nowrap;
  }

  .data-table td {
    padding: 1rem 0.75rem;
    border-bottom: 1px solid #f1f5f9;
    font-size: 0.875rem;
    vertical-align: middle;
  }

  .data-table tr:hover {
    background: #f8fafc;
  }

  .risk-badge {
    display: inline-block;
    padding: 0.375rem 0.75rem;
    border-radius: 16px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .metric-cell {
    font-weight: 600;
    color: #1e293b;
  }

  .satisfaction-score {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .score-value {
    font-weight: 700;
    color: #1e293b;
  }

  .stars {
    display: flex;
    gap: 0.125rem;
  }

  .star {
    color: #e2e8f0;
    font-size: 0.875rem;
  }

  .star.filled {
    color: #fbbf24;
  }

  .correlation-cell {
    text-align: center;
  }

  .correlation-value {
    font-size: 1rem;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    background: #f8fafc;
  }

  .trend-cell {
    text-align: center;
  }

  .trend-indicator {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #f8fafc;
  }

  .insights-panel {
    padding: 1rem;
    background: #f8fafc;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
  }

  .insights-title {
    font-size: 1rem;
    font-weight: 600;
    color: #1e293b;
    margin: 0 0 1rem 0;
  }

  .insights-list {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .insight-item {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-size: 0.875rem;
    color: #475569;
  }

  .insight-indicator {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  .high-correlation {
    background: #dc2626;
  }

  .positive-trend {
    background: #22c55e;
  }

  .optimal-range {
    background: #3b82f6;
  }

  @media (max-width: 768px) {
    .data-table th,
    .data-table td {
      padding: 0.75rem 0.5rem;
      font-size: 0.75rem;
    }

    .satisfaction-score {
      flex-direction: column;
      gap: 0.25rem;
    }
  }
</style>