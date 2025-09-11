<script lang="ts">
  let formData = {
    name: '',
    email: '',
    rating: 5,
    category: 'general',
    message: ''
  };
  
  let isSubmitting = false;
  let showSuccess = false;
  
  async function handleSubmit() {
    isSubmitting = true;
    
    // Simulate form submission
    setTimeout(() => {
      isSubmitting = false;
      showSuccess = true;
      formData = {
        name: '',
        email: '',
        rating: 5,
        category: 'general',
        message: ''
      };
      
      setTimeout(() => {
        showSuccess = false;
      }, 3000);
    }, 1000);
  }
</script>

<div class="feedback-container">
  <div class="container">
    <div class="hero-section">
      <h1 class="page-title">Your Feedback Matters</h1>
      <p class="page-subtitle">Help us improve our services and better serve the mental health community</p>
    </div>

    {#if showSuccess}
      <div class="success-message">
        <div class="success-icon">✓</div>
        <h3>Thank you for your feedback!</h3>
        <p>Your message has been received. We appreciate your input and will use it to improve our services.</p>
      </div>
    {:else}
      <form class="feedback-form" on:submit|preventDefault={handleSubmit}>
        <div class="form-row">
          <div class="form-group">
            <label for="name">Name *</label>
            <input 
              type="text" 
              id="name" 
              bind:value={formData.name}
              required
              placeholder="Your full name"
            />
          </div>
          <div class="form-group">
            <label for="email">Email *</label>
            <input 
              type="email" 
              id="email" 
              bind:value={formData.email}
              required
              placeholder="your.email@example.com"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="rating">Overall Rating</label>
            <select id="rating" bind:value={formData.rating}>
              <option value={5}>⭐⭐⭐⭐⭐ Excellent</option>
              <option value={4}>⭐⭐⭐⭐ Good</option>
              <option value={3}>⭐⭐⭐ Average</option>
              <option value={2}>⭐⭐ Poor</option>
              <option value={1}>⭐ Very Poor</option>
            </select>
          </div>
          <div class="form-group">
            <label for="category">Feedback Category</label>
            <select id="category" bind:value={formData.category}>
              <option value="general">General Feedback</option>
              <option value="service">Service Quality</option>
              <option value="technical">Technical Issues</option>
              <option value="accessibility">Accessibility</option>
              <option value="suggestion">Suggestion</option>
              <option value="complaint">Complaint</option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label for="message">Your Message *</label>
          <textarea 
            id="message" 
            bind:value={formData.message}
            required
            placeholder="Please share your thoughts, suggestions, or concerns..."
            rows="6"
          ></textarea>
        </div>

        <button type="submit" class="submit-btn" disabled={isSubmitting}>
          {#if isSubmitting}
            <span class="spinner"></span>
            Sending...
          {:else}
            Send Feedback
          {/if}
        </button>
      </form>
    {/if}
  </div>
</div>

<style>
  .feedback-container {
    padding: 2rem 0 4rem;
    background: white;
    min-height: 80vh;
  }

  .container {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  .hero-section {
    text-align: center;
    margin-bottom: 3rem;
  }

  .page-title {
    font-size: 3rem;
    font-weight: 300;
    color: #374151;
    margin-bottom: 1rem;
  }

  .page-subtitle {
    font-size: 1.25rem;
    color: #6B7280;
    line-height: 1.6;
    max-width: 600px;
    margin: 0 auto;
  }

  .success-message {
    text-align: center;
    padding: 3rem 2rem;
    background: linear-gradient(135deg, #F0FDF4 0%, #ECFDF5 100%);
    border-radius: 1rem;
    border: 1px solid #BBF7D0;
  }

  .success-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 60px;
    height: 60px;
    background: #10B981;
    color: white;
    border-radius: 50%;
    font-size: 2rem;
    font-weight: bold;
    margin-bottom: 1rem;
  }

  .success-message h3 {
    color: #065F46;
    font-size: 1.5rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
  }

  .success-message p {
    color: #047857;
    font-size: 1rem;
    margin: 0;
  }

  .feedback-form {
    background: #F8FAFC;
    padding: 3rem;
    border-radius: 1rem;
    border: 1px solid #E2E8F0;
  }

  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
    margin-bottom: 2rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .form-group label {
    font-weight: 600;
    color: #374151;
    font-size: 1rem;
  }

  .form-group input,
  .form-group select,
  .form-group textarea {
    padding: 0.75rem 1rem;
    border: 2px solid #E5E7EB;
    border-radius: 0.5rem;
    font-size: 1rem;
    transition: all 0.3s ease;
    font-family: inherit;
  }

  .form-group input:focus,
  .form-group select:focus,
  .form-group textarea:focus {
    outline: none;
    border-color: #4F46E5;
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  }

  .form-group textarea {
    resize: vertical;
    min-height: 120px;
  }

  .submit-btn {
    background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
    color: white;
    border: none;
    padding: 1rem 2rem;
    border-radius: 0.5rem;
    font-size: 1.1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    justify-content: center;
    width: 100%;
    max-width: 200px;
    margin: 2rem auto 0;
  }

  .submit-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(79, 70, 229, 0.3);
  }

  .submit-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
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
    to { transform: rotate(360deg); }
  }

  @media (max-width: 768px) {
    .container {
      padding: 0 1rem;
    }

    .page-title {
      font-size: 2.5rem;
    }

    .feedback-form {
      padding: 2rem 1.5rem;
    }

    .form-row {
      grid-template-columns: 1fr;
      gap: 1.5rem;
    }
  }
</style>