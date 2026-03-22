# Target12 — Product Specification

## Overview

A South African National Bisley shooting competition simulator (0.22" calibre). The player aims a crosshair on a scorecard of 11 targets, fighting hand shake, breathing rhythm, and heartbeat to place accurate shots. This spec is intended to be complete enough to reimplement the game on any platform.

## Language

All UI text is in English (translated from the Afrikaans original).

---

## Canvas and Coordinate System

- **Virtual resolution**: 640×480 pixels
- **Aspect ratio**: 4:3
- **Coordinate origin**: top-left corner (0, 0)
- **Y axis**: increases downward
- **Playfield boundaries**: top `BG=55`, bottom `OG=430`, left `LG=50`, right `RG=582`

### Display Scaling

The canvas is CSS-scaled to fit the viewport while preserving 4:3 aspect ratio. The internal 640×480 resolution never changes — all coordinates and sizes in this spec are in virtual canvas pixels.

Scaling logic:
1. Compare viewport aspect to 640/480
2. If viewport is wider than 4:3: `h = viewportHeight`, `w = h × (640/480)`
3. If viewport is taller than 4:3: `w = viewportWidth`, `h = w / (640/480)`
4. A `cssScale` factor (`w / 640`) is maintained for converting screen-space touch/mouse coordinates back to canvas coordinates

Fullscreen mode (toggled with F11) applies the same scaling at 100vw × 100vh with the canvas border removed.

---

## Game States

```
intro → shooting ⇄ help
                 ⇄ input_name / input_team / input_comp
       shooting → results ⇄ scorecard
                  results → shooting (restart)
                  results → intro (exit)
```

### 1. `intro` (Title Screen)

Black background, centered text:

| Element | Color | Font | Position (y) |
|---------|-------|------|-------------|
| "SOUTH AFRICAN" | `#0a0` | 22px serif | 75 |
| "NATIONAL" | `#ff0` | 24px serif | 110 |
| "BISLEY SHOOTING 1995" | `#a00` | bold 30px serif | 160 |
| "(0.22\" CALIBRE)" | `#00a` | 22px serif | 195 |
| "Press [6] for Help \| Press any key to start" | `#888` | 12px sans-serif | 450 |

All text is horizontally centered at x=320.

**Demo targets**: Three targets drawn at y=275, x positions 170, 320, 470.

**Demo shot animation** (timed from state entry):
| Time (ms) | Shot | Position |
|-----------|------|----------|
| 1500 | 1 | (340, 255) |
| 2500 | 2 | (320, 260) |
| 3500 | 3 | (320, 275) |

Each demo shot triggers a fire sound. Shot dots are `#a00`, 5px radius.

**Auto-advance**: At 5500ms, automatically transitions to `shooting`.

> **Note**: The displayed text suggests keyboard interaction ("Press [6] for Help | Press any key to start") but the current implementation only advances via the auto-advance timer. Keyboard input during intro has no effect (except F11 for fullscreen).

### 2. `shooting` (Main Game)

Scorecard displayed as background with all 11 targets. Crosshair overlaid, subject to drift, heartbeat, and breathing effects. Player aims and fires shots.

**Muzzle flash** on each shot: radial gradient centered on shot position, 60px radius:
- Stop 0.0: `rgba(255, 255, 255, α)`
- Stop 0.3: `rgba(255, 255, 100, α×0.7)`
- Stop 0.7: `rgba(255, 160, 0, α×0.3)`
- Stop 1.0: `rgba(255, 160, 0, 0)`

Plus a full-screen white overlay at `α×0.15`. Flash alpha starts at 1.0 and decays by `dt/100` per frame (fades over ~100ms).

**Round end** when either condition is met:
- 10 scoring shots fired (shots closest to a scoring target, not practice), OR
- 13 total shots fired

On round end → `results`.

### 3. `help` (Overlay)

Dark blue overlay `rgba(0, 0, 100, 0.95)` at rect (130, 80, 380×374).

Layout (all text left-aligned at x=145):

| y | Color | Font | Content |
|---|-------|------|---------|
| 105 | `#0a0` | 12px mono | "HELP - SA NATIONAL BISLEY SHOOTING .22" |
| 130 | `#ff0` | 12px mono | "KEYS" |
| 150–192 | `#ff0` | 12px mono | Arrow key controls (4 lines, 14px spacing) |
| 206–248 | `#ff0` | 12px mono | WASD controls (4 lines, 14px spacing) |
| 262 | `#ff0` | 12px mono | "SHOOT = [ENTER / SPACE]" |
| 276 | `#ff0` | 12px mono | "HOLD BREATH = [SHIFT]" |
| 290 | `#ff0` | 12px mono | "EXIT = [ESC]" |
| 310 | `#fff` | 12px mono | "FUNCTIONS" |
| 330–386 | `#fff` | 12px mono | Function keys 1–3, 5–6, F11 (14px spacing) |
| ~406 | `#888` | 11px mono | "Press any key to close" |

Any key → return to `shooting`.

### 4. `input_name` / `input_team` / `input_comp`

HTML overlay positioned absolute over the canvas, styled:
- Background: `rgba(0, 0, 80, 0.95)`
- Flex-centered vertically and horizontally
- Label: 18px serif, white, margin-bottom 16px
- Input field: 18px monospace, 240px wide, 8px/16px padding, background `#060`, border `2px solid #0a0` (focus: `#0f0`), white text, centered
- Hint text: 12px, `#888`, margin-top 8px, "Press Enter to confirm"

Labels:
- Name: "ENTER YOUR NAME"
- Team: "ENTER YOUR TEAM"
- Competition: "ENTER THE COMPETITION"

Enter confirms and stores value, Escape cancels. Both return to `shooting`. Drift continues in background during input.

### 5. `results` (Score Screen)

Dark blue overlay `rgba(0, 0, 100, 0.95)` at rect (130, 80, 380×340). All text centered at x=320.

| y | Color | Font | Content |
|---|-------|------|---------|
| 130 | `#fff` | 22px serif | "YOUR ROUNDS ARE DONE!" |
| 175 | `#0a0` | 22px serif | "SCORE" (at x=300) |
| 260 | `#ff0` | bold 64px serif | Score value (x=220 if 100, else x=260) |
| 250 | `#ff0` | bold 48px serif | "%" (at x=400) |
| 300 | `#a00` | 16px serif | "OPTIONS" |
| 330 | `#a00` | 16px serif | "[9]   VIEW SCORECARD (10s)" |
| 355 | `#a00` | 16px serif | "[5]   SHOOT AGAIN" |
| 380 | `#a00` | 16px serif | "[ESC] EXIT" |

Options: `[9]` → scorecard, `[5]` → restart, `[ESC]` → exit to intro.

### 6. `scorecard` (View Card)

Full scorecard rendered (same as shooting background). A countdown box is overlaid:
- Box: `rgba(0, 0, 100, 0.9)` rect at (290, 400, 60×35)
- Number: `#fff`, 22px serif, centered at (320, 425)
- Countdown: starts at 9, decrements every 1000ms
- When countdown reaches <0 → auto-return to `results`
- Any key → return to `results` early

---

## Scorecard Layout

640×480 canvas. 11 targets arranged in 3 rows:

```
Row 1 (y=80):   [1]@90   [2]@240   [3]@390   [4]@540
Row 2 (y=220):  [5]@170  [PRACTICE]@320       [6]@470
Row 3 (y=350):  [7]@90   [8]@240   [9]@390   [10]@540
```

Target positions (0-indexed array, index 5 is practice):

| Index | x | y | Label |
|-------|---|---|-------|
| 0 | 90 | 80 | "1" |
| 1 | 240 | 80 | "2" |
| 2 | 390 | 80 | "3" |
| 3 | 540 | 80 | "4" |
| 4 | 170 | 220 | "5" |
| 5 | 320 | 220 | *(practice)* |
| 6 | 470 | 220 | "6" |
| 7 | 90 | 350 | "7" |
| 8 | 240 | 350 | "8" |
| 9 | 390 | 350 | "9" |
| 10 | 540 | 350 | "10" |

### Target Appearance

- Filled circle: 50px radius, olive (`#808000`)
- Concentric rings at radii 5, 15, 25, 35px: black (`#000`), 1px stroke
- Scoring target number labels: white, 12px monospace, centered, 60px above target center
- Practice target label: "PRACTICE SHOTS ONLY", white, 11px monospace, centered at (320, 285)

### Scorecard Labels

| Element | Position | Color | Font | Content |
|---------|----------|-------|------|---------|
| Shot counter (scoring) | left, (40, 208) | `#fff` | 11px serif | `Shots: X/10` |
| Shot counter (total) | right, (630, 15) | `#fff` | 11px mono, right-aligned | `Y/13` |
| Total label | (40, 220) | `#fff` | 11px serif | "TOTAL" |
| Total dots | (40, 235) | `#fff` | 11px serif | "......" |
| Score value | (42, 248) | `#0a0` | 12px mono | Score number |
| Team label | (560, 220) | `#fff` | 11px serif | "TEAM" |
| Team dots | (555, 235) | `#fff` | 11px serif | "........." |
| Team value | (558, 248) | `#00a` | 12px mono | Team name |
| Name label | (40, 460) | `#fff` | 11px serif | "NAME ........................" |
| Name value | (100, 475) | `#00a` | 12px mono | Player name |
| Comp label | (450, 460) | `#fff` | 11px serif | "COMP .................." |
| Comp value | (510, 475) | `#00a` | 12px mono | Competition name |
| Organization | (320, 460) center | `#fff` | bold 20px serif | "S.A.N.S.S.U. 01" |

### Shot Marks

- Color: `#a00` (dark red)
- Radius: 5px filled circles
- Drawn at each recorded shot position

---

## Crosshair / Sight

### Appearance

- Circle: 55px radius, white (`#fff`) stroke, 1px line width
- Four tick marks: 10px long, extending both inward and outward from the circle at cardinal points (left, right, top, bottom)
- Center dot: 2px radius, white filled circle

### Position Computation

The crosshair is drawn at:

```
sightX = cx + driftOx + breathDx
sightY = cy + driftOy + breathDy
```

Where:
- `(cx, cy)` = center point (player-controlled)
- `(driftOx, driftOy)` = hand shake drift offset (includes heartbeat pulse)
- `(breathDx, breathDy)` = breathing displacement

The shot lands at `(sightX, sightY)` when the player fires.

### Center Point Control

**Initial position**: (320, 220) — center of the practice target.

**Keyboard movement** (discrete, on keydown):

| Input | Δx | Δy |
|-------|----|----|
| Arrow Up | 0 | −10 |
| Arrow Down | 0 | +10 |
| Arrow Left | −10 | 0 |
| Arrow Right | +10 | 0 |
| W | 0 | −50 |
| S | 0 | +50 |
| A | −50 | 0 |
| D | +50 | 0 |

**Touch joystick** (continuous, per frame): see Touch Controls section.

**Boundary clamping**: After any movement, if `cx ≥ RG` then `cx = RG−1`, if `cx ≤ LG` then `cx = LG+1`, if `cy ≥ OG` then `cy = OG−1`, if `cy ≤ BG` then `cy = BG+1`. A boundary sound plays on clamp.

### Drift / Hand Shake

Simulates hand tremor. Drift is a velocity-driven offset from the center point that wanders randomly and springs back toward zero.

**Per-frame update** (every `requestAnimationFrame` tick):

1. **Random acceleration**: `driftVx += (random()−0.5) × 0.12`, same for `driftVy` (i.e., ±DRIFT_ACCEL where DRIFT_ACCEL=0.06, multiplied by 2)
2. **Spring force**: `driftVx −= driftOx × 0.005`, `driftVy −= driftOy × 0.005`
3. **Speed clamp**: max speed = `0.5 × (1 + breathStress × 2.0)` px/frame. If `|velocity| > max`, normalize to max
4. **Drag**: `driftVx × = 0.95`, `driftVy × = 0.95`
5. **Apply velocity**: `driftOx += driftVx`, `driftOy += driftVy + heartDy` (heartbeat added to Y only)

If the resulting sight position exceeds boundaries, drift offset is clamped and velocity is reflected (bounced).

### Heartbeat Pulse

Adds a vertical displacement to `driftOy` each frame, simulating pulse transmitted through prone chest-to-ground contact.

**Parameters**:
- Resting BPM: 60
- Amplitude: `HEART_AMPLITUDE = 3.5` px (modified by stress: `× (1 + breathStress × 0.8)`)

**Double-bump waveform** (beatPos = 0..1 within one heartbeat cycle):

```
if beatPos < 0.08:
    pulse = sin(beatPos / 0.08 × π)           // systolic bump
else if 0.15 ≤ beatPos < 0.22:
    pulse = 0.4 × sin((beatPos−0.15) / 0.07 × π)  // dicrotic notch
else:
    pulse = 0
```

Applied as: `heartDy = −pulse × heartAmplitude`

Heart period = `60000 / heartBPM` ms. Phase accumulates via `heartPhase += dt`.

### Breathing Rhythm

Dominant disturbance in prone position. 5-second cycle (12 breaths/min).

**Waveform function** `breathingWaveform(t)` where `t` = 0..1 within one breath cycle:

| Phase | t range | Displacement |
|-------|---------|-------------|
| Inhale | 0.0–0.4 | `e = (1 − cos(t/0.4 × π)) / 2`; dy = e × 25.0; dx = e × 4.0 |
| Natural pause | 0.4–0.5 | dy = 25.0; dx = 4.0 (full displacement held) |
| Exhale | 0.5–1.0 | `e = (1 − cos((t−0.5)/0.5 × π)) / 2`; dy = (1−e) × 25.0; dx = (1−e) × 4.0 |

Constants: `BREATH_PERIOD = 5000ms`, `BREATH_AMPLITUDE_Y = 25.0px`, `BREATH_AMPLITUDE_X = 4.0px`.

Inhale lowers the sight (+dy = down), exhale raises it back. The natural pause at t=0.4–0.5 is the ideal moment to hold breath and fire.

### Breath Hold

Activated by holding Shift (keyboard) or the breath-hold touch button.

**On press**:
1. Record `breathHoldPhase` = current position in breath cycle (0..1)
2. Capture current waveform displacement: `breathHoldStartDx`, `breathHoldStartDy`
3. Calculate **hold quality** = `1 − min(|breathHoldPhase − 0.45| / 0.45, 1)` — pressing near the natural pause (0.45) yields quality ≈ 1.0; pressing at the worst point yields ≈ 0
4. Calculate **rest target** (where crosshair settles to):
   - `breathHoldRestDx = breathHoldStartDx × 0.9`
   - `breathHoldRestDy = breathHoldStartDy × (0.7 + 0.3 × breathHoldQuality)`

**While held** — settle phase (first 500ms):
- `settleProgress = min(1, holdElapsed / 500)`
- Smoothstep easing: `eased = settleProgress² × (3 − 2 × settleProgress)`
- `breathDx = breathHoldStartDx + (breathHoldRestDx − breathHoldStartDx) × eased`
- `breathDy = breathHoldStartDy + (breathHoldRestDy − breathHoldStartDy) × eased`

**Over-holding** (past `BREATH_HOLD_MAX = 4000ms`):
- `breathStress = min(1, (holdElapsed − 4000) / 2000)` — ramps 0→1 over 2s
- Drift max speed multiplied by `(1 + breathStress × 2.0)` = up to ×3.0
- Heart amplitude multiplied by `(1 + breathStress × 0.8)` = up to ×1.8
- **Slow circular wobble** added to breathing displacement:
  - Frequency: `t × 2π × 2` Hz (where t = performance.now() / 1000)
  - `breathDx += sin(freq) × breathStress × 4.0`
  - `breathDy += cos(freq × 0.7) × breathStress × 4.0 × 1.2` (slightly elliptical)
- **Heart rate target** ramps up with delayed onset:
  - `hrStress = max(0, (breathStress − 0.25) / 0.75)` — no HR increase in first ~0.5s past limit
  - `heartBPMTarget = 60 + hrStress × 40` — up to 100 BPM

### Breath Release / Recovery

On releasing Shift (or lifting breath touch button):
1. `breathHolding = false`, `breathRecovering = true`
2. `heartBPMTarget` reset to 60

**Gasp phase** (first 400ms after release):
- `breathDy = 8 × sin(gaspT × π)` where `gaspT = elapsed / 400` — crosshair jumps down ~8px (involuntary inhale arc peaking at 200ms)
- `breathDx = 0`

**Exaggerated breathing** (400ms to 400ms + `BREATH_RECOVERY_TIME`):
- `BREATH_RECOVERY_TIME = 2000ms`
- `recProgress = min(1, (elapsed − 400) / 2000)` — 0 to 1 over 2s
- Amplitude multiplier: `1.5 − 0.5 × recProgress` (1.5× fading to 1×)
- `breathStress` fades: `breathStress × (1 − recProgress)`
- Normal waveform applied with amplitude multiplier
- When `recProgress ≥ 1`: recovery ends, `breathStress = 0`

### Heart Rate Chase

Heart rate (`heartBPM`) smoothly chases `heartBPMTarget` with asymmetric rates:

```
rate = 0.0004 if heartBPMTarget > heartBPM else 0.0002
heartBPM += (heartBPMTarget − heartBPM) × rate × dt
```

- **Ramp-up**: rate `0.0004` — takes ~5s to cover 80% of gap toward elevated target
- **Recovery**: rate `0.0002` — takes ~10s+, so elevated HR lingers after release
- During normal breathing (not holding), target is always 60 BPM

---

## ECG Heartbeat Chart

- **Position**: (220, 432), 200×20px (centered vertically on y=432)
- **Background**: black fill, dark red border (`#300`), 1px stroke
- **Trace**: red (`#f00`), 1.5px line width
- **Buffer**: 200-sample circular buffer (one sample per horizontal pixel)
- **Time span**: 2000ms visible window → sample interval = 10ms per pixel
- **Phase tracking**: independent `ecgBeatPhase` (0..1) advanced by `sampleInterval / (60000/heartBPM)` per sample

**ECG waveform function** `heartbeatWaveform(t)` where `t` = 0..1:

| Segment | t range | Value |
|---------|---------|-------|
| Baseline | 0.00–0.05 | 0 |
| P wave | 0.05–0.08 | `0.15 × sin((t−0.05)/0.03 × π)` |
| PR segment | 0.08–0.12 | 0 |
| Q dip | 0.12–0.14 | −0.15 |
| R peak | 0.14–0.18 | `sin((t−0.14)/0.04 × π)` |
| S dip | 0.18–0.21 | −0.25 |
| ST return | 0.21–0.25 | `−0.25 × (1 − (t−0.21)/0.04)` |
| ST segment | 0.25–0.35 | 0 |
| T wave | 0.35–0.45 | `0.2 × sin((t−0.35)/0.10 × π)` |
| Baseline | 0.45–1.00 | 0 |

Vertical mapping: `py = centerY − value × (h/2) × 0.85`

---

## Breath Indicator Bar

- **Position**: (185, 432), 25×20px (centered vertically on y=432, left of ECG chart)
- **Background**: black fill, dark green border (`#030`), 1px stroke
- **Fill direction**: bottom-up
- **Fill level**: `breathDy / BREATH_AMPLITUDE_Y` clamped to 0..1

**Bar color states**:
| Condition | Color |
|-----------|-------|
| Stressed (`breathStress > 0.01`) | `rgb(255, 255×(1−stress), 0)` — yellow→red interpolation |
| Recovering | `#ff0` (yellow) |
| Holding breath | `#0f0` (bright green) |
| Normal breathing | `#0a0` (green) |

**Label**: When holding breath, "BREATH" is drawn in 7px monospace above the bar, centered, in the bar's current color.

---

## Scoring

### Algorithm (ported from Pascal `SCORE` procedure)

For each of the 10 scoring targets (indices 0–4, 6–10):
1. Find the minimum distance from any fired shot to the target center
2. `distance = round(sqrt(dx² + dy²))` where dx, dy are absolute differences
3. Score based on distance bands:

| Distance (px) | Points |
|---------------|--------|
| 0–11 | 10 |
| 12–21 | 9 |
| 22–30 | 8 |
| 31–40 | 7 |
| 41–56 | 6 |
| >56 | 0 |

- Each target scores independently based on its closest shot
- Maximum total: 100 (10 targets × 10 points)
- Score recalculated after each shot

### Scoring vs Practice Shots

- A shot is "scoring" if its nearest target (by squared Euclidean distance) is any target except index 5 (practice)
- A shot is "practice" if its nearest target is the practice target at (320, 220)
- Round ends when 10 scoring shots are fired OR 13 total shots are used

---

## Controls

### Keyboard — During Shooting

| Input | Action | Amount |
|-------|--------|--------|
| Arrow keys | Move center point | 10px |
| W/A/S/D | Move center point (large jump) | 50px |
| Enter / Space | Fire shot | — |
| Shift (hold) | Hold breath (stabilize aim) | — |
| 1 | Enter name | — |
| 2 | Enter team | — |
| 3 | Enter competition | — |
| 4 | Calculate & display score | — |
| 5 | Restart (clear all shots) | — |
| 6 | Show help overlay | — |
| Escape | Exit to intro screen | — |
| F11 | Toggle fullscreen | — |

### Keyboard — During Results

| Input | Action |
|-------|--------|
| 9 | View scorecard (10s countdown) |
| 5 | Restart game |
| Escape | Exit to intro |

### Keyboard — Other States

- **Intro**: auto-advances after 5500ms (no keyboard interaction)
- **Help / Scorecard**: any key returns to previous state
- **Input overlays**: Enter confirms, Escape cancels
- **F11**: toggles fullscreen from any state

### Touch Controls

Displayed only on touch devices, only during `shooting` state. Controls are positioned in the viewport margins (the black bars left and right of the 4:3 canvas).

- **Left margin**: breath-hold button (upper) and joystick activation zone (lower)
- **Right margin**: fire button only

**Touch event routing**: Touch/pointer events in the control zones (left margin, right margin) must be consumed by those controls and must NOT propagate to the game canvas. Only touches that land on the canvas area itself (between the margins) should trigger game actions like firing a shot. This prevents double-firing and unintended state transitions from control interactions. On non-shooting states (intro, help, results, scorecard), taps on the canvas advance/dismiss as described in those states' sections.

#### Layout (Web)

On landscape mobile screens wider than 4:3, the CSS-scaled canvas leaves horizontal margins. Touch controls are **HTML overlay `<div>` elements** in these margins, children of `#game-container`, siblings of the canvas. `updateCanvasScale()` computes margin widths and positions the control elements accordingly. When margin < 80px (narrow viewport), `#game-container` gets a `.narrow-margins` class — controls overlay the canvas edges with semi-transparency instead. Controls are hidden via CSS (`display: none`) when not in `shooting` state; JavaScript toggles `.touch-active` class based on game state.

#### Layout (Android)

Touch zones are defined by screen-space coordinate ranges: left of canvas offset = left margin, right of canvas offset + scaled width = right margin. A single unified pointer event handler hit-tests each touch against these zones before dispatching. Touches in margin zones are routed to the corresponding control; touches in the canvas area are routed to game actions. Controls are rendered on the canvas itself (not as separate Compose elements) during the `shooting` phase only. All control sizes (radii, stroke widths, font sizes) specified in the sections below are in screen pixels and apply to Android identically — do not substitute smaller values.

#### Joystick (left margin)

- **Activation zone**: entire left margin `<div>` (touch-action: none)
- **Idle hint**: centered in left margin at ~65% viewport height; filled circle radius 138px, fill `rgba(0,0,170)` α=0.40, stroke `rgba(100,100,255)` 6px α=0.55; "MOVE" label 30px bold monospace white α=0.85
- **Active**: base appears at touch-down point within the margin; outer ring — white stroke, 6px, α=0.35; thumb nub — 48px radius, white fill, α=0.60
- **Radius**: `JOYSTICK_RADIUS = 180px` (screen pixels)
- **Movement**: per frame, `cx += (joystickDx / JOYSTICK_RADIUS) × JOYSTICK_MAX_SPEED`, same for cy. `JOYSTICK_MAX_SPEED = 3` px/frame at full deflection. Boundary clamping and sound applied same as keyboard.

#### Fire Button (right margin, lower)

- **Position**: centered horizontally in right margin, at ~75% viewport height
- **Radius**: 156px (screen pixels)
- **Idle**: fill `#a00` α=0.40, stroke `#fff` 6px α=0.55, label "FIRE" white bold 39px mono α=0.85
- **Pressed**: fill `#f55` α=0.75, stroke `#fff` 6px α=0.90, label α=1.0
- **Behavior**: fires a shot on touch-start (same logic as Enter/Space)

#### Breath-Hold Button (left margin, upper)

- **Position**: centered horizontally in left margin, at ~35% viewport height
- **Radius**: 132px (screen pixels)
- **Idle**: fill `#060` α=0.40, stroke `#0f0` 6px α=0.55, label "HOLD / BREATH" white bold 33px mono α=0.85 (two lines: "HOLD" and "BREATH")
- **Pressed**: fill `#0f0` α=0.75, stroke `#0f0` 6px α=0.90, label α=1.0
- **Behavior**: activates breath hold on touch-start, releases on touch-end/cancel (same logic as Shift key)

#### Multi-touch

Separate DOM elements with independent touch event listeners — no manual touch ID tracking needed. The player can simultaneously aim with the joystick, hold breath, and fire.

#### Narrow Viewport Fallback

When margin width < 80px, controls overlay the canvas edges with semi-transparency (α=0.5 background on control containers). This ensures controls remain usable on devices where margins are too narrow.

---

## Sound Effects

All procedurally generated via `AudioContext` oscillators and buffers (no audio files).

| Event | Sound |
|-------|-------|
| Fire shot | Layer 1 — **Crack**: white noise burst (80ms, gain 0.4 → exponential decay to 0.001). Layer 2 — **Boom**: sine oscillator sweeping 150→50 Hz over 200ms (gain 0.3 → 0.001 exponential). |
| Boundary hit | 200 Hz sine, 50ms, gain 0.15 |
| Bump/tick | 50 Hz sine, 30ms, gain 0.15 |

---

## Visual Style

### Colors

| Element | Color |
|---------|-------|
| Background | `#000` |
| Target fill | `#808000` (olive) |
| Target rings | `#000` |
| Crosshair | `#fff` |
| Shot marks | `#a00` (dark red, 5px radius) |
| Score text | `#0a0` (green) |
| Highlight text | `#ff0` (yellow) |
| User data | `#00a` (dark blue) |
| Overlays | `rgba(0, 0, 100, 0.95)` |

### Typography

| Usage | Font |
|-------|------|
| UI elements, labels, code-like text | monospace |
| Titles, headings, scorecard headings | serif |
| Credits, prompts | sans-serif |

---

## Player Data

| Field | Max Length | Input Key | Label |
|-------|-----------|-----------|-------|
| Name | 15 chars | 1 | "NAME" |
| Team | 6 chars | 2 | "TEAM" |
| Competition | 11 chars | 3 | "COMP" |

Displayed on scorecard bottom section. Persists across restarts within the same session (not cleared by `resetGame`).

---

## Reset Behavior

On restart (`[5]` during shooting or results):
- Shot count reset to 1, score to 0
- Center point returns to (320, 220)
- All drift/velocity zeroed
- All breathing state reset (phase, hold, recovery, stress)
- Heart rate reset to 60 BPM
- Flash cleared
- Player data (name/team/competition) preserved

---

## App Icon

The app icon represents the game's core gameplay: a crosshair sight on a target.

### Design

- **Background**: olive/yellow (`#808000`) — matching the in-game target fill color
- **Target dots**: 11 solid black (`#000`) filled circles arranged in a 4-3-4 grid matching the target sheet layout
- **Crosshair**: white (`#fff`) — circle with 4 cardinal tick marks + center dot (matching the in-game sight)

### Usage

- **Android**: adaptive icon (vector drawable foreground/background layers) referenced via `android:icon` in AndroidManifest
- **Web**: inline SVG favicon via `<link rel="icon">` in the HTML `<head>`

---

## Future Considerations

- Difficulty levels (adjust drift intensity, heartbeat amplitude, BPM)
- High score persistence (localStorage)
- Multiplayer / score comparison
- Wind effect simulation
- Print scorecard
