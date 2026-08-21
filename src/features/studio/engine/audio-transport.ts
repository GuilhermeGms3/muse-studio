import type { StudioProject } from "@/shared/api/contracts";
import { audibleTrackIds } from "./studio-timeline";

class StudioAudioTransport {
  private context?: AudioContext;
  private buffers = new Map<string, Promise<AudioBuffer>>();
  private sources: AudioBufferSourceNode[] = [];
  private gains: GainNode[] = [];
  private generation = 0;

  async play(project: StudioProject, from: number) {
    const generation = ++this.generation;
    this.releaseNodes();
    const context = this.audioContext();
    await context.resume();
    const audible = audibleTrackIds(project);
    const clips = project.clips.filter((clip) => clip.audioUrl && audible.has(clip.trackId));
    const loaded = await Promise.all(
      clips.map(async (clip) => ({ clip, buffer: await this.load(clip.audioUrl as string) })),
    );
    if (generation !== this.generation) return;

    for (const { clip, buffer } of loaded) {
      const relative = from - clip.startSeconds;
      const offset = clip.offsetSeconds + Math.max(0, relative);
      const remaining = Math.min(
        clip.durationSeconds - Math.max(0, relative),
        buffer.duration - offset,
      );
      if (remaining <= 0) continue;
      const source = context.createBufferSource();
      const gain = context.createGain();
      const track = project.tracks.find((item) => item.id === clip.trackId);
      gain.gain.value = track?.volume ?? 1;
      source.buffer = buffer;
      source.connect(gain).connect(context.destination);
      source.start(context.currentTime + Math.max(0, -relative), offset, remaining);
      this.sources.push(source);
      this.gains.push(gain);
    }
  }

  stop() {
    this.generation += 1;
    this.releaseNodes();
  }

  private audioContext() {
    this.context ??= new AudioContext({ latencyHint: "interactive" });
    return this.context;
  }

  private load(url: string) {
    const cached = this.buffers.get(url);
    if (cached) return cached;
    const pending = fetch(url)
      .then((response) => {
        if (!response.ok) throw new Error(`Áudio indisponível (${response.status})`);
        return response.arrayBuffer();
      })
      .then((data) => this.audioContext().decodeAudioData(data));
    this.buffers.set(url, pending);
    pending.catch(() => this.buffers.delete(url));
    if (this.buffers.size > 32) {
      const oldest = this.buffers.keys().next().value;
      if (oldest) this.buffers.delete(oldest);
    }
    return pending;
  }

  private releaseNodes() {
    this.sources.forEach((source) => {
      try {
        source.stop();
      } catch {
        // A source that already ended is safe to discard.
      }
      source.disconnect();
    });
    this.gains.forEach((gain) => gain.disconnect());
    this.sources = [];
    this.gains = [];
  }
}

export const studioAudioTransport = new StudioAudioTransport();
