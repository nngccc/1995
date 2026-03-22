# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Web and Android recreations of two 1990s South African Bisley shooting simulator games:

- **TARGET12** (1995): Single-player National Bisley competition — 11 targets, web + Android
- **SHOOT1** (1997): Team-based SA Quadrangular championship with AI opponents — web only

## Development Workflow

This project uses **spec-driven development**. Each game has a `SPEC.md` that is the single source of truth for all behavior, layout, physics, and mechanics.

1. **Spec first**: All changes start by updating the relevant `SPEC.md`. Iterate on the spec until the desired behavior is fully described.
2. **Update code to match spec**: After changing the spec, update the existing code on all platforms (web + Android where applicable) so it matches the new spec. Read both the spec and the current code to make targeted changes. For new features or major rewrites, the spec is the primary reference; for incremental changes, read the existing code and modify it to align with the updated spec.
3. **Spec = authority**: If the code and spec disagree, the spec is correct and the code should be updated to match.

## Build Commands

### Web games (both)
No build step. Open `target12/web/target12.html` or `shoot1/web/shoot.html` directly in a browser.

### Android (target12 only)
```bash
cd target12/android
./gradlew assembleDebug        # Build debug APK
./gradlew installDebug         # Build and install to connected device/emulator
./gradlew lint                 # Run Android lint
```

No automated test suite exists; testing is manual gameplay.

## Architecture

### Web games
Each game is a single self-contained HTML file (~1300–1500 lines) using vanilla JavaScript, HTML5 Canvas 2D, and Web Audio API. Internal resolution is 640×480 (matching original BGI), CSS-scaled to viewport.

### Android app (target12)
Kotlin + Jetpack Compose. Key packages under `target12/android/app/src/main/java/za/co/target12/`:

- `GameState.kt` — Central mutable state holder for the entire game
- `GameConstants.kt` — All numeric constants (canvas size, target positions, physics params)
- `physics/` — DriftPhysics (hand shake), HeartbeatPhysics (pulse), BreathingPhysics (breath cycle)
- `scoring/ScoringEngine.kt` — Distance-based point calculation
- `audio/GameAudio.kt` — Procedural sound synthesis (no audio files)
- `input/TouchInputState.kt` — Multi-touch joystick + buttons
- `ui/GameScreen.kt` — Compose root with frame loop (`withFrameNanos`)
- `ui/rendering/` — Separate renderers for crosshair, scorecard, ECG chart, muzzle flash, touch controls, etc.

### Shared patterns across platforms
- **State machine**: `intro` → `shooting` → `results`/`scorecard` → `intro`
- **Physics**: Drift (Brownian motion + spring return), heartbeat (sinusoidal pulse at ~60 BPM), breathing (4–5s cycle with hold mechanic that increases stress)
- **Canvas scaling**: Always 640×480 virtual resolution, scaled to fit viewport/screen
- **Procedural audio**: All sounds synthesized from oscillators/noise, no audio files
- **No external runtime dependencies**: Everything is self-contained
