<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { Mic, MicOff, Video, VideoOff, Share2, PhoneOff, User, Clock, BrainCircuit, MessageSquare, Book } from 'lucide-svelte';

  // --- MOCK DATA & STATE ---
  const patientId = 'Patient-e5f6g7h8';

  // Session state
  let isMuted = false;
  let isVideoOff = false;
  let sessionTime = 0;
  let timerInterval: ReturnType<typeof setInterval>;

  // AI Assistant data
  const aiAssistant = {
    sentiment: 'Negative',
    keywords: ['lonely', 'stress', 'sleep', 'anxious'],
    prompts: [
      "Can you tell me more about your sleep patterns?",
      "How does that feeling of loneliness manifest?",
      "What situations tend to trigger your stress?"
    ]
  };

  // --- LIFECYCLE HOOKS ---
  onMount(() => {
    // Start the session timer
    timerInterval = setInterval(() => {
      sessionTime++;
    }, 1000);
  });

  onDestroy(() => {
    // Clean up the interval when the component is destroyed
    clearInterval(timerInterval);
  });

  // --- FORMATTERS & HANDLERS ---
  $: formattedTime = new Date(sessionTime * 1000).toISOString().substr(14, 5);

  function handleEndSession() {
    console.log("Session ended.");
    // Here you would navigate away or update the application state
    alert("Session Ended");
  }

  function handlePromptClick(prompt: string) {
    console.log("Therapist might use prompt:", prompt);
    // You could copy the prompt to the clipboard or integrate it elsewhere
  }
</script>

<div class="session-container">
  <!-- Left Column: Patient Video (60%) -->
  <div class="column patient-video-column">
    <div class="video-feed-large">
      <!-- In a real app, a <video> element would go here -->
      <div class="placeholder-content">
        <User size={96} stroke-width={1} />
        <p>Patient's Video Stream</p>
      </div>
    </div>
    <div class="video-overlay-info">
      <span>{patientId}</span>
    </div>
  </div>

  <!-- Middle Column: Therapist View & Controls -->
  <div class="column therapist-controls-column">
    <div class="therapist-view">
      <div class="video-feed-small">
        <!-- Therapist's own <video> element -->
        <div class="placeholder-content">
          <p>Your Video</p>
        </div>
      </div>
    </div>
    <div class="session-controls">
      <button class="control-btn" on:click={() => isMuted = !isMuted} class:active={isMuted}>
        {#if isMuted}
          <MicOff size={24} />
          <span>Unmute</span>
        {:else}
          <Mic size={24} />
          <span>Mute</span>
        {/if}
      </button>
      <button class="control-btn" on:click={() => isVideoOff = !isVideoOff} class:active={isVideoOff}>
         {#if isVideoOff}
          <VideoOff size={24} />
          <span>Start Video</span>
        {:else}
          <Video size={24} />
          <span>Stop Video</span>
        {/if}
      </button>
      <button class="control-btn">
        <Share2 size={24} />
        <span>Share</span>
      </button>
    </div>
    <div class="end-session-container">
      <button class="end-session-btn" on:click={handleEndSession}>
        <PhoneOff size={20} />
        <span>End Session</span>
      </button>
    </div>
  </div>

  <!-- Right Column: AI Assistant & Info Panel -->
  <div class="column ai-assistant-column">
    <div class="panel-card info-card">
      <div class="panel-header">
        <Clock size={18} />
        <h3>Session Timer</h3>
      </div>
      <div class="timer-display">{formattedTime}</div>
    </div>

    <div class="panel-card ai-card">
      <div class="panel-header">
        <BrainCircuit size={18} />
        <h3>AI Assistant</h3>
      </div>
      <div class="ai-section">
        <label>Live Sentiment</label>
        <p class="sentiment-tag">{aiAssistant.sentiment}</p>
      </div>
      <div class="ai-section">
        <label>Detected Keywords</label>
        <div class="keywords-list">
          {#each aiAssistant.keywords as keyword}
            <span class="keyword-tag">{keyword}</span>
          {/each}
        </div>
      </div>
      <div class="ai-section">
        <label>Suggested Prompts</label>
        <ul class="prompts-list">
          {#each aiAssistant.prompts as prompt}
            <li on:click={() => handlePromptClick(prompt)}>
              <MessageSquare size={16} />
              <span>{prompt}</span>
            </li>
          {/each}
        </ul>
      </div>
    </div>

     <div class="panel-card notes-card">
      <div class="panel-header">
        <Book size={18} />
        <h3>Private Notes</h3>
      </div>
      <textarea placeholder="Your scratchpad (not saved)..."></textarea>
    </div>
  </div>
</div>

<style>
  /* :global(body) {
    overflow: hidden; /* Prevent scrolling during video session */
    /* } */
    
  .session-container {
    display: grid;
    grid-template-columns: 6fr 1fr 3fr; /* 60% | 10% | 30% approx */
    height: 100vh;
    width: 100%;
    background-color: #111827; /* Dark background */
    color: #F9FAFB;
    gap: 1rem;
    padding: 1rem;
    box-sizing: border-box;
  }

  .column {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  
  /* --- Left Column --- */
  .patient-video-column {
    position: relative;
  }
  .video-feed-large {
    background-color: #000;
    border-radius: 1rem;
    flex-grow: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #374151;
  }
  .placeholder-content {
    color: #6B7280;
    text-align: center;
  }
  .placeholder-content p {
    margin-top: 1rem;
    font-size: 1.25rem;
  }
  .video-overlay-info {
    position: absolute;
    bottom: 1rem;
    left: 1rem;
    background-color: rgba(0, 0, 0, 0.6);
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    font-size: 0.875rem;
  }

  /* --- Middle Column --- */
  .therapist-controls-column {
    justify-content: space-between;
  }
  .video-feed-small {
    background-color: #000;
    border-radius: 1rem;
    width: 100%;
    aspect-ratio: 4 / 3;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #374151;
  }
  .session-controls {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }
  .control-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.25rem;
    background: #374151;
    color: #F9FAFB;
    border: none;
    padding: 0.75rem;
    border-radius: 50%;
    width: 70px;
    height: 70px;
    justify-content: center;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }
  .control-btn:hover {
    background: #4B5563;
  }
  .control-btn span {
    font-size: 0.75rem;
  }
  .control-btn.active {
    background-color: #DC2626;
  }

  .end-session-container {
    padding-bottom: 1rem;
  }
  .end-session-btn {
    width: 100%;
    background-color: #DC2626;
    color: white;
    border: none;
    border-radius: 0.5rem;
    padding: 1rem;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    transition: background-color 0.2s ease;
  }
  .end-session-btn:hover {
    background-color: #B91C1C;
  }

  /* --- Right Column --- */
  .ai-assistant-column {
    background-color: #1F2937;
    border-radius: 1rem;
    padding: 1.5rem;
    overflow-y: auto;
  }
  .panel-card {
    background-color: #374151;
    border-radius: 0.75rem;
    padding: 1rem;
  }
  .panel-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #D1D5DB;
    margin-bottom: 1rem;
  }
  .panel-header h3 {
    margin: 0;
    font-size: 1rem;
    font-weight: 600;
  }
  .timer-display {
    font-size: 2.5rem;
    font-weight: 700;
    color: white;
    text-align: center;
  }
  
  .ai-card .ai-section {
    margin-bottom: 1.25rem;
  }
  .ai-card label {
    display: block;
    font-size: 0.75rem;
    color: #9CA3AF;
    margin-bottom: 0.5rem;
    text-transform: uppercase;
    font-weight: 500;
  }
  
  .sentiment-tag {
    background-color: #FEF2F2;
    color: #B91C1C;
    padding: 0.25rem 0.75rem;
    border-radius: 1rem;
    font-weight: 600;
    display: inline-block;
  }

  .keywords-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
  }
  .keyword-tag {
    background-color: #4B5563;
    color: #E5E7EB;
    padding: 0.25rem 0.75rem;
    border-radius: 0.5rem;
    font-size: 0.875rem;
  }

  .prompts-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
  .prompts-list li {
    background-color: #4B5563;
    padding: 0.75rem;
    border-radius: 0.5rem;
    cursor: pointer;
    font-size: 0.875rem;
    transition: background-color 0.2s ease;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  .prompts-list li:hover {
    background-color: #6B7280;
  }
  
  .notes-card textarea {
    width: 100%;
    min-height: 120px;
    background-color: #4B5563;
    border: 1px solid #6B7280;
    border-radius: 0.5rem;
    color: #F9FAFB;
    padding: 0.75rem;
    resize: vertical;
    font-family: inherit;
    font-size: 0.875rem;
  }
  .notes-card textarea:focus {
    outline: none;
    border-color: #4F46E5;
  }
</style>