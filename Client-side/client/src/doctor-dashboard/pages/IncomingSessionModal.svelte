<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  import { Bell, Video } from 'lucide-svelte';

  export let patientId: string;
  export let sessionType: 'VIDEO' | 'CHAT';

  const dispatch = createEventDispatcher();
</script>

<div class="modal-backdrop">
  <div class="modal-content">
    <div class="icon-container"><Bell size={48} /></div>
    <h2 class="modal-title">New Session Request!</h2>
    <div class="session-info">
      <div class="info-item">
        <span class="label">Patient ID:</span>
        <span class="value">{patientId}</span>
      </div>
      <div class="info-item">
        <span class="label">Session Type:</span>
        <div class="value type-video"><Video size={20} /><span>{sessionType}</span></div>
      </div>
    </div>
    <div class="action-buttons">
      <button class="btn btn-secondary" on:click={() => dispatch('cancel')}>Cancel Session</button>
      <button class="btn btn-primary" on:click={() => dispatch('join')}>Join Session Now</button>
    </div>
  </div>
</div>

<style>
  .modal-backdrop { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(17, 24, 39, 0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 1000; }
  .modal-content { background-color: white; padding: 3rem; border-radius: 1rem; width: 100%; max-width: 500px; text-align: center; box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1); transform: scale(0.95); animation: scale-in 0.2s ease-out forwards; }
  @keyframes scale-in { to { transform: scale(1); } }
  .icon-container { color: #4F46E5; margin-bottom: 1rem; }
  .modal-title { font-size: 2rem; font-weight: 700; color: #1F2937; margin: 0 0 1.5rem 0; }
  .session-info { text-align: left; margin-bottom: 2.5rem; }
  .info-item { display: flex; justify-content: space-between; align-items: center; padding: 1rem; background-color: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 0.5rem; }
  .info-item:not(:last-child) { margin-bottom: 1rem; }
  .label { font-size: 1rem; color: #6B7280; font-weight: 500; }
  .value { font-size: 1rem; font-weight: 600; color: #1F2937; }
  .type-video { display: flex; align-items: center; gap: 0.5rem; color: #374151; }
  .action-buttons { display: flex; gap: 1rem; justify-content: center; }
  .btn { padding: 0.875rem 1.5rem; border: none; border-radius: 0.5rem; font-size: 1rem; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }
  .btn-primary { background-color: #4F46E5; color: white; flex-grow: 1; }
  .btn-primary:hover { background-color: #4338CA; }
  .btn-secondary { background-color: #E5E7EB; color: #374151; }
  .btn-secondary:hover { background-color: #D1D5DB; }
</style>