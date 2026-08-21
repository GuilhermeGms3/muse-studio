import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/repertorio/$songId")({
  beforeLoad: ({ params }) => {
    throw redirect({ to: "/musicas/$songId", params: { songId: params.songId }, replace: true });
  },
});
