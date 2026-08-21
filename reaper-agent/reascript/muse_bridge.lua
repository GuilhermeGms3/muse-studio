-- Muse Studio ReaScript bridge. Install in REAPER and run as a startup/background action.
-- It processes only the typed command vocabulary emitted by the local Muse Reaper Agent.

local workspace = os.getenv("MUSE_REAPER_WORKSPACE")
if not workspace or workspace == "" then
  reaper.ShowConsoleMsg("Muse Bridge: MUSE_REAPER_WORKSPACE não configurado.\n")
  return
end

local separator = package.config:sub(1, 1)
local bridge = workspace .. separator .. ".bridge"
local commands = bridge .. separator .. "commands"
local results = bridge .. separator .. "results"
reaper.RecursiveCreateDirectory(results, 0)

local function read_file(file)
  local handle = io.open(file, "rb")
  if not handle then return nil end
  local value = handle:read("*a")
  handle:close()
  return value
end

local function write_file(file, value)
  local handle = io.open(file, "wb")
  if not handle then return false end
  handle:write(value)
  handle:close()
  return true
end

local function json_string(source, key)
  local raw = source:match('"' .. key .. '"%s*:%s*"(.-)"')
  if not raw then return nil end
  return raw:gsub('\\"', '"'):gsub('\\\\', '\\'):gsub('\\n', '\n')
end

local function json_number(source, key)
  return tonumber(source:match('"' .. key .. '"%s*:%s*([%-0-9%.]+)'))
end

local function json_boolean(source, key)
  local value = source:match('"' .. key .. '"%s*:%s*(true)') or source:match('"' .. key .. '"%s*:%s*(false)')
  return value == "true"
end

local function escape(value)
  return (value or ""):gsub('\\', '\\\\'):gsub('"', '\\"'):gsub('\n', '\\n')
end

local function find_track(external_id)
  for index = 0, reaper.CountTracks(0) - 1 do
    local track = reaper.GetTrack(0, index)
    local _, value = reaper.GetSetMediaTrackInfo_String(track, "P_EXT:MUSE_ID", "", false)
    if value == external_id then return track end
  end
  return nil
end

local function process(file_name)
  local file = commands .. separator .. file_name
  local source = read_file(file)
  if not source then return end
  local command_id = json_string(source, "commandId") or file_name
  local project_id = json_string(source, "projectId")
  local operation = json_string(source, "operation")
  local ok, message = pcall(function()
    if operation == "SET_TEMPO" then
      reaper.SetCurrentBPM(0, json_number(source, "bpm"), true)
    elseif operation == "SET_METER" then
      reaper.SetProjectTimeSignature2(0, json_number(source, "numerator"), json_number(source, "denominator"))
    elseif operation == "ADD_TRACK" then
      local id, name = json_string(source, "id"), json_string(source, "name")
      if not find_track(id) then
        reaper.InsertTrackAtIndex(reaper.CountTracks(0), true)
        local track = reaper.GetTrack(0, reaper.CountTracks(0) - 1)
        reaper.GetSetMediaTrackInfo_String(track, "P_NAME", name, true)
        reaper.GetSetMediaTrackInfo_String(track, "P_EXT:MUSE_ID", id, true)
      end
    elseif operation == "ADD_MEDIA" then
      local track = find_track(json_string(source, "trackId"))
      if not track then error("track não encontrada") end
      local previous = reaper.GetSelectedTrack(0, 0)
      reaper.SetOnlyTrackSelected(track)
      reaper.SetEditCurPos(json_number(source, "position"), false, false)
      reaper.InsertMedia(json_string(source, "path"), 0)
      local item = reaper.GetSelectedMediaItem(0, 0)
      if item then reaper.GetSetMediaItemInfo_String(item, "P_EXT:MUSE_ID", json_string(source, "id"), true) end
      if previous then reaper.SetOnlyTrackSelected(previous) end
    elseif operation == "ADD_MARKER" then
      local index = reaper.AddProjectMarker2(0, false, json_number(source, "position"), 0,
        json_string(source, "name"), -1, 0)
      reaper.SetProjExtState(0, "Muse", "marker:" .. json_string(source, "id"), tostring(index))
    elseif operation == "ADD_REGION" then
      local index = reaper.AddProjectMarker2(0, true, json_number(source, "start"), json_number(source, "end"),
        json_string(source, "name"), -1, 0)
      reaper.SetProjExtState(0, "Muse", "region:" .. json_string(source, "id"), tostring(index))
    elseif operation == "SET_LOOP" then
      reaper.GetSet_LoopTimeRange(true, true, json_number(source, "start"), json_number(source, "end"), false)
    elseif operation == "ARM_TRACK" then
      local track = find_track(json_string(source, "trackId"))
      if not track then error("track não encontrada") end
      reaper.SetMediaTrackInfo_Value(track, "I_RECARM", json_boolean(source, "armed") and 1 or 0)
    elseif operation == "SET_POSITION" then
      reaper.SetEditCurPos(json_number(source, "position"), true, false)
    elseif operation == "SAVE_PROJECT" then
      reaper.Main_SaveProject(0, false)
    elseif operation == "PLAY" then
      reaper.OnPlayButton()
    elseif operation == "PAUSE" then
      reaper.Main_OnCommand(1008, 0)
    elseif operation == "STOP" then
      reaper.OnStopButton()
    elseif operation == "RECORD" then
      reaper.Main_OnCommand(1013, 0)
    else
      error("operação não suportada")
    end
    reaper.SetProjExtState(0, "Muse", "projectId", project_id or "")
    reaper.UpdateArrange()
  end)

  local result = '{"commandId":"' .. escape(command_id) .. '","success":' .. tostring(ok) ..
    ',"message":"' .. escape(ok and "Concluído" or message) .. '","at":"' .. os.date("!%Y-%m-%dT%H:%M:%SZ") .. '"}'
  write_file(results .. separator .. command_id .. ".json", result)
  os.remove(file)
end

local last_heartbeat = 0
local function normalized_path(value)
  return (value or ""):gsub("\\", "/"):lower()
end

local function loop()
  local now = reaper.time_precise()
  if now - last_heartbeat > 1 then
    local expected = read_file(bridge .. separator .. "active-project.json") or ""
    local project_id = json_string(expected, "projectId") or ""
    local expected_path = json_string(expected, "path") or ""
    local _, project_path = reaper.EnumProjects(-1, "")
    if normalized_path(project_path) ~= normalized_path(expected_path) then
      project_id = ""
    elseif project_id ~= "" then
      reaper.SetProjExtState(0, "Muse", "projectId", project_id)
    end
    local play_state = reaper.GetPlayState()
    local position = play_state == 0 and reaper.GetCursorPosition() or reaper.GetPlayPosition()
    local heartbeat = '{"at":"' .. os.date("!%Y-%m-%dT%H:%M:%SZ") .. '","reaperVersion":"' ..
      escape(reaper.GetAppVersion()) .. '","projectId":"' .. escape(project_id) ..
      '","projectPath":"' .. escape(project_path) .. '","positionSeconds":' .. tostring(position) ..
      ',"playState":' .. tostring(play_state) .. '}'
    write_file(bridge .. separator .. "heartbeat.json", heartbeat)
    last_heartbeat = now
  end

  local index = 0
  while true do
    local file = reaper.EnumerateFiles(commands, index)
    if not file then break end
    if file:match("%.json$") then process(file) end
    index = index + 1
  end
  reaper.defer(loop)
end

reaper.atexit(function() os.remove(bridge .. separator .. "heartbeat.json") end)
loop()
