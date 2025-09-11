<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  import { X } from 'lucide-svelte';

  // The component receives the full notification object as a prop
  export let notification: {
    id: number;
    message: string;
    time: string;
    unread: boolean;
    details?: string; // Optional detailed description
  };

  const dispatch = createEventDispatcher();

  // Function to close the modal, which dispatches a 'close' event
  function closeModal() {
    dispatch('close');
  }
</script>

<div class="modal-backdrop" on:click={closeModal}>
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div class="modal-content" on:click|stopPropagation>
    <div class="modal-header">
      <h3>Notification Details</h3>
      <button class="close-btn" on:click={closeModal}>
        <X size={24} />
      </button>
    </div>
    <div class="modal-body">
      <p class="message">{notification.message}</p>
      <span class="time">{notification.time}</span>
      {#if notification.details}
        <p class="details">{notification.details}</p>
      {/if}
    </div>
    <div class="modal-footer">
      <button class="btn-secondary" on:click={closeModal}>Close</button>
      <button class="btn-primary">View Details</button>
    </div>
  </div>
</div>

<style>
  .modal-backdrop { position: fixed; inset: 0; background-color: rgba(17, 24, 39, 0.7); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 1000; }
  .modal-content { background-color: white; border-radius: 1rem; width: 90%; max-width: 500px; box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1); display: flex; flex-direction: column; transform: scale(0.95); animation: scale-in 0.2s ease-out forwards; }
  @keyframes scale-in { to { transform: scale(1); } }
  .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 1.5rem; border-bottom: 1px solid #E5E7EB; }
  .modal-header h3 { margin: 0; font-size: 1.25rem; font-weight: 600; color: #1F2937; }
  .close-btn { background: none; border: none; color: #9CA3AF; cursor: pointer; padding: 0.25rem; }
  .modal-body { padding: 1.5rem; }
  .message { font-size: 1rem; font-weight: 500; color: #374151; margin: 0; }
  .time { display: block; font-size: 0.875rem; color: #6B7280; margin-top: 0.25rem; }
  .details { font-size: 0.9rem; color: #4B5563; line-height: 1.6; margin-top: 1.5rem; border-top: 1px dashed #E5E7EB; padding-top: 1.5rem; }
  .modal-footer { display: flex; justify-content: flex-end; gap: 0.75rem; padding: 1rem 1.5rem; background-color: #F9FAFB; border-bottom-left-radius: 1rem; border-bottom-right-radius: 1rem; }
  .btn-primary, .btn-secondary { padding: 0.5rem 1.25rem; border-radius: 0.5rem; border: none; font-weight: 500; cursor: pointer; }
  .btn-primary { background-color: #4F46E5; color: white; }
  .btn-secondary { background-color: #E5E7EB; color: #374151; }
</style>