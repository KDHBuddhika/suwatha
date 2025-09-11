<script lang="ts">
  import { AlertTriangle, TrendingDown, Clock, Star } from 'lucide-svelte';

  const churnRiskData = [
    {
      id: 'T-001',
      name: 'Dr. Sarah Johnson',
      riskLevel: 'high',
      lastActive: '2025-01-20',
      sessionDecline: -35,
      avgRating: 4.1,
      totalSessions: 145,
      warningFlags: ['Declining sessions', 'Low engagement']
    },
    {
      id: 'T-002',
      name: 'Dr. Michael Chen',
      riskLevel: 'medium',
      lastActive: '2025-01-25',
      sessionDecline: -15,
      avgRating: 4.6,
      totalSessions: 203,
      warningFlags: ['Reduced availability']
    },
    {
      id: 'T-003',
      name: 'Dr. Emily Rodriguez',
      riskLevel: 'low',
      lastActive: '2025-01-27',
      sessionDecline: 5,
      avgRating: 4.8,
      totalSessions: 287,
      warningFlags: []
    },
    {
      id: 'T-004',
      name: 'Dr. James Wilson',
      riskLevel: 'high',
      lastActive: '2025-01-18',
      sessionDecline: -42,
      avgRating: 3.9,
      totalSessions: 98,
      warningFlags: ['Declining sessions', 'Low ratings', 'Inactive']
    },
    {
      id: 'T-005',
      name: 'Dr. Lisa Anderson',
      riskLevel: 'low',
      lastActive: '2025-01-27',
      sessionDecline: 12,
      avgRating: 4.7,
      totalSessions: 234,
      warningFlags: []
    },
    {
      id: 'T-006',
      name: 'Dr. Robert Taylor',
      riskLevel: 'medium',
      lastActive: '2025-01-24',
      sessionDecline: -8,
      avgRating: 4.3,
      totalSessions: 156,
      warningFlags: ['Reduced availability']
    },
    {
      id: 'T-007',
      name: 'Dr. Maria Garcia',
      riskLevel: 'high',
      lastActive: '2025-01-19',
      sessionDecline: -28,
      avgRating: 4.0,
      totalSessions: 167,
      warningFlags: ['Declining sessions', 'Irregular schedule']
    },
    {
      id: 'T-008',
      name: 'Dr. David Kim',
      riskLevel: 'low',
      lastActive: '2025-01-27',
      sessionDecline: 8,
      avgRating: 4.9,
      totalSessions: 312,
      warningFlags: []
    }
  ];

  function getRiskBadgeClass(riskLevel: string) {
    switch (riskLevel) {
      case 'high': return 'risk-high';
      case 'medium': return 'risk-medium';
      case 'low': return 'risk-low';
      default: return 'risk-low';
    }
  }

  function getDeclineColor(decline: number) {
    if (decline < -20) return 'text-red-600';
    if (decline < 0) return 'text-yellow-600';
    return 'text-green-600';
  }

  function getDaysAgo(dateString: string) {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  }

  function renderStars(rating: number) {
    return '★'.repeat(Math.floor(rating)) + '☆'.repeat(5 - Math.floor(rating));
  }
</script>

<div class="churn-risk-table">
  <div class="table-header">
    <div class="risk-summary">
      <div class="risk-stat high">
        <AlertTriangle size={16} />
        <span class="stat-value">3</span>
        <span class="stat-label">High Risk</span>
      </div>
      <div class="risk-stat medium">
        <Clock size={16} />
        <span class="stat-value">2</span>
        <span class="stat-label">Medium Risk</span>
      </div>
      <div class="risk-stat low">
        <Star size={16} />
        <span class="stat-value">3</span>
        <span class="stat-label">Low Risk</span>
      </div>
    </div>
  </div>

  <div class="table-wrapper">
    <table class="risk-table">
      <thead>
        <tr>
          <th>Therapist</th>
          <th>Risk Level</th>
          <th>Last Active</th>
          <th>Session Change</th>
          <th>Avg Rating</th>
          <th>Total Sessions</th>
          <th>Warning Flags</th>
        </tr>
      </thead>
      <tbody>
        {#each churnRiskData as therapist}
          <tr class="risk-row {getRiskBadgeClass(therapist.riskLevel)}">
            <td class="therapist-info">
              <div class="therapist-name">{therapist.name}</div>
              <div class="therapist-id">{therapist.id}</div>
            </td>
            <td>
              <span class="risk-badge {getRiskBadgeClass(therapist.riskLevel)}">
                <span class="risk-dot"></span>
                {therapist.riskLevel} risk
              </span>
            </td>
            <td class="last-active">
              <div class="date-info">
                <span class="date">{new Date(therapist.lastActive).toLocaleDateString()}</span>
                <span class="days-ago">{getDaysAgo(therapist.lastActive)} days ago</span>
              </div>
            </td>
            <td class="session-change">
              <div class="change-indicator {getDeclineColor(therapist.sessionDecline)}">
                {#if therapist.sessionDecline > 0}
                  <TrendingDown class="trend-icon up" size={16} style="transform: rotate(180deg)" />
                  +{therapist.sessionDecline}%
                {:else}
                  <TrendingDown class="trend-icon down" size={16} />
                  {therapist.sessionDecline}%
                {/if}
              </div>
            </td>
            <td class="rating">
              <div class="rating-display">
                <span class="stars">{renderStars(therapist.avgRating)}</span>
                <span class="rating-value">({therapist.avgRating})</span>
              </div>
            </td>
            <td class="session-count">{therapist.totalSessions}</td>
            <td class="warning-flags">
              {#if therapist.warningFlags.length > 0}
                <div class="flags-list">
                  {#each therapist.warningFlags as flag}
                    <span class="warning-flag">{flag}</span>
                  {/each}
                </div>
              {:else}
                <span class="no-flags">No issues</span>
              {/if}
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  </div>

  <div class="action-panel">
    <div class="action-suggestions">
      <h4 class="suggestions-title">Recommended Actions</h4>
      <div class="suggestions-list">
        <div class="suggestion-item high-priority">
          <AlertTriangle size={16} />
          <span>Schedule check-in calls with 3 high-risk therapists</span>
        </div>
        <div class="suggestion-item medium-priority">
          <Clock size={16} />
          <span>Review workload distribution for medium-risk therapists</span>
        </div>
        <div class="suggestion-item low-priority">
          <Star size={16} />
          <span>Recognize top performers in team meeting</span>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
  .churn-risk-table {
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .table-header {
    margin-bottom: 1.5rem;
  }

  .risk-summary {
    display: flex;
    gap: 1rem;
    justify-content: center;
  }

  .risk-stat {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    border-radius: 8px;
    font-size: 0.875rem;
    font-weight: 600;
  }

  .risk-stat.high {
    background: #fee2e2;
    color: #991b1b;
  }

  .risk-stat.medium {
    background: #fef3c7;
    color: #d97706;
  }

  .risk-stat.low {
    background: #dcfce7;
    color: #166534;
  }

  .stat-value {
    font-size: 1.25rem;
    font-weight: 700;
  }

  .table-wrapper {
    flex: 1;
    overflow-x: auto;
    margin-bottom: 1.5rem;
  }

  .risk-table {
    width: 100%;
    border-collapse: collapse;
    min-width: 900px;
  }

  .risk-table th {
    background: #f8fafc;
    padding: 1rem 0.75rem;
    text-align: left;
    font-weight: 600;
    color: #475569;
    border-bottom: 2px solid #e2e8f0;
    font-size: 0.875rem;
    white-space: nowrap;
  }

  .risk-table td {
    padding: 1rem 0.75rem;
    border-bottom: 1px solid #f1f5f9;
    font-size: 0.875rem;
    vertical-align: middle;
  }

  .risk-row:hover {
    background: #f8fafc;
  }

  .risk-row.risk-high {
    border-left: 4px solid #dc2626;
  }

  .risk-row.risk-medium {
    border-left: 4px solid #f59e0b;
  }

  .risk-row.risk-low {
    border-left: 4px solid #22c55e;
  }

  .therapist-info {
    min-width: 180px;
  }

  .therapist-name {
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 0.25rem;
  }

  .therapist-id {
    font-size: 0.75rem;
    color: #64748b;
  }

  .risk-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.375rem 0.75rem;
    border-radius: 16px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: capitalize;
  }

  .risk-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .risk-badge.risk-high {
    background: #fee2e2;
    color: #991b1b;
  }

  .risk-badge.risk-high .risk-dot {
    background: #dc2626;
  }

  .risk-badge.risk-medium {
    background: #fef3c7;
    color: #d97706;
  }

  .risk-badge.risk-medium .risk-dot {
    background: #f59e0b;
  }

  .risk-badge.risk-low {
    background: #dcfce7;
    color: #166534;
  }

  .risk-badge.risk-low .risk-dot {
    background: #22c55e;
  }

  .last-active {
    min-width: 120px;
  }

  .date-info {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }

  .date {
    font-weight: 600;
    color: #1e293b;
  }

  .days-ago {
    font-size: 0.75rem;
    color: #64748b;
  }

  .session-change {
    text-align: center;
  }

  .change-indicator {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.25rem;
    font-weight: 600;
  }

  .rating {
    min-width: 120px;
  }

  .rating-display {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }

  .stars {
    color: #fbbf24;
    font-size: 0.875rem;
  }

  .rating-value {
    font-size: 0.75rem;
    color: #64748b;
  }

  .session-count {
    font-weight: 600;
    color: #1e293b;
    text-align: center;
  }

  .warning-flags {
    min-width: 200px;
  }

  .flags-list {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }

  .warning-flag {
    display: inline-block;
    padding: 0.25rem 0.5rem;
    background: #fee2e2;
    color: #991b1b;
    border-radius: 12px;
    font-size: 0.75rem;
    font-weight: 500;
  }

  .no-flags {
    color: #22c55e;
    font-weight: 600;
    font-size: 0.75rem;
  }

  .action-panel {
    padding: 1rem;
    background: #f8fafc;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
  }

  .suggestions-title {
    font-size: 1rem;
    font-weight: 600;
    color: #1e293b;
    margin: 0 0 1rem 0;
  }

  .suggestions-list {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .suggestion-item {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.75rem;
    border-radius: 6px;
    font-size: 0.875rem;
    font-weight: 500;
  }

  .suggestion-item.high-priority {
    background: #fee2e2;
    color: #991b1b;
  }

  .suggestion-item.medium-priority {
    background: #fef3c7;
    color: #d97706;
  }

  .suggestion-item.low-priority {
    background: #dcfce7;
    color: #166534;
  }

  @media (max-width: 768px) {
    .risk-summary {
      flex-direction: column;
      gap: 0.5rem;
    }

    .risk-stat {
      justify-content: center;
    }

    .risk-table th,
    .risk-table td {
      padding: 0.75rem 0.5rem;
      font-size: 0.75rem;
    }

    .suggestions-list {
      gap: 0.5rem;
    }

    .suggestion-item {
      padding: 0.5rem;
      font-size: 0.75rem;
    }
  }
</style>