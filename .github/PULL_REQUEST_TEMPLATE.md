## What this changes

<!-- One or two sentences. What is different after this PR? -->

## Why

<!-- The problem, or a link to the issue. -->

Closes #

## Type of change

- [ ] 🐛 Bug fix — no API or behaviour change beyond the fix
- [ ] ✨ Feature — new user-facing capability
- [ ] 🎨 UI / design
- [ ] ♻️ Refactor — no behaviour change
- [ ] 🔒 Security — rules, auth, or data exposure
- [ ] 📝 Docs only
- [ ] 🔧 Build / CI / dependencies

## Screenshots

<!-- Required for any UI change. Before and after, side by side if you can. -->

| Before | After |
| :---: | :---: |
|  |  |

## How this was tested

<!-- Device or emulator, API level, and the path you actually walked through. -->

- Device / emulator:
- API level:
- Steps exercised:

## Checklist

- [ ] `./gradlew assembleDebug lintDebug` passes locally
- [ ] Run on a device or emulator — not just compiled
- [ ] No hardcoded strings, colours, or dimensions; everything added to `values/`
- [ ] `firestore.rules` updated if the data shape changed (a new model field without a
      rules change makes every write fail with `PERMISSION_DENIED`)
- [ ] All Firestore I/O goes through `FirestoreHelper` — no direct `FirebaseFirestore` use
      in an activity, fragment, or adapter
- [ ] Any API level above `minSdk 24` is handled with a `values-vNN/` overlay, not a lint
      suppression
- [ ] `app/google-services.json`, `local.properties`, keystores, and credentials are **not**
      in the diff
- [ ] Commits are scoped and the branch is rebased onto `main`

## Anything reviewers should look at closely

<!-- Trade-offs you made, a decision you are unsure about, or a follow-up you left out. -->
