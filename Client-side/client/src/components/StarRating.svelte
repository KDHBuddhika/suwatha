<script lang="ts">
  export let rating: number = 0;
  export let maxRating: number = 5;
  export let showValue: boolean = true;

  $: fullStars = Math.floor(rating);
  $: hasHalfStar = rating % 1 >= 0.5;
  $: emptyStars = maxRating - fullStars - (hasHalfStar ? 1 : 0);
</script>

<div class="star-rating">
  <div class="stars">
    {#each Array(fullStars) as _}
      <span class="star filled">★</span>
    {/each}
    {#if hasHalfStar}
      <span class="star half">★</span>
    {/if}
    {#each Array(emptyStars) as _}
      <span class="star empty">★</span>
    {/each}
  </div>
  {#if showValue}
    <span class="rating-value">{rating} / {maxRating}</span>
  {/if}
</div>

<style>
  .star-rating {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .stars {
    display: flex;
    gap: 0.125rem;
  }

  .star {
    font-size: 1.5rem;
    line-height: 1;
  }

  .star.filled {
    color: #fbbf24;
  }

  .star.half {
    background: linear-gradient(90deg, #fbbf24 50%, #d1d5db 50%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .star.empty {
    color: #d1d5db;
  }

  .rating-value {
    font-size: 1.25rem;
    font-weight: bold;
    color: #111827;
  }
</style>