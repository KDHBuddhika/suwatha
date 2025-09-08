<script lang="ts">
  import Navbar from '../components/Navbar.svelte';
</script>

<div class="layout">
  <Navbar />
  <main class="main-content">
    <slot />
  </main>
</div>

<style>
  .layout {
    min-height: 100vh;
    background: linear-gradient(135deg, #E8F4FD 0%, #F0F9F4 50%, #FEF3E7 100%);
  }

  .main-content {
    padding-top: 70px; /* Account for fixed navbar */
    min-height: calc(100vh - 70px);
  }
</style>