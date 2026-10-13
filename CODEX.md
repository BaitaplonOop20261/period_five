# AGENTS.md - Game Project Rules

Read this file fully before each task. Keep changes minimal, maintainable, and within the requested scope.

## Before coding

- Inspect existing dependencies, standard-library/framework APIs, and project abstractions first. Reuse suitable solutions; do not reinvent utilities (collision, physics, input, animation, I/O, UI, rendering, audio, etc.).
- Add a dependency only when it provides a clear advantage.
- Inspect only the smallest set of relevant files. Do not scan unrelated packages, assets, build output, or dependencies, or run broad searches without need. Expand scope only when evidence requires it.

## Design

- Give each class/module one clear responsibility; keep public APIs small.
- Reuse existing abstractions before adding classes/packages. Add a new one only for a genuine distinct responsibility.
- Keep UI, gameplay, story, and rendering appropriately decoupled.
- Use domain-based folders (`gameplay/`, `ui/`, `story/`, `audio/`, `util/`) and consistent naming (`PascalCase` classes; project/language conventions for files and modules).
- Build only what the current task needs, with a clean foundation for obvious next extensions. Do not implement premature systems or create giant managers/tiny needless classes.

## Completion

- Remove temporary debug output, unused imports/dependencies, and unjustified commented-out code.
- If requirements are unclear or multiple approaches materially differ, explain the options and ask before making a large change; do not guess.
- Stop exploring when the task is complete.

Checklist: read this file; inspect only relevant files; check existing APIs; stay in scope; avoid over-engineering; follow naming/structure conventions; leave no dead code.
