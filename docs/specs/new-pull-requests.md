---
jira: XFA-3577
feature: new-pull-requests
status: in-flight
repo: xfa-keycloak-login-hint-mapper
---

# New pull requests: assign to author

Jira: https://xfatech.atlassian.net/browse/XFA-3577

## Problem

GitHub does not assign a pull request to the person who opened it. The assignee
field stays empty, so board columns, filters and notifications that key off
assignee silently miss work in flight in this repo. Most gl-ventures repos
already run `.github/workflows/new-pull-requests.yml` for this (for example
`xfa-api`, `xfa-organizations-service`); this repo lacked it.

## Change

`.github/workflows/new-pull-requests.yml`, on `pull_request: opened`:

- `assign`: assigns the PR to its author. Bot authors (Renovate, Dependabot) are
  skipped; an author who cannot be assigned gives a warning, not a red check.
  Runs on `ubuntu-slim` (API only, no checkout).
- Permissions: `permissions: {}` at workflow level; `assign` alone gets
  `pull-requests: write` + `issues: write`, so a job added later starts with no
  write access.

Not on the merge path: no required check is added, renamed or removed.

## Decisions

- 2026-10-17, Lars: finish the open rollout PRs (this one and the other open
  "assign new pull requests to their author" PRs across gl-ventures) and send
  them to Code Review. All open rollout PRs take the job-level permissions
  variant first used in `xfa-versions-service#420`, so the open copies are identical.

## How to validate

- `actionlint .github/workflows/new-pull-requests.yml` passes.
- After merge, open any PR in this repo: the "Assign to author" check runs and
  passes, and the PR's assignee is its author.
- A Renovate or Dependabot PR: the job logs `Skipping bot author` and passes.

## Acceptance criteria

- [x] actionlint clean
- [ ] First PR opened after merge is assigned to its author

## Left

- Copies already merged in other repos still grant `pull-requests`/`issues: write`
  at workflow level; they could get the same job-level hardening (not changed here).
