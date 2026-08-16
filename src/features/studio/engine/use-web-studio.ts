import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import type { StudioProject } from "@/shared/api/contracts";
import { audibleTrackIds, selectedLoop, studioDuration } from "./studio-timeline";

export function useWebStudio(project: StudioProject) {
  const [position, setPosition] = useState(0);
  const [playing, setPlaying] = useState(false);
  const startedAt = useRef(0);
  const startPosition = useRef(0);
  const frame = useRef<number>();
  const audio = useRef<HTMLAudioElement[]>([]);
  const pendingStarts = useRef<number[]>([]);
  const duration = useMemo(() => studioDuration(project), [project]);

  const releaseAudio = useCallback(() => {
    pendingStarts.current.forEach((id) => window.clearTimeout(id));
    pendingStarts.current = [];
    audio.current.forEach((item) => {
      item.pause();
      item.src = "";
    });
    audio.current = [];
  }, []);

  const pause = useCallback(() => {
    releaseAudio();
    if (frame.current) cancelAnimationFrame(frame.current);
    setPlaying(false);
  }, [releaseAudio]);

  const startAudio = useCallback(
    (from: number) => {
      releaseAudio();
      const audible = audibleTrackIds(project);
      project.clips
        .filter((clip) => clip.audioUrl && audible.has(clip.trackId))
        .forEach((clip) => {
          const track = project.tracks.find((item) => item.id === clip.trackId);
          const element = new Audio(clip.audioUrl);
          element.volume = track?.volume ?? 1;
          const relative = from - clip.startSeconds;
          const begin = () => {
            element.currentTime = clip.offsetSeconds + Math.max(0, relative);
            void element.play().catch(() => undefined);
          };
          if (relative >= 0) begin();
          else pendingStarts.current.push(window.setTimeout(begin, Math.max(0, -relative * 1000)));
          audio.current.push(element);
        });
    },
    [project, releaseAudio],
  );

  const playFrom = useCallback(
    (from: number) => {
      const safe = Math.max(0, Math.min(duration, from));
      startPosition.current = safe;
      startedAt.current = performance.now();
      setPosition(safe);
      setPlaying(true);
      startAudio(safe);
    },
    [duration, startAudio],
  );

  useEffect(() => {
    if (!playing) return;
    const tick = () => {
      const next = startPosition.current + (performance.now() - startedAt.current) / 1000;
      const loop = selectedLoop(project);
      if (loop && next >= loop.endSeconds) {
        playFrom(loop.startSeconds);
        return;
      }
      if (next >= duration) {
        pause();
        setPosition(duration);
        return;
      }
      setPosition(next);
      frame.current = requestAnimationFrame(tick);
    };
    frame.current = requestAnimationFrame(tick);
    return () => frame.current && cancelAnimationFrame(frame.current);
  }, [duration, pause, playFrom, playing, project]);

  useEffect(() => releaseAudio, [releaseAudio]);

  return {
    position,
    duration,
    playing,
    play: () => playFrom(position >= duration ? 0 : position),
    pause,
    stop: () => {
      pause();
      setPosition(0);
    },
    seek: (value: number) => {
      const wasPlaying = playing;
      pause();
      setPosition(value);
      if (wasPlaying) playFrom(value);
    },
  };
}
