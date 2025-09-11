<script lang="ts">
  import { onMount } from 'svelte';
  import { authStore } from '../../stores/auth'; // Ensure this path is correct for your project
  import { Edit3, Save, X, Camera } from 'lucide-svelte';

  // --- COMPONENT STATE ---
  let profileData = null;
  let loading = true;
  let error = null;

  let isProfileEditing = false;
  let isPasswordFormVisible = false;

  // Temporary object to hold edits for the profile name/picture
  let editableProfileData = { name: '', pictureUrl: '' };

  // Object for the password form fields
  let passwordData = {
    current: '',
    new: '',
    confirm: ''
  };
  
  // --- STATE FOR PASSWORD UPDATE UI FEEDBACK ---
  let isUpdatingPassword = false;
  let passwordErrorMessage = null;
  let passwordSuccessMessage = null;

  // --- DATA FETCHING ---
  onMount(async () => {
    let authState;
    // Subscribe to the store to get the current authentication state
    const unsubscribe = authStore.subscribe(value => {
      authState = value;
    });
    unsubscribe(); // Unsubscribe immediately after getting the value

    if (!authState || !authState.user || !authState.token) {
      error = "Authentication details not found. Please log in again.";
      loading = false;
      return;
    }

    const userId = authState.user.id;
    const token = authState.token;

    try {
      const response = await fetch(`http://localhost:8090/api/doctor/get-therapists/${userId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch profile data from the server.');
      }

      const data = await response.json();
      
      // Map the API response to the component's state variable
      profileData = {
        name: data.name,
        email: data.email,
        pictureUrl: data.profilePictureUrl,
        status: data.currentStatus === 'AVAILABLE' ? 'Active' : 'Inactive',
        specializations: data.specializations,
        isHandSignSpecialist: data.handSignSpecialist,
      };

    } catch (err) {
      error = err.message;
    } finally {
      loading = false;
    }
  });

  // --- PROFILE EDIT FUNCTIONS (Client-Side Mock) ---
  function startEditingProfile() {
    editableProfileData.name = profileData.name;
    editableProfileData.pictureUrl = profileData.pictureUrl;
    isProfileEditing = true;
  }

  function cancelEditingProfile() {
    isProfileEditing = false;
  }

  function saveProfileChanges() {
    // In a real app, you would make a PUT/PATCH API call here
    // to update the user's name and picture URL.
    profileData.name = editableProfileData.name;
    profileData.pictureUrl = editableProfileData.pictureUrl;
    
    alert('Profile Updated!'); // This is a mock success message
    isProfileEditing = false;
  }

  // --- PASSWORD UPDATE FUNCTION (Connected to Backend) ---
  async function handlePasswordUpdate() {
    // 1. Reset UI feedback messages
    passwordErrorMessage = null;
    passwordSuccessMessage = null;
    
    // 2. Perform client-side validation
    if (passwordData.new !== passwordData.confirm) {
      passwordErrorMessage = 'The new passwords do not match.';
      return;
    }
    if (passwordData.new.length < 5) {
      passwordErrorMessage = 'The new password must be at least 5 characters long.';
      return;
    }

    isUpdatingPassword = true;

    // 3. Get authentication details from the store
    let authState;
    const unsubscribe = authStore.subscribe(value => { authState = value; });
    unsubscribe();
    
    if (!authState?.user?.id || !authState?.token) {
      passwordErrorMessage = 'Could not find authentication details to proceed.';
      isUpdatingPassword = false;
      return;
    }

    // 4. Send the request to the backend API
    try {
      const response = await fetch(`http://localhost:8090/api/doctor/update-therapists/password/${authState.user.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authState.token}`
        },
        body: JSON.stringify({
          currentPassword: passwordData.current,
          newPassword: passwordData.new
        })
      });

      // 5. Handle the server's response
      if (response.ok) {
        passwordSuccessMessage = "Your password has been updated successfully!";
        // Reset and hide the form after a short delay for the user to see the message
        setTimeout(() => {
          passwordData = { current: '', new: '', confirm: '' };
          isPasswordFormVisible = false;
          passwordSuccessMessage = null;
        }, 2500);
      } else {
        // If the server returns an error (like 400 Bad Request)
        const errorData = await response.json();
        passwordErrorMessage = errorData.detail || 'An unexpected server error occurred.';
      }
    } catch (err) {
      // Handle network errors (e.g., backend is down)
      passwordErrorMessage = 'A network error occurred. Please try again later.';
    } finally {
      isUpdatingPassword = false;
    }
  }
</script>

<div class="settings-container">
  <div class="page-header">
    <h1>My Profile & Settings</h1>
  </div>

  {#if loading}
    <div class="card">
      <p>Loading profile...</p>
    </div>
  {:else if error}
    <div class="card error-card">
      <p>Error: {error}</p>
    </div>
  {:else if profileData}
    <!-- Profile Information Card -->
    <div class="card profile-card">
      <div class="profile-picture-wrapper">
        <img src={profileData.pictureUrl} alt={profileData.name} class="profile-picture" />
        <div class="picture-overlay">
          <Camera size={24} />
          <span>Change</span>
        </div>
      </div>
      
      {#if !isProfileEditing}
        <!-- Display Mode -->
        <div class="profile-details">
          <h2>{profileData.name}</h2>
          <p>{profileData.email}</p>
        </div>
        <button class="edit-btn" on:click={startEditingProfile}>
          <Edit3 size={16} />
          <span>Edit Profile</span>
        </button>
      {:else}
        <!-- Edit Mode -->
        <div class="profile-edit-form">
          <div class="form-group">
            <label for="fullName">Full Name</label>
            <input id="fullName" type="text" bind:value={editableProfileData.name} />
          </div>
          <div class="form-group">
            <label for="pictureUrl">Profile Picture URL</label>
            <input id="pictureUrl" type="text" bind:value={editableProfileData.pictureUrl} />
          </div>
          <div class="form-actions">
            <button class="btn-secondary" on:click={cancelEditingProfile}>
              <X size={16} />
              <span>Cancel</span>
            </button>
            <button class="btn-primary" on:click={saveProfileChanges}>
              <Save size={16} />
              <span>Save Changes</span>
            </button>
          </div>
        </div>
      {/if}
    </div>

    <!-- Professional Details Card (Read-Only) -->
    <div class="card details-card">
      <h3>Professional Details</h3>
      <ul class="details-list">
        <li>
          <span>Account Status</span>
          <strong><span class="status-dot"></span>{profileData.status}</strong>
        </li>
        <li>
          <span>Specializations</span>
          <div class="tags-list">
            {#each profileData.specializations as spec}
              <span class="tag">{spec}</span>
            {/each}
          </div>
        </li>
        <li>
          <span>Hand-Sign Specialist</span>
          <strong>{profileData.isHandSignSpecialist ? 'Yes' : 'No'}</strong>
        </li>
      </ul>
      <p class="helper-text">
        To update your professional details, please contact the platform administrator.
      </p>
    </div>

    <!-- Account Security Card (Fully Functional) -->
    <div class="card security-card">
      <h3>Account Security</h3>
      {#if !isPasswordFormVisible}
        <button class="btn-secondary" on:click={() => { isPasswordFormVisible = true; passwordErrorMessage = null; passwordSuccessMessage = null; }}>
          Change Password
        </button>
      {:else}
        <!-- Feedback Messages for Password Update -->
        {#if passwordSuccessMessage}
          <div class="feedback-message success">{passwordSuccessMessage}</div>
        {/if}
        {#if passwordErrorMessage}
          <div class="feedback-message error">{passwordErrorMessage}</div>
        {/if}
      
        <form class="password-form" on:submit|preventDefault={handlePasswordUpdate}>
          <div class="form-group">
            <label for="currentPassword">Current Password</label>
            <input id="currentPassword" type="password" bind:value={passwordData.current} required />
          </div>
          <div class="form-group">
            <label for="newPassword">New Password</label>
            <input id="newPassword" type="password" bind:value={passwordData.new} minlength="5" required />
            <small>Must be at least 5 characters.</small>
          </div>
          <div class="form-group">
            <label for="confirmPassword">Confirm New Password</label>
            <input id="confirmPassword" type="password" bind:value={passwordData.confirm} required />
          </div>
          <button type="submit" class="btn-primary" disabled={isUpdatingPassword}>
            {#if isUpdatingPassword}
              <span class="spinner"></span> Updating...
            {:else}
              Update Password
            {/if}
          </button>
        </form>
      {/if}
    </div>
  {/if}
</div>

<style>
  .settings-container { max-width: 900px; margin: auto; display: flex; flex-direction: column; gap: 2rem; }
  .page-header h1 { font-size: 1.75rem; font-weight: 600; color: #1F2937; }
  .card { background-color: white; border: 1px solid #E5E7EB; border-radius: 1rem; padding: 2rem; }
  .error-card { background-color: #FEF2F2; color: #991B1B; }
  
  /* Profile Card */
  .profile-card { display: flex; align-items: center; gap: 2rem; flex-wrap: wrap; }
  .profile-picture-wrapper { position: relative; cursor: pointer; flex-shrink: 0; }
  .profile-picture { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; }
  .picture-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.5); border-radius: 50%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: white; opacity: 0; transition: opacity 0.2s ease; }
  .profile-picture-wrapper:hover .picture-overlay { opacity: 1; }
  .profile-details { flex-grow: 1; }
  .profile-details h2 { margin: 0; font-size: 1.5rem; color: #111827; }
  .profile-details p { margin: 0.25rem 0 0; color: #6B7280; }
  .edit-btn { background: none; border: 1px solid #D1D5DB; color: #374151; padding: 0.5rem 1rem; border-radius: 0.5rem; cursor: pointer; display: flex; align-items: center; gap: 0.5rem; font-weight: 500; transition: background-color 0.2s; }
  .edit-btn:hover { background-color: #F3F4F6; }
  
  /* Profile Edit Form */
  .profile-edit-form { flex-grow: 1; display: flex; flex-direction: column; gap: 1rem; }
  .form-group { display: flex; flex-direction: column; gap: 0.25rem; }
  .form-group label { font-weight: 500; font-size: 0.875rem; color: #374151; }
  .form-group input { padding: 0.5rem 0.75rem; border: 1px solid #D1D5DB; border-radius: 0.5rem; }
  .form-actions { display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 0.5rem; }
  .btn-primary { background-color: #4F46E5; color: white; border: none; }
  .btn-secondary { background-color: #F3F4F6; color: #374151; border: 1px solid #D1D5DB; }
  .btn-primary, .btn-secondary { padding: 0.5rem 1rem; border-radius: 0.5rem; cursor: pointer; display: flex; align-items: center; gap: 0.5rem; font-weight: 500; transition: opacity 0.2s; }
  .btn-primary:hover, .btn-secondary:hover { opacity: 0.9; }

  /* Details Card */
  .details-card h3 { margin-top: 0; font-size: 1.25rem; }
  .details-list { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 1.25rem; }
  .details-list li { display: flex; justify-content: space-between; align-items: flex-start; gap: 1rem; }
  .details-list li span { color: #6B7280; flex-shrink: 0; }
  .status-dot { display: inline-block; width: 8px; height: 8px; background-color: #10B981; border-radius: 50%; margin-right: 0.5rem; vertical-align: middle; }
  .tags-list { display: flex; flex-wrap: wrap; gap: 0.5rem; justify-content: flex-end; max-width: 75%; }
  .tag { background-color: #EEF2FF; color: #4338CA; padding: 0.25rem 0.75rem; border-radius: 1rem; font-size: 0.875rem; }
  .helper-text { font-size: 0.875rem; color: #9CA3AF; margin-top: 1.5rem; text-align: center; }

  /* Security Card */
  .security-card h3 { margin-top: 0; font-size: 1.25rem; }
  .password-form { display: flex; flex-direction: column; gap: 1rem; max-width: 400px; }
  .password-form small { font-size: 0.875rem; color: #6B7280; margin-top: -0.5rem; }
  .btn-primary:disabled { background-color: #9CA3AF; cursor: not-allowed; }
  
  /* Feedback Messages */
  .feedback-message { padding: 0.75rem 1rem; border-radius: 0.5rem; margin-bottom: 1rem; font-size: 0.9rem; border: 1px solid transparent; }
  .feedback-message.success { background-color: #F0FDF4; color: #166534; border-color: #A7F3D0; }
  .feedback-message.error { background-color: #FEF2F2; color: #991B1B; border-color: #FECACA; }

  /* Spinner for loading state */
  @keyframes spin { to { transform: rotate(360deg); } }
  .spinner {
    display: inline-block;
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
</style>