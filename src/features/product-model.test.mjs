import test from "node:test";
import assert from "node:assert/strict";
import {
  journeyStatusCopy,
  missionRequiresRecording,
  primaryNavigationPaths,
  selectHomeFocus,
  visibleMissionPhases,
} from "./product-model.ts";

const baseHome = { coach: { recommendations: [] } };

test("Home prioriza experiência ativa, depois Coach e depois coleta de evidência", () => {
  assert.equal(
    selectHomeFocus({ ...baseHome, learningExperience: { status: "PAUSED" } }),
    "ACTIVE_EXPERIENCE",
  );
  assert.equal(
    selectHomeFocus({ ...baseHome, coach: { recommendations: [{ kind: "PRACTICE" }] } }),
    "COACH_RECOMMENDATION",
  );
  assert.equal(selectHomeFocus(baseHome), "EVIDENCE_ACTION");
  assert.equal(
    selectHomeFocus({ ...baseHome, learningExperience: { status: "COMPLETED" } }),
    "EVIDENCE_ACTION",
  );
});

test("Mission adapta fases e gravação aos conteúdos disponíveis", () => {
  assert.equal(missionRequiresRecording(["slow", "record"], "evidência prática"), true);
  assert.equal(missionRequiresRecording(["slow"], "reflexão escrita"), false);
  assert.deepEqual(
    visibleMissionPhases({ lessonCount: 0, exerciseCount: 1, hasMusicalApplication: false }),
    ["ORIENTATION", "DIRECT_EXPERIMENTATION", "PRACTICE", "APPLICATION_REFLECTION", "CLOSING"],
  );
});

test("Jornada traduz todos os estados curriculares sem linguagem de jogo", () => {
  assert.deepEqual(Object.values(journeyStatusCopy), [
    "Consolidado",
    "Em desenvolvimento",
    "Disponível",
    "Revisão necessária",
    "Bloqueado",
  ]);
});

test("navegação principal contém somente as cinco rotas de produto", () => {
  assert.deepEqual(
    primaryNavigationPaths.map((item) => item.path),
    ["/", "/jornada", "/musicas", "/historico", "/explorar"],
  );
});
