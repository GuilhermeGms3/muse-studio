import { useApiQuery } from "./client";

export interface BuildIdentity {
  application: string;
  version: string;
  buildId: string;
  builtAt: string;
  gitSha: string;
  source: string;
}

export const useBuildIdentity = () => useApiQuery<BuildIdentity>(["system-build"], "/system/build");
