<script lang="ts">
  import { FileText } from 'lucide-svelte';

  // --- MOCK DATA ---
  // In a real app, this data would be passed in as props or from a store
  const sessionDetails = {
    patientId: 'Patient-a1b2c3d4',
    date: 'Jan 27, 2025'
  };

  const aiGeneratedSummary = `The patient expressed significant feelings of loneliness and isolation, linking them to recent changes in their work environment. Key themes included stress related to project deadlines and disrupted sleep patterns, which they described as "fitful." They reported a decrease in social engagement and a general sense of anxiety about the future.`;

  // --- FORM STATE ---
  let formData = {
    aiSummary: aiGeneratedSummary,
    privateNotes: '',
    identifiedIllness: '',
    patientAge: null,
    patientGender: 'Prefer Not to Say',
    city: '',
    risk: '' // For the radio button group
  };

  const riskLevels = ['NONE', 'LOW', 'MODERATE', 'HIGH'];
  const genderOptions = ['Male', 'Female', 'Non-Binary', 'Prefer Not to Say'];

  function handleSubmit() {
    // Basic validation check
    if (!formData.risk) {
      alert('Risk Assessment is a required field.');
      return;
    }
    console.log('Submitting Final Summary:', formData);
    // Here, you would send the formData to your API
    alert('Summary Submitted!');
  }
</script>

<div class="summary-container">
  <!-- Page Header -->
  <div class="page-header">
    <h1>Submit Session Summary</h1>
    <p class="sub-header">
      For Session with <strong>{sessionDetails.patientId}</strong> on <strong>{sessionDetails.date}</strong>
    </p>
  </div>

  <!-- Form -->
  <form class="form-layout" on:submit|preventDefault={handleSubmit}>
    <div class="form-grid">
      <!-- Left Column -->
      <div class="form-column">
        <div class="form-group">
          <label for="ai-summary">AI Suggested Summary</label>
          <small>Please review and edit this summary before submitting.</small>
          <textarea id="ai-summary" rows="12" bind:value={formData.aiSummary}></textarea>
        </div>
        <div class="form-group">
          <label for="private-notes">Private Clinical Notes (Visible only to you)</label>
          <textarea id="private-notes" rows="10" bind:value={formData.privateNotes}></textarea>
        </div>
      </div>

      <!-- Right Column -->
      <div class="form-column">
        <div class="form-group">
          <label for="illness">Identified Illness</label>
          <input type="text" id="illness" placeholder="e.g., General Anxiety Disorder" bind:value={formData.identifiedIllness} />
        </div>
        <div class="form-group">
          <label for="age">Patient Age</label>
          <input type="number" id="age" placeholder="e.g., 34" bind:value={formData.patientAge} />
        </div>
        <div class="form-group">
          <label for="gender">Patient Gender</label>
          <select id="gender" bind:value={formData.patientGender}>
            {#each genderOptions as option}
              <option value={option}>{option}</option>
            {/each}
          </select>
        </div>
        <div class="form-group">
          <label for="city">City</label>
          <input type="text" id="city" placeholder="e.g., New York" bind:value={formData.city} />
        </div>
        <div class="form-group">
          <label>Risk Assessment (Required)</label>
          <div class="radio-group">
            {#each riskLevels as level}
              <label class="radio-label" class:checked={formData.risk === level}>
                <input type="radio" name="risk" value={level} bind:group={formData.risk} required />
                {level}
              </label>
            {/each}
          </div>
        </div>
      </div>
    </div>
    
    <!-- Footer Action -->
    <div class="form-actions">
      <button type="submit" class="submit-btn">
        <FileText size={20} />
        Submit Final Summary
      </button>
    </div>
  </form>
</div>

<style>
 .session-container {
    display: grid;
    grid-template-columns: 6fr 1fr 3fr;
    height: 100vh;
    width: 100%;
    background-color: #111827;
    color: #F9FAFB;
    gap: 1rem;
    padding: 1rem;
    box-sizing: border-box;
  }
  .page-header {
    border-bottom: 1px solid #E5E7EB;
    padding-bottom: 1.5rem;
    margin-bottom: 2rem;
  }
  .page-header h1 {
    font-size: 1.75rem;
    font-weight: 600;
    color: #1F2937;
    margin: 0;
  }
  .sub-header {
    font-size: 1rem;
    color: #6B7280;
    margin-top: 0.5rem;
  }
  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2.5rem;
  }
  .form-column {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
  label {
    font-weight: 500;
    color: #374151;
  }
  small {
    font-size: 0.875rem;
    color: #6B7280;
  }
  input, textarea, select {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #D1D5DB;
    border-radius: 0.5rem;
    font-size: 1rem;
    transition: border-color 0.2s, box-shadow 0.2s;
  }
  input:focus, textarea:focus, select:focus {
    outline: none;
    border-color: #4F46E5;
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  }
  textarea {
    resize: vertical;
    min-height: 120px;
    font-family: inherit;
  }
  .radio-group {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
  }
  .radio-label {
    display: block;
    padding: 0.75rem 1rem;
    border: 2px solid #D1D5DB;
    border-radius: 0.5rem;
    text-align: center;
    cursor: pointer;
    font-weight: 500;
    transition: all 0.2s ease;
  }
  .radio-label:hover {
    border-color: #9CA3AF;
  }
  .radio-label.checked {
    background-color: #EEF2FF;
    border-color: #4F46E5;
    color: #4F46E5;
  }
  /* Hide the actual radio button */
  .radio-label input[type="radio"] {
    display: none;
  }

  .form-actions {
    text-align: center;
    margin-top: 3rem;
  }
  .submit-btn {
    background-color: #4F46E5;
    color: white;
    font-size: 1rem;
    font-weight: 600;
    border: none;
    border-radius: 0.5rem;
    padding: 1rem 2.5rem;
    cursor: pointer;
    transition: background-color 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 0.75rem;
  }
  .submit-btn:hover {
    background-color: #4338CA;
  }

  /* Responsive adjustments for smaller screens */
  @media (max-width: 900px) {
    .form-grid {
      grid-template-columns: 1fr;
    }
  }
</style>