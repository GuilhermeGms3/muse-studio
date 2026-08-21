import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/treino-musica/$songId/$instrument")({
  beforeLoad: () => {
    throw redirect({ to: "/musicas", replace: true });
  },
});
