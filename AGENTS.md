# AGENTS.md — Game Project Agent Rules

This document defines the mandatory rules for every agent working on this game project.
Read this file **in full before** performing any task.

---

## 1. Library-First — Don't Reinvent the Wheel

Before implementing any functionality, check whether an existing library, framework, or API already provides it.

**Required process:**

1. Check the project's existing dependencies.
2. Check the standard library and framework APIs.
3. If a suitable solution exists → **use it**, do not reimplement it.
4. If no suitable solution exists → implement it as simply and maintainably as possible.
5. Before adding a new dependency → confirm it provides a clear advantage; avoid unnecessary dependencies.

> Applies to: collision detection, physics, pathfinding, input handling, animation, serialization, file I/O, networking, UI components, math, data structures, parsing, rendering, audio, and all other utilities.

---

## 2. Minimize File and Context Usage

This project is developed with limited agent usage. Read only what you actually need.

**Before starting a task:**

1. Read this `AGENTS.md`.
2. Identify the smallest set of files required for the task.
3. Open and read only those files.

**Not allowed:**

- Scanning the entire repository by default.
- Reading unrelated packages, assets, build output, or dependencies.
- Running broad searches when a targeted search is sufficient.

**Only expand scope** when the files already inspected clearly prove it is necessary.
When the task is done, stop — do not continue exploring unrelated parts of the project.

---

## 3. Make Changes Deliberately, With Future Expansion in Mind

**Design principles:**

- Every class/module has **one clear responsibility**.
- Do not stuff unrelated logic into `GameScreen`, `Player`, or a giant manager class.
- Reuse existing abstractions before introducing new ones.
- Create a new class/package **only when** there is a real, distinct responsibility that justifies it.
- Avoid both extremes:
  - ❌ Do not put everything in one file.
  - ❌ Do not create dozens of tiny classes without a real need.
- Name and place files/packages based on responsibility, not convenience.
- Keep public APIs small and intentional.
- Avoid unnecessary coupling between UI, gameplay logic, story state, and rendering.
- Design new systems so they can be **extended later without requiring a full rewrite**.

---

## 4. Do Not Over-Engineer — Build Only What the Current Task Requires

Future expansion matters, but **do not implement future systems prematurely**.

**Concrete example — implementing basic dialogue:**

- ✅ Build a clean, clear dialogue foundation.
- ❌ Do not implement a full choice/condition/save/story scripting system unless the current task requires it.

**Rule:** Create the minimum architecture needed for the current feature and its obvious next extensions — nothing more.

---

## 5. Naming Conventions and File Structure

Be consistent across the entire project:

- **Classes:** `PascalCase` — e.g. `PlayerController`, `DialogueManager`.
- **Files/modules:** follow the language and framework conventions, but stay **consistent within the project**.
- **Packages/folders:** named by functional domain — `gameplay/`, `ui/`, `story/`, `audio/`, `util/`.
- Do not place a file in the wrong folder just because it is convenient.

---

## 6. Leave No Dead Code Behind

When a task is complete:

- Remove temporary debug code, `console.log`, and `print` statements not needed in production.
- Do not leave commented-out code without an explanatory comment justifying its presence.
- Remove unused imports and dependencies.

---

## 7. When in Doubt — Ask, Don't Guess

If a task's requirements are unclear or multiple valid approaches exist:

- **Do not guess** and implement an arbitrary direction.
- Lay out the available options and your reasoning, then ask for confirmation before proceeding.
- Stopping to ask one question is always better than refactoring an entire system afterward.

---

## Summary — Pre-Task Checklist

```
[ ] Read AGENTS.md
[ ] Identified the minimum set of files needed
[ ] Checked for an existing library/API before writing custom code
[ ] Understand the task scope — will not do more than required
[ ] Design is extensible but not over-engineered
[ ] File and class names reflect their responsibility
[ ] No dead code left behind after completion
```
