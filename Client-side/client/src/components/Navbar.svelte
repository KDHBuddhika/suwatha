<script lang="ts">
  import { currentPage } from '../stores/navigation';
  import Logo2 from './Logo2.svelte';
  
  let mobileMenuOpen = false;
  
  const navItems = [
    { label: 'Home', page: 'home' },
    { label: 'About Us', page: 'about' },
    { label: 'Terms & Conditions', page: 'terms' },
    { label: 'Feedback', page: 'feedback' },
    { label: 'Privacy Policy', page: 'privacy' },
  ];
  
  function navigateTo(page: string) {
    currentPage.set(page);
    mobileMenuOpen = false;
  }
</script>

<nav class="navbar">
  <div class="nav-container">
    <div class="nav-brand" on:click={() => navigateTo('home')} on:keydown>
      <Logo2 />
      <span class="brand-text">Manosara</span>
    </div>
    
    <div class="nav-links" class:active={mobileMenuOpen}>
      {#each navItems as item}
        <button 
          class="nav-link"
          class:active={$currentPage === item.page}
          on:click={() => navigateTo(item.page)}
        >
          {item.label}
        </button>
      {/each}
      <button 
        class="nav-link doctor-login"
        on:click={() => navigateTo('doctor-login')}
      >
        Doctor Login
      </button>
    </div>
    
    <button 
      class="mobile-toggle"
      on:click={() => mobileMenuOpen = !mobileMenuOpen}
      aria-label="Toggle menu"
    >
      <span></span>
      <span></span>
      <span></span>
    </button>
  </div>
</nav>

<style>
  .navbar {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid #E5E7EB;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    transition: all 0.3s ease;
  }

  .nav-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 1rem;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 70px;
  }

  .nav-brand {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    transition: transform 0.3s ease;
  }

  .nav-brand:hover {
    transform: scale(1.05);
  }

  .brand-text {
    font-size: 1.5rem;
    font-weight: 500;
    color: #374151;
    letter-spacing: 0.02em;
  }

  .nav-links {
    display: flex;
    align-items: center;
    gap: 2rem;
  }

  .nav-link {
    background: none;
    border: none;
    color: #6B7280;
    text-decoration: none;
    font-weight: 500;
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    transition: all 0.3s ease;
    cursor: pointer;
    position: relative;
  }

  .nav-link:hover {
    color: #4F46E5;
    background: #F3F4F6;
    transform: translateY(-1px);
  }

  .nav-link.active {
    color: #4F46E5;
    background: #EEF2FF;
  }

  .doctor-login {
    background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
    color: white !important;
    font-weight: 600;
    padding: 0.75rem 1.5rem;
    border-radius: 2rem;
    box-shadow: 0 4px 15px rgba(79, 70, 229, 0.3);
  }

  .doctor-login:hover {
    background: linear-gradient(135deg, #4338CA 0%, #6D28D9 100%);
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(79, 70, 229, 0.4);
  }

  .mobile-toggle {
    display: none;
    flex-direction: column;
    background: none;
    border: none;
    cursor: pointer;
    padding: 0.5rem;
  }

  .mobile-toggle span {
    width: 25px;
    height: 3px;
    background: #374151;
    margin: 3px 0;
    transition: 0.3s;
    border-radius: 2px;
  }

  @media (max-width: 768px) {
    .mobile-toggle {
      display: flex;
    }

    .nav-links {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background: white;
      flex-direction: column;
      padding: 2rem 1rem;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
      transform: translateY(-100%);
      opacity: 0;
      visibility: hidden;
      transition: all 0.3s ease;
    }

    .nav-links.active {
      transform: translateY(0);
      opacity: 1;
      visibility: visible;
    }

    .nav-link {
      width: 100%;
      text-align: center;
      margin-bottom: 0.5rem;
    }
  }
</style>