import { createFileRoute } from "@tanstack/react-router";
import { SongPracticeWorkspace } from "@/features/practice-song/SongPracticeWorkspace";

export const Route = createFileRoute("/treino-musica/$songId/$instrument")({
  validateSearch: (search: Record<string, unknown>) => ({
    q: typeof search.q === "string" ? search.q : undefined,
  }),
  head: () => ({ meta: [{ title: "Treino musical - Muse Studio" }] }),
  component: PracticeSongRoute,
});

function PracticeSongRoute() {
  const { q } = Route.useSearch();
  return <SongPracticeWorkspace query={q} />;
}
