<script lang="ts">
  import { doctorsStore } from '../stores/doctors';
  import { authStore } from '../stores/auth';
  import { push } from 'svelte-spa-router';
  import { ArrowLeft, User, Mail, Briefcase, UserPlus } from 'lucide-svelte';
  import { onMount } from 'svelte';
  import { get } from 'svelte/store';

  let formData = {
    name: '',
    email: '',
    isHandSignSpecialist: false,
    isActive: true,
    currentStatus: 'OFFLINE',
    specializations: [] as number[],
    profilePicture: null as File | null
  };

  let errors: Record<string, string> = {};
  let isSubmitting = false;
  let availableSpecializations: {id: number, name: string}[] = [];

  onMount(async () => {
    try {
      await doctorsStore.fetchSpecializations();
      doctorsStore.subscribe(store => {
        availableSpecializations = store.specializations;
      });
    } catch (error) {
      console.error('Failed to load specializations:', error);
      errors.specializations = 'Failed to load specializations. Please refresh.';
    }
  });

  function validateForm() {
    errors = {};
    let isValid = true;

    if (!formData.name.trim()) {
      errors.name = 'Name is required';
      isValid = false;
    }

    if (!formData.email.trim()) {
      errors.email = 'Email is required';
      isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Please enter a valid email address';
      isValid = false;
    }

    if (formData.specializations.length === 0) {
      errors.specializations = 'At least one specialization is required';
      isValid = false;
    }

    return isValid;
  }

  function handleFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.[0]) {
      formData.profilePicture = input.files[0];
    }
  }

  async function handleSubmit() {
    if (!validateForm()) return;

    isSubmitting = true;

    try {
      const formDataToSend = new FormData();
      
      // 1. Add image file with EXACT key 'file'
      if (formData.profilePicture) {
        formDataToSend.append('file', formData.profilePicture);
      }

      // 2. Create therapistDto with EXACT field names expected by backend
      const therapistDto = {
        name: formData.name,
        email: formData.email,
        isHandSignSpecialist: formData.isHandSignSpecialist,
        isActive: formData.isActive,
        currentStatus: formData.currentStatus,
        specializationIds: formData.specializations.map(id => Number(id)) // Ensure numbers
      };

      // Debug before sending
      console.log('Therapist DTO:', therapistDto);
      console.log('Image file:', formData.profilePicture?.name);

      // 3. Add therapistDto as JSON string with EXACT key 'therapistDto'
      formDataToSend.append('therapistDto', JSON.stringify(therapistDto));

      // 4. Submit to backend
      await doctorsStore.add(formDataToSend);
      push('/doctors');
    } catch (error) {
      console.error('Full error details:', error);
      errors.submit = error instanceof Error ? error.message : 'Failed to create therapist. Check console for details.';
    } finally {
      isSubmitting = false;
    }
  }
  function handleCancel() {
    push('/doctors');
  }
</script>

<div class="add-therapist">
  <div class="page-header">
    <button class="back-btn" on:click={handleCancel}>
      <ArrowLeft size={20} />
      Back to Doctors
    </button>
    <div class="header-content">
      <h1 class="page-title">Add New Therapist</h1>
      <p class="page-subtitle">Register a new therapist doctor to the platform</p>
    </div>
  </div>

  <div class="form-container">
    <form class="therapist-form" on:submit|preventDefault={handleSubmit}>
      <!-- Personal Information Section -->
      <div class="form-section">
        <h2 class="section-title">
          <User size={20} />
          Personal Information
        </h2>
        
        <div class="form-group">
          <label for="name" class="form-label">Full Name *</label>
          <input
            type="text"
            id="name"
            bind:value={formData.name}
            placeholder="Enter full name"
            class="form-input {errors.name ? 'error' : ''}"
          />
          {#if errors.name}
            <span class="error-message">{errors.name}</span>
          {/if}
        </div>

        <div class="form-group">
          <label for="email" class="form-label">Email Address *</label>
          <div class="input-group">
            <Mail class="input-icon" size={20} />
            <input
              type="email"
              id="email"
              bind:value={formData.email}
              placeholder="Enter email address"
              class="form-input with-icon {errors.email ? 'error' : ''}"
            />
          </div>
          {#if errors.email}
            <span class="error-message">{errors.email}</span>
          {/if}
        </div>

        <div class="form-group">
          <label for="profilePicture" class="form-label">Profile Picture</label>
          <input
            type="file"
            id="profilePicture"
            accept="image/*"
            on:change={handleFileChange}
            class="form-input"
          />
        </div>
      </div>

      <!-- Professional Information Section -->
      <div class="form-section">
        <h2 class="section-title">
          <Briefcase size={20} />
          Professional Information
        </h2>

        <div class="form-group">
          <label class="form-label">Specializations *</label>
          {#if errors.specializations}
            <span class="error-message">{errors.specializations}</span>
          {/if}
          
          <div class="checkbox-group">
            {#each availableSpecializations as spec}
              <label class="checkbox-option">
                <input
                  type="checkbox"
                  bind:group={formData.specializations}
                  value={spec.id}
                />
                <span class="checkbox-label">{spec.name}</span>
              </label>
            {/each}
          </div>
        </div>

        <div class="form-row">
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

          <div class="form-group">
            <label class="form-label">Status</label>
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
            Adding Therapist...
          {:else}
            <UserPlus size={20} />
            Add Therapist
          {/if}
        </button>
      </div>
    </form>
  </div>
</div>
<style>
  .add-therapist {
    padding: 2rem;
    max-width: 800px;
    margin: 0 auto;
  }

  .page-header {
    margin-bottom: 2rem;
  }

  .back-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: none;
    border: none;
    color: #6b7280;
    cursor: pointer;
    padding: 0.5rem 0;
    margin-bottom: 1rem;
    transition: color 0.2s;
  }

  .back-btn:hover {
    color: #2563eb;
  }

  .page-title {
    font-size: 2rem;
    font-weight: bold;
    color: #111827;
    margin: 0 0 0.5rem 0;
  }

  .page-subtitle {
    color: #6b7280;
    font-size: 1rem;
    margin: 0;
  }

  .form-container {
    background: white;
    border-radius: 12px;
    padding: 2rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border: 1px solid #e5e7eb;
  }

  .form-section {
    margin-bottom: 2rem;
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 1.25rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 1.5rem 0;
    padding-bottom: 0.75rem;
    border-bottom: 1px solid #e5e7eb;
  }

  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    margin-bottom: 1.5rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
  }

  .form-label {
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
    font-size: 0.875rem;
  }

  .input-group {
    position: relative;
    display: flex;
    align-items: center;
  }

  .input-icon {
    position: absolute;
    left: 1rem;
    color: #9ca3af;
    z-index: 1;
  }
   /* Add your styles here */
  .checkbox-group {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem;
    margin-top: 0.5rem;
  }

  .checkbox-option {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
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
    cursor: pointer;
  }

  .form-error {
    color: #dc2626;
    background: #fee2e2;
    padding: 1rem;
    border-radius: 0.5rem;
    margin-bottom: 1.5rem;
    text-align: center;
  }

  .form-input {
    width: 100%;
    padding: 0.75rem;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    font-size: 1rem;
    transition: border-color 0.2s;
  }

  .form-input.with-icon {
    padding-left: 3rem;
  }

  .form-input:focus {
    outline: none;
    border-color: #2563eb;
  }

  .form-input.error {
    border-color: #dc2626;
  }

  .form-select {
    width: 100%;
    padding: 0.75rem;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    font-size: 1rem;
    background: white;
    cursor: pointer;
    transition: border-color 0.2s;
  }

  .form-select:focus {
    outline: none;
    border-color: #2563eb;
  }

  .form-select.error {
    border-color: #dc2626;
  }

  .radio-group {
    display: flex;
    gap: 1.5rem;
    margin-top: 0.5rem;
  }

  .radio-option {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
  }

  .radio-option input[type="radio"] {
    width: 1rem;
    height: 1rem;
    accent-color: #2563eb;
  }

  .radio-label {
    font-size: 0.875rem;
    color: #374151;
  }

  .error-message {
    color: #dc2626;
    font-size: 0.875rem;
    margin-top: 0.25rem;
  }

  .form-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    padding-top: 2rem;
    border-top: 1px solid #e5e7eb;
  }

  .btn-cancel {
    padding: 0.875rem 1.5rem;
    border: 2px solid #e5e7eb;
    background: white;
    color: #374151;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s;
  }

  .btn-cancel:hover {
    border-color: #d1d5db;
    background: #f9fafb;
  }

  .btn-submit {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.875rem 1.5rem;
    border: none;
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    color: white;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s;
  }

  .btn-submit:hover:not(:disabled) {
    background: linear-gradient(135deg, #1d4ed8, #2563eb);
    transform: translateY(-1px);
  }

  .btn-submit:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none;
  }

  .spinner {
    width: 20px;
    height: 20px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top: 2px solid white;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }

  @media (max-width: 768px) {
    .add-therapist {
      padding: 1rem;
    }

    .form-container {
      padding: 1.5rem;
    }

    .form-row {
      grid-template-columns: 1fr;
      gap: 1rem;
    }

    .form-actions {
      flex-direction: column;
    }
  }
</style>