# Rule: Senior Audit & Architectural Integrity Workflow

## Core Directives
1. **Deep Audit First**: Thoroughly inspect the existing codebase, architecture, UI/UX, performance, and mobile edge cases (ANR, memory leaks, responsiveness) before implementing.
2. **Critique & Express Opinion**: If a request contains flaws, redundancies, or potential performance issues, explicitly explain them to the user.
3. **Offer Superior Alternatives**: Provide high-level, production-grade proposals or design options before writing code.
4. **Mandatory UI Responsiveness**: Every screen and UI component MUST be 100% responsive across all screen widths (320dp small phones to large tablets) and system display/font scales without text clipping or layout overflow.

---

## 📚 Critical Architectural Lessons & Invariants

### 1. Room Database Schema Update Invariant
* **Rule**: Whenever modifying any Room `@Entity` class (adding/removing columns or changing types), **ALWAYS immediately increment `version` in `FocusDatabase.kt`** and verify `.fallbackToDestructiveMigration(true)` to prevent startup `IllegalStateException: Room cannot verify data integrity` crashes.

### 2. Scaffold `innerPadding` & Status Bar Insets Management
* **Rule**: Top-level `Scaffold` `innerPadding` contains both top status bar insets and bottom navigation bar insets. 
* **Fix**: Apply ONLY bottom padding from `innerPadding` at the root navigation host: `.padding(bottom = innerPadding.calculateBottomPadding())`. This prevents double top status bar padding on nested screens and avoids pushing TopAppBars down.

### 3. Compact Bottom Padding for Scrollable Containers
* **Rule**: When outer navigation handles bottom bar insets, use standard compact bottom content padding (`24.dp`) for `LazyColumn` and `verticalScroll` containers. Never stack arbitrary large bottom paddings (`100dp+`), which cause ugly empty gaps.

### 4. Single-Responsibility UX (Management Hub vs Quick Picker)
* **Rule**: When a dedicated hub screen exists for a feature (e.g. `TasksScreen` for Task CRUD & Goals), keep modal bottom sheets on other screens (e.g. `PomodoroScreen`) lightweight, fast, and 1-tap quick selector focused.
