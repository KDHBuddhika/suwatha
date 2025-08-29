<script lang="ts">
  export let title: string = '';
  export let headers: string[] = [];
  export let data: any[] = [];

  let sortColumn = '';
  let sortDirection: 'asc' | 'desc' = 'asc';

  function sortTable(column: string) {
    if (sortColumn === column) {
      sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      sortColumn = column;
      sortDirection = 'asc';
    }

    data = [...data].sort((a, b) => {
      const aVal = a[column];
      const bVal = b[column];
      
      if (typeof aVal === 'string' && typeof bVal === 'string') {
        return sortDirection === 'asc' 
          ? aVal.localeCompare(bVal)
          : bVal.localeCompare(aVal);
      }
      
      return sortDirection === 'asc' ? aVal - bVal : bVal - aVal;
    });
  }

  function getStatusClass(status: string) {
    switch (status.toLowerCase()) {
      case 'available':
        return 'status-available';
      case 'busy':
        return 'status-busy';
      case 'offline':
        return 'status-offline';
      default:
        return 'status-offline';
    }
  }
</script>

<div class="table-container">
  {#if title}
    <h3 class="table-title">{title}</h3>
  {/if}
  <div class="table-wrapper">
    <table class="data-table">
      <thead>
        <tr>
          {#each headers as header}
            <th 
              class="sortable"
              class:sorted={sortColumn === header.toLowerCase()}
              on:click={() => sortTable(header.toLowerCase())}
            >
              {header}
              <span class="sort-indicator">
                {#if sortColumn === header.toLowerCase()}
                  {sortDirection === 'asc' ? '↑' : '↓'}
                {:else}
                  ↕
                {/if}
              </span>
            </th>
          {/each}
        </tr>
      </thead>
      <tbody>
        {#each data as row}
          <tr>
            {#each headers as header}
              <td>
                {#if header.toLowerCase() === 'status'}
                  <span class="status-badge {getStatusClass(row[header.toLowerCase()])}">
                    <span class="status-dot"></span>
                    {row[header.toLowerCase()]}
                  </span>
                {:else}
                  {row[header.toLowerCase()]}
                {/if}
              </td>
            {/each}
          </tr>
        {/each}
      </tbody>
    </table>
  </div>
</div>

<style>
  .table-container {
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e5e7eb;
    height: 100%;
  }

  .table-title {
    font-size: 1.125rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 1rem 0;
    text-align: center;
  }

  .table-wrapper {
    overflow-x: auto;
  }

  .data-table {
    width: 100%;
    border-collapse: collapse;
  }

  .data-table th {
    background: #f9fafb;
    padding: 0.75rem;
    text-align: left;
    font-weight: 600;
    color: #374151;
    border-bottom: 2px solid #e5e7eb;
    font-size: 0.875rem;
  }

  .data-table th.sortable {
    cursor: pointer;
    user-select: none;
    transition: background-color 0.2s;
    position: relative;
  }

  .data-table th.sortable:hover {
    background: #f3f4f6;
  }

  .data-table th.sorted {
    background: #eff6ff;
    color: #2563eb;
  }

  .sort-indicator {
    margin-left: 0.5rem;
    font-size: 0.75rem;
    opacity: 0.6;
  }

  .data-table td {
    padding: 0.75rem;
    border-bottom: 1px solid #e5e7eb;
    font-size: 0.875rem;
    color: #111827;
  }

  .data-table tr:hover {
    background: #f9fafb;
  }

  .status-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.25rem 0.75rem;
    border-radius: 16px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: capitalize;
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .status-available {
    background: #dcfce7;
    color: #166534;
  }

  .status-available .status-dot {
    background: #22c55e;
  }

  .status-busy {
    background: #fef3c7;
    color: #d97706;
  }

  .status-busy .status-dot {
    background: #f59e0b;
  }

  .status-offline {
    background: #f3f4f6;
    color: #6b7280;
  }

  .status-offline .status-dot {
    background: #9ca3af;
  }

  @media (max-width: 768px) {
    .data-table {
      font-size: 0.75rem;
    }

    .data-table th,
    .data-table td {
      padding: 0.5rem;
    }
  }
</style>