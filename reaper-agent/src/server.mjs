import { createServer } from "node:http";
import { randomUUID } from "node:crypto";
import { spawn } from "node:child_process";
import { access, mkdir, readFile, writeFile } from "node:fs/promises";
import { constants } from "node:fs";
import path from "node:path";
import { pathToFileURL } from "node:url";

const VERSION = "0.1.0";
const HOST = "127.0.0.1";
const PORT = integerEnv("MUSE_REAPER_AGENT_PORT", 47831, 1024, 65535);
const TOKEN = process.env.MUSE_REAPER_TOKEN?.trim();
const WORKSPACE = path.resolve(requiredEnv("MUSE_REAPER_WORKSPACE"));
const MEDIA_ROOT = path.resolve(
  process.env.MUSE_REAPER_MEDIA_ROOT?.trim() || path.join(WORKSPACE, "media"),
);
const REAPER_EXE = path.resolve(requiredEnv("MUSE_REAPER_EXECUTABLE"));
const ALLOWED_ORIGINS = new Set(
  (process.env.MUSE_REAPER_ALLOWED_ORIGINS || "http://127.0.0.1:3000,http://localhost:3000")
    .split(",")
    .map((value) => value.trim())
    .filter(Boolean),
);

if (!TOKEN || TOKEN.length < 24) {
  throw new Error("MUSE_REAPER_TOKEN é obrigatório e deve possuir ao menos 24 caracteres.");
}

await access(REAPER_EXE, constants.X_OK);
await mkdir(path.join(WORKSPACE, ".bridge", "commands"), { recursive: true });
await mkdir(MEDIA_ROOT, { recursive: true });

const server = createServer(async (request, response) => {
  const startedAt = Date.now();
  try {
    applyCors(request, response);
    if (request.method === "OPTIONS") return send(response, 204);
    authorize(request);
    const url = new URL(request.url || "/", `http://${HOST}`);

    if (request.method === "GET" && url.pathname === "/v1/status") {
      return json(response, 200, await status());
    }
    if (request.method === "POST" && url.pathname === "/v1/projects/open") {
      return json(response, 201, await openProject(await body(request)));
    }
    if (request.method === "POST" && url.pathname === "/v1/transport") {
      return json(response, 202, await transport(await body(request)));
    }
    const commandMatch = url.pathname.match(/^\/v1\/projects\/([a-zA-Z0-9-]+)\/commands$/);
    if (request.method === "POST" && commandMatch) {
      return json(response, 202, await enqueue(commandMatch[1], await body(request)));
    }
    return json(response, 404, { code: "NOT_FOUND", message: "Operação não suportada." });
  } catch (error) {
    const statusCode = Number.isInteger(error.statusCode) ? error.statusCode : 500;
    json(response, statusCode, {
      code: error.code || "AGENT_ERROR",
      message: statusCode >= 500 ? "O Agent não concluiu a operação." : error.message,
    });
  } finally {
    const line = JSON.stringify({
      at: new Date().toISOString(),
      method: request.method,
      url: request.url,
      status: response.statusCode,
      durationMs: Date.now() - startedAt,
    });
    process.stdout.write(`${line}\n`);
  }
});

if (process.argv[1] && import.meta.url === pathToFileURL(path.resolve(process.argv[1])).href) {
  server.listen(PORT, HOST, () => {
    process.stdout.write(`Muse Reaper Agent ${VERSION} em http://${HOST}:${PORT}\n`);
  });
}

async function status() {
  const heartbeatPath = path.join(WORKSPACE, ".bridge", "heartbeat.json");
  try {
    const heartbeat = JSON.parse(await readFile(heartbeatPath, "utf8"));
    const ageMs = Date.now() - Date.parse(heartbeat.at);
    if (Number.isFinite(ageMs) && ageMs < 6000) {
      const expected = await readJson(path.join(WORKSPACE, ".bridge", "active-project.json"));
      const connected =
        expected &&
        samePath(expected.path, heartbeat.projectPath) &&
        heartbeat.projectId === expected.projectId;
      return {
        status: connected ? "PROJECT_CONNECTED" : "REAPER_AVAILABLE",
        agentVersion: VERSION,
        reaperVersion: heartbeat.reaperVersion || null,
        projectId: connected ? heartbeat.projectId : null,
        projectPath: heartbeat.projectPath || null,
        positionSeconds: finiteOrNull(heartbeat.positionSeconds),
        playState: Number.isInteger(heartbeat.playState) ? heartbeat.playState : null,
        mediaRoot: MEDIA_ROOT,
        checkedAt: new Date().toISOString(),
      };
    }
  } catch {
    // An absent heartbeat means the ReaScript bridge is not live.
  }
  return {
    status: "REAPER_OFFLINE",
    agentVersion: VERSION,
    reaperVersion: null,
    projectId: null,
    projectPath: null,
    positionSeconds: null,
    playState: null,
    mediaRoot: MEDIA_ROOT,
    checkedAt: new Date().toISOString(),
  };
}

async function openProject(value) {
  const input = object(value);
  const projectId = identifier(input.projectId, "projectId");
  const externalProjectId = identifier(input.externalProjectId, "externalProjectId");
  const rpp = text(input.rpp, "rpp", 20_000_000);
  const projectDir = confined(WORKSPACE, "projects", projectId);
  const revisionDir = confined(projectDir, "revisions");
  await mkdir(revisionDir, { recursive: true });

  const manifestPath = confined(projectDir, "ownership.json");
  const ownership = await readJson(manifestPath);
  if (ownership && ownership.projectId !== projectId) {
    throw clientError(409, "OWNERSHIP_CONFLICT", "A pasta pertence a outro projeto Muse.");
  }
  await writeFile(manifestPath, JSON.stringify({ projectId, externalProjectId }, null, 2), {
    flag: "wx",
  }).catch(async (error) => {
    if (error.code !== "EEXIST") throw error;
    if (!ownership || ownership.externalProjectId !== externalProjectId) {
      throw clientError(409, "OWNERSHIP_CONFLICT", "Identidade externa divergente.");
    }
  });

  const revision = `${new Date().toISOString().replace(/[:.]/g, "-")}-${randomUUID()}.rpp`;
  const revisionPath = confined(revisionDir, revision);
  await writeFile(revisionPath, rpp, { encoding: "utf8", flag: "wx" });
  await writeFile(
    confined(projectDir, "current.json"),
    JSON.stringify(
      {
        projectId,
        externalProjectId,
        revision,
        path: revisionPath,
        createdAt: new Date().toISOString(),
      },
      null,
      2,
    ),
    { encoding: "utf8" },
  );
  await writeFile(
    confined(WORKSPACE, ".bridge", "active-project.json"),
    JSON.stringify({
      projectId,
      externalProjectId,
      path: revisionPath,
    }),
    "utf8",
  );

  const child = spawn(REAPER_EXE, [revisionPath], {
    cwd: projectDir,
    detached: true,
    stdio: "ignore",
    windowsHide: false,
    env: { ...process.env, MUSE_REAPER_WORKSPACE: WORKSPACE },
  });
  child.unref();
  const currentStatus = await status();
  return {
    ...currentStatus,
    projectId,
    externalProjectId,
    projectPath: revisionPath,
    message: "Revisão imutável criada e entregue ao REAPER; aguardando handshake do ReaScript.",
  };
}

async function transport(value) {
  const input = object(value);
  const action = enumValue(input.action, "action", ["PLAY", "PAUSE", "STOP", "RECORD"]);
  const current = await requireConnectedProject();
  const command = await enqueue(current.projectId, { operation: action, payload: {} });
  return { ...command, action };
}

async function enqueue(rawProjectId, value) {
  const projectId = identifier(rawProjectId, "projectId");
  const input = object(value);
  const operation = enumValue(input.operation, "operation", [
    "SET_TEMPO",
    "SET_METER",
    "ADD_TRACK",
    "ADD_MEDIA",
    "ADD_MARKER",
    "ADD_REGION",
    "SET_LOOP",
    "ARM_TRACK",
    "SET_POSITION",
    "SAVE_PROJECT",
    "PLAY",
    "PAUSE",
    "STOP",
    "RECORD",
  ]);
  await requireConnectedProject(projectId);
  const commandId = randomUUID();
  const payload = validateOperation(operation, input.payload);
  const commandPath = confined(WORKSPACE, ".bridge", "commands", `${Date.now()}-${commandId}.json`);
  await writeFile(
    commandPath,
    JSON.stringify({
      commandId,
      projectId,
      operation,
      payload,
      createdAt: new Date().toISOString(),
    }),
    { encoding: "utf8", flag: "wx" },
  );
  return { accepted: true, commandId, operation, status: (await status()).status };
}

function validateOperation(operation, rawPayload) {
  const payload = object(rawPayload || {});
  if (operation === "SET_TEMPO") return { bpm: number(payload.bpm, "bpm", 30, 300) };
  if (operation === "SET_METER")
    return {
      numerator: number(payload.numerator, "numerator", 1, 16),
      denominator: number(payload.denominator, "denominator", 1, 16),
    };
  if (operation === "ADD_TRACK")
    return { id: identifier(payload.id, "id"), name: text(payload.name, "name", 200) };
  if (operation === "ADD_MEDIA")
    return {
      id: identifier(payload.id, "id"),
      trackId: identifier(payload.trackId, "trackId"),
      path: confined(MEDIA_ROOT, text(payload.path, "path", 2000)),
      position: number(payload.position, "position", 0, 86400),
    };
  if (operation === "ADD_MARKER")
    return {
      id: identifier(payload.id, "id"),
      name: text(payload.name, "name", 200),
      position: number(payload.position, "position", 0, 86400),
    };
  if (operation === "ADD_REGION")
    return {
      id: identifier(payload.id, "id"),
      name: text(payload.name, "name", 200),
      start: number(payload.start, "start", 0, 86400),
      end: number(payload.end, "end", 0, 86400),
    };
  if (operation === "SET_LOOP")
    return {
      start: number(payload.start, "start", 0, 86400),
      end: number(payload.end, "end", 0, 86400),
    };
  if (operation === "ARM_TRACK")
    return { trackId: identifier(payload.trackId, "trackId"), armed: Boolean(payload.armed) };
  if (operation === "SET_POSITION")
    return { position: number(payload.position, "position", 0, 86400) };
  return {};
}

async function requireConnectedProject(expectedProjectId) {
  const current = await status();
  const connected =
    current.status === "PROJECT_CONNECTED" &&
    current.projectId &&
    (!expectedProjectId || current.projectId === expectedProjectId);
  if (!connected) {
    throw clientError(
      409,
      "PROJECT_NOT_CONNECTED",
      "O projeto precisa estar conectado ao ReaScript antes de receber comandos.",
    );
  }
  return current;
}

function authorize(request) {
  if (request.headers.authorization !== `Bearer ${TOKEN}`)
    throw clientError(401, "UNAUTHORIZED", "Token inválido.");
}

function applyCors(request, response) {
  const origin = request.headers.origin;
  if (!origin) return;
  if (!ALLOWED_ORIGINS.has(origin))
    throw clientError(403, "ORIGIN_DENIED", "Origem não permitida.");
  response.setHeader("Access-Control-Allow-Origin", origin);
  response.setHeader("Vary", "Origin");
  response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
  response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
}

async function body(request) {
  let size = 0;
  const chunks = [];
  for await (const chunk of request) {
    size += chunk.length;
    if (size > 21_000_000) throw clientError(413, "PAYLOAD_TOO_LARGE", "Payload excede o limite.");
    chunks.push(chunk);
  }
  try {
    return JSON.parse(Buffer.concat(chunks).toString("utf8") || "{}");
  } catch {
    throw clientError(400, "INVALID_JSON", "JSON inválido.");
  }
}

function confined(root, ...segments) {
  const candidate = path.resolve(root, ...segments);
  const relative = path.relative(path.resolve(root), candidate);
  if (relative.startsWith("..") || path.isAbsolute(relative))
    throw clientError(400, "PATH_OUTSIDE_WORKSPACE", "Caminho fora do workspace.");
  return candidate;
}

function samePath(first, second) {
  if (typeof first !== "string" || typeof second !== "string") return false;
  return path.resolve(first).toLocaleLowerCase() === path.resolve(second).toLocaleLowerCase();
}

function finiteOrNull(value) {
  return typeof value === "number" && Number.isFinite(value) ? value : null;
}

async function readJson(file) {
  try {
    return JSON.parse(await readFile(file, "utf8"));
  } catch (error) {
    if (error.code === "ENOENT") return null;
    throw error;
  }
}
function requiredEnv(name) {
  const value = process.env[name]?.trim();
  if (!value) throw new Error(`${name} é obrigatório.`);
  return value;
}
function integerEnv(name, fallback, min, max) {
  const value = Number(process.env[name] || fallback);
  if (!Number.isInteger(value) || value < min || value > max) throw new Error(`${name} inválido.`);
  return value;
}
function object(value) {
  if (!value || typeof value !== "object" || Array.isArray(value))
    throw clientError(400, "INVALID_BODY", "Objeto esperado.");
  return value;
}
function identifier(value, name) {
  const result = text(value, name, 100);
  if (!/^[a-zA-Z0-9-]+$/.test(result))
    throw clientError(400, "INVALID_IDENTIFIER", `${name} inválido.`);
  return result;
}
function text(value, name, max) {
  if (typeof value !== "string" || !value.trim() || value.length > max)
    throw clientError(400, "INVALID_FIELD", `${name} inválido.`);
  return value.trim();
}
function number(value, name, min, max) {
  if (typeof value !== "number" || !Number.isFinite(value) || value < min || value > max)
    throw clientError(400, "INVALID_FIELD", `${name} inválido.`);
  return value;
}
function enumValue(value, name, values) {
  if (!values.includes(value)) throw clientError(400, "INVALID_FIELD", `${name} inválido.`);
  return value;
}
function clientError(statusCode, code, message) {
  const error = new Error(message);
  error.statusCode = statusCode;
  error.code = code;
  return error;
}
function json(response, code, value) {
  response.statusCode = code;
  response.setHeader("Content-Type", "application/json; charset=utf-8");
  response.end(JSON.stringify(value));
}
function send(response, code) {
  response.statusCode = code;
  response.end();
}

export { confined, validateOperation };
