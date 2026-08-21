import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/treino-musica")({
  beforeLoad: () => {
    throw redirect({ to: "/musicas", replace: true });
  },
});
