<div class="about-container">
  <div class="container">
    <div class="hero-section">
      <h1 class="page-title">About Manosara</h1>
      <p class="page-subtitle">Dedicated to transforming mental healthcare through compassionate, accessible, and innovative solutions</p>
    </div>

    <div class="content-section">
      <div class="mission-section">
        <h2>Our Mission</h2>
        <p>At Manosara, we believe that mental health is just as important as physical health. Our mission is to break down barriers to mental healthcare by providing accessible, culturally sensitive, and comprehensive mental health services to everyone, regardless of their background or circumstances.</p>
      </div>

      <div class="values-section">
        <h2>Our Core Values</h2>
        <div class="values-grid">
          <div class="value-card">
            <div class="value-icon">💙</div>
            <h3>Compassion</h3>
            <p>We approach every interaction with empathy, understanding, and genuine care for your well-being.</p>
          </div>
          <div class="value-card">
            <div class="value-icon">🌍</div>
            <h3>Accessibility</h3>
            <p>Mental healthcare should be available to everyone, including those with special needs and accessibility requirements.</p>
          </div>
          <div class="value-card">
            <div class="value-icon">🔒</div>
            <h3>Privacy</h3>
            <p>Your personal information and therapeutic journey are kept strictly confidential and secure.</p>
          </div>
          <div class="value-card">
            <div class="value-icon">⚡</div>
            <h3>Innovation</h3>
            <p>We continuously improve our services using the latest technology and evidence-based practices.</p>
          </div>
        </div>
      </div>

      <div class="team-section">
        <h2>Our Approach</h2>
        <p>We combine traditional therapeutic approaches with modern technology to create a holistic mental health experience. Our platform offers multiple ways to connect with mental health professionals, ensuring that you can access care in the way that feels most comfortable for you.</p>
        <ul class="approach-list">
          <li>Video consultations with licensed therapists</li>
          <li>24/7 chat support for immediate assistance</li>
          <li>Specialized services for hearing-impaired individuals</li>
          <li>Culturally competent care tailored to diverse communities</li>
          <li>Evidence-based therapeutic interventions</li>
        </ul>
      </div>
    </div>
  </div>
</div>

<style>
  .about-container {
    padding: 2rem 0 4rem;
    background: white;
  }

  .container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  .hero-section {
    text-align: center;
    margin-bottom: 4rem;
  }

  .page-title {
    font-size: 3rem;
    font-weight: 300;
    color: #374151;
    margin-bottom: 1rem;
  }

  .page-subtitle {
    font-size: 1.25rem;
    color: #6B7280;
    line-height: 1.6;
    max-width: 600px;
    margin: 0 auto;
  }

  .content-section {
    display: flex;
    flex-direction: column;
    gap: 4rem;
  }

  .mission-section h2, .values-section h2, .team-section h2 {
    font-size: 2rem;
    font-weight: 500;
    color: #374151;
    margin-bottom: 1.5rem;
  }

  .mission-section p, .team-section p {
    font-size: 1.1rem;
    line-height: 1.7;
    color: #6B7280;
    margin-bottom: 2rem;
  }

  .values-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
    margin-top: 2rem;
  }

  .value-card {
    padding: 2rem;
    background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
    border-radius: 1rem;
    text-align: center;
    border: 1px solid #E2E8F0;
  }

  .value-icon {
    font-size: 2.5rem;
    margin-bottom: 1rem;
  }

  .value-card h3 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 1rem;
  }

  .value-card p {
    color: #6B7280;
    line-height: 1.6;
    margin: 0;
  }

  .approach-list {
    list-style: none;
    padding: 0;
    margin-top: 2rem;
  }

  .approach-list li {
    padding: 0.75rem 0;
    color: #6B7280;
    position: relative;
    padding-left: 2rem;
  }

  .approach-list li::before {
    content: "✓";
    position: absolute;
    left: 0;
    color: #10B981;
    font-weight: bold;
  }

  @media (max-width: 768px) {
    .container {
      padding: 0 1rem;
    }

    .page-title {
      font-size: 2.5rem;
    }

    .values-grid {
      grid-template-columns: 1fr;
    }
  }
</style>