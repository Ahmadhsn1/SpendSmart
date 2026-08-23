# Security Policy

SpendSmart stores personal financial history and reaches Cloud Firestore **directly from
the client**, with no backend server in between. That makes two areas especially
security-sensitive:

- [`firestore.rules`](firestore.rules) — the only authorization layer in the system
- anything that could leak a Firebase config, credential, or another user's data

---

## Supported versions

| Version | Supported |
| :--- | :--- |
| `main` | ✅ Actively maintained |
| Tagged releases | Latest tag only |

---

## Reporting a vulnerability

**Please do not open a public issue.**

Use GitHub's private reporting flow:

> **Security** tab → **Report a vulnerability**

That opens a [private advisory](https://docs.github.com/en/code-security/security-advisories/guidance-on-reporting-and-writing-information-about-vulnerabilities/privately-reporting-a-security-vulnerability)
visible only to the maintainer.

Please include:

- what the issue is, and which file or rule it lives in
- the steps to reproduce it, including any modified client or `curl` against the
  Firestore REST API
- what an attacker gets out of it
- a suggested fix, if you have one

**Response targets:** acknowledgement within 72 hours, an assessment within 7 days, and a
fix or a documented mitigation before the advisory is published. Credit in the advisory
and the changelog unless you'd rather stay anonymous.

---

## In scope

- Any rule in `firestore.rules` that permits a user to read or write outside
  `users/{their-own-uid}`
- Schema-validation bypasses — writing an unknown field, a non-positive `amount`, a
  category outside the seven-item allowlist, or mutating `email` / `createdAt` / `timestamp`
- Authentication bypasses, session handling flaws, or logout that leaves the previous
  user's data reachable
- Secrets, tokens, or `google-services.json` committed to the repository or leaked through
  a build artifact
- Anything in the CI workflow that could exfiltrate a secret or run untrusted code with
  repository write access

## Out of scope

- **The Firebase Android API key is not a secret.** It identifies the project; it does not
  authorize access. Access is governed entirely by Auth + Firestore rules. Finding one in
  a decompiled APK is expected behaviour, not a vulnerability.
- Findings that require a rooted device, a debugger attached to the app's own process, or
  physical access to an unlocked phone
- Vulnerabilities in Firebase, Android, or Gradle themselves — report those upstream
- Denial of service against your own Firebase project via your own quota
- Missing hardening that has no demonstrated impact (absent certificate pinning,
  `allowBackup`, and similar) — a report needs a concrete exploit path

---

## Hardening already in place

| Control | Where |
| :--- | :--- |
| Owner-only path authorization (`request.auth.uid` must match the path segment) | `firestore.rules` |
| Closed-schema writes via `keys().hasOnly([...])` | `firestore.rules` |
| Value bounds — `amount > 0 && amount <= 1e9`, `title.size() <= 120`, category allowlist | `firestore.rules` |
| Immutable `email`, `createdAt`, and `timestamp` | `firestore.rules` |
| Default-deny catch-all for any collection added later | `firestore.rules` |
| Expenses stored as a subcollection of the owner, so no query can name another user's data | data model |
| Cached UID cleared on logout (`FirestoreHelper.reset()`) | `helpers/FirestoreHelper.java` |
| Real config git-ignored; a placeholder template is committed instead | `.gitignore`, `app/google-services.json.example` |
| CI builds against the placeholder, so the pipeline cannot reach production data | `.github/workflows/android-ci.yml` |

---

## For contributors and forkers

Before you push, check that none of these are staged:

```bash
git status --porcelain | grep -E 'google-services\.json$|local\.properties|\.jks$|\.keystore$'
```

That command should print nothing. If it prints something, unstage it — every one of those
paths is already in [`.gitignore`](.gitignore), so seeing it means it was forced in.

If a credential has already been pushed, **rotate it first, then rewrite history.** Removing
the file in a later commit does not remove it from the repository — the old blob is still
reachable, and public repos are scraped continuously.
