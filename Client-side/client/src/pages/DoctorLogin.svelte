<script lang="ts">
  import { authActions, authStore } from '../stores/auth';
  import { push } from 'svelte-spa-router';
  
  let loginData = {
    email: '',
    password: ''
  };
  
  let showError = false;
  let errorMessage = '';
  
  async function handleLogin() {
    showError = false;
    
    if (!loginData.email || !loginData.password) {
      showError = true;
      errorMessage = 'Please enter both email and password.';
      return;
    }
    
    const success = await authActions.login(loginData.email, loginData.password);
    
    if (success) {
      push('/doctor-dashboard');
    } else {
      showError = true;
      errorMessage = 'Invalid credentials. Please try again.';
    }
  }
</script>

<div class="login-container">
  <div class="login-card">
    <div class="login-header">
      <h1>Doctor Portal</h1>
      <p>Access your professional dashboard</p>
    </div>

    {#if showError}
      <div class="error-message">
        <span class="error-icon">⚠️</span>
        {errorMessage}
      </div>
    {/if}

    <form class="login-form" on:submit|preventDefault={handleLogin}>
      <div class="form-group">
        <label for="email">Email Address</label>
        <input 
          type="email" 
          id="email" 
          bind:value={loginData.email}
          required
          placeholder="doctor@example.com"
          autocomplete="email"
        />
      </div>

      <div class="form-group">
        <label for="password">Password</label>
        <input 
          type="password" 
          id="password" 
          bind:value={loginData.password}
          required
          placeholder="Enter your password"
          autocomplete="current-password"
        />
      </div>

      <div class="form-options">
        <label class="checkbox-label">
          <input type="checkbox" />
          <span class="checkmark"></span>
          Remember me
        </label>
        <a href="#forgot" class="forgot-link">Forgot password?</a>
      </div>

      <button type="submit" class="login-btn" disabled={$authStore.loading}>
          {#if $authStore.loading}
            <span class="spinner"></span>
            Signing in...
          {:else}
            Sign In
          {/if}
       </button>
    </form>

    <div class="login-footer">
      <p>Don't have an account? <a href="#register" class="register-link">Register as a healthcare provider</a></p>
    </div>

    <div class="security-info">
      <div class="security-badge">
        <span class="security-icon">🔒</span>
        <div>
          <strong>HIPAA Compliant</strong>
          <p>Your data is protected with enterprise-level security</p>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
  .login-container {
    min-height: calc(100vh - 70px);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
    background: linear-gradient(135deg, #E8F4FD 0%, #F0F9F4 50%, #FEF3E7 100%);
  }

  .login-card {
    background: white;
    border-radius: 1rem;
    padding: 3rem;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 450px;
    animation: fadeInUp 0.6s ease-out;
  }

  .login-header {
    text-align: center;
    margin-bottom: 2rem;
  }

  .login-header h1 {
    font-size: 2.25rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
  }

  .login-header p {
    color: #6B7280;
    font-size: 1rem;
    margin: 0;
    margin-bottom: 1rem;
  }

  .demo-credentials {
    background: #F0F9F4;
    border: 1px solid #BBF7D0;
    border-radius: 0.5rem;
    padding: 1rem;
    margin-top: 1rem;
  }

  .demo-credentials p {
    margin: 0.25rem 0;
    font-size: 0.875rem;
    color: #047857;
  }

  .demo-credentials p:first-child {
    font-weight: 600;
    margin-bottom: 0.5rem;
  }

  .error-message {
    background: #FEF2F2;
    border: 1px solid #FECACA;
    color: #DC2626;
    padding: 1rem;
    border-radius: 0.5rem;
    margin-bottom: 1.5rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.9rem;
  }

  .login-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .form-group label {
    font-weight: 600;
    color: #374151;
    font-size: 0.9rem;
  }

  .form-group input {
    padding: 0.875rem;
    border: 2px solid #E5E7EB;
    border-radius: 0.5rem;
    font-size: 1rem;
    transition: all 0.3s ease;
  }

  .form-group input:focus {
    outline: none;
    border-color: #4F46E5;
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: -0.5rem 0;
  }

  .checkbox-label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.9rem;
    color: #6B7280;
    cursor: pointer;
  }

  .checkbox-label input[type="checkbox"] {
    margin: 0;
    width: 18px;
    height: 18px;
  }

  .forgot-link {
    color: #4F46E5;
    text-decoration: none;
    font-size: 0.9rem;
    font-weight: 500;
  }

  .forgot-link:hover {
    text-decoration: underline;
  }

  .login-btn {
    background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
    color: white;
    border: none;
    padding: 1rem;
    border-radius: 0.5rem;
    font-size: 1.1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    margin-top: 1rem;
  }

  .login-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(79, 70, 229, 0.3);
  }

  .login-btn:disabled {
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

  .login-footer {
    text-align: center;
    margin-top: 2rem;
    padding-top: 1.5rem;
    border-top: 1px solid #E5E7EB;
  }

  .login-footer p {
    color: #6B7280;
    font-size: 0.9rem;
    margin: 0;
  }

  .register-link {
    color: #4F46E5;
    text-decoration: none;
    font-weight: 500;
  }

  .register-link:hover {
    text-decoration: underline;
  }

  .security-info {
    margin-top: 2rem;
    padding: 1.5rem;
    background: #F8FAFC;
    border-radius: 0.5rem;
    border: 1px solid #E2E8F0;
  }

  .security-badge {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
  }

  .security-icon {
    font-size: 1.5rem;
  }

  .security-badge strong {
    color: #374151;
    font-size: 0.9rem;
    display: block;
    margin-bottom: 0.25rem;
  }

  .security-badge p {
    color: #6B7280;
    font-size: 0.8rem;
    margin: 0;
    line-height: 1.4;
  }

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  @media (max-width: 568px) {
    .login-card {
      padding: 2rem 1.5rem;
      margin: 1rem;
    }

    .login-header h1 {
      font-size: 2rem;
    }

    .form-options {
      flex-direction: column;
      gap: 1rem;
      align-items: flex-start;
    }
  }
</style>