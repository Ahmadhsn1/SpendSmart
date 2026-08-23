# Contributing to SpendSmart

Thanks for taking the time to look at this. SpendSmart is a small, deliberately
plain-Java Android codebase — contributions of any size are welcome, from a typo in a
string resource to a new screen.

---

## Ground rules

1. **Open an issue before a large PR.** For anything beyond a bug fix, a short issue
   first saves both of us from a rewrite. Use the templates in
   [`.github/ISSUE_TEMPLATE`](ISSUE_TEMPLATE).
2. **Never commit `app/google-services.json`.** It is git-ignored for a reason. If you
   need to change its shape, edit `app/google-services.json.example` instead.
3. **Never commit credentials, device dumps, or `local.properties`.** If you accidentally
   stage one, see [SECURITY.md](SECURITY.md) before pushing.
4. **The build must stay lint-clean at the error level.** CI fails on any Android Lint
   *error*, and that gate does not get relaxed to land a change.

---

## Setting up

Follow [Getting started](../README.md#getting-started) in the README — you need your own
Firebase project, since the maintainer's config is not in the repo.

To verify only that the project compiles, the placeholder config is enough:

```bash
cp app/google-services.json.example app/google-services.json
./gradlew assembleDebug
```

<details>
<summary>Windows / Git Bash</summary>

```bash
JAVA_HOME="/c/Program Files/Android/Android Studio/jbr" ./gradlew assembleDebug
```

</details>

---

## Before you push

Run exactly what CI runs:

```bash
./gradlew assembleDebug lintDebug
```

Both must pass. The lint report is written to
`app/build/reports/lint-results-debug.html` — open it if the task fails and the console
output is not specific enough.

If your change touches a screen, please attach a before/after screenshot to the PR.

---

## Code style

The repository ships an [`.editorconfig`](../.editorconfig); please make sure your editor
honours it. Beyond that:

| | |
| :--- | :--- |
| **Indentation** | 4 spaces in Java, 2 in XML / YAML / JSON |
| **Line endings** | LF — enforced by [`.gitattributes`](../.gitattributes) |
| **Encoding** | UTF-8, final newline, no trailing whitespace |
| **Java naming** | `PascalCase` types, `camelCase` members, `UPPER_SNAKE` constants |
| **View fields** | Bound through `viewBinding`; do not reintroduce `findViewById` |
| **Layout IDs** | `snake_case`, prefixed by widget role — `tv_total`, `btn_save`, `et_amount` |
| **Resources** | Add to `values/` — do **not** hardcode a colour, dimension, or string in a layout or in Java |
| **New API levels** | If an attribute needs API > 24, layer it in a `values-vNN/` overlay rather than suppressing lint |

### Where things go

| Adding… | Put it in |
| :--- | :--- |
| A screen | `activities/` (own window) or `fragments/` (inside `MainActivity`) |
| Firestore read/write | `helpers/FirestoreHelper.java` — **all** database I/O lives there, nowhere else |
| A category, emoji, colour, or currency rule | `helpers/CategoryHelper.java` |
| A field on an expense | `models/Expense.java` **and** the `hasOnly([...])` list in [`firestore.rules`](../firestore.rules) |

> Adding a field to the model without adding it to the rules will make every write fail
> with `PERMISSION_DENIED`. That is the rules working correctly — update both.

---

## Commits

Short, imperative subject lines, ideally [Conventional Commits](https://www.conventionalcommits.org):

```
feat(dashboard): scope the monthly total to the current month
fix(adapter): keep the search filter after a delete
docs(readme): correct the minSdk badge
chore(deps): bump material to 1.12.0
refactor(helpers): extract the expense collection path
```

One logical change per commit. Squash the "fix typo" follow-ups before opening the PR.

---

## Branches

```
feat/<short-slug>      fix/<short-slug>      docs/<short-slug>      chore/<short-slug>
```

Branch from `main`, rebase onto `main` before requesting review.

---

## Pull requests

Fill in [the PR template](PULL_REQUEST_TEMPLATE.md). A PR is ready for review when:

- [ ] `./gradlew assembleDebug lintDebug` passes locally
- [ ] It has been run on a device or emulator (API 24 and API 34 if the change is UI-level)
- [ ] No hardcoded strings, colours, or dimensions were introduced
- [ ] `firestore.rules` was updated if the data shape changed
- [ ] Screenshots are attached for UI changes
- [ ] `google-services.json`, `local.properties`, and any credentials are **not** in the diff

---

## Good first issues

If you want something concrete, the [roadmap](../README.md#roadmap) doubles as a task
list. These are the most self-contained items:

- Detaching the `ListenerRegistration`s in `onDestroyView()`
- Replacing `CategoryHelper.formatAmount()` with a locale-aware formatter
- Unit tests for `ExpenseAdapter.filter()` — it is a pure function over two lists
- A `values-night/` colour set for dark theme

---

## Reporting security issues

Please **do not** open a public issue for anything involving credentials, the Firestore
rules, or data exposure. Follow [SECURITY.md](SECURITY.md).
