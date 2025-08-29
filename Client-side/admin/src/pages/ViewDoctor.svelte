<script lang="ts">
  import { doctorsStore, type Doctor } from '../stores/doctors';
  import { authStore } from '../stores/auth';
  import { push } from 'svelte-spa-router';
  import { ArrowLeft, Edit, Mail, Phone, Calendar, Briefcase, User, Clock } from 'lucide-svelte';
  import { onMount } from 'svelte';
  import { get } from 'svelte/store';

  export let params: { id: string };

  let doctor: Doctor | null = null;
  let isLoading = true;
  let error: string | null = null;

  onMount(async () => {
    await loadDoctor();
  });

  async function loadDoctor() {
    try {
      isLoading = true;
      error = null;
      
      const token = get(authStore).token;
      const response = await fetch(`http://localhost:8090/api/doctor/get-therapists/${params.id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch doctor: ${response.status}`);
      }

      const doctorData = await response.json();
      
      // Transform backend data to match frontend interface
      doctor = {
        id: doctorData.id,
        name: doctorData.name,
        email: doctorData.email,
        profilePictureUrl: doctorData.profilePictureUrl,
        currentStatus: doctorData.currentStatus,
        specializations: doctorData.specializations,
        active: doctorData.active,
        handSignSpecialist: doctorData.handSignSpecialist
      };
      
    } catch (err) {
      console.error('Error loading doctor:', err);
      error = err instanceof Error ? err.message : 'Failed to load doctor profile';
    } finally {
      isLoading = false;
    }
  }

  function handleEdit() {
    if (doctor) {
      push(`/doctors/edit/${doctor.id}`);
    }
  }

  function handleBack() {
    push('/doctors');
  }

  function getStatusBadgeClass(active: boolean) {
    return active ? 'status-active' : 'status-inactive';
  }

  function getStatusText(active: boolean) {
    return active ? 'Active' : 'Inactive';
  }
</script>

{#if isLoading}
  <div class="loading-state">
    <div class="spinner"></div>
    <p>Loading doctor profile...</p>
  </div>
{:else if error}
  <div class="error-state">
    <h2>Error Loading Profile</h2>
    <p>{error}</p>
    <button class="back-btn" on:click={handleBack}>
      <ArrowLeft size={20} />
      Back to Doctors
    </button>
  </div>
{:else if doctor}
  <div class="view-doctor">
    <div class="page-header">
      <button class="back-btn" on:click={handleBack}>
        <ArrowLeft size={20} />
        Back to Doctors
      </button>
      <div class="header-content">
        <h1 class="page-title">Doctor Profile</h1>
        <p class="page-subtitle">View therapist doctor information</p>
      </div>
      <button class="edit-btn" on:click={handleEdit}>
        <Edit size={20} />
        Edit Profile
      </button>
    </div>

    <div class="doctor-profile">
      <div class="profile-header">
        <div class="doctor-avatar">
          {#if doctor.profilePictureUrl}
            <img src={doctor.profilePictureUrl} alt={doctor.name} />
          {:else}
            <div class="avatar-placeholder">
              {doctor.name.split(' ').map(n => n[0]).join('')}
            </div>
          {/if}
          <div class="status-badge {getStatusBadgeClass(doctor.active)}">
            {getStatusText(doctor.active)}
          </div>
        </div>
        <div class="doctor-basic-info">
          <h2 class="doctor-name">{doctor.name}</h2>
          <p class="doctor-specialization">{doctor.specializations.join(', ')}</p>
          <p class="doctor-status">Current Status: {doctor.currentStatus}</p>
        </div>
      </div>

      <div class="profile-sections">
        <div class="info-section">
          <h3 class="section-title">
            <User size={20} />
            Personal Information
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <Mail class="info-icon" size={16} />
              <div class="info-content">
                <span class="info-label">Email Address</span>
                <span class="info-value">{doctor.email}</span>
              </div>
            </div>
            <div class="info-item">
              <User class="info-icon" size={16} />
              <div class="info-content">
                <span class="info-label">Account Status</span>
                <span class="info-value status-text {getStatusBadgeClass(doctor.active)}">
                  {getStatusText(doctor.active)}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h3 class="section-title">
            <Briefcase size={20} />
            Professional Information
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <Briefcase class="info-icon" size={16} />
              <div class="info-content">
                <span class="info-label">Specializations</span>
                <span class="info-value">
                  {#each doctor.specializations as spec, index}
                    {spec}{#if index < doctor.specializations.length - 1}, {/if}
                  {/each}
                </span>
              </div>
            </div>
            <div class="info-item">
              <User class="info-icon" size={16} />
              <div class="info-content">
                <span class="info-label">Current Status</span>
                <span class="info-value">{doctor.currentStatus}</span>
              </div>
            </div>
            <div class="info-item">
              <User class="info-icon" size={16} />
              <div class="info-content">
                <span class="info-label">Sign Language Specialist</span>
                <span class="info-value">{doctor.handSignSpecialist ? 'Yes' : 'No'}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
{:else}
  <div class="not-found">
    <h2>Doctor Not Found</h2>
    <p>The requested doctor profile could not be found.</p>
    <button class="back-btn" on:click={handleBack}>
      <ArrowLeft size={20} />
      Back to Doctors
    </button>
  </div>
{/if}

<style>
  .view-doctor {
    margin-left: 380px;
    padding: 2rem;
    max-width: 1000px;
    margin: 0 auto;
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    gap: 1rem;
    flex-wrap: wrap;
  }

  .back-btn, .edit-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.25rem;
    border: 1px solid #e5e7eb;
    background: white;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
  }

  .back-btn:hover, .edit-btn:hover {
    background: #f3f4f6;
  }

  .edit-btn {
    background: #3b82f6;
    color: white;
    border: none;
  }

  .edit-btn:hover {
    background: #2563eb;
  }

  .header-content {
    flex: 1;
    text-align: center;
  }

  .page-title {
    font-size: 2rem;
    font-weight: 700;
    color: #111827;
    margin: 0 0 0.5rem 0;
  }

  .page-subtitle {
    color: #6b7280;
    margin: 0;
  }

  .doctor-profile {
    background: white;
    border-radius: 12px;
    padding: 2rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }

  .profile-header {
    display: flex;
    align-items: center;
    gap: 2rem;
    margin-bottom: 2rem;
    padding-bottom: 2rem;
    border-bottom: 1px solid #e5e7eb;
  }

  .doctor-avatar {
    position: relative;
    flex-shrink: 0;
  }

  .doctor-avatar img {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    object-fit: cover;
  }

  .avatar-placeholder {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    font-size: 2rem;
  }

  .status-badge {
    position: absolute;
    bottom: 0;
    right: 0;
    padding: 0.5rem 1rem;
    border-radius: 20px;
    font-size: 0.875rem;
    font-weight: 600;
  }

  .status-active {
    background: #dcfce7;
    color: #166534;
  }

  .status-inactive {
    background: #fee2e2;
    color: #991b1b;
  }

  .doctor-basic-info {
    flex: 1;
  }

  .doctor-name {
    font-size: 1.5rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 0.5rem 0;
  }

  .doctor-specialization {
    color: #3b82f6;
    font-weight: 500;
    margin: 0 0 0.5rem 0;
  }

  .doctor-status {
    color: #6b7280;
    margin: 0;
  }

  .profile-sections {
    display: grid;
    gap: 2rem;
  }

  .info-section {
    background: #f9fafb;
    padding: 1.5rem;
    border-radius: 8px;
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #374151;
    margin: 0 0 1.5rem 0;
    font-size: 1.125rem;
  }

  .info-grid {
    display: grid;
    gap: 1rem;
  }

  .info-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    background: white;
    border-radius: 6px;
    border: 1px solid #e5e7eb;
  }

  .info-icon {
    color: #6b7280;
    flex-shrink: 0;
  }

  .info-content {
    flex: 1;
  }

  .info-label {
    display: block;
    font-size: 0.875rem;
    color: #6b7280;
    margin-bottom: 0.25rem;
  }

  .info-value {
    display: block;
    color: #111827;
    font-weight: 500;
  }

  .status-text {
    padding: 0.25rem 0.75rem;
    border-radius: 12px;
    font-size: 0.875rem;
    font-weight: 600;
  }

  .loading-state, .error-state, .not-found {
    text-align: center;
    padding: 4rem 2rem;
  }

  .spinner {
    width: 3rem;
    height: 3rem;
    border: 3px solid #e5e7eb;
    border-top: 3px solid #3b82f6;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 1rem;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }
</style>