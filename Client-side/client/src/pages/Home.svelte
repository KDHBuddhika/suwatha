<script lang="ts">
  import { push } from 'svelte-spa-router';
  import { Video, MessageCircle, Ear } from 'lucide-svelte';
  import ServiceLoadingOverlay from '../components/ServiceLoadingOverlay.svelte';
  
  let isLoading = false;
  let loadingService = '';
  let loadingIcon = '';
  
  function navigateTo(page: string) {
    // Set loading state based on the service
    isLoading = true;
    loadingService = getServiceName(page);
    loadingIcon = getServiceIcon(page);
    
    // Show loading for 3 seconds then navigate
    setTimeout(() => {
      isLoading = false;
      push(getPagePath(page));
    }, 6500);
  }
  
  function getServiceName(page: string) {
    switch(page) {
      case 'video': return 'Video Consultation';
      case 'chat': return 'Chat Support';
      case 'special-support': return 'Special Support';
      default: return 'Service';
    }
  }
  
  function getPagePath(page: string) {
    switch(page) {
      case 'video': return '/live-video-session';
      case 'chat': return '/chat-session';
      case 'special-support': return '/special-support';
      default: return '/';
    }
  }
  
  function getServiceIcon(page: string) {
    switch(page) {
      case 'video': return '<svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>';
      case 'chat': return '<svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 21 1.9-5.7a8.5 8.5 0 1 1 3.8 3.8z"></path></svg>';
      case 'special-support': return '<svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 8.5a6.5 6.5 0 1 1 13 0c0 6-6 6-6 10a3.5 3.5 0 1 1-7 0"></path><path d="M15 8.5a2.5 2.5 0 0 0-5 0v1a2 2 0 1 1 0 4"></path></svg>';
      default: return '<svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle></svg>';
    }
  }
</script>

<ServiceLoadingOverlay 
  bind:isVisible={isLoading} 
  serviceName={loadingService}
  serviceIcon={loadingIcon}
/>

<div class="home-container">
  <section class="hero-section">
    <div class="hero-content">
      <h1 class="hero-title">
        Welcome to <span class="brand-highlight">Manosara</span>
      </h1>
      <p class="hero-subtitle">
        Your journey to mental wellness starts here. We provide compassionate care and support for your mental health needs.
      </p>
      
      <div class="action-buttons">
        <button 
          class="action-btn video-btn"
          on:click={() => navigateTo('video')}
        >
          <div class="btn-icon">
            <Video size={32} />
          </div>
          <div class="btn-content">
            <h3>Video Consultation</h3>
            <p>Connect with mental health professionals</p>
          </div>
        </button>
        
        <button 
          class="action-btn chat-btn"
          on:click={() => navigateTo('chat')}
        >
          <div class="btn-icon">
            <MessageCircle size={32} />
          </div>
          <div class="btn-content">
            <h3>Chat Support</h3>
            <p>24/7 text-based mental health support</p>
          </div>
        </button>
        
        <button 
          class="action-btn special-btn"
          on:click={() => navigateTo('special-support')}
        >
          <div class="btn-icon">
            <Ear size={32} />
          </div>
          <div class="btn-content">
            <h3>Special Support</h3>
            <p>Specialized care for hearing impaired</p>
          </div>
        </button>
      </div>
    </div>
    
    <div class="hero-visual">
      <div class="floating-elements">
        <div class="floating-circle circle-1"></div>
        <div class="floating-circle circle-2"></div>
        <div class="floating-circle circle-3"></div>
      </div>
    </div>
  </section>
  
  <section class="features-section">
    <div class="container">
      <h2 class="section-title">Why Choose Manosara?</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">🧘‍♀️</div>
          <h3>Mindful Approach</h3>
          <p>Evidence-based therapeutic techniques combined with mindfulness practices</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🤝</div>
          <h3>Compassionate Care</h3>
          <p>Empathetic professionals who understand your unique mental health journey</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🔒</div>
          <h3>Safe & Secure</h3>
          <p>Your privacy and confidentiality are our highest priorities</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🌟</div>
          <h3>Accessible</h3>
          <p>Special accommodations and support for all accessibility needs</p>
        </div>
      </div>
    </div>
  </section>
</div>

<style>
  .home-container {
    width: 100%;
  }

  .hero-section {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 4rem;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
    padding: 4rem 2rem;
    min-height: 80vh;
  }

  .hero-content {
    animation: fadeInLeft 1s ease-out;
  }

  .hero-title {
    font-size: 3.5rem;
    font-weight: 300;
    color: #374151;
    margin-bottom: 1.5rem;
    line-height: 1.2;
  }

  .brand-highlight {
    background: linear-gradient(135deg, #4F46E5, #7C3AED);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    font-weight: 600;
  }

  .hero-subtitle {
    font-size: 1.25rem;
    color: #6B7280;
    margin-bottom: 3rem;
    line-height: 1.6;
    font-weight: 300;
  }

  .action-buttons {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .action-btn {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    padding: 2rem;
    background: white;
    border: none;
    border-radius: 1rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: left;
    width: 100%;
    max-width: 400px;
  }

  .action-btn:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }

  .btn-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 60px;
    height: 60px;
    border-radius: 1rem;
    flex-shrink: 0;
  }

  .video-btn .btn-icon {
    background: linear-gradient(135deg, #EF4444, #F97316);
    color: white;
  }

  .chat-btn .btn-icon {
    background: linear-gradient(135deg, #10B981, #059669);
    color: white;
  }

  .special-btn .btn-icon {
    background: linear-gradient(135deg, #8B5CF6, #A855F7);
    color: white;
  }

  .btn-content h3 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #374151;
    margin: 0 0 0.5rem;
  }

  .btn-content p {
    font-size: 0.9rem;
    color: #6B7280;
    margin: 0;
    line-height: 1.5;
  }

  .hero-visual {
    position: relative;
    height: 400px;
    animation: fadeInRight 1s ease-out;
  }

  .floating-elements {
    position: relative;
    width: 100%;
    height: 100%;
  }

  .floating-circle {
    position: absolute;
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;
  }

  .circle-1 {
    width: 120px;
    height: 120px;
    background: linear-gradient(135deg, #4F46E5, #7C3AED);
    opacity: 0.1;
    top: 20%;
    left: 20%;
    animation-delay: 0s;
  }

  .circle-2 {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #10B981, #059669);
    opacity: 0.15;
    top: 50%;
    right: 30%;
    animation-delay: 2s;
  }

  .circle-3 {
    width: 100px;
    height: 100px;
    background: linear-gradient(135deg, #F59E0B, #EF4444);
    opacity: 0.1;
    bottom: 20%;
    left: 40%;
    animation-delay: 4s;
  }

  .features-section {
    padding: 6rem 2rem;
    background: white;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-title {
    text-align: center;
    font-size: 2.5rem;
    font-weight: 300;
    color: #374151;
    margin-bottom: 3rem;
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 2rem;
  }

  .feature-card {
    text-align: center;
    padding: 3rem 2rem;
    background: white;
    border-radius: 1rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
  }

  .feature-card:hover {
    transform: translateY(-10px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }

  .feature-icon {
    font-size: 3rem;
    margin-bottom: 1.5rem;
  }

  .feature-card h3 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 1rem;
  }

  .feature-card p {
    color: #6B7280;
    line-height: 1.6;
    margin: 0;
  }

  @keyframes fadeInLeft {
    from {
      opacity: 0;
      transform: translateX(-50px);
    }
    to {
      opacity: 1;
      transform: translateX(0);
    }
  }

  @keyframes fadeInRight {
    from {
      opacity: 0;
      transform: translateX(50px);
    }
    to {
      opacity: 1;
      transform: translateX(0);
    }
  }

  @keyframes float {
    0%, 100% {
      transform: translateY(0px);
    }
    50% {
      transform: translateY(-20px);
    }
  }

  @media (max-width: 768px) {
    .hero-section {
      grid-template-columns: 1fr;
      gap: 2rem;
      padding: 2rem 1rem;
      text-align: center;
    }

    .hero-title {
      font-size: 2.5rem;
    }

    .action-buttons {
      align-items: center;
    }

    .hero-visual {
      height: 200px;
    }

    .features-section {
      padding: 4rem 1rem;
    }
  }
</style>