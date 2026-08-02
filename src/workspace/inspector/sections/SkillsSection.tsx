import type { Skill } from "@/shared/api/contracts";
import { Meter, Panel, StateTag } from "@/shared/ui/workspace/Panel";

export function SkillsSection({ skills }: { skills: Skill[] }) {
  return (
    <Panel title="Em desenvolvimento" bodyClassName="space-y-2 p-2">
      {skills.slice(0, 5).map((skill) => (
        <div key={skill.id} className="space-y-1">
          <div className="flex items-center justify-between gap-2">
            <span className="truncate text-xs">{skill.technicalName}</span>
            <StateTag state={skill.state} />
          </div>
          <Meter value={skill.progress} tone={skill.progress > 80 ? "ok" : "info"} />
          <div className="flex justify-between">
            <span className="label-tech">{skill.hours.toFixed(1)}h</span>
            {skill.targetBpm && (
              <span className="num text-2xs text-text-muted">
                {skill.currentBpm ?? 0}/{skill.targetBpm} BPM
              </span>
            )}
          </div>
        </div>
      ))}
      {!skills.length && <p className="text-2xs text-text-muted">Nada em desenvolvimento.</p>}
    </Panel>
  );
}
