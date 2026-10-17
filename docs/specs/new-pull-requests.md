---
jira: XFA-3577
feature: new-pull-requests
status: in-flight
repo: xfa-keycloak-login-hint-mapper
---

# New pull requests: assign to author

Jira: https://xfatech.atlassian.net/browse/XFA-3577

## Problem

GitHub does not assign a pull request to the person who opened it. Assignee-based
views and filters miss work in flight in this repo.

## Change

`.github/workflows/new-pull-requests.yml`, on `pull_request: opened`, assigns the
PR to its author. Bots are skipped; a failed assign (for example an author outside
the org) only warns. `ubuntu-slim`, API only. The workflow grants nothing at
workflow level; only the `assign` job gets `pull-requests: write` + `issues: write`.
Not on the merge path.

## Decisions

- 2026-10-17, Lars: private repos call the shared devops workflow instead of a
  copy. This repo is public and cannot call a workflow in private devops, so it
  keeps this inline copy of the assign job. No board job: it needs
  `PROJECT_BOARD_TOKEN`, which GitHub does not pass to PRs from forks.
## How to validate

- `actionlint .github/workflows/new-pull-requests.yml` passes.
- After merge, open a PR here: "Assign to author" assigns its author.
- A Renovate or Dependabot PR: the job logs `Skipping bot author`.
