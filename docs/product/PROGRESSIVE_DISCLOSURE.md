# Progressive Disclosure

Progressive disclosure is mandatory in Muse Studio. The product is powerful, but the user should not face the entire system at the same time.

## Disclosure Layers

### Layer 1: Orientation

Show only the current instrument, active macro context, active tab, basic session state, and search.

Examples:

- Current instrument selector.
- Active session state.
- Compact BPM control.
- Global search.
- Sidebar toggle and inspector toggle.

### Layer 2: Contextual Menu

Once a macro context is selected, show only the menu for that context.

Example:

- Biblioteca exposes content categories, repertoire collections, import, artists, favorites, and downloads.
- Praticar exposes session, plan, exercises, repertoire in practice, ear training, metronome, and recordings.

### Layer 3: Object Workspace

Opening a lesson, song, skill, exercise, project, or diary entry creates a tab. The tab should show the primary modules needed to work with that object.

### Layer 4: Details And Tools

Secondary data and advanced tools should move to:

- Inspector.
- Expandable card sections.
- Contextual actions.
- Docked tools.
- Command palette.
- Object-specific menus.

## What Should Be Visible By Default

Default views should show:

- Current task.
- Most relevant next action.
- Compact context.
- A small set of related modules.
- Clear path to deeper detail.

Default views should not show:

- Every capability.
- Every field.
- All historical data.
- Administrative tables unless the task requires them.
- Large metrics that do not directly guide musical action.

## Disclosure By Object Type

### Skill

Default:

- Name, domain, state, progress, next requirement, linked exercises.

Reveal progressively:

- Prerequisites.
- Related lessons.
- Related songs.
- Review schedule.
- Evidence history.
- Next skills.

### Song

Default:

- Title, artist, instrument, status, BPM, tuning, progress, sections.

Reveal progressively:

- Section details.
- Techniques and scales.
- Recordings.
- Practice plan generation.
- Notes and files.
- Related skills.

### Lesson

Default:

- Summary, objectives, primary explanation.

Reveal progressively:

- Steps.
- Diagrams.
- Tablature.
- Audio examples.
- Common mistakes.
- Related objects.

### Practice Session

Default:

- Current activity, timer, BPM, notes, next action.

Reveal progressively:

- Full activity list.
- Recording tools.
- Feedback form.
- Session summary.
- Skill evidence details.

### Project

Default:

- Name, status, key, BPM, active ideas, recent riffs.

Reveal progressively:

- Lyrics.
- Versions.
- References.
- Recordings.
- External integrations.

## Disclosure Rules

- A macro context may contain many options, but only its own options.
- A tab should never become a dumping ground for unrelated modules.
- Inspector content must follow the selected object.
- Search can expose everything because the user has expressed intent.
- Advanced controls should be one click away, not permanently visible.
- Empty states should suggest the next meaningful action, not explain the whole product.

