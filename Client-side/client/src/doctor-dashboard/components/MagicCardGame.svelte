<script lang="ts">
  import { onMount } from 'svelte';

  // --- CONFIGURATION & STATE ---
  const ALL_PROMPTS = [
    "Trust", "Freedom", "Fear", "A New Beginning", "Overcoming Obstacles", 
    "Family", "Hope", "Change", "Strength", "Letting Go", "Gratitude", 
    "Self-Compassion", "A Past Success", "Your Happy Place", "Resilience"
  ];

  type Card = {
    id: number;
    prompt: string;
    isFlipped: boolean;
  };

  let cards: Card[] = [];
  let selectedCardId: number | null = null;
  let currentReflection = '';
  let savedReflections: { [prompt: string]: string } = {};

  // --- LIFECYCLE & INITIALIZATION ---
  onMount(() => {
    // Load reflections from browser's local storage
    const stored = localStorage.getItem('magicCardReflections');
    if (stored) {
      savedReflections = JSON.parse(stored);
    }
    // Set up the initial game state
    shuffleAndDeal();
  });

  // --- CORE FUNCTIONS ---
  function shuffleAndDeal() {
    // Shuffle the master list of prompts
    const shuffledPrompts = [...ALL_PROMPTS].sort(() => 0.5 - Math.random());
    
    // Create 10 new card objects
    cards = Array.from({ length: 10 }, (_, i) => ({
      id: i,
      prompt: shuffledPrompts[i],
      isFlipped: false
    }));

    // Reset the UI state
    selectedCardId = null;
    currentReflection = '';
  }

  function handleCardClick(clickedCard: Card) {
    // If we're re-clicking the currently selected card, deselect it
    if (selectedCardId === clickedCard.id) {
      selectedCardId = null;
      clickedCard.isFlipped = false;
      return;
    }

    // Flip all other cards down
    cards.forEach(card => card.isFlipped = false);

    // Flip the selected card up
    clickedCard.isFlipped = true;
    selectedCardId = clickedCard.id;
    
    // Load any saved reflection for this prompt
    currentReflection = savedReflections[clickedCard.prompt] || '';
  }

  function saveReflection() {
    if (selectedCardId === null) return;
    
    const activePrompt = cards.find(c => c.id === selectedCardId)?.prompt;
    if (activePrompt) {
      savedReflections[activePrompt] = currentReflection;
      // Persist to local storage
      localStorage.setItem('magicCardReflections', JSON.stringify(savedReflections));
      alert('Reflection Saved!');
    }
  }
</script>

<div class="game-container">
  <div class="cards-grid">
    {#each cards as card (card.id)}
      <div class="card-container">
        <div class="card-flipper" class:flipped={card.isFlipped}>
          <!-- Card Back -->
          <button class="card-face card-back" on:click={() => handleCardClick(card)}>
            <div class="card-back-design"></div>
          </button>
          <!-- Card Front -->
          <div class="card-face card-front">
            <p>{card.prompt}</p>
          </div>
        </div>

        <!-- Reflection Area (only shows for the selected card) -->
        {#if selectedCardId === card.id}
          <div class="reflection-area">
            <textarea 
              placeholder="Write your reflections here..."
              bind:value={currentReflection}
            ></textarea>
            <button class="save-btn" on:click={saveReflection}>Save Reflection</button>
          </div>
        {/if}
      </div>
    {/each}
  </div>

  <div class="controls">
    <button class="reset-btn" on:click={shuffleAndDeal}>Shuffle & Reset</button>
  </div>
</div>

<style>
  .game-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 2rem;
    padding: 1rem;
    box-sizing: border-box;
  }
  .cards-grid {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 1.5rem;
    perspective: 1000px;
  }
  .card-container {
    width: 150px;
    height: 220px;
  }
  .card-flipper {
    width: 100%;
    height: 100%;
    position: relative;
    transform-style: preserve-3d;
    transition: transform 0.7s cubic-bezier(0.4, 0.2, 0.2, 1);
  }
  .card-flipper.flipped {
    transform: rotateY(180deg);
  }
  .card-face {
    position: absolute;
    width: 100%;
    height: 100%;
    backface-visibility: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  }
  .card-back {
    background: #4A5568;
    border: 2px solid #718096;
    cursor: pointer;
  }
  .card-back-design {
    width: 60%;
    height: 60%;
    border: 3px dashed rgba(255,255,255,0.3);
    border-radius: 50%;
  }
  .card-front {
    background: #EBF4FF;
    color: #2C5282;
    transform: rotateY(180deg);
    font-size: 1.25rem;
    text-align: center;
    font-weight: 600;
    padding: 1rem;
  }
  .reflection-area {
    margin-top: 1rem;
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    animation: fade-in 0.5s ease;
  }
  @keyframes fade-in {
    from { opacity: 0; transform: translateY(-10px); }
    to { opacity: 1; transform: translateY(0); }
  }
  textarea {
    background-color: #374151;
    border: 1px solid #4B5563;
    border-radius: 8px;
    color: #E5E7EB;
    padding: 0.75rem;
    font-family: inherit;
    font-size: 0.9rem;
    min-height: 100px;
    resize: none;
  }
  .save-btn, .reset-btn {
    padding: 0.75rem 1rem;
    border-radius: 8px;
    border: none;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  .save-btn {
    background-color: #6366F1;
    color: white;
  }
  .save-btn:hover { background-color: #4F46E5; }
  .reset-btn {
    background-color: #374151;
    color: white;
  }
  .reset-btn:hover { background-color: #4B5563; }

  @media (max-width: 600px) {
    .card-container { width: 120px; height: 180px; }
    .card-front { font-size: 1rem; }
  }
</style>