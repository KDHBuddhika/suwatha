<!-- src/routes/+page.svelte (or wherever your loading screen is) -->
<script lang="ts">
  import { onMount } from 'svelte';
  import Logo from './Logo.svelte';
  
  export let isLoading = true;
  
  onMount(() => {
    // --- CHANGE: Increased timeout to let the full, slow animation play out ---
    setTimeout(() => {
      isLoading = false;
    }, 6500); 
  });
</script>

{#if isLoading}
  <div class="loading-screen">
    <div class="loading-content">
      <Logo />
      <h1 class="brand-name">Manosara</h1>
      <p class="tagline">Mental Wellness & Care</p>
    </div>
  </div>
{/if}

<style>
  .loading-screen {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: linear-gradient(135deg, #E8F4FD 0%, #F0F9F4 50%, #FEF3E7 100%);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    
    /* --- CHANGE: Delayed the fade out to match the new animation length --- */
    animation: fadeOut 0.5s ease-out 6.0s forwards;
  }

  .loading-content {
    text-align: center;
    animation: fadeInUp 1.2s ease-out 0.5s both;
  }

  .brand-name {
    font-size: 2.5rem;
    font-weight: 300;
    color: #374151;
    margin: 1rem 0 0.5rem;
    letter-spacing: 0.05em;
  }

  .tagline {
    font-size: 1.1rem;
    color: #6B7280;
    font-weight: 300;
    margin: 0;
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

  @keyframes fadeOut {
    to {
      opacity: 0;
      visibility: hidden;
    }
  }
</style>