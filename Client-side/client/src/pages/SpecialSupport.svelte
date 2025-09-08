<script lang="ts">
  import { Ear, Video, MessageCircle, Users, Hand } from 'lucide-svelte';
</script>

<div class="special-container">
  <div class="container">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="page-title">Special Support Services</h1>
        <p class="page-subtitle">Comprehensive mental health care designed for the deaf and hard-of-hearing community</p>
      </div>
      <div class="hero-icon">
        <Ear size={64} />
      </div>
    </div>

    <div class="content-sections">
      <div class="services-grid">
        <div class="service-card primary">
          <div class="service-icon">
            <Hand size={48} />
          </div>
          <h3>ASL Video Counseling</h3>
          <p>Connect with licensed mental health professionals who are fluent in American Sign Language</p>
          <button class="service-btn">Schedule ASL Session</button>
        </div>

        <div class="service-card">
          <div class="service-icon">
            <MessageCircle size={48} />
          </div>
          <h3>Text-Based Therapy</h3>
          <p>Real-time text communication with specialized counselors trained in deaf culture and community</p>
          <button class="service-btn">Start Text Chat</button>
        </div>

        <div class="service-card">
          <div class="service-icon">
            <Video size={48} />
          </div>
          <h3>Video Relay Services</h3>
          <p>Professional interpreters facilitate communication between you and mental health professionals</p>
          <button class="service-btn">Request Interpreter</button>
        </div>

        <div class="service-card">
          <div class="service-icon">
            <Users size={48} />
          </div>
          <h3>Community Support Groups</h3>
          <p>Join online support groups specifically for deaf and hard-of-hearing individuals</p>
          <button class="service-btn">Join Group</button>
        </div>
      </div>

      <div class="info-sections">
        <div class="section-card">
          <h2>🤟 Accessibility Features</h2>
          <div class="features-grid">
            <div class="feature-item">
              <div class="feature-icon">👁️</div>
              <div>
                <h4>Visual Alerts</h4>
                <p>Screen flashing and visual notifications for all platform alerts</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">📱</div>
              <div>
                <h4>TTY/TDD Compatible</h4>
                <p>Full compatibility with text telephone devices</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🔤</div>
              <div>
                <h4>Closed Captioning</h4>
                <p>Real-time captions for all video content and sessions</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">⚡</div>
              <div>
                <h4>High Contrast Mode</h4>
                <p>Enhanced visual accessibility with customizable display options</p>
              </div>
            </div>
          </div>
        </div>

        <div class="section-card">
          <h2>👥 Culturally Deaf Counselors</h2>
          <p>Our team includes mental health professionals who are members of the deaf community and understand the unique cultural, linguistic, and social aspects of deaf identity.</p>
          
          <div class="counselor-features">
            <div class="counselor-item">
              <span class="check-icon">✓</span>
              <span>Native ASL fluency and cultural competency</span>
            </div>
            <div class="counselor-item">
              <span class="check-icon">✓</span>
              <span>Specialized training in deaf mental health issues</span>
            </div>
            <div class="counselor-item">
              <span class="check-icon">✓</span>
              <span>Understanding of deaf community experiences</span>
            </div>
            <div class="counselor-item">
              <span class="check-icon">✓</span>
              <span>Experience with interpreting services coordination</span>
            </div>
          </div>
        </div>

        <div class="section-card">
          <h2>🔧 Technical Support</h2>
          <p>We provide comprehensive technical assistance to ensure you can fully access our services:</p>
          
          <div class="support-options">
            <div class="support-option">
              <h4>24/7 Text Support</h4>
              <p>Get immediate help via text chat or email</p>
            </div>
            <div class="support-option">
              <h4>Video Call Assistance</h4>
              <p>ASL-fluent tech support via video call</p>
            </div>
            <div class="support-option">
              <h4>Platform Training</h4>
              <p>Free tutorials and training sessions for using our services</p>
            </div>
          </div>
        </div>

        <div class="section-card community-info">
          <h2>🌟 Community Resources</h2>
          <p>Access additional resources and support within the deaf and hard-of-hearing community:</p>
          
          <div class="resource-links">
            <div class="resource-item">
              <h4>Deaf Mental Health Alliance</h4>
              <p>Educational resources and community support</p>
              <a href="#" class="resource-link">Learn More</a>
            </div>
            <div class="resource-item">
              <h4>ASL Mental Health Glossary</h4>
              <p>Mental health terms and concepts in ASL</p>
              <a href="#" class="resource-link">View Glossary</a>
            </div>
            <div class="resource-item">
              <h4>Crisis Text Line for Deaf</h4>
              <p>Specialized crisis support for the deaf community</p>
              <a href="#" class="resource-link">Get Help</a>
            </div>
          </div>
        </div>

        <div class="section-card emergency-info">
          <h2>🚨 Emergency Resources</h2>
          <p>If you're experiencing a mental health crisis:</p>
          <div class="emergency-options">
            <div class="emergency-option">
              <h4>Text 911</h4>
              <p>In areas where available, text 911 for emergency services</p>
            </div>
            <div class="emergency-option">
              <h4>Deaf Crisis Line</h4>
              <p>VP: 321-800-3323 | Text: HAND to 741741</p>
            </div>
            <div class="emergency-option">
              <h4>National Suicide Prevention Lifeline</h4>
              <p>Chat online at suicidepreventionlifeline.org</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
  .special-container {
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
    color: #8B5CF6;
    padding: 2rem;
    background: linear-gradient(135deg, #F5F3FF, #EDE9FE);
    border-radius: 1rem;
  }

  .content-sections {
    display: flex;
    flex-direction: column;
    gap: 3rem;
  }

  .services-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 2rem;
    margin-bottom: 2rem;
  }

  .service-card {
    background: white;
    border: 2px solid #E5E7EB;
    border-radius: 1rem;
    padding: 2rem;
    text-align: center;
    transition: all 0.3s ease;
    cursor: pointer;
  }

  .service-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }

  .service-card.primary {
    border-color: #8B5CF6;
    background: linear-gradient(135deg, #F5F3FF 0%, #EDE9FE 100%);
  }

  .service-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 80px;
    height: 80px;
    border-radius: 1rem;
    margin: 0 auto 1.5rem;
    background: linear-gradient(135deg, #8B5CF6, #A855F7);
    color: white;
  }

  .service-card h3 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 1rem;
  }

  .service-card p {
    color: #6B7280;
    line-height: 1.6;
    margin-bottom: 2rem;
  }

  .service-btn {
    background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%);
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 2rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    width: 100%;
  }

  .service-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(139, 92, 246, 0.3);
  }

  .section-card {
    background: #F8FAFC;
    padding: 2rem;
    border-radius: 1rem;
    border: 1px solid #E2E8F0;
    margin-bottom: 2rem;
  }

  .section-card h2 {
    font-size: 1.75rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 1.5rem;
  }

  .section-card p {
    color: #6B7280;
    line-height: 1.7;
    margin-bottom: 1.5rem;
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
    margin-top: 2rem;
  }

  .feature-item {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
  }

  .feature-icon {
    font-size: 1.5rem;
    flex-shrink: 0;
  }

  .feature-item h4 {
    font-size: 1rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
  }

  .feature-item p {
    color: #6B7280;
    font-size: 0.9rem;
    line-height: 1.5;
    margin: 0;
  }

  .counselor-features {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-top: 1.5rem;
  }

  .counselor-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    color: #6B7280;
  }

  .check-icon {
    color: #10B981;
    font-weight: bold;
    font-size: 1.2rem;
  }

  .support-options {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1.5rem;
    margin-top: 1.5rem;
  }

  .support-option {
    padding: 1.5rem;
    background: white;
    border-radius: 0.5rem;
    border: 1px solid #E5E7EB;
  }

  .support-option h4 {
    color: #374151;
    font-weight: 600;
    margin-bottom: 0.5rem;
  }

  .support-option p {
    color: #6B7280;
    font-size: 0.9rem;
    margin: 0;
    line-height: 1.5;
  }

  .community-info {
    background: linear-gradient(135deg, #F0F9F4 0%, #DCFCE7 100%);
    border-color: #10B981;
  }

  .resource-links {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    margin-top: 1.5rem;
  }

  .resource-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 1rem;
    background: white;
    border-radius: 0.5rem;
    border: 1px solid #D1FAE5;
  }

  .resource-item h4 {
    color: #374151;
    font-weight: 600;
    margin-bottom: 0.25rem;
  }

  .resource-item p {
    color: #6B7280;
    font-size: 0.9rem;
    margin: 0;
  }

  .resource-link {
    color: #10B981;
    text-decoration: none;
    font-weight: 500;
    flex-shrink: 0;
    margin-left: 1rem;
  }

  .resource-link:hover {
    text-decoration: underline;
  }

  .emergency-info {
    background: linear-gradient(135deg, #FEF2F2 0%, #FECACA 100%);
    border-color: #EF4444;
  }

  .emergency-options {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-top: 1.5rem;
  }

  .emergency-option {
    padding: 1rem;
    background: white;
    border-radius: 0.5rem;
    border: 1px solid #FECACA;
  }

  .emergency-option h4 {
    color: #DC2626;
    font-weight: 600;
    margin-bottom: 0.5rem;
  }

  .emergency-option p {
    color: #7F1D1D;
    font-size: 0.9rem;
    margin: 0;
    font-weight: 500;
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

    .services-grid {
      grid-template-columns: 1fr;
    }

    .features-grid {
      grid-template-columns: 1fr;
    }

    .support-options {
      grid-template-columns: 1fr;
    }

    .resource-item {
      flex-direction: column;
      gap: 1rem;
    }

    .resource-link {
      margin-left: 0;
      align-self: flex-start;
    }
  }
</style>