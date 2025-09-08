<script lang="ts">
  import { doctorStore } from '../../stores/doctor';
  import { Search, Plus, Filter, MoreVertical, Phone, Mail, Calendar } from 'lucide-svelte';

  let searchQuery = '';
  let statusFilter = 'all';

  $: filteredPatients = $doctorStore.patients.filter(patient => {
    const matchesSearch = patient.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         patient.email.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'all' || patient.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  function getStatusColor(status: string) {
    switch (status) {
      case 'active': return '#10B981';
      case 'inactive': return '#6B7280';
      case 'pending': return '#F59E0B';
      default: return '#6B7280';
    }
  }
</script>

<div class="patients-container">
  <div class="page-header">
    <div class="header-content">
      <h1>Patients</h1>
      <p>Manage your patient records and information</p>
    </div>
    <button class="add-patient-btn">
      <Plus size={20} />
      <span>Add New Patient</span>
    </button>
  </div>

  <div class="filters-section">
    <div class="search-container">
      <Search size={20} class="search-icon" />
      <input 
        type="text" 
        placeholder="Search patients..." 
        bind:value={searchQuery}
        class="search-input"
      />
    </div>
    
    <div class="filter-controls">
      <select bind:value={statusFilter} class="status-filter">
        <option value="all">All Status</option>
        <option value="active">Active</option>
        <option value="inactive">Inactive</option>
        <option value="pending">Pending</option>
      </select>
      
      <button class="filter-btn">
        <Filter size={18} />
        <span>More Filters</span>
      </button>
    </div>
  </div>

  <div class="patients-grid">
    {#each filteredPatients as patient}
      <div class="patient-card">
        <div class="patient-header">
          <div class="patient-avatar">
            <span>{patient.name.charAt(0)}</span>
          </div>
          <div class="patient-basic-info">
            <h3>{patient.name}</h3>
            <p class="patient-email">{patient.email}</p>
          </div>
          <button class="more-options">
            <MoreVertical size={18} />
          </button>
        </div>

        <div class="patient-details">
          <div class="detail-item">
            <Phone size={16} />
            <span>{patient.phone}</span>
          </div>
          <div class="detail-item">
            <Calendar size={16} />
            <span>Born: {new Date(patient.dateOfBirth).toLocaleDateString()}</span>
          </div>
          <div class="detail-item">
            <Mail size={16} />
            <span>Last session: {new Date(patient.lastSession).toLocaleDateString()}</span>
          </div>
        </div>

        <div class="patient-stats">
          <div class="stat">
            <span class="stat-value">{patient.sessionCount}</span>
            <span class="stat-label">Sessions</span>
          </div>
          <div class="stat">
            <span class="status-indicator" style="background-color: {getStatusColor(patient.status)}"></span>
            <span class="stat-label">{patient.status}</span>
          </div>
        </div>

        <div class="patient-notes">
          <p>{patient.notes}</p>
        </div>

        <div class="patient-actions">
          <button class="action-btn primary">View Profile</button>
          <button class="action-btn secondary">Schedule</button>
          <button class="action-btn secondary">Message</button>
        </div>
      </div>
    {/each}
  </div>

  {#if filteredPatients.length === 0}
    <div class="empty-state">
      <div class="empty-icon">👥</div>
      <h3>No patients found</h3>
      <p>Try adjusting your search criteria or add a new patient</p>
      <button class="add-patient-btn">
        <Plus size={20} />
        <span>Add New Patient</span>
      </button>
    </div>
  {/if}
</div>

<style>
  .patients-container {
    max-width: 1400px;
    margin: 0 auto;
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 2rem;
  }

  .header-content h1 {
    font-size: 2rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
  }

  .header-content p {
    color: #6B7280;
    margin: 0;
  }

  .add-patient-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: linear-gradient(135deg, #4F46E5, #7C3AED);
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 0.5rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .add-patient-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 15px rgba(79, 70, 229, 0.3);
  }

  .filters-section {
    display: flex;
    gap: 1rem;
    margin-bottom: 2rem;
    align-items: center;
  }

  .search-container {
    position: relative;
    flex: 1;
    max-width: 400px;
  }

  .search-input {
    width: 100%;
    padding: 0.75rem 1rem 0.75rem 3rem;
    border: 2px solid #E5E7EB;
    border-radius: 0.5rem;
    font-size: 0.9rem;
    transition: all 0.2s ease;
  }

  .search-input:focus {
    outline: none;
    border-color: #4F46E5;
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  }

  .search-input :global(.search-icon) {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #9CA3AF;
    pointer-events: none;
  }

  .filter-controls {
    display: flex;
    gap: 1rem;
    align-items: center;
  }

  .status-filter {
    padding: 0.75rem 1rem;
    border: 2px solid #E5E7EB;
    border-radius: 0.5rem;
    font-size: 0.9rem;
    background: white;
    cursor: pointer;
  }

  .filter-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    background: white;
    border: 2px solid #E5E7EB;
    border-radius: 0.5rem;
    color: #6B7280;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .filter-btn:hover {
    border-color: #4F46E5;
    color: #4F46E5;
  }

  .patients-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 1.5rem;
  }

  .patient-card {
    background: white;
    border: 1px solid #E5E7EB;
    border-radius: 1rem;
    padding: 1.5rem;
    transition: all 0.2s ease;
  }

  .patient-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  }

  .patient-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1rem;
  }

  .patient-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: linear-gradient(135deg, #4F46E5, #7C3AED);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    font-size: 1.25rem;
  }

  .patient-basic-info {
    flex: 1;
  }

  .patient-basic-info h3 {
    margin: 0 0 0.25rem 0;
    font-size: 1.125rem;
    font-weight: 600;
    color: #374151;
  }

  .patient-email {
    margin: 0;
    font-size: 0.875rem;
    color: #6B7280;
  }

  .more-options {
    background: none;
    border: none;
    color: #9CA3AF;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 0.25rem;
    transition: all 0.2s ease;
  }

  .more-options:hover {
    background: #F3F4F6;
    color: #6B7280;
  }

  .patient-details {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    margin-bottom: 1rem;
  }

  .detail-item {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    color: #6B7280;
    font-size: 0.875rem;
  }

  .patient-stats {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    padding: 1rem;
    background: #F8FAFC;
    border-radius: 0.5rem;
  }

  .stat {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .stat-value {
    font-size: 1.25rem;
    font-weight: 600;
    color: #374151;
  }

  .stat-label {
    font-size: 0.875rem;
    color: #6B7280;
    text-transform: capitalize;
  }

  .status-indicator {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .patient-notes {
    margin-bottom: 1.5rem;
  }

  .patient-notes p {
    margin: 0;
    font-size: 0.875rem;
    color: #6B7280;
    line-height: 1.5;
    font-style: italic;
  }

  .patient-actions {
    display: flex;
    gap: 0.75rem;
  }

  .action-btn {
    flex: 1;
    padding: 0.75rem;
    border-radius: 0.5rem;
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    border: none;
  }

  .action-btn.primary {
    background: #4F46E5;
    color: white;
  }

  .action-btn.primary:hover {
    background: #4338CA;
  }

  .action-btn.secondary {
    background: #F3F4F6;
    color: #374151;
    border: 1px solid #E5E7EB;
  }

  .action-btn.secondary:hover {
    background: #E5E7EB;
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4rem 2rem;
    text-align: center;
    background: white;
    border-radius: 1rem;
    border: 1px solid #E5E7EB;
  }

  .empty-icon {
    font-size: 4rem;
    margin-bottom: 1rem;
  }

  .empty-state h3 {
    font-size: 1.5rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
  }

  .empty-state p {
    color: #6B7280;
    margin-bottom: 2rem;
  }

  @media (max-width: 768px) {
    .page-header {
      flex-direction: column;
      gap: 1rem;
      align-items: stretch;
    }

    .filters-section {
      flex-direction: column;
      align-items: stretch;
    }

    .filter-controls {
      justify-content: space-between;
    }

    .patients-grid {
      grid-template-columns: 1fr;
    }

    .patient-actions {
      flex-direction: column;
    }
  }
</style>