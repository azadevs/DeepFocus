# Rule: Senior Audit & Architectural Integrity Workflow

## Core Directives
1. **Deep Audit & Blast Radius Check First**: Thoroughly inspect the existing codebase, architecture, dependencies, and potential side-effects on ALL other screens/components before modifying any code.
2. **Critique & Express Senior Opinion**: If a request contains flaws, redundancies, or potential performance/UI regressions, explicitly explain them to the user before proceeding.
3. **Offer Superior Alternatives**: Provide high-level, production-grade proposals or design options before writing code.
4. **Mandatory UI Responsiveness**: Every screen and UI component MUST be 100% responsive across all screen widths (320dp small phones to large tablets) and system display/font scales without text clipping or layout overflow.

---

## 📚 Critical Architectural Lessons & Invariants

### 1. Multi-File Blast Radius & Side-Effect Impact Audit
* **Rule**: BEFORE editing any shared layout (`AppNavigation`, `AppBottomNavigationBar`), theme (`themes.xml`), or data model, **ALWAYS perform a comprehensive multi-file impact search**. Trace all invocation sites and dependent screens to guarantee zero regressions or unexpected layout side-effects.

### 2. Room Database Schema Update Invariant
* **Rule**: Whenever modifying any Room `@Entity` class (adding/removing columns or changing types), **ALWAYS immediately increment `version` in `FocusDatabase.kt`** and verify `.fallbackToDestructiveMigration(true)` to prevent startup `IllegalStateException: Room cannot verify data integrity` crashes.

### 3. Scaffold `innerPadding` & Status Bar Insets Management
* **Rule**: Top-level `Scaffold` `innerPadding` contains both top status bar insets and bottom navigation bar insets. Handle insets precisely without double-padding status bars or cropping navigation containers.

### 4. Compact Edge-to-Edge Scrollable Content Padding
* **Rule**: When outer navigation renders edge-to-edge behind transparent navigation bars, apply `contentPadding = PaddingValues(bottom = 88.dp)` directly inside list views so content flows edge-to-edge while the last item remains fully visible above bottom icons.

### 5. Single-Responsibility UX (Management Hub vs Quick Picker)
* **Rule**: When a dedicated hub screen exists for a feature (e.g. `TasksScreen` for Task CRUD & Goals), keep modal bottom sheets on other screens (e.g. `PomodoroScreen`) lightweight, fast, and 1-tap quick selector focused.
