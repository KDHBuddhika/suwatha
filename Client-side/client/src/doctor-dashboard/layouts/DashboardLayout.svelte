<script lang="ts">
  import Sidebar from '../components/Sidebar.svelte';
  import Header from '../components/Header.svelte';
</script>

<div class="dashboard-layout">
  <Sidebar />
  <div class="main-content">
    <Header />
    <main class="content-area">
      <slot />
    </main>
  </div>
</div>

<style>
  .dashboard-layout {
    display: flex;
    min-height: 100vh;
    background: #F8FAFC;
  }

  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-left: 280px;
    transition: margin-left 0.3s ease;
  }

  .content-area {
    flex: 1;
    padding: 2rem;
    overflow-y: auto;
  }

  @media (max-width: 1024px) {
    .main-content {
      margin-left: 0;
    }
  }
</style>