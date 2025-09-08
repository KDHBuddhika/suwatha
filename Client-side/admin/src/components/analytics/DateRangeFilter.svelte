<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  import { Calendar, ChevronDown } from 'lucide-svelte';

  const dispatch = createEventDispatcher();

  let selectedPreset = 'last30days';
  let customStartDate = '';
  let customEndDate = '';
  let showCustomRange = false;

  const presets = [
    { value: 'today', label: 'Today' },
    { value: 'yesterday', label: 'Yesterday' },
    { value: 'last7days', label: 'Last 7 Days' },
    { value: 'last30days', label: 'Last 30 Days' },
    { value: 'last90days', label: 'Last 90 Days' },
    { value: 'thismonth', label: 'This Month' },
    { value: 'lastmonth', label: 'Last Month' },
    { value: 'custom', label: 'Custom Range' }
  ];

  function getDateRange(preset: string) {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    
    switch (preset) {
      case 'today':
        return { start: today, end: now };
      case 'yesterday':
        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);
        return { start: yesterday, end: yesterday };
      case 'last7days':
        const week = new Date(today);
        week.setDate(week.getDate() - 7);
        return { start: week, end: now };
      case 'last30days':
        const month = new Date(today);
        month.setDate(month.getDate() - 30);
        return { start: month, end: now };
      case 'last90days':
        const quarter = new Date(today);
        quarter.setDate(quarter.getDate() - 90);
        return { start: quarter, end: now };
      case 'thismonth':
        const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
        return { start: monthStart, end: now };
      case 'lastmonth':
        const lastMonthStart = new Date(now.getFullYear(), now.getMonth() - 1, 1);
        const lastMonthEnd = new Date(now.getFullYear(), now.getMonth(), 0);
        return { start: lastMonthStart, end: lastMonthEnd };
      default:
        return { start: today, end: now };
    }
  }

  function handlePresetChange() {
    if (selectedPreset === 'custom') {
      showCustomRange = true;
      return;
    }
    
    showCustomRange = false;
    const range = getDateRange(selectedPreset);
    
    dispatch('change', {
      start: range.start.toISOString().split('T')[0],
      end: range.end.toISOString().split('T')[0],
      preset: selectedPreset
    });
  }

  function handleCustomRangeApply() {
    if (customStartDate && customEndDate) {
      dispatch('change', {
        start: customStartDate,
        end: customEndDate,
        preset: 'custom'
      });
      showCustomRange = false;
    }
  }

  // Initialize with default preset
  handlePresetChange();
</script>

<div class="date-range-filter">
  <div class="filter-header">
    <Calendar size={20} />
    <span class="filter-label">Date Range</span>
  </div>
  
  <div class="filter-controls">
    <div class="preset-selector">
      <select bind:value={selectedPreset} on:change={handlePresetChange} class="preset-select">
        {#each presets as preset}
          <option value={preset.value}>{preset.label}</option>
        {/each}
      </select>
      <ChevronDown class="select-icon" size={16} />
    </div>

    {#if showCustomRange}
      <div class="custom-range">
        <input
          type="date"
          bind:value={customStartDate}
          class="date-input"
          placeholder="Start date"
        />
        <span class="date-separator">to</span>
        <input
          type="date"
          bind:value={customEndDate}
          class="date-input"
          placeholder="End date"
        />
        <button class="apply-btn" on:click={handleCustomRangeApply}>
          Apply
        </button>
      </div>
    {/if}
  </div>
</div>

<style>
  .date-range-filter {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 1.5rem;
    min-width: 300px;
  }

  .filter-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 1rem;
    color: #475569;
    font-weight: 600;
  }

  .filter-label {
    font-size: 0.875rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .filter-controls {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .preset-selector {
    position: relative;
  }

  .preset-select {
    width: 100%;
    padding: 0.75rem 2.5rem 0.75rem 1rem;
    border: 2px solid #e2e8f0;
    border-radius: 8px;
    background: white;
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    appearance: none;
    transition: border-color 0.2s;
  }

  .preset-select:focus {
    outline: none;
    border-color: #2563eb;
  }

  .select-icon {
    position: absolute;
    right: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #64748b;
    pointer-events: none;
  }

  .custom-range {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    padding: 1rem;
    background: white;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
  }

  .date-input {
    padding: 0.75rem;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    font-size: 0.875rem;
  }

  .date-input:focus {
    outline: none;
    border-color: #2563eb;
  }

  .date-separator {
    text-align: center;
    color: #64748b;
    font-size: 0.875rem;
    font-weight: 500;
  }

  .apply-btn {
    padding: 0.75rem 1.5rem;
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    color: white;
    border: none;
    border-radius: 6px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }

  .apply-btn:hover {
    background: linear-gradient(135deg, #1d4ed8, #2563eb);
    transform: translateY(-1px);
  }

  @media (max-width: 768px) {
    .date-range-filter {
      min-width: auto;
      width: 100%;
    }
  }
</style>