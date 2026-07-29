# Development Guidelines for AI Agents

This document records conventions and hard-won rules for this repository.

## CI: never use `paths-ignore` on the `pull_request` trigger

`CI` is a **required status check** on `main`. GitHub treats two superficially
similar situations completely differently, and the distinction is the whole
reason this workflow is shaped the way it is:

| Cause | Result |
| --- | --- |
| A workflow is skipped by path filtering, branch filtering, or a commit message | Associated checks stay in a **"Pending" state and block merging** |
| **A job is skipped by a conditional** | **The job reports "Success"** |
| A job depends on a failed job | The dependent job is skipped and **may not block merging** |

*(Source: GitHub Docs, "Troubleshooting required status checks".)*

So `paths-ignore` on `pull_request` would not produce a skipped check — it stops
the workflow triggering at all, the check run is never created, and branch
protection waits forever for something that will never arrive. The pull request
sits at `mergeStateStatus: BLOCKED` with nothing anyone can do to clear it.
Posting a status through the statuses API does not help either: GitHub rejects
hand-posted statuses for merge purposes, and it leaves a green that no build
produced.

**The rule:** any path filtering that decides whether work is *worth doing* must
be expressed as a **job-level `if:`**, never as a filter on the `pull_request`
trigger.

### How `.github/workflows/ci.yml` implements it

- `pull_request` has **no `paths-ignore`**; it always triggers, so a `CI` check
  run always exists.
- A separate `detect` job diffs the PR against its base and exposes a
  `docs_only` output.
- The `ci` job is gated on that single condition. When it is skipped, GitHub
  reports the required `CI` check as **Success**, so a docs-only PR merges.
- `push` to `main` **keeps** its `paths-ignore`. Nothing requires a check there,
  and a docs-only commit needs no deploy.
- `merge_group` always does the real work. It is the last gate before `main`, so
  it is never inspected — only `pull_request` is.

### Invariants to preserve when editing this workflow

- **Keep the job named `CI`.** That string is the required context name;
  renaming it silently un-protects `main`.
- **Keep `!cancelled()` in the `ci` job's `if:`.** Without it a *failed* `detect`
  job would skip `ci`, and a skipped job reports Success — a broken detector
  would wave unbuilt code straight through. With it, `docs_only` is empty on
  failure, which is not `'true'`, so the full pipeline runs. **Fail safe, never
  fail open.**
- **Fail safe generally.** An empty diff, or any file that is neither `*.md` nor
  under `docs/`, must run the full pipeline. Two traps that must *not* count as
  docs-only: `x.md.go` and `notdocs/a.go`.
- **`fetch-depth: 0`** on the `detect` checkout — the base-vs-HEAD diff needs
  full history.
- **Do not gate the merge-queue teardown step**; it keeps `always()` so a stack
  that got created is always terminated.
- **No untrusted input in `run:`.** `pull_request.base.sha` is passed via `env:`
  and quoted, never interpolated inline.

Precedent and rationale: `gl-ventures/xfa-organizations-service#508`.

> This does **not** apply to repos where markdown *is* the shipped product
> (`xfa-docs`, `xfa.tech`, `xfa-guides`). There, skipping the build on a `.md`
> change would skip building the actual site — the premise is inverted.
