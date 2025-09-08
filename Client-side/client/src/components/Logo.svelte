<!-- src/lib/components/Logo.svelte -->
<script lang="ts">
  import { onMount } from 'svelte';
  
  let isAnimating = true;

  onMount(() => {
    // The animation is now much longer, so we update this accordingly.
    setTimeout(() => {
      isAnimating = false;
    }, 6500); 
  });
</script>

<div class="logo-container">
  <div class="lotus-logo" class:animating={isAnimating}>
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
      </defs>

      <g style="filter: url(#softGlow);">
        <!-- Petal Layers -->
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
        
        <circle cx="60" cy="60" r="8" class="lotus-center" fill="url(#gradCenter)" />
      </g>
    </svg>
  </div>
</div>

<style>
  .logo-container {
    display: flex;
    justify-content: center;
    align-items: center;
    /* --- CHANGE: Increased size for bigger impact --- */
    width: 260px;
    height: 260px;
  }

  .lotus-logo {
    width: 100%;
    height: 100%;
    transition: transform 0.5s cubic-bezier(0.23, 1, 0.32, 1);
  }

  .lotus-svg {
    width: 100%;
    height: 100%;
    overflow: visible; 
  }

  .petal-layer {
    transform-origin: 60px 60px;
    transform: rotate(var(--rotation));
  }
  
  .petal, .lotus-center {
    opacity: 0;
    transform-origin: 50% 100%;
  }

  /* --- Animation Trigger --- */
  .animating .petal {
    /* --- CHANGE: Dramatically slowed down the bloom animation --- */
    animation: petalBloom 4s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  }
  
  .animating .lotus-center {
    animation: centerGlow 2.5s ease-out forwards;
  }

  /* --- CHANGE: Stretched out delays for a more graceful sequence --- */
  .animating .petal-outer { animation-delay: 0.2s; }
  .animating .petal-middle { animation-delay: 1.2s; }
  .animating .petal-inner { animation-delay: 2.2s; }
  .animating .lotus-center { animation-delay: 3.0s; }

  /* --- Keyframes --- */
  @keyframes petalBloom {
    0% {
      transform: scale(0.1) translateY(20px) rotate(-20deg);
      opacity: 0;
    }
    70% {
      transform: scale(1.05) translateY(-5px);
      opacity: 1;
    }
    100% {
      transform: scale(1) translateY(0);
      opacity: 1;
    }
  }

  @keyframes centerGlow {
    from {
      transform: scale(0);
      opacity: 0;
    }
    to {
      transform: scale(1);
      opacity: 1;
    }
  }

  .lotus-logo:not(.animating):hover {
    transform: scale(1.1);
  }
</style>