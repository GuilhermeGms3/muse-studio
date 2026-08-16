import test from "node:test";
import assert from "node:assert/strict";
import { audibleTrackIds, selectedLoop, studioDuration } from "./studio-timeline.ts";

const project = {
  tracks: [
    { id: "backing", muted: false, solo: false },
    { id: "take", muted: false, solo: true },
  ],
  clips: [{ trackId: "backing", startSeconds: 4, durationSeconds: 20 }],
  regions: [{ id: "region-a", startSeconds: 8, endSeconds: 32 }],
  loopEnabled: true,
  selectedRegionId: "region-a",
};

test("timeline derives duration and selected practice loop", () => {
  assert.equal(studioDuration(project), 32);
  assert.equal(selectedLoop(project)?.id, "region-a");
});

test("solo tracks take precedence over ordinary audible tracks", () => {
  assert.deepEqual([...audibleTrackIds(project)], ["take"]);
});

test("loop is absent when transport loop is disabled", () => {
  assert.equal(selectedLoop({ ...project, loopEnabled: false }), undefined);
});
