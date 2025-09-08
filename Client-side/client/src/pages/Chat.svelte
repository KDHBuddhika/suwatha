<script lang="ts">
  import { MessageCircle, Send, Phone, Shield } from 'lucide-svelte';
  
  let currentMessage = '';
  let isConnected = false;
  let isConnecting = false;
  
  let messages = [
    {
      id: 1,
      type: 'system',
      text: 'Welcome to Manosara Chat Support. How can we help you today?',
      timestamp: new Date()
    }
  ];
  
  function startChat() {
    isConnecting = true;
    setTimeout(() => {
      isConnecting = false;
      isConnected = true;
      messages = [
        ...messages,
        {
          id: messages.length + 1,
          type: 'counselor',
          text: 'Hello! I\'m Sarah, a licensed mental health counselor. I\'m here to support you. What\'s on your mind today?',
          timestamp: new Date()
        }
      ];
    }, 2000);
  }
  
  function sendMessage() {
    if (!currentMessage.trim()) return;
    
    messages = [
      ...messages,
      {
        id: messages.length + 1,
        type: 'user',
        text: currentMessage,
        timestamp: new Date()
      }
    ];
    
    currentMessage = '';
    
    // Simulate counselor response
    setTimeout(() => {
      const responses = [
        "Thank you for sharing that with me. Can you tell me more about how that makes you feel?",
        "I understand that must be difficult for you. You're not alone in feeling this way.",
        "That sounds really challenging. What support systems do you have in place?",
        "I appreciate your openness. Have you noticed any patterns with these feelings?",
        "It takes courage to reach out. How long have you been experiencing these thoughts?"
      ];
      
      messages = [
        ...messages,
        {
          id: messages.length + 1,
          type: 'counselor',
          text: responses[Math.floor(Math.random() * responses.length)],
          timestamp: new Date()
        }
      ];
    }, 1500);
  }
  
  function formatTime(date: Date) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
</script>

<div class="chat-container">
  <div class="container">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="page-title">Chat Support</h1>
        <p class="page-subtitle">Get immediate mental health support through our secure text-based counseling</p>
      </div>
      <div class="hero-icon">
        <MessageCircle size={64} />
      </div>
    </div>

    <div class="content-grid">
      <div class="chat-section">
        <div class="chat-card">
          <div class="chat-header">
            <div class="status-indicator">
              <div class="status-dot" class:connected={isConnected} class:connecting={isConnecting}></div>
              <span class="status-text">
                {#if isConnecting}
                  Connecting to counselor...
                {:else if isConnected}
                  Connected - Sarah (Licensed Counselor)
                {:else}
                  Chat Support Available 24/7
                {/if}
              </span>
            </div>
            <div class="chat-controls">
              <button class="control-btn">
                <Phone size={18} />
              </button>
            </div>
          </div>

          <div class="chat-messages">
            {#each messages as message (message.id)}
              <div class="message {message.type}">
                <div class="message-content">
                  <p>{message.text}</p>
                  <span class="message-time">{formatTime(message.timestamp)}</span>
                </div>
              </div>
            {/each}
          </div>

          {#if !isConnected}
            <div class="chat-start">
              <button 
                class="start-chat-btn"
                on:click={startChat}
                disabled={isConnecting}
              >
                {#if isConnecting}
                  <span class="spinner"></span>
                  Connecting...
                {:else}
                  Start Chat Session
                {/if}
              </button>
            </div>
          {:else}
            <div class="chat-input">
              <div class="input-container">
                <input 
                  type="text" 
                  bind:value={currentMessage}
                  placeholder="Type your message..."
                  on:keydown={(e) => e.key === 'Enter' && sendMessage()}
                />
                <button 
                  class="send-btn"
                  on:click={sendMessage}
                  disabled={!currentMessage.trim()}
                >
                  <Send size={20} />
                </button>
              </div>
            </div>
          {/if}
        </div>
      </div>

      <div class="info-section">
        <div class="section-card">
          <h2><MessageCircle size={24} /> Chat Features</h2>
          <ul class="features-list">
            <li>Instant connection with licensed counselors</li>
            <li>24/7 availability for mental health support</li>
            <li>Completely anonymous and confidential</li>
            <li>No appointment needed</li>
            <li>Crisis intervention available</li>
            <li>Secure, encrypted messaging</li>
          </ul>
        </div>

        <div class="section-card">
          <h2><Shield size={24} /> Privacy & Security</h2>
          <div class="privacy-info">
            <div class="privacy-item">
              <div class="privacy-icon">🔒</div>
              <div>
                <h4>End-to-End Encryption</h4>
                <p>All messages are encrypted and secure</p>
              </div>
            </div>
            <div class="privacy-item">
              <div class="privacy-icon">🕶️</div>
              <div>
                <h4>Anonymous Support</h4>
                <p>No personal information required to start</p>
              </div>
            </div>
            <div class="privacy-item">
              <div class="privacy-icon">⚡</div>
              <div>
                <h4>Instant Access</h4>
                <p>Connect with counselors immediately</p>
              </div>
            </div>
          </div>
        </div>

        <div class="section-card emergency-info">
          <h2>🚨 Crisis Support</h2>
          <p>If you're in immediate danger or having thoughts of self-harm, please reach out for emergency help:</p>
          <div class="emergency-numbers">
            <p><strong>National Suicide Prevention Lifeline:</strong> 988</p>
            <p><strong>Crisis Text Line:</strong> Text HOME to 741741</p>
            <p><strong>Emergency Services:</strong> 911</p>
          </div>
        </div>

        <div class="section-card">
          <h2>💡 Tips for Effective Chat</h2>
          <ul class="tips-list">
            <li>Be open and honest about your feelings</li>
            <li>Take your time to express yourself</li>
            <li>Ask questions if something isn't clear</li>
            <li>Let the counselor know if you need a break</li>
            <li>Save important insights for later reflection</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
  .chat-container {
    padding: 2rem 0 4rem;
    background: white;
    min-height: 80vh;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  .hero-section {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 3rem;
    padding: 2rem 0;
    border-bottom: 1px solid #E5E7EB;
  }

  .hero-content h1 {
    font-size: 3rem;
    font-weight: 300;
    color: #374151;
    margin-bottom: 1rem;
  }

  .hero-content p {
    font-size: 1.25rem;
    color: #6B7280;
    line-height: 1.6;
    max-width: 500px;
  }

  .hero-icon {
    color: #10B981;
    padding: 2rem;
    background: linear-gradient(135deg, #F0FDF4, #DCFCE7);
    border-radius: 1rem;
  }

  .content-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 3rem;
  }

  .chat-card {
    background: white;
    border: 1px solid #E5E7EB;
    border-radius: 1rem;
    overflow: hidden;
    height: 600px;
    display: flex;
    flex-direction: column;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }

  .chat-header {
    padding: 1rem 1.5rem;
    background: #F8FAFC;
    border-bottom: 1px solid #E5E7EB;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .status-indicator {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .status-dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: #6B7280;
  }

  .status-dot.connecting {
    background: #F59E0B;
    animation: pulse 2s infinite;
  }

  .status-dot.connected {
    background: #10B981;
  }

  .status-text {
    font-weight: 500;
    color: #374151;
  }

  .control-btn {
    background: none;
    border: none;
    color: #6B7280;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 0.5rem;
    transition: all 0.3s ease;
  }

  .control-btn:hover {
    background: #E5E7EB;
    color: #374151;
  }

  .chat-messages {
    flex: 1;
    padding: 1rem 1.5rem;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .message {
    display: flex;
    margin-bottom: 1rem;
  }

  .message.user {
    justify-content: flex-end;
  }

  .message.system {
    justify-content: center;
  }

  .message-content {
    max-width: 80%;
    padding: 1rem;
    border-radius: 1rem;
    position: relative;
  }

  .message.user .message-content {
    background: #10B981;
    color: white;
    border-bottom-right-radius: 0.25rem;
  }

  .message.counselor .message-content {
    background: #F3F4F6;
    color: #374151;
    border-bottom-left-radius: 0.25rem;
  }

  .message.system .message-content {
    background: #FEF3E7;
    color: #92400E;
    max-width: 90%;
    text-align: center;
    border-radius: 0.5rem;
  }

  .message-time {
    font-size: 0.75rem;
    opacity: 0.7;
    display: block;
    margin-top: 0.5rem;
  }

  .chat-start {
    padding: 1.5rem;
    border-top: 1px solid #E5E7EB;
    text-align: center;
  }

  .start-chat-btn {
    width: 100%;
    padding: 1rem;
    background: linear-gradient(135deg, #10B981 0%, #059669 100%);
    color: white;
    border: none;
    border-radius: 0.5rem;
    font-size: 1.1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }

  .start-chat-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(16, 185, 129, 0.3);
  }

  .start-chat-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }

  .chat-input {
    padding: 1rem 1.5rem;
    border-top: 1px solid #E5E7EB;
  }

  .input-container {
    display: flex;
    gap: 0.75rem;
    align-items: center;
  }

  .input-container input {
    flex: 1;
    padding: 0.75rem 1rem;
    border: 2px solid #E5E7EB;
    border-radius: 1.5rem;
    font-size: 1rem;
    transition: all 0.3s ease;
  }

  .input-container input:focus {
    outline: none;
    border-color: #10B981;
  }

  .send-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: #10B981;
    color: white;
    border: none;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .send-btn:hover:not(:disabled) {
    background: #059669;
    transform: scale(1.1);
  }

  .send-btn:disabled {
    background: #D1D5DB;
    cursor: not-allowed;
  }

  .section-card {
    background: #F8FAFC;
    padding: 2rem;
    border-radius: 1rem;
    border: 1px solid #E2E8F0;
    margin-bottom: 2rem;
  }

  .section-card h2 {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 1.5rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 1.5rem;
  }

  .features-list, .tips-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .features-list li, .tips-list li {
    padding: 0.75rem 0;
    color: #6B7280;
    position: relative;
    padding-left: 2rem;
  }

  .features-list li::before, .tips-list li::before {
    content: "✓";
    position: absolute;
    left: 0;
    color: #10B981;
    font-weight: bold;
  }

  .privacy-info {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .privacy-item {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
  }

  .privacy-icon {
    font-size: 1.5rem;
    flex-shrink: 0;
  }

  .privacy-item h4 {
    font-size: 1rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.25rem;
  }

  .privacy-item p {
    color: #6B7280;
    font-size: 0.9rem;
    margin: 0;
  }

  .emergency-info {
    background: linear-gradient(135deg, #FEF2F2 0%, #FECACA 100%);
    border-color: #EF4444;
  }

  .emergency-numbers {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px solid #EF4444;
  }

  .emergency-numbers p {
    margin: 0.5rem 0;
    color: #DC2626;
  }

  .spinner {
    width: 20px;
    height: 20px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top: 2px solid white;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes pulse {
    0%, 100% {
      opacity: 1;
    }
    50% {
      opacity: 0.5;
    }
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  @media (max-width: 768px) {
    .container {
      padding: 0 1rem;
    }

    .hero-section {
      flex-direction: column;
      text-align: center;
      gap: 2rem;
    }

    .hero-content h1 {
      font-size: 2.5rem;
    }

    .content-grid {
      grid-template-columns: 1fr;
      gap: 2rem;
    }

    .chat-card {
      height: 500px;
    }
  }
</style>