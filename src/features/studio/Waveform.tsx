import { generatePeaks } from "@waveform-playlist/core";
import { useEffect, useRef } from "react";

export function Waveform({ url }: { url?: string }) {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    if (!url || !canvasRef.current) return;
    const controller = new AbortController();
    const canvas = canvasRef.current;
    const context = canvas.getContext("2d");
    if (!context) return;
    fetch(url, { signal: controller.signal })
      .then((response) => response.arrayBuffer())
      .then(async (data) => {
        const audio = new AudioContext();
        const buffer = await audio.decodeAudioData(data);
        const samples = buffer.getChannelData(0);
        const peaks = generatePeaks(
          samples,
          Math.max(1, Math.floor(samples.length / canvas.width)),
          8,
        );
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.fillStyle = "rgba(245, 158, 11, 0.72)";
        const middle = canvas.height / 2;
        for (let x = 0; x < canvas.width; x++) {
          const peak = Math.abs(Number(peaks[Math.min(peaks.length - 1, x * 2)] ?? 0)) / 128;
          const height = Math.max(1, peak * canvas.height);
          context.fillRect(x, middle - height / 2, 1, height);
        }
        await audio.close();
      })
      .catch(() => undefined);
    return () => controller.abort();
  }, [url]);

  return <canvas ref={canvasRef} width={800} height={40} className="h-full w-full" aria-hidden />;
}
