import { createFileRoute, Outlet, useRouterState } from "@tanstack/react-router";
import { PracticeSongSearch } from "@/features/practice-song/PracticeSongSearch";

export const Route = createFileRoute("/treino-musica")({
  head: () => ({ meta: [{ title: "Treinar música - Muse Studio" }] }),
  component: PracticeSongLayout,
});

function PracticeSongLayout() {
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  return pathname === "/treino-musica" ? <PracticeSongSearch /> : <Outlet />;
}
