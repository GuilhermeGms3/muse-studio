import { createFileRoute } from "@tanstack/react-router";
import { SongPage } from "@/features/repertoire/SongWorkspace";

export const Route = createFileRoute("/repertorio/$songId")({
  head: () => ({ meta: [{ title: "Repertório - Muse Studio" }] }),
  component: SongPage,
});
