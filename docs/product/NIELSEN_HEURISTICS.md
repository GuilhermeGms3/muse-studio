# Nielsen Heuristics Applied To Muse Studio

## 1. Visibility Of System Status

Muse Studio should always show:

- Current instrument.
- Active tab.
- Session state.
- Timer when a session is active.
- BPM and metronome state when relevant.
- Data save/import/backup status.

The status bar is appropriate for persistent low-noise state.

## 2. Match Between System And The Real World

Use musician language, not admin language.

Prefer:

- Sessão
- Repertório
- Técnica
- Habilidade
- Gravação
- Diário
- Riff
- Revisão
- Afinação
- Tom
- BPM

Avoid:

- Dashboard
- Analytics
- KPI
- Workspace settings
- Organization
- Team
- Billing

## 3. User Control And Freedom

The user must be able to:

- Open multiple tabs.
- Close tabs.
- Return to Home.
- Toggle sidebar.
- Toggle inspector.
- Search globally.
- Pause or finish sessions.
- Recover from imports, backup, and restore flows.

## 4. Consistency And Standards

Repeated objects should behave consistently:

- Lessons, songs, exercises, skills, projects, and diary entries can open as tabs.
- Cards represent compact object summaries.
- Inspector shows supporting context.
- Contextual menus change by macro context.
- Search results open the most specific target.

## 5. Error Prevention

Prevent:

- Starting the wrong instrument session.
- Losing session notes.
- Importing unsupported files without clear feedback.
- Restoring backup without confirmation.
- Recording without knowing what context it will attach to.
- Changing skill state without visible criteria.

## 6. Recognition Rather Than Recall

The product should help the user recognize:

- Current objective.
- Recent tabs.
- Continue point.
- Active song.
- Active skills.
- Available practice blocks.
- Related content for selected objects.

Global search reduces menu recall, but contextual menus reduce the need to search.

## 7. Flexibility And Efficiency Of Use

Support both exploration and fast operation:

- Sidebar for browsing.
- Tabs for parallel work.
- Command palette for direct navigation.
- Keyboard shortcuts for repeated actions.
- Inspector for quick reference.
- Docked tools for metronome and recording.

## 8. Aesthetic And Minimalist Design

Minimalist does not mean empty. For Muse Studio it means:

- Compact density.
- Clear hierarchy.
- No corporate KPI wall.
- No marketing hero.
- No decorative excess.
- Cards and panels with specific work purpose.

## 9. Help Users Recognize, Diagnose, And Recover From Errors

Errors should state:

- What failed.
- Whether user data is safe.
- What action can be retried.
- Which context was affected.

Examples:

- Import failed for this file type.
- Backup restore could not complete.
- Recording could not be saved.
- API unavailable; local workspace remains open.

## 10. Help And Documentation

Help should be contextual:

- A skill explains its next requirement.
- A session explains what feedback will affect.
- Import explains supported file types.
- Review explains why something is due.
- Empty states point to the next action.

Avoid long tutorial pages as the primary experience.

