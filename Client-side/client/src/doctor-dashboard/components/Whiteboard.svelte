<script lang="ts">
  import { onMount } from 'svelte';
  import { Brush, Eraser, Trash2 } from 'lucide-svelte';

  // --- COMPONENT STATE ---
  let canvas: HTMLCanvasElement;
  let ctx: CanvasRenderingContext2D;
  let isDrawing = false;

  // --- TOOLBAR STATE ---
  let activeTool: 'draw' | 'erase' = 'draw';
  let activeColor = '#FFFFFF';
  let activeWidth = 5;

  const colors = ['#FFFFFF', '#EF4444', '#3B82F6', '#22C55E', '#F97316'];
  const line_widths = [2, 5, 10];

  // --- LIFECYCLE & SETUP ---
  onMount(() => {
    // Wait for the component to be in the DOM before initializing
    initializeCanvas();
    window.addEventListener('resize', initializeCanvas);

    return () => {
      window.removeEventListener('resize', initializeCanvas);
    };
  });

  function initializeCanvas() {
    if (!canvas) return;
    ctx = canvas.getContext('2d');
    // Set canvas resolution to its CSS display size to avoid blurriness
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
  }

  // --- DRAWING LOGIC ---
  function getMousePos(e: MouseEvent) {
    const rect = canvas.getBoundingClientRect();
    return {
      x: e.clientX - rect.left,
      y: e.clientY - rect.top
    };
  }

  function startDrawing(e: MouseEvent) {
    isDrawing = true;
    const { x, y } = getMousePos(e);

    // Set tool properties *before* starting the path
    ctx.lineWidth = activeWidth;
    ctx.strokeStyle = activeColor;
    
    // Logic for switching between drawing and erasing
    if (activeTool === 'erase') {
      ctx.globalCompositeOperation = 'destination-out'; // This erases content
    } else {
      ctx.globalCompositeOperation = 'source-over'; // Default drawing mode
    }

    ctx.beginPath();
    ctx.moveTo(x, y);
  }

  function draw(e: MouseEvent) {
    if (!isDrawing) return;
    const { x, y } = getMousePos(e);
    ctx.lineTo(x, y);
    ctx.stroke();
  }

  function stopDrawing() {
    isDrawing = false;
  }
  
  function clearCanvas() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
  }
</script>

<div class="whiteboard-container">
  <div class="toolbar">
    <!-- Tool Selection: Draw/Erase -->
    <div class="tool-group">
      <button class="tool-btn" class:active={activeTool === 'draw'} on:click={() => activeTool = 'draw'} title="Draw">
        <Brush size={20} />
      </button>
      <button class="tool-btn" class:active={activeTool === 'erase'} on:click={() => activeTool = 'erase'} title="Erase">
        <Eraser size={20} />
      </button>
    </div>

    <!-- Color Palette -->
    <div class="tool-group">
      {#each colors as color}
        <button 
          class="color-swatch"
          class:active={activeColor === color && activeTool === 'draw'}
          style="background-color: {color};"
          on:click={() => { activeColor = color; activeTool = 'draw'; }}
          title={color}
        ></button>
      {/each}
    </div>

    <!-- Line Width -->
    <div class="tool-group">
      {#each line_widths as width}
        <button class="width-selector" class:active={activeWidth === width} on:click={() => activeWidth = width} title={`Line width: ${width}px`}>
          <div class="width-dot" style="width: {width * 2}px; height: {width * 2}px;"></div>
        </button>
      {/each}
    </div>
    
    <!-- Clear Canvas -->
    <div class="tool-group">
       <button class="tool-btn" on:click={clearCanvas} title="Clear Whiteboard">
        <Trash2 size={20} />
      </button>
    </div>
  </div>

  <canvas 
    bind:this={canvas}
    on:mousedown={startDrawing}
    on:mousemove={draw}
    on:mouseup={stopDrawing}
    on:mouseout={stopDrawing}
  ></canvas>
</div>

<style>
  .whiteboard-container { position: relative; width: 100%; height: 100%; }
  .toolbar { position: absolute; top: 1rem; left: 50%; transform: translateX(-50%); background-color: #1F2937; border: 1px solid #4B5563; border-radius: 0.75rem; padding: 0.5rem; display: flex; align-items: center; gap: 1rem; z-index: 10; }
  .tool-group { display: flex; align-items: center; gap: 0.5rem; }
  .tool-btn, .width-selector { background: none; border: none; color: #D1D5DB; padding: 0.5rem; border-radius: 0.375rem; cursor: pointer; transition: background-color 0.2s, color 0.2s; }
  .tool-btn:hover, .width-selector:hover { background-color: #4B5563; }
  .tool-btn.active, .width-selector.active { background-color: #4F46E5; color: white; }
  .color-swatch { width: 28px; height: 28px; border-radius: 50%; border: 2px solid transparent; cursor: pointer; transition: border-color 0.2s; }
  .color-swatch:hover { border-color: #9CA3AF; }
  .color-swatch.active { border-color: #6366F1; }
  .width-selector { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; }
  .width-dot { background-color: #D1D5DB; border-radius: 50%; }
  canvas { width: 100%; height: 100%; cursor: crosshair; }
</style>