<script lang="ts">
  import { doctorsStore, type Doctor } from '../stores/doctors';
  import { authStore } from '../stores/auth';
  import { push } from 'svelte-spa-router';
  import { ArrowLeft, Save, User, Mail, Briefcase, UserCheck } from 'lucide-svelte';
  import { onMount } from 'svelte';
  import { get } from 'svelte/store';

  export let params: { id: string };

  let doctor: Doctor | null = null;
  let availableSpecializations: {id: number, name: string}[] = [];
  let formData = {
    name: '',
    email: '',
    isActive: true,
    isHandSignSpecialist: false,
    specializationIds: [] as number[]
  };

  let errors: Record<string, string> = {};
  let isSubmitting = false;
  let isLoading = true;

  onMount(async () => {
    await loadDoctorAndSpecializations();
  });

  async function loadDoctorAndSpecializations() {
    try {
      isLoading = true;
      const token = get(authStore).token;
      
      // Load doctor data
      const doctorResponse = await fetch(`http://localhost:8090/api/doctor/get-therapists/${params.id}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!doctorResponse.ok) {
        throw new Error('Failed to fetch doctor data');
      }

      const doctorData = await doctorResponse.json();
      doctor = doctorData;

      // Load specializations
      await doctorsStore.fetchSpecializations();
      doctorsStore.subscribe(store => {
        availableSpecializations = store.specializations;
      });

      // Populate form data
      if (doctor) {
        formData = {
          name: doctor.name,
          email: doctor.email,
          isActive: doctor.active,
          isHandSignSpecialist: doctor.handSignSpecialist,
          specializationIds: [] // Will be populated after specializations load
        };
      }

    } catch (error) {
      console.error('Error loading data:', error);
      errors.load = 'Failed to load doctor data';
    } finally {
      isLoading = false;
    }
  }

  function validateForm() {
    errors = {};

    if (!formData.name.trim()) {
      errors.name = 'Name is required';
    }

    if (!formData.email.trim()) {
      errors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Please enter a valid email address';
    }

    if (formData.specializationIds.length === 0) {
      errors.specializationIds = 'At least one specialization is required';
    }

    return Object.keys(errors).length === 0;
  }

  async function handleSubmit() {
    if (!validateForm()) {
      return;
    }

    isSubmitting = true;

    try {
      const token = get(authStore).token;
      
      const updateData = {
        name: formData.name,
        email: formData.email,
        isActive: formData.isActive,
        isHandSignSpecialist: formData.isHandSignSpecialist,
        specializationIds: formData.specializationIds.map(id => Number(id))
      };

      console.log('Updating with:', updateData);

      const response = await fetch(`http://localhost:8090/api/doctor/update-therapists/details/${params.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updateData)
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Failed to update doctor');
      }

      // Refresh doctors list and redirect
      await doctorsStore.fetchAll();
      push(`/doctors/view/${params.id}`);
    } catch (error) {
      console.error('Error updating doctor:', error);
      errors.submit = error instanceof Error ? error.message : 'Failed to update doctor profile';
    } finally {
      isSubmitting = false;
    }
  }

  function handleCancel() {
    push(`/doctors/view/${params.id}`);
  }
</script>

{#if isLoading}
  <div class="loading-state">
    <div class="spinner"></div>
    <p>Loading doctor data...</p>
  </div>
{:else if errors.load}
  <div class="error-state">
    <h2>Error Loading Data</h2>
    <p>{errors.load}</p>
    <button class="back-btn" on:click={() => push('/doctors')}>
      <ArrowLeft size={20} />
      Back to Doctors
    </button>
  </div>
{:else if doctor}
  <div class="edit-doctor">
    <div class="page-header">
      <button class="back-btn" on:click={handleCancel}>
        <ArrowLeft size={20} />
        Back to Profile
      </button>
      <div class="header-content">
        <h1 class="page-title">Edit Doctor Profile</h1>
        <p class="page-subtitle">Update therapist doctor information</p>
      </div>
    </div>

    <div class="form-container">
      <form class="doctor-form" on:submit|preventDefault={handleSubmit}>
        <div class="form-section">
          <h2 class="section-title">
            <User size={20} />
            Personal Information
          </h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="name" class="form-label">Full Name *</label>
              <input
                type="text"
                id="name"
                bind:value={formData.name}
                placeholder="Enter full name"
                class="form-input"
                class:error={errors.name}
              />
              {#if errors.name}
                <span class="error-message">{errors.name}</span>
              {/if}
            </div>

            <div class="form-group">
              <label for="email" class="form-label">Email Address *</label>
              <div class="input-group">
                <!-- <Mail class="input-icon" size={20} /> -->
                <input
                  type="email"
                  id="email"
                  bind:value={formData.email}
                  placeholder="Enter email address"
                  class="form-input with-icon"
                  class:error={errors.email}
                />
              </div>
              {#if errors.email}
                <span class="error-message">{errors.email}</span>
              {/if}
            </div>
          </div>
        </div>

        <div class="form-section">
          <h2 class="section-title">
            <Briefcase size={20} />
            Professional Information
          </h2>

          <div class="form-group">
            <label class="form-label">Specializations *</label>
            <div class="checkbox-group">
              {#each availableSpecializations as spec}
                <label class="checkbox-option">
                  <input
                    type="checkbox"
                    bind:group={formData.specializationIds}
                    value={spec.id}
                  />
                  <span class="checkbox-label">{spec.name}</span>
                </label>
              {/each}
            </div>
            {#if errors.specializationIds}
              <span class="error-message">{errors.specializationIds}</span>
            {/if}
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Account Status</label>
              <div class="radio-group">
                <label class="radio-option">
                  <input
                    type="radio"
                    bind:group={formData.isActive}
                    value={true}
                  />
                  <span class="radio-label">Active</span>
                </label>
                <label class="radio-option">
                  <input
                    type="radio"
                    bind:group={formData.isActive}
                    value={false}
                  />
                  <span class="radio-label">Inactive</span>
                </label>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Sign Language Specialist</label>
              <div class="radio-group">
                <label class="radio-option">
                  <input
                    type="radio"
                    bind:group={formData.isHandSignSpecialist}
                    value={true}
                  />
                  <span class="radio-label">Yes</span>
                </label>
                <label class="radio-option">
                  <input
                    type="radio"
                    bind:group={formData.isHandSignSpecialist}
                    value={false}
                  />
                  <span class="radio-label">No</span>
                </label>
              </div>
            </div>
          </div>
        </div>

        {#if errors.submit}
          <div class="form-error">
            {errors.submit}
          </div>
        {/if}

        <div class="form-actions">
          <button type="button" class="btn-cancel" on:click={handleCancel}>
            Cancel
          </button>
          <button type="submit" class="btn-submit" disabled={isSubmitting}>
            {#if isSubmitting}
              <div class="spinner"></div>
              Updating Profile...
            {:else}
              <Save size={20} />
              Save Changes
            {/if}
          </button>
        </div>
      </form>
    </div>
  </div>
{:else}
  <div class="not-found">
    <h2>Doctor Not Found</h2>
    <p>The requested doctor profile could not be found.</p>
    <button class="back-btn" on:click={() => push('/doctors')}>
      <ArrowLeft size={20} />
      Back to Doctors
    </button>
  </div>
{/if}

<style>
  .edit-doctor {
    padding: 2rem;
    max-width: 800px;
    margin: 0 auto;
  }

  .page-header {
    display: flex;
    align-items: center;
    margin-bottom: 2rem;
    gap: 1rem;
  }

  .back-btn {
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

  .back-btn:hover {
    background: #f3f4f6;
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

  .form-container {
    background: white;
    border-radius: 12px;
    padding: 2rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }

  .form-section {
    margin-bottom: 2rem;
    padding-bottom: 2rem;
    border-bottom: 1px solid #e5e7eb;
  }

  .form-section:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #3b82f6;
    margin-bottom: 1.5rem;
    font-size: 1.25rem;
  }

  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    margin-bottom: 1.5rem;
  }

  .form-group {
    margin-bottom: 1.5rem;
  }

  .form-label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
    color: #374151;
  }

  .form-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    font-size: 1rem;
  }

  .form-input.error {
    border-color: #ef4444;
  }

  .error-message {
    color: #ef4444;
    font-size: 0.875rem;
    margin-top: 0.25rem;
    display: block;
  }

  .input-group {
    position: relative;
  }

  .input-icon {
    position: absolute;
    left: 0.75rem;
    top: 50%;
    transform: translateY(-50%);
    color: #9ca3af;
  }

  .form-input.with-icon {
    padding-left: 2.5rem;
  }

  .checkbox-group {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 0.75rem;
    margin-top: 0.5rem;
  }

  .checkbox-option {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;
  }

  .checkbox-option:hover {
    background: #f9fafb;
  }

  .checkbox-label {
    user-select: none;
  }

  .radio-group {
    display: flex;
    gap: 1rem;
    margin-top: 0.5rem;
  }

  .radio-option {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;
  }

  .radio-option:hover {
    background: #f9fafb;
  }

  .radio-label {
    user-select: none;
  }

  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
    padding-top: 2rem;
    border-top: 1px solid #e5e7eb;
  }

  .btn-cancel, .btn-submit {
    padding: 0.75rem 1.5rem;
    border-radius: 6px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
  }

  .btn-cancel {
    background: white;
    border: 1px solid #e5e7eb;
    color: #4b5563;
  }

  .btn-cancel:hover {
    background: #f3f4f6;
  }

  .btn-submit {
    background: #3b82f6;
    color: white;
    border: none;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .btn-submit:hover {
    background: #2563eb;
  }

  .btn-submit:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }

  .spinner {
    width: 1rem;
    height: 1rem;
    border: 2px solid rgba(255,255,255,0.3);
    border-radius: 50%;
    border-top-color: white;
    animation: spin 1s ease-in-out infinite;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  .form-error {
    color: #ef4444;
    background: #fee2e2;
    padding: 1rem;
    border-radius: 6px;
    margin-bottom: 1.5rem;
    text-align: center;
  }

  .loading-state, .error-state, .not-found {
    text-align: center;
    padding: 4rem 2rem;
  }

  .loading-state .spinner {
    width: 3rem;
    height: 3rem;
    border: 3px solid #e5e7eb;
    border-top: 3px solid #3b82f6;
    margin: 0 auto 1rem;
  }
</style>