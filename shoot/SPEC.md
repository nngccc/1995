# Target12 — Product Specification

## Overview

Web-based recreation of a South African National Bisley shooting competition simulator (0.22" caliber), originally written in Turbo Pascal 7.0 by Nico Gerber (1996). Single HTML file, no dependencies.

## Origin

- **Original**: `TARGET12.PAS` — Turbo Pascal 7.0, BGI graphics, 640x480
- **Author**: Nico Gerber, Standard 9, 1996
- **Organization**: S.A.N.S.S.U. (Suid-Afrikaanse Nasionale Skietskyfskiet-Unie)
- **Competition**: National Bisley target shooting, 0.22" caliber rifles

## Technical Stack

- Single file: `target12.html`
- Canvas 2D (640x480, matching original BGI resolution)
- Vanilla JavaScript, no frameworks or build tools
- `requestAnimationFrame` game loop
- `AudioContext` oscillator for sound effects
- HTML overlays for text input (name/team/competition)

---

## Game States

```
drawrose → intro → shooting ⇄ help
                            ⇄ input_name / input_team / input_comp
                  shooting → results ⇄ scorecard
                             results → shooting (restart)
                             results → intro (exit)
```

### 1. `drawrose` (Splash Screen)
- "LCC PRODUKSIES" with decorative rose graphic
- "BIED AAN..." text
- Auto-advances to step 2: "GEPROGRAMEER IN TURBO PASCAL 7.0" with typewriter effect
- Skip: any key → `intro`

### 2. `intro` (Title Screen)
- Title: "SUID-AFRIKAANSE NASIONALE BISLEY SKIET 1995 (0.22" KALIBER)"
- Three demo targets with animated shot sequence (timed at 1.5s intervals with fire sound)
- Credit: "PROGRAMERING - NICO GERBER"
- Prompt: "Druk [6] vir Help | Press any key to start"
- Press `6` → `help`, any other key → `shooting`

### 3. `shooting` (Main Game)
- Scorecard displayed as background with all 11 targets
- Crosshair overlaid, subject to drift and heartbeat effects
- Player aims and fires shots
- Muzzle flash on each shot: radial gradient (white/yellow core → orange → transparent, 60px) at shot position + subtle full-screen white overlay, fades over ~100ms
- Round ends when either condition is met:
  - 10 scoring shots fired (shots closest to a scoring target, not practice), OR
  - 13 total shots fired
- On round end → `results`

### 4. `help` (Overlay)
- Blue overlay on top of scorecard
- Shows all controls (bilingual Afrikaans/English)
- Any key → return to `shooting`

### 5. `input_name` / `input_team` / `input_comp`
- HTML overlay with text input field
- Enter confirms, Escape cancels
- Returns to `shooting`
- Drift continues in background

### 6. `results` (Score Screen)
- Blue overlay: "JOU RONTES IS KLAAR!"
- Large score display with `%` symbol
- Options: `[9]` view scorecard, `[5]` restart, `[ESC]` exit to intro

### 7. `scorecard` (View Card)
- Full scorecard with all shots visible
- 10-second countdown timer (9→0) displayed
- Auto-returns to `results` when countdown ends
- Any key → return to `results` early

---

## Scorecard Layout

640x480 canvas. 11 targets arranged in 3 rows:

```
Row 1 (y=80):   [1]@90   [2]@240   [3]@390   [4]@540
Row 2 (y=220):  [5]@170  [PROEF]@320         [6]@470
Row 3 (y=350):  [7]@90   [8]@240   [9]@390   [10]@540
```

- 10 scoring targets (labeled 1-10)
- 1 practice target (center, labeled "SLEGS PROEFSKOTE")
- Each target: 50px radius filled circle with concentric rings at 5, 15, 25, 35px
- Target fill color: olive (`#808000`), ring color: black

### Scorecard Labels
- Top-right: shot counter `Skote: X/10 (Y/13)` — scoring shots / total shots
- Left side (y=220): "TOTAAL" with live score
- Right side (y=220): "SPAN" with team name
- Bottom: "NAAM ........" (left), "S.A.N.S.S.U. 01" (center), "KOMP ........" (right)
- ECG heartbeat chart above the S.A.N.S.S.U. text

---

## Crosshair / Sight

### Appearance
- Circle of radius 55px (white stroke)
- Four tick marks extending 10px inward/outward at cardinal points
- Center dot (2px radius)

### Movement Model

The crosshair position = **center point** + **drift offset**.

**Center point** (`cx`, `cy`):
- Controlled by player input (arrow keys, WASD)
- Clamped to game boundaries
- Persists between frames

**Drift offset** (`driftOx`, `driftOy`):
- Simulates hand shake — continuous random wandering
- Velocity-based: random acceleration each frame (±0.06), clamped to 0.5 px/frame max
- Spring force pulls offset back toward (0,0) — factor 0.005
- Drag (0.95× per frame) — ground contact dampens movement
- Bounces off boundaries
- Parameters tuned for prone position with sling support (minimal wander)

**Heartbeat effect**:
- 60 BPM (1000ms period)
- Double-bump waveform at beat positions 0-0.08 (systole) and 0.15-0.22 (dicrotic notch)
- Applies vertical displacement of ±3.5px to drift offset — prone transmits pulse through chest-to-ground contact
- Synced with ECG chart display

**Breathing rhythm** (dominant disturbance in prone):
- 4-second cycle (15 breaths/min): inhale (0–40%), natural pause (40–50%), exhale (50–100%)
- Adds vertical sway (±12px) and horizontal sway (±1px) to crosshair — chest rise/fall is the primary motion, large enough to force the player to hold breath for accurate shots
- Natural pause at 40–50% of cycle is the ideal moment to hold breath and fire
- **Hold breath** (Shift): freezes breathing waveform at current position, stabilizing aim
  - Comfortable hold up to 4.0 seconds (prone is more stable)
  - Over-holding (past 4.0s): stress ramps 0→1 over 2s, increasing drift speed (×3.0), heart amplitude (×1.8), and adding 15Hz tremor (×2.0) — stress effects are dramatic against the stable prone baseline
  - Releasing after over-hold triggers 2s recovery with exaggerated breathing (1.5× amplitude fading to 1×)
- Breath indicator bar (25×20px, left of ECG chart): shows lung fill level, green/yellow/red based on state

> **Note:** All movement parameters are tuned for prone Bisley shooting with sling support. Drift is minimal, breathing is the dominant disturbance (primarily vertical), and heartbeat is transmitted directly through chest-to-ground contact. Stress penalties are sharper to contrast with the otherwise stable platform.

### Boundaries
- Top: y=55, Bottom: y=430, Left: x=50, Right: x=582

---

## Scoring

### Algorithm (ported from Pascal `SCORE` procedure)

For each of the 10 scoring targets:
1. Find the minimum distance from any fired shot to the target center
2. Score based on distance bands:

| Distance (px) | Points |
|---------------|--------|
| 0–11          | 10     |
| 12–21         | 9      |
| 22–30         | 8      |
| 31–40         | 7      |
| 41–56         | 6      |
| >56           | 0      |

- Distance = `round(sqrt(dx² + dy²))` where dx, dy are absolute differences
- Each target scores independently based on its closest shot
- Maximum total: 100 (10 targets × 10 points)
- Score updates live after each shot

### Scoring vs Practice Shots
- A shot is classified as "scoring" if its nearest target is any of the 10 numbered targets
- A shot is "practice" if its nearest target is the center practice target (index 5, at 320,220)
- Round ends when 10 scoring shots are fired OR 13 total shots are used

---

## Controls

### During Shooting

| Input | Action | Amount |
|-------|--------|--------|
| Arrow keys | Move center point (medium) | 10px |
| W/A/S/D | Move center point (large jump) | 50px |
| Enter / Space | Fire shot | — |
| Shift | Hold breath (stabilize aim) | — |
| 1 | Enter name | — |
| 2 | Enter team | — |
| 3 | Enter competition | — |
| 4 | Calculate & display score | — |
| 5 | Restart (clear all shots) | — |
| 6 | Show help overlay | — |
| Escape | Exit to intro screen | — |
| F11 | Toggle fullscreen | — |

### During Results

| Input | Action |
|-------|--------|
| 9 | View scorecard (10s countdown) |
| 5 | Restart game |
| Escape | Exit to intro |

### Global (Any State)

| Input | Action |
|-------|--------|
| F11 | Toggle fullscreen mode |

### During Other States
- **drawrose/intro**: any key advances (except `6` → help from intro)
- **help/scorecard**: any key returns to previous state
- **Input overlays**: Enter confirms, Escape cancels

---

## Sound Effects

All procedurally generated via `AudioContext` (no audio files).

| Event | Sound |
|-------|-------|
| Fire shot | White noise burst (80ms, exponential decay from 0.4 gain) + sine sweep 150→50Hz over 200ms (gain 0.3). Gives a crack+boom gunshot effect. |
| Boundary hit | 200Hz for 50ms |
| Bump/tick | 50Hz for 30ms |

---

## ECG Heartbeat Chart

- Position: above "S.A.N.S.S.U. 01" text, at (220, 432), 200×20px
- Black background with dark red border
- Red trace (`#f00`), 1.5px line width
- Shows ~2 heartbeat cycles scrolling left-to-right
- Waveform: simplified ECG with P wave, QRS complex (Q dip, R peak, S dip), ST segment, T wave
- Synced to the same heartbeat phase that affects the crosshair

---

## Visual Style

### Colors
| Element | Color | Notes |
|---------|-------|-------|
| Background | `#000` | Black |
| Target fill | `#808000` | Olive/dark yellow |
| Target rings | `#000` | Black on olive |
| Crosshair | `#fff` | White |
| Shot marks | `#a00` | Dark red, 5px radius |
| Muzzle flash | White→yellow→orange radial gradient | 60px radius, ~100ms fade |
| Score text | `#0a0` | Green |
| Highlight text | `#ff0` | Yellow |
| User data | `#00a` | Dark blue |
| Overlays | `rgba(0,0,100,0.95)` | Dark blue, near-opaque |

### Typography
- Monospace for UI elements and labels
- Serif for titles and headings
- Sans-serif for credits

---

## Fullscreen Mode

- Toggle with F11 (intercepted with `preventDefault`, works from any game state)
- Uses Fullscreen API on the `#game-container` element
- Canvas CSS-scaled to fit viewport while preserving 4:3 aspect ratio (internal 640x480 resolution unchanged)
- `.fullscreen` CSS class: flex-centered container at 100vw/100vh, canvas border removed
- `fullscreenchange` event triggers `updateCanvasScale()` to recalculate dimensions
- Escape also exits fullscreen (browser default)

---

## Player Data

| Field | Max Length | Input Key |
|-------|-----------|-----------|
| Naam (Name) | 15 chars | 1 |
| Span (Team) | 6 chars | 2 |
| Kompetisie (Competition) | 11 chars | 3 |

Displayed on scorecard bottom section. Persists across restarts within the same session.

---

## Language

Primary: Afrikaans (preserving the character of the 1996 original)
Help screen: Bilingual (Afrikaans / English)

---

## Known Differences from Pascal Original

1. **No CGA/EGA monitor selection** — web version uses a fixed modern color palette
2. **Crosshair rendering** — drawn procedurally each frame instead of GETIMAGE/PUTIMAGE sprite
3. **Non-blocking architecture** — state machine replaces Pascal's blocking READKEY/DELAY loops
4. **Heartbeat effect** — new feature not in original; adds realistic sight pulse at 60 BPM
5. **ECG chart** — new feature; visual heartbeat timing aid
6. **Smooth drift** — velocity-based with spring return, replacing discrete ±5px jumps
7. **Center point / drift separation** — keys move center, drift wanders around it (original moved position directly)
8. **Round completion** — ends at 10 scoring shots OR 13 total (original: always 13)
9. **Live score** — updates after each shot (original: only on request or round end)
10. **Space bar** — added as alternate fire key alongside Enter
11. **Gunshot sound** — procedural crack+boom synthesis replacing simple oscillator tones
12. **Muzzle flash** — visual flash effect on firing (not in original)
13. **Fullscreen mode** — F11 toggle with aspect-ratio-preserving scaling
14. **Breathing rhythm** — new feature; adds respiratory sway cycle with hold-breath mechanic (Shift key)

---

## Future Considerations

- Mobile/touch support
- Difficulty levels (adjust drift intensity, heartbeat amplitude, BPM)
- High score persistence (localStorage)
- Multiplayer / score comparison
- Wind effect simulation
- Print scorecard
