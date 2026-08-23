# Changelog

All notable changes to this project are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

Planned work is tracked in the [roadmap](README.md#roadmap).

---

## [1.0.0] — 2026-08-24

First public release. A complete, working expense tracker on Firebase with no backend
server of its own.

### Added

**Authentication**
- Email/password sign-up and sign-in via Firebase Authentication.
- `SplashActivity` session gate — a returning user is routed straight to the dashboard
  instead of the login form.
- Profile document written to `users/{uid}` on sign-up (`name`, `email`, `createdAt`).
- Logout that clears both the Firebase session and the cached UID in `FirestoreHelper`.

**Expenses**
- Full create / read / update / delete against `users/{uid}/expenses`.
- Real-time reads — every list is bound to a Firestore `addSnapshotListener` ordered by
  `timestamp DESC`, so changes appear on every signed-in device without a refresh.
- Partial updates sent as an explicit field map, so editing a note cannot rewrite
  `timestamp` and reshuffle the list.
- Delete guarded by a `MaterialAlertDialogBuilder` confirmation.
- Seven categories with per-category emoji and accent colour, defined once in
  `CategoryHelper` and re-validated server-side.
- Native `DatePickerDialog` on a non-focusable date field, so the soft keyboard never
  competes with the dialog.

**Dashboard**
- Time-aware greeting derived from `Calendar.HOUR_OF_DAY`.
- Live total spent, transaction count, and highest single expense — folded from the
  expense stream rather than stored as counters that can drift.
- Five most recent expenses via a `limit(5)` listener, with a *See All* link that switches
  the bottom-navigation tab.

**Expense list**
- Instant client-side search across title **and** category, matching against a retained
  unfiltered backing list — zero Firestore reads per keystroke.
- Designed empty states on every list.

**Profile**
- Aggregate stats plus *Member Since*, read from the Firebase Auth token's creation
  timestamp.

**Security**
- `firestore.rules` as the sole authorization layer: owner-only path access, closed-schema
  writes via `keys().hasOnly([...])`, value bounds on `amount` and `title`, a category
  allowlist, immutable `email` / `createdAt` / `timestamp`, and a default-deny catch-all
  for any collection added later.
- Expenses modelled as a subcollection of the owning user, so authorization happens on the
  path and no query can name another user's data.

**Design**
- Material 3 throughout — themed `TextInputLayout`s, `MaterialButton` styles, and a
  pill-indicator `BottomNavigationView`.
- Centralised token set in `colors.xml` and `dimens.xml`; no hardcoded colours or
  dimensions in layouts.
- Shared 135° indigo gradient across the dashboard and auth headers, differing only in
  corner radius.
- `values-v27/` overlay for `android:windowLightNavigationBar`, so the API-27 attribute is
  layered by the resource system instead of suppressed with `tools:targetApi`.
- RTL-ready: `supportsRtl="true"` with `Start`/`End` padding rather than `Left`/`Right`.

**Project**
- GitHub Actions CI running `assembleDebug` then `lintDebug` on every push and pull
  request, uploading both the debug APK and the lint HTML report. CI builds against a
  placeholder `google-services.json`, so the pipeline cannot reach production data.
- `.editorconfig` and `.gitattributes` pinning encoding, indentation, and LF line endings.
- MIT license, contribution guide, security policy, issue and pull-request templates, and
  weekly Dependabot updates for Gradle and Actions.

### Security

- The real `app/google-services.json` is git-ignored. `app/google-services.json.example`
  documents the expected shape instead, so a fork never inherits another project's
  identity.
- Local scratch output — device captures, uiautomator dumps, and `local.properties` — is
  excluded from version control.

---

[Unreleased]: https://github.com/Ahmadhsn1/SpendSmart/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/Ahmadhsn1/SpendSmart/releases/tag/v1.0.0
