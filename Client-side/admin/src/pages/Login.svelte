<script lang="ts">
    import { authStore } from '../stores/auth';
    import { replace } from 'svelte-spa-router';
    import { Eye, EyeOff, User, Lock } from 'lucide-svelte';

    let email = '';
    let password = '';
    let showPassword = false;

    $: isLoading = $authStore.isLoading;
    $: error = $authStore.error;

    async function handleLogin() {
        if (!email || !password) {
            authStore.update(state => ({ 
                ...state, 
                error: 'Please fill in all fields' 
            }));
            return;
        }

        // Clear any previous errors
        authStore.update(state => ({ ...state, error: null }));

        const success = await authStore.login(email, password);
        
        if (success) {
            // Use a small timeout to ensure state is updated
            setTimeout(() => {
                replace('/dashboard');
            }, 100);
        }
    }

    function togglePasswordVisibility() {
        showPassword = !showPassword;
    }

    // Clear errors when user starts typing
    function clearError() {
        if (error) {
            authStore.update(state => ({ ...state, error: null }));
        }
    }
</script>

<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <div class="logo">
                <div class="logo-icon">S</div>
                <h1 class="logo-text">Suwatha Dashboard</h1>
            </div>
            <p class="login-subtitle">Therapist & Counseling Management Platform</p>
        </div>

        <form class="login-form" on:submit|preventDefault={handleLogin}>
            <div class="form-group">
                <label for="email" class="form-label">Email Address</label>
                <div class="input-group">
                    <User class="input-icon" size={20} />
                    <input
                        type="email"
                        id="email"
                        bind:value={email}
                        on:input={clearError}
                        placeholder="Enter your email"
                        class="form-input"
                        required
                        disabled={isLoading}
                    />
                </div>
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <div class="input-group">
                    <Lock class="input-icon" size={20} />
                    {#if showPassword}
                        <input
                            type="text"
                            id="password"
                            bind:value={password}
                            on:input={clearError}
                            placeholder="Enter your password"
                            class="form-input"
                            required
                            disabled={isLoading}
                        />
                    {:else}
                        <input
                            type="password"
                            id="password"
                            bind:value={password}
                            on:input={clearError}
                            placeholder="Enter your password"
                            class="form-input"
                            required
                            disabled={isLoading}
                        />
                    {/if}
                    <button
                        type="button"
                        class="password-toggle"
                        on:click={togglePasswordVisibility}
                        disabled={isLoading}
                    >
                        {#if showPassword}
                            <EyeOff size={20} />
                        {:else}
                            <Eye size={20} />
                        {/if}
                    </button>
                </div>
            </div>

            {#if error}
                <div class="error-message">
                    {error}
                </div>
            {/if}

            <button type="submit" class="login-button" disabled={isLoading}>
                {#if isLoading}
                    <div class="spinner"></div>
                    Signing In...
                {:else}
                    Sign In to Dashboard
                {/if}
            </button>
        </form>

        <div class="login-footer">
            <p class="demo-note">
                <strong>Login with your credentials:</strong> Enter your email and password
            </p>
        </div>
    </div>
</div>
<style>
    .login-container {
        min-height: 100vh;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 1rem;
    }

    .login-card {
        background: white;
        border-radius: 16px;
        padding: 2.5rem;
        width: 100%;
        max-width: 420px;
        box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    }

    .login-header {
        text-align: center;
        margin-bottom: 2rem;
    }

    .logo {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.75rem;
        margin-bottom: 0.5rem;
    }

    .logo-icon {
        width: 48px;
        height: 48px;
        background: linear-gradient(135deg, #2563eb, #3b82f6);
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: bold;
        font-size: 1.5rem;
    }

    .logo-text {
        font-size: 1.75rem;
        font-weight: bold;
        color: #1f2937;
        margin: 0;
    }

    .login-subtitle {
        color: #6b7280;
        font-size: 0.875rem;
        margin: 0;
    }

    .login-form {
        margin-bottom: 1.5rem;
    }

    .form-group {
        margin-bottom: 1.5rem;
    }

    .form-label {
        display: block;
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

    .form-input {
        width: 100%;
        padding: 0.75rem 1rem 0.75rem 3rem;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        font-size: 1rem;
        transition: border-color 0.2s, box-shadow 0.2s;
    }

    .form-input:focus {
        outline: none;
        border-color: #2563eb;
        box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
    }

    .password-toggle {
        position: absolute;
        right: 1rem;
        background: none;
        border: none;
        color: #9ca3af;
        cursor: pointer;
        padding: 0.25rem;
        border-radius: 4px;
        transition: color 0.2s;
    }

    .password-toggle:hover {
        color: #6b7280;
    }

    .error-message {
        background: #fee2e2;
        border: 1px solid #fecaca;
        color: #dc2626;
        padding: 0.75rem;
        border-radius: 8px;
        font-size: 0.875rem;
        margin-bottom: 1rem;
    }

    .login-button {
        width: 100%;
        background: linear-gradient(135deg, #2563eb, #3b82f6);
        color: white;
        border: none;
        padding: 0.875rem 1rem;
        border-radius: 8px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        transition: background-color 0.2s, transform 0.2s;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
    }

    .login-button:hover:not(:disabled) {
        background: linear-gradient(135deg, #1d4ed8, #2563eb);
        transform: translateY(-1px);
    }

    .login-button:disabled {
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

    .login-footer {
        text-align: center;
        padding-top: 1.5rem;
        border-top: 1px solid #e5e7eb;
    }

    .demo-note {
        background: #f3f4f6;
        color: #6b7280;
        font-size: 0.75rem;
        padding: 0.75rem;
        border-radius: 6px;
        margin: 0;
    }

    @media (max-width: 480px) {
        .login-card {
            padding: 2rem 1.5rem;
        }

        .logo-text {
            font-size: 1.5rem;
        }
    }
</style>