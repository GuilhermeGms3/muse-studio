import type { Instrument } from "@/shared/api/contracts";
import { Panel, Row } from "@/shared/ui/workspace/Panel";

export function ContextSection({ instrument }: { instrument?: Instrument }) {
  return (
    <Panel title="Contexto">
      <Row label="Instrumento" value={instrument?.name ?? "—"} mono={false} />
      <Row label="Foco" value={instrument?.focus.join(" · ") ?? "—"} mono={false} />
    </Panel>
  );
}
