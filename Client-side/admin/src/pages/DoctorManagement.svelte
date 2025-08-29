<script lang="ts">
  import { doctorsStore, type Doctor } from '../stores/doctors';
  import { push } from 'svelte-spa-router';
  import { Plus, Search, Eye, Edit, Trash2, UserPlus, Filter, AlertCircle, Loader } from 'lucide-svelte';
  import { onMount } from 'svelte';

  let searchTerm = '';
  let statusFilter = 'all';
  let showDeleteModal = false;
  let doctorToDelete: Doctor | null = null;

  $: doctors = $doctorsStore.doctors;
  $: isLoading = $doctorsStore.isLoading;
  $: error = $doctorsStore.error;

  $: filteredDoctors = doctors.filter(doctor => {
    const matchesSearch = doctor.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         doctor.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         doctor.specializations.join(', ').toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStatus = statusFilter === 'all' || 
                         (statusFilter === 'active' && doctor.active) || 
                         (statusFilter === 'inactive' && !doctor.active);
    
    return matchesSearch && matchesStatus;
  });

  onMount(() => {
    doctorsStore.fetchAll();
  });

  function handleAddDoctor() {
    push('/doctors/add');
  }

  function handleViewDoctor(id: number) {
    push(`/doctors/view/${id}`);
  }

  function handleEditDoctor(id: number) {
    push(`/doctors/edit/${id}`);
  }

  function confirmDelete(doctor: Doctor) {
    doctorToDelete = doctor;
    showDeleteModal = true;
  }

  async function handleDelete() {
    if (doctorToDelete) {
      try {
        await doctorsStore.delete(doctorToDelete.id);
        showDeleteModal = false;
        doctorToDelete = null;
      } catch (error) {
        console.error('Delete failed:', error);
      }
    }
  }

  function cancelDelete() {
    showDeleteModal = false;
    doctorToDelete = null;
  }

  function getStatusBadgeClass(active: boolean) {
    return active ? 'status-active' : 'status-inactive';
  }

  function handleRefresh() {
    doctorsStore.fetchAll();
  }

  function clearError() {
    doctorsStore.clearError();
  }
</script>

<div class="doctor-management">
  <div class="page-header">
    <div class="header-content">
      <h1 class="page-title">Doctor Management</h1>
      <p class="page-subtitle">Manage therapist doctors and counselors</p>
    </div>
    <div class="header-actions">
      <button class="refresh-btn" on:click={handleRefresh} disabled={isLoading}>
        {#if isLoading}
          <Loader class="spin" size={20} />
        {:else}
          Refresh
        {/if}
      </button>
      <button class="add-btn" on:click={handleAddDoctor}>
        <UserPlus size={20} />
        Add New Therapist
      </button>
    </div>
  </div>

  {#if error}
    <div class="error-banner">
      <AlertCircle size={20} />
      <span>{error}</span>
      <button class="error-close" on:click={clearError}>×</button>
    </div>
  {/if}

  <div class="filters-section">
    <div class="search-box">
      <Search class="search-icon" size={20} />
      <input
        type="text"
        placeholder="Search doctors by name, email, or specialization..."
        bind:value={searchTerm}
        class="search-input"
      />
    </div>
    
    <div class="filter-group">
      <Filter size={16} />
      <select bind:value={statusFilter} class="status-filter">
        <option value="all">All Status</option>
        <option value="active">Active</option>
        <option value="inactive">Inactive</option>
      </select>
    </div>
  </div>

  {#if isLoading && doctors.length === 0}
    <div class="loading-state">
      <Loader class="spin" size={48} />
      <p>Loading doctors...</p>
    </div>
  {:else}
    <div class="doctors-grid">
      {#each filteredDoctors as doctor (doctor.id)}
        <div class="doctor-card">
          <div class="doctor-avatar">
            {#if doctor.profilePictureUrl}
              <img src={doctor.profilePictureUrl} alt={doctor.name} />
            {:else}
              <div class="avatar-placeholder">
                {doctor.name.split(' ').map(n => n[0]).join('')}
              </div>
            {/if}
            <div class="status-badge {getStatusBadgeClass(doctor.active)}">
              {doctor.active ? 'active' : 'inactive'}
            </div>
          </div>
          
          <div class="doctor-info">
            <h3 class="doctor-name">{doctor.name}</h3>
            <p class="doctor-specialization">{doctor.specializations.join(', ')}</p>
            <div class="doctor-details">
              <span class="detail-item">
                <strong>Email:</strong> {doctor.email}
              </span>
              <span class="detail-item">
                <strong>Status:</strong> {doctor.currentStatus}
              </span>
              <span class="detail-item">
                <strong>Sign Language Specialist:</strong> {doctor.handSignSpecialist ? 'Yes' : 'No'}
              </span>
            </div>
          </div>

          <div class="doctor-actions">
            <button class="action-btn view" on:click={() => handleViewDoctor(doctor.id)}>
              <Eye size={16} />
              View
            </button>
            <button class="action-btn edit" on:click={() => handleEditDoctor(doctor.id)}>
              <Edit size={16} />
              Edit
            </button>
            <button 
              class="action-btn delete" 
              on:click={() => confirmDelete(doctor)}
              disabled={isLoading}
            >
              <Trash2 size={16} />
              Delete
            </button>
          </div>
        </div>
      {/each}
    </div>

    {#if filteredDoctors.length === 0 && !isLoading}
      <div class="empty-state">
        <UserPlus size={48} />
        <h3>No doctors found</h3>
        <p>
          {#if doctors.length === 0}
            No doctors have been added yet.
          {:else}
            No doctors match your current search criteria.
          {/if}
        </p>
        <button class="add-btn" on:click={handleAddDoctor}>
          Add First Therapist
        </button>
      </div>
    {/if}
  {/if}
</div>

<!-- Delete Confirmation Modal -->
{#if showDeleteModal}
  <div class="modal-overlay" on:click={cancelDelete}>
    <div class="modal" on:click|stopPropagation>
      <h3>Confirm Delete</h3>
      <p>Are you sure you want to delete <strong>{doctorToDelete?.name}</strong>?</p>
      <p class="warning">This action cannot be undone.</p>
      <div class="modal-actions">
        <button class="btn-cancel" on:click={cancelDelete} disabled={isLoading}>
          Cancel
        </button>
        <button class="btn-delete" on:click={handleDelete} disabled={isLoading}>
          {#if isLoading}
            <Loader class="spin" size={16} />
            Deleting...
          {:else}
            Delete
          {/if}
        </button>
      </div>
    </div>
  </div>
{/if}


<style>
.doctor-management {
  padding: 2rem;
  max-width: 1400px;
  margin-left: 280px;
  min-height: 100vh;
  background-color: #f9fafb;
  transition: margin 0.3s ease;
}

.sidebar.collapsed ~ .doctor-management {
  margin-left: 80px;
}

/* ===== Header Section ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.header-content {
  flex: 1;
  min-width: 300px;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #111827;
  margin: 0 0 0.25rem 0;
  line-height: 1.3;
}

.page-subtitle {
  color: #6b7280;
  font-size: 0.95rem;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background-color: white;
  color: #2563eb;
  border: 1px solid #e5e7eb;
  padding: 0.75rem 1.25rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.refresh-btn:hover {
  background-color: #f0f5ff;
  border-color: #bfdbfe;
}

.refresh-btn .spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* ===== Filters Section ===== */
.filters-section {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  align-items: center;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 300px;
}

.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.search-input {
  width: 75%;
  padding: 0.75rem 1rem 0.75rem 3rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.95rem;
  background-color: white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}

.search-input::placeholder {
  color: #9ca3af;
}

.search-input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6b7280;
}

.status-filter {
  padding: 0.75rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  min-width: 150px;
  font-size: 0.95rem;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* ===== Error Handling ===== */
.error-banner {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  background-color: #fee2e2;
  color: #b91c1c;
  border-radius: 8px;
  margin-bottom: 2rem;
  border: 1px solid #fecaca;
  font-size: 0.95rem;
}

.error-close {
  margin-left: auto;
  background: none;
  border: none;
  color: #b91c1c;
  font-size: 1.25rem;
  cursor: pointer;
  padding: 0 0.25rem;
}

/* ===== Loading State ===== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  color: #6b7280;
}

.loading-state .spin {
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

/* ===== Empty State ===== */
.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-top: 1rem;
  border: 1px solid #e5e7eb;
}

.empty-state svg {
  color: #2563eb;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.25rem;
  color: #111827;
  margin: 0 0 0.5rem 0;
}

.empty-state p {
  color: #6b7280;
  margin-bottom: 1.5rem;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

/* ===== Doctors Grid ===== */
.doctors-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.doctor-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.doctor-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.doctor-avatar {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 1.25rem;
}

.doctor-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 1.5rem;
}

.status-badge {
  position: absolute;
  bottom: 0;
  right: calc(50% - 50px);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: capitalize;
}

.status-active {
  background: #dcfce7;
  color: #166534;
}

.status-inactive {
  background: #fee2e2;
  color: #991b1b;
}

.doctor-info {
  text-align: center;
  margin-bottom: 1.5rem;
}

.doctor-name {
  font-size: 1.15rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 0.5rem 0;
}

.doctor-specialization {
  color: #2563eb;
  font-weight: 500;
  margin: 0 0 1rem 0;
  font-size: 0.95rem;
}

.doctor-details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  text-align: left;
  font-size: 0.9rem;
}

.detail-item {
  color: #6b7280;
}

.detail-item strong {
  color: #374151;
}

.doctor-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem 0.75rem;
  border: none;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.view {
  background: #eff6ff;
  color: #2563eb;
}

.action-btn.view:hover {
  background: #dbeafe;
}

.action-btn.edit {
  background: #fef3c7;
  color: #d97706;
}

.action-btn.edit:hover {
  background: #fde68a;
}

.action-btn.delete {
  background: #fee2e2;
  color: #dc2626;
}

.action-btn.delete:hover {
  background: #fecaca;
}

/* ===== Modal Styles ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal h3 {
  margin: 0 0 1rem 0;
  color: #111827;
  font-size: 1.25rem;
}

.modal p {
  margin: 0 0 0.5rem 0;
  color: #6b7280;
  line-height: 1.5;
}

.warning {
  color: #dc2626;
  font-weight: 600;
  margin-top: 1rem;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

.btn-cancel {
  padding: 0.75rem 1.5rem;
  border: 1px solid #e5e7eb;
  background: white;
  color: #374151;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #f3f4f6;
}

.btn-delete {
  padding: 0.75rem 1.5rem;
  border: none;
  background: #dc2626;
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-delete:hover {
  background: #b91c1c;
}

.btn-delete .spin {
  animation: spin 1s linear infinite;
}

.add-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  color: white;
  border: none;
  padding: 0.875rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(37, 99, 235, 0.2);
  position: relative;
  overflow: hidden;
}

.add-btn:hover {
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(37, 99, 235, 0.2);
}

.add-btn:active {
  transform: translateY(0);
}

.add-btn::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to bottom right,
    rgba(255, 255, 255, 0.3),
    rgba(255, 255, 255, 0)
  );
  transform: rotate(30deg);
  transition: all 0.3s;
}

.add-btn:hover::after {
  left: 100%;
  top: 100%;
}

/* ===== Improved Action Buttons ===== */
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.9rem;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.action-btn.view {
  background: #2563eb;
  color: white;
}

.action-btn.view:hover {
  background: #1d4ed8;
  box-shadow: 0 2px 4px rgba(29, 78, 216, 0.2);
}

.action-btn.edit {
  background: #f59e0b;
  color: white;
}

.action-btn.edit:hover {
  background: #d97706;
  box-shadow: 0 2px 4px rgba(217, 119, 6, 0.2);
}

.action-btn.delete {
  background: #ef4444;
  color: white;
}

.action-btn.delete:hover {
  background: #dc2626;
  box-shadow: 0 2px 4px rgba(220, 38, 38, 0.2);
}

/* Disabled state for all buttons */
button:disabled,
button[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

/* Loading spinner in buttons */
.button-loader {
  animation: spin 1s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* ===== Responsive Adjustments for Buttons ===== */
@media (max-width: 768px) {
  .add-btn {
    padding: 0.75rem 1.25rem;
    font-size: 0.9rem;
  }
  
  .action-btn {
    padding: 0.5rem 0.75rem;
    font-size: 0.85rem;
  }
}

/* ===== Responsive Adjustments ===== */
@media (max-width: 1024px) {
  .doctor-management {
    margin-left: 0;
  }
  
  .sidebar.collapsed ~ .doctor-management {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .doctor-management {
    padding: 1.5rem;
  }
  
  .filters-section {
    flex-direction: column;
  }
  
  .search-box {
    min-width: 100%;
  }
  
  .doctors-grid {
    grid-template-columns: 1fr;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .doctor-actions {
    flex-direction: column;
  }
  
  .action-btn {
    justify-content: center;
  }
  
  .modal-actions {
    flex-direction: column;
  }
  
  .btn-cancel,
  .btn-delete {
    width: 100%;
  }
}

</style>