<script lang="ts">
  export let isVisible = false;
  export let serviceName = '';

  type ServiceKey = 'video' | 'chat' | 'special-support';

  const serviceMessages: Record<ServiceKey, string> = {
    'video': 'Connecting you with mental health professionals...',
    'chat': 'Setting up your secure chat session...',
    'special-support': 'Preparing specialized accessibility services...'
  };

  $: serviceKey = serviceName.toLowerCase().replace(' ', '-') as ServiceKey;
</script>

{#if isVisible}
  <div class="loading-overlay">
    <div class="loading-content">
      <div class="lotus-container">
        <svg viewBox="0 0 120 120" class="lotus-svg">
          <defs>
            <!-- Gradients for a soft, painterly look -->
            <linearGradient id="gradOuter" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" style="stop-color:#fbcfe8;" />
              <stop offset="100%" style="stop-color:#be185d;" />
            </linearGradient>
            <linearGradient id="gradMiddle" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" style="stop-color:#fecdd3;" />
              <stop offset="100%" style="stop-color:#e11d48;" />
            </linearGradient>
            <linearGradient id="gradInner" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" style="stop-color:#fed7aa;" />
              <stop offset="100%" style="stop-color:#ea580c;" />
            </linearGradient>
            <radialGradient id="gradCenter">
              <stop offset="0%" stop-color="#fef3c7" />
              <stop offset="100%" stop-color="#f59e0b" />
            </radialGradient>

            <!-- Filter for the ethereal glow -->
            <filter id="softGlow" x="-50%" y="-50%" width="200%" height="200%">
              <feGaussianBlur in="SourceGraphic" stdDeviation="2.5" result="blur" />
              <feMerge>
                <feMergeNode in="blur" />
                <feMergeNode in="SourceGraphic" />
              </feMerge>
            </filter>
            
            <!-- Filter for subtle petal texture -->
            <filter id="petalTexture">
              <feTurbulence type="fractalNoise" baseFrequency="0.7" numOctaves="3" seed="10"/>
              <feDisplacementMap in="SourceGraphic" scale="0.5"/>
            </filter>
          </defs>

          <g style="filter: url(#softGlow);">
            <!-- Petal Layers with texture -->
            <g style="filter: url(#petalTexture);">
              <g class="petal-layer" style="--rotation: 0deg;">
                <path d="M60 100 C 20 90, 25 40, 60 20 C 95 40, 100 90, 60 100" fill="url(#gradOuter)" class="petal petal-outer"/>
              </g>
              <g class="petal-layer" style="--rotation: 60deg;">
                <path d="M60 100 C 20 90, 25 40, 60 20 C 95 40, 100 90, 60 100" fill="url(#gradOuter)" class="petal petal-outer"/>
              </g>
              <g class="petal-layer" style="--rotation: -60deg;">
                <path d="M60 100 C 20 90, 25 40, 60 20 C 95 40, 100 90, 60 100" fill="url(#gradOuter)" class="petal petal-outer"/>
              </g>

              <g class="petal-layer" style="--rotation: 30deg;">
                <path d="M60 90 C 35 85, 40 45, 60 30 C 80 45, 85 85, 60 90" fill="url(#gradMiddle)" class="petal petal-middle" />
              </g>
              <g class="petal-layer" style="--rotation: -30deg;">
                <path d="M60 90 C 35 85, 40 45, 60 30 C 80 45, 85 85, 60 90" fill="url(#gradMiddle)" class="petal petal-middle" />
              </g>
              <g class="petal-layer" style="--rotation: 90deg;">
                <path d="M60 90 C 35 85, 40 45, 60 30 C 80 45, 85 85, 60 90" fill="url(#gradMiddle)" class="petal petal-middle" />
              </g>
              <g class="petal-layer" style="--rotation: -90deg;">
                <path d="M60 90 C 35 85, 40 45, 60 30 C 80 45, 85 85, 60 90" fill="url(#gradMiddle)" class="petal petal-middle" />
              </g>

              <g class="petal-layer" style="--rotation: 0deg;">
                <path d="M60 80 C 50 77, 50 55, 60 40 C 70 55, 70 77, 60 80" fill="url(#gradInner)" class="petal petal-inner" />
              </g>
              <g class="petal-layer" style="--rotation: 45deg;">
                <path d="M60 80 C 50 77, 50 55, 60 40 C 70 55, 70 77, 60 80" fill="url(#gradInner)" class="petal petal-inner" />
              </g>
              <g class="petal-layer" style="--rotation: -45deg;">
                <path d="M60 80 C 50 77, 50 55, 60 40 C 70 55, 70 77, 60 80" fill="url(#gradInner)" class="petal petal-inner" />
              </g>
            </g>
            
            <circle cx="60" cy="60" r="8" class="lotus-center" fill="url(#gradCenter)" />
          </g>
        </svg>
      </div>

      <h2 class="loading-title">{serviceName}</h2>
      <p class="loading-message">{serviceMessages[serviceKey] || 'Preparing your session...'}</p>
    </div>

    <!-- Atmospheric light motes -->
    <div class="mote mote-1"></div>
    <div class="mote mote-2"></div>
    <div class="mote mote-3"></div>
  </div>
{/if}

<style>
  :root {
    --bloom-duration: 6s;
    --sway-duration: 12s;
  }

  .loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: radial-gradient(circle, #fff1f2 0%, #f5f3ff 100%);
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    animation: fadeIn 1.5s ease-out;
  }

  .loading-content {
    text-align: center;
    animation: slideUp 1.2s cubic-bezier(0.23, 1, 0.32, 1);
    z-index: 2;
  }

  .lotus-container {
    transform: perspective(1200px) rotateX(20deg);
    margin-bottom: 1rem;
    animation: gentleSway var(--sway-duration) ease-in-out infinite;
  }

  .lotus-svg {
    width: 220px;
    height: 220px;
  }

  .petal-layer {
    transform-origin: 60px 60px;
    transform: rotate(var(--rotation));
  }
  
  .petal {
    transform-origin: 50% 100%;
    opacity: 0;
    animation: petalBloom var(--bloom-duration) cubic-bezier(0.65, 0, 0.35, 1) forwards;
  }

  .petal-outer { animation-delay: 0.2s; }
  .petal-middle { animation-delay: 1.5s; }
  .petal-inner { animation-delay: 2.8s; }

  .lotus-center {
    transform-origin: 50% 50%;
    opacity: 0;
    animation: 
      centerGlow 4s ease-out 3s forwards,
      centerPulse 6s ease-in-out 7s infinite;
  }

  .loading-title {
    font-size: 1.75rem;
    font-weight: 300;
    color: #4b5563;
    margin-bottom: 0.5rem;
  }

  .loading-message {
    color: #6b7280;
    font-size: 1rem;
    font-weight: 300;
  }

  .mote {
    position: absolute;
    background: #fde68a;
    border-radius: 50%;
    opacity: 0;
    filter: blur(10px);
    animation: floatMote 20s linear infinite;
  }

  .mote-1 {
    width: 80px; height: 80px; top: 20%; left: 15%;
    animation-duration: 25s;
    animation-delay: 0s;
  }
  .mote-2 {
    width: 50px; height: 50px; top: 70%; left: 80%;
    animation-duration: 30s;
    animation-delay: -10s;
  }
  .mote-3 {
    width: 100px; height: 100px; top: 50%; left: 50%;
    animation-duration: 20s;
    animation-delay: -5s;
  }


  @keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
  }

  @keyframes slideUp {
    from { opacity: 0; transform: translateY(30px); }
    to { opacity: 1; transform: translateY(0); }
  }
  
  @keyframes petalBloom {
    0% {
      transform: translateY(25px) scale(0.05) rotateX(-70deg) rotateY(10deg);
      opacity: 0;
    }
    70% {
      transform: translateY(-5px) scale(1.05) rotateX(10deg) rotateY(-2deg);
      opacity: 1;
    }
    100% {
      transform: translateY(0) scale(1) rotateX(0deg) rotateY(0deg);
      opacity: 1;
    }
  }

  @keyframes centerGlow {
    from { transform: scale(0); opacity: 0; }
    to { transform: scale(1); opacity: 1; }
  }

  @keyframes centerPulse {
      0%, 100% { transform: scale(1); }
      50% { transform: scale(1.1); }
  }

  @keyframes gentleSway {
    0%, 100% {
      transform: perspective(1200px) rotateX(20deg) rotateZ(0deg);
    }
    50% {
      transform: perspective(1200px) rotateX(22deg) rotateZ(-1.5deg);
    }
  }

  @keyframes floatMote {
    0% { transform: translate(0, 0); opacity: 0; }
    25% { opacity: 0.1; }
    50% { transform: translate(20px, -40px); }
    75% { opacity: 0.05; }
    100% { transform: translate(-20px, -80px); opacity: 0; }
  }
</style>