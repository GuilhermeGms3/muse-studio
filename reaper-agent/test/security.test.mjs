import test from "node:test";
import assert from "node:assert/strict";
import path from "node:path";

process.env.MUSE_REAPER_TOKEN = "test-token-with-at-least-24-chars";
process.env.MUSE_REAPER_WORKSPACE = path.resolve("reaper-agent/test/fixture");
process.env.MUSE_REAPER_EXECUTABLE = process.execPath;
process.env.MUSE_REAPER_AGENT_PORT = "47839";
const { confined, validateOperation } = await import("../src/server.mjs");

test("rejeita traversal fora do workspace", () => {
  assert.throws(() => confined(process.env.MUSE_REAPER_WORKSPACE, "..", "escape"), /workspace/);
});

test("aceita apenas operações tipadas", () => {
  assert.deepEqual(validateOperation("SET_TEMPO", { bpm: 92 }), { bpm: 92 });
  assert.throws(() => validateOperation("SET_TEMPO", { bpm: 900 }), /bpm/);
  assert.deepEqual(validateOperation("PLAY", {}), {});
});
