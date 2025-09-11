<script lang="ts">
  import { Video, Calendar, Clock, Users } from 'lucide-svelte';
  
  let selectedTimeSlot = '';
  let selectedDoctor = '';
  
  const timeSlots = [
    '09:00 AM - 10:00 AM',
    '10:30 AM - 11:30 AM',
    '02:00 PM - 03:00 PM',
    '03:30 PM - 04:30 PM',
    '05:00 PM - 06:00 PM'
  ];
  
  const doctors = [
    { id: 1, name: 'Dr. Sarah Johnson', specialization: 'Anxiety & Depression', rating: 4.9 },
    { id: 2, name: 'Dr. Michael Chen', specialization: 'PTSD & Trauma', rating: 4.8 },
    { id: 3, name: 'Dr. Emily Davis', specialization: 'Family Therapy', rating: 4.9 },
    { id: 4, name: 'Dr. Robert Wilson', specialization: 'Addiction Counseling', rating: 4.7 }
  ];
</script>

<div class="video-container">
  <div class="container">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="page-title">Video Consultation</h1>
        <p class="page-subtitle">Connect with licensed mental health professionals from the comfort of your home</p>
      </div>
      <div class="hero-icon">
        <Video size={64} />
      </div>
    </div>

    <div class="content-grid">
      <div class="booking-section">
        <div class="section-card">
          <h2><Calendar size={24} /> Schedule Your Session</h2>
          
          <div class="form-group">
            <label>Select a Mental Health Professional</label>
            <div class="doctors-list">
              {#each doctors as doctor}
                <label class="doctor-option">
                  <input 
                    type="radio" 
                    bind:group={selectedDoctor} 
                    value={doctor.name}
                  />
                  <div class="doctor-card">
                    <div class="doctor-info">
                      <h4>{doctor.name}</h4>
                      <p class="specialization">{doctor.specialization}</p>
                      <div class="rating">
                        <span class="stars">⭐⭐⭐⭐⭐</span>
                        <span class="rating-text">{doctor.rating}</span>
                      </div>
                    </div>
                  </div>
                </label>
              {/each}
            </div>
          </div>

          <div class="form-group">
            <label>Available Time Slots</label>
            <div class="time-slots">
              {#each timeSlots as slot}
                <label class="time-slot">
                  <input 
                    type="radio" 
                    bind:group={selectedTimeSlot} 
                    value={slot}
                  />
                  <span class="slot-text">{slot}</span>
                </label>
              {/each}
            </div>
          </div>

          <button 
            class="book-btn"
            disabled={!selectedDoctor || !selectedTimeSlot}
          >
            Book Video Session
          </button>
        </div>
      </div>

      <div class="info-section">
        <div class="section-card">
          <h2><Video size={24} /> How It Works</h2>
          <div class="steps">
            <div class="step">
              <div class="step-number">1</div>
              <div class="step-content">
                <h4>Choose Your Therapist</h4>
                <p>Select from our qualified mental health professionals</p>
              </div>
            </div>
            <div class="step">
              <div class="step-number">2</div>
              <div class="step-content">
                <h4>Schedule Session</h4>
                <p>Pick a time that works best for your schedule</p>
              </div>
            </div>
            <div class="step">
              <div class="step-number">3</div>
              <div class="step-content">
                <h4>Join Video Call</h4>
                <p>Connect securely from any device with internet</p>
              </div>
            </div>
          </div>
        </div>

        <div class="section-card">
          <h2><Users size={24} /> Features</h2>
          <ul class="features-list">
            <li>HD video and crystal-clear audio</li>
            <li>End-to-end encrypted sessions</li>
            <li>Screen sharing capabilities</li>
            <li>Session recording (optional)</li>
            <li>Multi-device compatibility</li>
            <li>Waiting room functionality</li>
          </ul>
        </div>

        <div class="section-card emergency-info">
          <h2>⚠️ Emergency Notice</h2>
          <p>If you're experiencing a mental health emergency, please contact your local emergency services or call a crisis hotline immediately. Video consultations are not appropriate for emergency situations.</p>
          <div class="emergency-numbers">
            <p><strong>Crisis Text Line:</strong> Text HOME to 741741</p>
            <p><strong>National Suicide Prevention Lifeline:</strong> 988</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
  .video-container {
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
    color: #EF4444;
    padding: 2rem;
    background: linear-gradient(135deg, #FEF2F2, #FEE2E2);
    border-radius: 1rem;
  }

  .content-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 3rem;
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

  .form-group {
    margin-bottom: 2rem;
  }

  .form-group label {
    display: block;
    font-weight: 600;
    color: #374151;
    margin-bottom: 1rem;
    font-size: 1.1rem;
  }

  .doctors-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .doctor-option {
    cursor: pointer;
  }

  .doctor-option input[type="radio"] {
    display: none;
  }

  .doctor-card {
    padding: 1.5rem;
    background: white;
    border: 2px solid #E5E7EB;
    border-radius: 0.5rem;
    transition: all 0.3s ease;
  }

  .doctor-option input[type="radio"]:checked + .doctor-card {
    border-color: #EF4444;
    background: #FEF2F2;
  }

  .doctor-info h4 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
  }

  .specialization {
    color: #6B7280;
    font-size: 0.9rem;
    margin-bottom: 0.5rem;
  }

  .rating {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .rating-text {
    font-size: 0.9rem;
    font-weight: 600;
    color: #374151;
  }

  .time-slots {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1rem;
  }

  .time-slot {
    cursor: pointer;
  }

  .time-slot input[type="radio"] {
    display: none;
  }

  .slot-text {
    display: block;
    padding: 1rem;
    background: white;
    border: 2px solid #E5E7EB;
    border-radius: 0.5rem;
    text-align: center;
    font-weight: 500;
    transition: all 0.3s ease;
  }

  .time-slot input[type="radio"]:checked + .slot-text {
    border-color: #EF4444;
    background: #FEF2F2;
    color: #DC2626;
  }

  .book-btn {
    width: 100%;
    padding: 1rem;
    background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
    color: white;
    border: none;
    border-radius: 0.5rem;
    font-size: 1.1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .book-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(239, 68, 68, 0.3);
  }

  .book-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .steps {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .step {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
  }

  .step-number {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: #EF4444;
    color: white;
    border-radius: 50%;
    font-weight: 600;
    flex-shrink: 0;
  }

  .step-content h4 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
  }

  .step-content p {
    color: #6B7280;
    line-height: 1.5;
    margin: 0;
  }

  .features-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .features-list li {
    padding: 0.75rem 0;
    color: #6B7280;
    position: relative;
    padding-left: 2rem;
  }

  .features-list li::before {
    content: "✓";
    position: absolute;
    left: 0;
    color: #10B981;
    font-weight: bold;
  }

  .emergency-info {
    background: linear-gradient(135deg, #FEF3E7 0%, #FFFBEB 100%);
    border-color: #F59E0B;
  }

  .emergency-numbers {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px solid #F59E0B;
  }

  .emergency-numbers p {
    margin: 0.5rem 0;
    color: #92400E;
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

    .time-slots {
      grid-template-columns: 1fr;
    }
  }
</style>