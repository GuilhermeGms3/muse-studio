import type { Instrument } from "@/shared/api/contracts";
import { formatClock } from "@/workspace/store/WorkspaceProvider";
import { Panel, Row } from "@/shared/ui/workspace/Panel";

export function ContextSection({
  instrument,
  sessionSeconds,
  remainingMinutes,
}: {
  instrument?: Instrument;
  sessionSeconds: number;
  remainingMinutes: number;
}) {
  return (
    <Panel title="Contexto">
      <Row label="Instrumento" value={instrument?.name ?? "—"} mono={false} />
      <Row label="Foco" value={instrument?.focus.join(" · ") ?? "—"} mono={false} />
      <Row label="Sessão" value={formatClock(sessionSeconds)} />
      <Row label="Restante hoje" value={`${remainingMinutes} min`} />
    </Panel>
  );
}
