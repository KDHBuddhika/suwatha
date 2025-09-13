<script lang="ts">
  // --- 1. ADDED IMPORTS ---
  import { onMount, onDestroy } from 'svelte';
  import { authStore } from '../../stores/auth'; // Ensure this path is correct

  // --- ICONS AND COMPONENTS ---
  import { Send, Share2, PhoneOff, Clock, BrainCircuit, MessageSquare, Book } from 'lucide-svelte';

  // --- 2. NEW STATE VARIABLES FOR FEATURE CONTROL ---
  let featuresEnabled = false; // Controls visibility of advanced features. Default to false.
  let isCheckingFeatures = true; // Shows a loading state until the check is complete.

  // --- EXISTING STATE & MOCK DATA ---
  const patientId = 'Patient-a1b2c3d4';
  let chatHistory = [
    { sender: 'Patient', text: 'Hi Dr. Johnson, thanks for seeing me.', time: '10:01 AM' },
    { sender: 'Therapist', text: 'Of course. How have you been since our last session?', time: '10:01 AM' },
    { sender: 'Patient', text: 'Not great, to be honest. My anxiety has been really high this week.', time: '10:02 AM' },
    { sender: 'Patient', text: 'I\'ve been having trouble with sleep again, just like we talked about.', time: '10:03 AM' },
  ];
  const aiAssistant = {
    sentiment: 'Negative',
    keywords: ['anxiety', 'stress', 'sleep'],
    prompts: [
      "Can you describe what the anxiety feels like?",
      "What have you tried so far to manage your sleep?",
      "Has anything specific triggered this increase in anxiety?"
    ]
  };
  let sessionFormData = { city: '', illness: '', gender: '', age: null, level: '', privateNotes: '' };
  const illnessOptions = ['General Anxiety', 'Depression', 'PTSD', 'OCD', 'Bipolar Disorder'];
  const genderOptions = ['Male', 'Female', 'Non-Binary', 'Prefer Not to Say'];
  const levelOptions = ['Low', 'Moderate', 'High Risk'];
  const CITIES = ['Colombo', 'Kaluthara', 'Kandy', 'Galle', 'Jaffna', 'Negombo', 'Matara'];
  let citySuggestions: string[] = [];

  let sessionTime = 0;
  let timerInterval: ReturnType<typeof setInterval>;
  let newMessage = '';
  let chatContainer: HTMLElement;

  // --- 3. API CALL IN onMount ---
  onMount(async () => {
    // Start session timer and lock scroll immediately
    timerInterval = setInterval(() => sessionTime++, 1000);
    document.body.style.overflow = 'hidden';
    scrollToBottom();

    // Get auth token from the store
    let authState;
    const unsubscribe = authStore.subscribe(s => { authState = s; });
    unsubscribe();

    if (!authState?.token) {
      console.error("Authentication token not found.");
      isCheckingFeatures = false; // Stop loading, features will remain disabled
      return;
    }

    // Check the feature enablement endpoint
    try {
      const response = await fetch('http://localhost:8090/api/doctor/enable', {
        headers: {
          'Authorization': `Bearer ${authState.token}`
        }
      });
      
      if (response.ok) {
        const hasAccess = await response.json();
        if (hasAccess === true) {
          featuresEnabled = true; // Enable all features if response is true
        }
      }
      // If response is not ok or not true, featuresEnabled remains false
    } catch (error) {
      console.error("Error checking feature enablement:", error);
      // featuresEnabled will remain false
    } finally {
      isCheckingFeatures = false; // We are done checking, show the UI
    }
  });

  onDestroy(() => {
    clearInterval(timerInterval);
    document.body.style.overflow = 'auto';
  });

  // --- EXISTING FUNCTIONS (unchanged) ---
  $: formattedTime = new Date(sessionTime * 1000).toISOString().substr(14, 5);
  $: {
    if (sessionFormData.city && sessionFormData.city.length > 1) {
      citySuggestions = CITIES.filter(c => c.toLowerCase().startsWith(sessionFormData.city.toLowerCase()));
    } else {
      citySuggestions = [];
    }
  }
  function selectCity(city: string) {
    sessionFormData.city = city;
    citySuggestions = [];
  }
  function handleSubmitSessionDetails() {
    if (!sessionFormData.level) { alert('Risk Level is a required field.'); return; }
    console.log("Submitting Session Details:", sessionFormData);
    alert("Session details have been submitted!");
    sessionFormData = { city: '', illness: '', gender: '', age: null, level: '', privateNotes: '' };
  }
  function scrollToBottom() {
    setTimeout(() => { if (chatContainer) { chatContainer.scrollTop = chatContainer.scrollHeight; } }, 0);
  }
  function handleSendMessage() {
    if (newMessage.trim() === '') return;
    const currentTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    chatHistory = [...chatHistory, { sender: 'Therapist', text: newMessage.trim(), time: currentTime }];
    newMessage = '';
    scrollToBottom();
  }
  function handleEndSession() { alert("Chat Session Ended"); }
</script>

<!-- 4. The grid layout is now dynamic based on whether features are enabled -->
<div class="session-container" style="grid-template-columns: {featuresEnabled ? '6fr 1fr 3fr' : '8fr 1fr'};">

  {#if isCheckingFeatures}
    <div class="loading-screen">
      <p>Initializing Secure Chat...</p>
    </div>
  {:else}
    <!-- Left Column: Chat Interface (Always visible) -->
    <div class="column chat-column">
      <div class="chat-display-area" bind:this={chatContainer}>
        {#each chatHistory as message}
          <div class="message-wrapper" class:therapist-wrapper={message.sender === 'Therapist'}>
            <div class="message-bubble" class:patient={message.sender === 'Patient'} class:therapist={message.sender === 'Therapist'}>
              <p class="message-text">{message.text}</p>
              <span class="message-time">{message.time}</span>
            </div>
          </div>
        {/each}
      </div>
      <form class="chat-input-area" on:submit|preventDefault={handleSendMessage}>
        <textarea 
          placeholder="Type your message..." 
          bind:value={newMessage}
          on:keydown={(e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); handleSendMessage(); } }}
        ></textarea>
        <button type="submit"><Send size={20} /></button>
      </form>
    </div>

    <!-- Middle Column: Controls (Always visible) -->
    <div class="column controls-column">
      <div class="therapist-info-card">
        <h4>You</h4>
        <!-- This name could also be dynamic from the authStore -->
        <p>{$authStore.user?.name || 'Therapist'}</p>
      </div>
      <div class="session-controls">
        <button class="control-btn"><Share2 size={24} /><span>Share</span></button>
      </div>
      <div class="end-session-container">
        <button class="end-session-btn" on:click={handleEndSession}><PhoneOff size={20} /><span>End Session</span></button>
      </div>
    </div>

    <!-- 5. Right Column: AI Assistant & Info Panel (Conditionally rendered) -->
    {#if featuresEnabled}
      <div class="column ai-assistant-column">
        <div class="panel-card info-card">
          <div class="panel-header"><Clock size={18} /><h3>Session Timer</h3></div>
          <div class="timer-display">{formattedTime}</div>
        </div>
        <div class="panel-card ai-card">
          <div class="panel-header"><BrainCircuit size={18} /><h3>AI Assistant</h3></div>
          <div class="ai-section"><label>Live Sentiment</label><p class="sentiment-tag">{aiAssistant.sentiment}</p></div>
          <div class="ai-section"><label>Detected Keywords</label><div class="keywords-list">{#each aiAssistant.keywords as k}<span class="keyword-tag">{k}</span>{/each}</div></div>
          <div class="ai-section"><label>Suggested Prompts</label><ul class="prompts-list">{#each aiAssistant.prompts as p}<li><MessageSquare size={16} /><span>{p}</span></li>{/each}</ul></div>
        </div>
        <div class="panel-card notes-card">
          <div class="panel-header"><Book size={18} /><h3>Private Notes</h3></div>
          <textarea placeholder="Your scratchpad (not saved)..."></textarea>
        </div>
        <div class="panel-card session-form-card">
          <div class="panel-header"><MessageSquare size={18} /><h3>Session Details Form</h3></div>
          <form class="session-form" on:submit|preventDefault={handleSubmitSessionDetails}>
            <div class="form-group city-group">
              <label for="city">City</label>
              <input type="text" id="city" placeholder="Type to search..." bind:value={sessionFormData.city} autocomplete="off" />
              {#if citySuggestions.length > 0}<ul class="autocomplete-suggestions">{#each citySuggestions as city}<li on:click={() => selectCity(city)}>{city}</li>{/each}</ul>{/if}
            </div>
            <div class="form-group"><label for="illness">Illness</label><select id="illness" bind:value={sessionFormData.illness}><option value="" disabled>Select an option</option>{#each illnessOptions as option}<option value={option}>{option}</option>{/each}</select></div>
            <div class="form-group-inline">
              <div class="form-group"><label for="gender">Gender</label><select id="gender" bind:value={sessionFormData.gender}><option value="" disabled>Select...</option>{#each genderOptions as option}<option value={option}>{option}</option>{/each}</select></div>
              <div class="form-group"><label for="age">Age</label><input type="number" id="age" bind:value={sessionFormData.age} placeholder="e.g., 34"/></div>
            </div>
            <div class="form-group"><label>Level</label><div class="radio-group">{#each levelOptions as level}<label class="radio-label" class:checked={sessionFormData.level === level}><input type="radio" name="level" value={level} bind:group={sessionFormData.level} required />{level}</label>{/each}</div></div>
            <div class="form-group"><label for="private-notes">Therapist Private Note</label><textarea id="private-notes" bind:value={sessionFormData.privateNotes} rows="4"></textarea></div>
            <button type="submit" class="submit-btn"><Send size={16}/>Submit Details</button>
          </form>
        </div>
      </div>
    {/if}
  {/if}
</div>

<style>
 /* ==========================================================================
   1. Base Layout & Grid System
   ========================================================================== */

.session-container {
  display: grid;
  grid-template-columns: 6fr 1fr 3fr; /* Main layout columns */
  height: 90vh;
  width: 100%;
  background-color: #111827; /* Dark page background */
  color: #F9FAFB; /* Default text color */
  gap: 1rem;
  padding: 1rem;
  box-sizing: border-box;
  
}

.column {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}


/* ==========================================================================
   2. Left Column: Chat Area
   ========================================================================== */

.chat-column {
  background-color: #1F2937; /* Column background */
  border-radius: 1rem;
  padding: 1rem;
}

/* --- Chat Message Display --- */
.chat-display-area {
  flex-grow: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message-wrapper {
  display: flex;
  width: 100%;
}

.message-wrapper.therapist-wrapper {
  justify-content: flex-end; /* Align therapist messages to the right */
}

.message-bubble {
  max-width: 75%;
  padding: 0.75rem 1rem;
  border-radius: 1rem;
  line-height: 1.5;
}

.message-bubble.patient {
  background-color: #374151;
  border-bottom-left-radius: 0.25rem; /* "Tailed" bubble effect */
}

.message-bubble.therapist {
  background-color: #4F46E5; /* Accent color for therapist */
  border-bottom-right-radius: 0.25rem; /* "Tailed" bubble effect */
}

.message-text {
  margin: 0;
}

.message-time {
  display: block;
  text-align: right;
  font-size: 0.75rem;
  color: #9CA3AF;
  margin-top: 0.25rem;
}

/* --- Chat Input Area --- */
.chat-input-area {
  display: flex;
  gap: 0.5rem;
  border-top: 1px solid #374151;
  padding-top: 1rem;
}

.chat-input-area textarea {
  flex-grow: 1;
  background-color: #374151;
  color: #F9FAFB;
  border: 1px solid #4B5563;
  border-radius: 0.5rem;
  padding: 0.75rem;
  resize: none;
  font-family: inherit;
  font-size: 1rem;
}

.chat-input-area textarea:focus {
  outline: none;
  border-color: #4F46E5;
}

.chat-input-area button {
  background-color: #4F46E5;
  color: white;
  border: none;
  border-radius: 0.5rem;
  padding: 0 1rem;
  cursor: pointer;
}


/* ==========================================================================
   3. Middle Column: Controls & Information
   ========================================================================== */

.controls-column {
  justify-content: space-between;
}

/* --- Therapist Info Card --- */
.therapist-info-card {
  background-color: #1F2937;
  padding: 1rem;
  border-radius: 1rem;
  text-align: center;
}

.therapist-info-card h4,
.therapist-info-card p {
  margin: 0;
}

.therapist-info-card p {
  font-size: 0.875rem;
  color: #9CA3AF;
  margin-top: 0.25rem;
}

/* --- Session Control Buttons --- */
.session-controls {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.control-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  background: #374151;
  color: #F9FAFB;
  border: none;
  padding: 0.75rem;
  border-radius: 50%;
  width: 70px;
  height: 70px;
  cursor: pointer;
}

.control-btn:hover {
  background: #4B5563;
}

.control-btn span {
  font-size: 0.75rem;
}

.end-session-btn {
  width: 100%;
  background-color: #DC2626; /* Destructive action color */
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
}

.end-session-btn:hover {
  background-color: #B91C1C;
}


/* ==========================================================================
   4. Right Column: AI Assistant & Panels
   ========================================================================== */

.ai-assistant-column {
  background-color: #1F2937;
  border-radius: 1rem;
  padding: 1.5rem;
  overflow-y: auto;
}

/* --- General Panel Styles --- */
.panel-card {
  background-color: #374151;
  border-radius: 0.75rem;
  padding: 1rem;
  margin-bottom: 1rem; /* Added for spacing between cards */
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

/* --- Specific Panel Cards --- */
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
  display: flex;
  align-items: center;
  gap: 0.5rem;
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
}



/* --- NEW STYLES FOR THE SESSION DETAILS FORM --- */
  .session-form-card {
    background-color: #374151; /* A slightly different shade to stand out */
  }
  .session-form {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
  .form-group-inline {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
  }
  .session-form label {
    font-size: 0.875rem;
    font-weight: 500;
    color: #9CA3AF;
  }
  .session-form input,
  .session-form select,
  .session-form textarea {
    background-color: #1F2937;
    color: #E5E7EB;
    border: 1px solid #4B5563;
    border-radius: 0.5rem;
    padding: 0.75rem;
    font-size: 0.9rem;
    font-family: inherit;
  }
  .session-form input:focus,
  .session-form select:focus,
  .session-form textarea:focus {
    outline: none;
    border-color: #6366F1;
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.4);
  }
  .session-form textarea {
    resize: vertical;
  }

  /* City Autocomplete */
  .city-group {
    position: relative;
  }
  .autocomplete-suggestions {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background-color: #374151;
    border: 1px solid #4B5563;
    border-radius: 0.5rem;
    list-style: none;
    padding: 0.5rem;
    margin-top: 0.25rem;
    z-index: 20;
    max-height: 150px;
    overflow-y: auto;
  }
  .autocomplete-suggestions li {
    padding: 0.5rem 0.75rem;
    border-radius: 0.375rem;
    cursor: pointer;
  }
  .autocomplete-suggestions li:hover {
    background-color: #4F46E5;
  }

  /* Custom Radio Buttons */
  .radio-group {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 0.5rem;
  }
  .radio-label {
    display: block;
    padding: 0.5rem;
    border: 2px solid #4B5563;
    border-radius: 0.5rem;
    text-align: center;
    cursor: pointer;
    font-weight: 500;
    font-size: 0.875rem;
    transition: all 0.2s ease;
  }
  .radio-label:hover { border-color: #6B7280; }
  .radio-label.checked { background-color: #4F46E5; border-color: #4F46E5; color: white; }
  .radio-label input[type="radio"] { display: none; }
  
  .submit-btn {
    background-color: #4F46E5;
    color: white;
    font-weight: 600;
    border: none;
    border-radius: 0.5rem;
    padding: 0.75rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    margin-top: 0.5rem;
  }
  .submit-btn:hover {
    background-color: #4338CA;
  }
</style>