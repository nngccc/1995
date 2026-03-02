# SHOOT — SA Quadrangular Bisley Simulator (1997)

## Overview

Web-based recreation of a South African Quadrangular Bisley championship simulator, originally written in Turbo Pascal 7.0 by Nico Gerber (1997). Sequel to TARGET12 (1995); adds AI team competition with 4 nations (RSA, ENG, GER, USA), three shooting distances, a multi-card structure, and an enhanced HUD with spotting scope. Single HTML file, no dependencies.

## Origin

- **Original**: `SHOOT1.PAS` — Turbo Pascal 7.0, BGI graphics, 640×480
- **Author**: Nico Gerber, Grade 12, 1997
- **Organization**: S.A.N.S.S.U. (Suid-Afrikaanse Nasionale Skietskyfskiet-Unie)
- **Competition**: SA Quadrangular Bisley, 0.22" caliber rifles
- **Predecessor**: TARGET12.PAS (1995), converted at `projects/1995/shoot/`

## Technical Stack

- Single file: `shoot.html`
- Canvas 2D (640×480, matching original BGI resolution)
- Vanilla JavaScript, no frameworks or build tools
- `requestAnimationFrame` game loop
- `AudioContext` oscillator for sound effects
- HTML overlays for text input (name, team selection)

---

## Game States

```
menu ──────────────────────────────────────────────────┐
  │  [F8 practice]                [F9 competition]     │
  ▼                               ▼                    │
shooting ←─────────────────── shooting                 │
  │                                                    │
  ▼                                                    │
results (card complete)                                │
  │  [more cards remain]  [last card]                  │
  ▼                       ▼                            │
shooting              standings (competition mode)     │
                          │                            │
                      standings ─────────────────────► │
                                       [ESC]           │
help ⇄ shooting (overlay, any state during shooting)
```

### 1. `menu` (KIESBLAD — Selection Screen)

- Displays player name, chosen team, and selected distance
- Arrow/function keys cycle options (see Controls → Menu)
- F8 starts practice mode; F9 starts competition mode
- ESC exits

### 2. `shooting` (Main Game)

- Full HUD with crosshair + target card
- Player fires cards of shots at the target board
- Spotting scope available with TAB
- Card ends when all scoring shots for that card are fired
- On card end → `results`

### 3. `results` (Card Complete)

- Shows score for the just-completed card and running total (TELT)
- If more cards remain for this distance → return to `shooting` (next card)
- If all cards for all distances are done and competition mode → `standings`
- Otherwise → `menu`

### 4. `standings` (Competition Team Table)

- Shown after the final card in competition mode
- Displays AI-generated scores for all 4 teams alongside the player's team
- ESC or any key → `menu`

### 5. `help` (Overlay)

- Semi-opaque overlay on top of `shooting`
- Lists all controls (bilingual Afrikaans/English)
- Any key → return to `shooting`

---

## Screen Layout

640×480 canvas. Five functional zones:

```
┌──────────────────────┬────────┬──────────────────────────┐
│  SCRA — Team Scores  │ FLAGS  │  SCRB — Competition      │
│  (x:10–155, y:70–160)│(x:220  │  (x:320–540, y:70–160)   │
│  Country  Total      │–310)   │  Country  Individual     │
├──────────────────────┤        ├──────────┬───────────────┤
│  SPOTTING SCOPE      │        │  DIST    │  TIME LIMIT   │
│  (x:10–210, y:170–   │        │  label   │  (x:550–635)  │
│   460) circle r≈40   │        │          │               │
│  at (110,260)        ├────────┴──────────┴───────────────┤
│                      │  KAART — Shooting Card             │
│  label (y:170–200)   │  (x:320–635, y:210–460)           │
│  "VERKYKER" / scope  │                                   │
│  shot dots plotted   │  [sighter1] [sighter2]   y≈288    │
│  at (105,280) base   │  ────────────────────── y=310     │
│                      │  [target1]  [target2]    y≈331    │
│                      │  [target3]  [target4]    y≈374    │
├──────────────────────┤                                   │
│  HELP / OPTIONS      │  [shots/card]  [card N / CRD]     │
│  F1/F2/F3/F5–F9/ESC  │  SIGHTERS / SCORING SHOTS / CARD  │
└──────────────────────┴───────────────────────────────────┘
```

---

## Menu (KIESBLAD)

| Key | Action |
|-----|--------|
| F2  | Enter player name (text input, live update on card) |
| F3  | Cycle team: South Africa → England → Germany → United States → … |
| F5  | Select distance: 25m |
| F6  | Select distance: 50m (default) |
| F7  | Select distance: 100 yards |
| F8  | Start practice mode (no AI, no standings) |
| F9  | Start competition mode (KOMPX = TRUE; AI scores generated via CALC) |
| ESC | Exit |

---

## Shooting Mechanics

### Crosshair

Same physics model as TARGET12 (see `projects/1995/shoot/SPEC.md`):
- Position = **center point** + **drift offset**
- Center point controlled by arrow keys (player input)
- Drift offset: velocity-based random wandering, spring return, drag
- Heartbeat: 60 BPM, double-bump waveform, vertical displacement ±3.5px
- Breathing: 4-second cycle with inhale/pause/exhale phases; Shift to hold breath

#### Crosshair Bounds (pixel-exact from source)

| Edge | Coordinate |
|------|-----------|
| Left (LG)   | x = 400 |
| Right (RG)  | x = 570 |
| Top (BG)    | y = 248 |
| Bottom (OG) | y = 414 |

#### Starting Position

Crosshair starts at (470, 280) — centered over the sighter row.

### Target Card (KAART)

Background bar drawn from (435, 255) to (535, 405). Each target is an 8px-radius circle.

#### Target Positions (pixel-exact)

| Target | Role | x   | y   |
|--------|------|-----|-----|
| Sighter 1 | Aim-in, no score | 463 | 288 |
| Sighter 2 | Aim-in, no score | 507 | 288 |
| Scoring 1 | Counts for score  | 463 | 331 |
| Scoring 2 | Counts for score  | 507 | 331 |
| Scoring 3 | Counts for score  | 463 | 374 |
| Scoring 4 | Counts for score  | 507 | 374 |

#### Scoring Zone Detection

A shot registers as a scoring attempt only when fired within:
- x: 435–535
- y: 310–405

Shots outside this zone (or in the sighter strip y < 310) are sighters and do not count toward the card's scoring quota.

### Cards per Distance

| Distance | Cards (CRD) | Scoring shots/card | Max pts/card |
|----------|-------------|-------------------|--------------|
| 25m      | 6           | 10                | 40           |
| 50m      | 3           | 20                | 40           |
| 100y     | 3           | 20                | 40           |

Running total `TELT = TELA + TELB + TELC` (sum of last 3 card scores, matching Pascal variable names).

---

## Scoring

Only `SCORE_50` is used for all three distances (the broader `SCORE` procedure in the source is dead code — never called):

| Distance from target center (px) | Points |
|----------------------------------|--------|
| 0–1   | 10 |
| 2–3   |  9 |
| 4–5   |  8 |
| 6–7   |  7 |
| 8–9   |  6 |
| 10–11 |  5 |
| 12–13 |  4 |
| > 13  |  0 |

Distance = `Math.round(Math.sqrt(dx² + dy²))` where dx, dy are absolute pixel differences from shot to nearest scoring target center.

Each scoring shot is matched to the closest target on the card. Maximum per shot: 10 points.

---

## Spotting Scope (VERKYKER)

- Toggle with **TAB** during shooting
- Shows zoomed view of one sighter target
- Base position: (105, 280) — circle of radius ≈ 40px
- **Keys 1 / 2** (while scope visible): switch between sighter 1 and sighter 2
- Previous shot placements are plotted as 3px circles offset from (105, 280) using SXPLOT/SYPLOT coordinates
- Exiting scope (TAB again) returns to normal card view

---

## Competition AI (CALC)

### Formula (ported from Pascal `CALC` procedure)

For each of the 4 AI teams (RSA, ENG, GER, USA), 5 individual members, 3 cards per distance:

```
B = 200 - (G[i] ± Math.floor(Math.random() * FAKTOR))
B = Math.max(0, Math.min(200, B))   // clamp 0–200
```

Where:
- `G[i]` is the handicap value for team member `i`
- `FAKTOR` controls spread (distance-dependent)
- Team total = sum of all 5 members across all cards for the distance

### Hardcoded Team Data (JS objects mirroring Pascal constants)

```js
const TEAMS = {
  RSA: { name: 'Suid-Afrika',     flag: 'rsa', handicaps: [/* 5 values */] },
  ENG: { name: 'Engeland',        flag: 'eng', handicaps: [/* 5 values */] },
  GER: { name: 'Duitsland',       flag: 'ger', handicaps: [/* 5 values */] },
  USA: { name: 'Verenigde State', flag: 'usa', handicaps: [/* 5 values */] },
};
```

Exact handicap values must be read from `SHOOT1.PAS` arrays during implementation.

---

## Controls

### During Menu (KIESBLAD)

| Key | Action |
|-----|--------|
| F2  | Enter name |
| F3  | Cycle team |
| F5  | Distance 25m |
| F6  | Distance 50m |
| F7  | Distance 100y |
| F8  | Start practice |
| F9  | Start competition |
| ESC | Exit |

### During Shooting

| Input | Action |
|-------|--------|
| Arrow keys | Move crosshair center (medium step) |
| Enter / Space | Fire shot |
| Shift | Hold breath (stabilize aim) |
| TAB | Toggle spotting scope |
| 1 / 2 | (Scope open) Switch sighter view |
| F1 | Show help overlay |
| ESC | Abort to menu |

### During Results / Standings

| Input | Action |
|-------|--------|
| Any key / ESC | Advance (next card, or back to menu) |

### Global

| Input | Action |
|-------|--------|
| F11 | Toggle fullscreen |

---

## Sound Effects

All procedurally generated via `AudioContext` (no audio files).

| Event | Sound |
|-------|-------|
| Fire shot | White noise burst (80ms, exponential decay) + low sine sweep — crack+boom |
| Boundary hit | Short high-frequency tone (200Hz, 50ms) |
| Card complete | Distinct chime or sequence |
| Scope toggle | Subtle click (50Hz, 30ms) |

---

## Flags (VLAG Panel)

Flags for all four competing nations, drawn with filled polygons and rectangles (matching the Pascal BGI FILLPOLY approach):

| Nation | Key colors |
|--------|------------|
| South Africa (RSA) | Green, gold, black, white, red |
| England (ENG) | White background, red St George's cross |
| Germany (GER) | Three horizontal bands: black, red, gold |
| United States (USA) | Red/white stripes, blue canton with white stars |

Flags displayed in the VLAG panel (approx x:220–310, y:70–200). Active/player team highlighted.

---

## Visual Style

### Colors

| Element | Color | Notes |
|---------|-------|-------|
| Background | `#000` | Black |
| Card background bar | `#808000` | Olive |
| Target circles | `#000` | Black on olive bar |
| Crosshair | `#fff` | White |
| Shot marks (scoring zone) | `#a00` | Dark red, 3px radius |
| Score text | `#0a0` | Green |
| Highlight / distance label | `#ff0` | Yellow |
| Panel borders | `#fff` or `#888` | Thin lines separating HUD zones |
| Scope circle | `#000` bg, `#0a0` border | Dark with green ring |
| Overlays | `rgba(0,0,100,0.95)` | Dark blue, near-opaque |

### Typography

- Monospace for HUD labels, scores, counters
- Capitals for panel headers (matching Pascal `OUTTEXT` style)

---

## Differences from Pascal Original

1. **No CGA/EGA monitor selection** — web version uses a fixed modern color palette
2. **Non-blocking architecture** — state machine replaces Pascal's blocking `READKEY`/`DELAY` loops
3. **Crosshair rendering** — drawn procedurally each frame instead of `GETIMAGE`/`PUTIMAGE` sprite XOR
4. **Smooth drift** — velocity-based with spring return, replacing discrete pixel jumps
5. **Breathing rhythm** — Shift key hold-breath mechanic added (not in original)
6. **Heartbeat effect** — ECG chart and sight pulse added (not in original)
7. **Muzzle flash** — visual flash on firing (not in original)
8. **Fullscreen mode** — F11 toggle with aspect-ratio-preserving scaling
9. **Live score** — updates after each shot (original: only on card completion)
10. **Dead `SCORE` procedure omitted** — only `SCORE_50` is implemented (matches actual game behavior)
11. **Flag graphics** — canvas polygon drawing replaces BGI `FILLPOLY` calls

---

## Language

Primary: Afrikaans (preserving the character of the 1997 original).
Key terms used in UI:

| Afrikaans | English |
|-----------|---------|
| KAART | Card |
| VERKYKER | Spotting scope (lit. "binoculars") |
| PROEFSKOTE | Sighter shots |
| AFSTAND | Distance |
| TYDSLIMIET | Time limit |
| TELLING | Score |
| SPAN | Team |
| NAAM | Name |
| RONTES | Rounds |

---

## Future Considerations

- Mobile/touch support (virtual crosshair joystick)
- Difficulty levels (adjust FAKTOR for AI spread, drift intensity)
- Animated flag rendering (waving effect)
- High score persistence (localStorage)
- Print scorecard
- Wind effect simulation (not in original)
