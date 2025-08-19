<script lang="ts">
  import { link } from 'svelte-spa-router';
</script>

<div class="not-found-container">
  <div class="not-found-content">
    <h1 class="error-code">404</h1>
    <h2 class="error-title">Page Not Found</h2>
    <p class="error-description">
      The page you're looking for doesn't exist or has been moved.
    </p>
    <a href="/dashboard" use:link class="back-link">
      Return to Dashboard
    </a>
  </div>
</div>

<style>
  .not-found-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
    background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  }

  .not-found-content {
    text-align: center;
  }

  .error-code {
    font-size: 6rem;
    font-weight: bold;
    color: #2563eb;
    margin: 0 0 1rem 0;
    line-height: 1;
  }

  .error-title {
    font-size: 2rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 1rem 0;
  }

  .error-description {
    font-size: 1.125rem;
    color: #6b7280;
    margin: 0 0 2rem 0;
    max-width: 400px;
  }

  .back-link {
    display: inline-block;
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    color: white;
    text-decoration: none;
    padding: 0.875rem 2rem;
    border-radius: 8px;
    font-weight: 600;
    transition: all 0.2s;
  }

  .back-link:hover {
    background: linear-gradient(135deg, #1d4ed8, #2563eb);
    transform: translateY(-1px);
  }
</style>