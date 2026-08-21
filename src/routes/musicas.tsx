import { createFileRoute, Outlet, useRouterState } from "@tanstack/react-router";
import { SongsWorkspace } from "@/features/songs/SongsWorkspace";

export const Route = createFileRoute("/musicas")({
  head: () => ({ meta: [{ title: "Músicas - Muse Studio" }] }),
  component: SongsRoute,
});

function SongsRoute() {
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  return pathname === "/musicas" ? <SongsWorkspace /> : <Outlet />;
}
