import assert from "node:assert/strict";
import test from "node:test";
import { completionMessage, experienceAction } from "./experience-model.ts";

test("distingue início, andamento, retomada e conclusão", () => {
  assert.equal(experienceAction("NOT_STARTED"), "start");
  assert.equal(experienceAction("IN_PROGRESS"), "resume");
  assert.equal(experienceAction("PAUSED"), "resume");
  assert.equal(experienceAction("COMPLETED"), "review");
});

test("preserva erro de persistência", () => {
  assert.equal(completionMessage(false, [], "Falha de rede").tone, "error");
});

test("não chama evidência provisória de domínio", () => {
  const message = completionMessage(true, [
    { masteryState: "DEVELOPING", evidenceConfidence: "INSUFFICIENT" },
  ]);
  assert.equal(message.tone, "insufficient");
  assert.match(message.detail, /provisória/);
});
