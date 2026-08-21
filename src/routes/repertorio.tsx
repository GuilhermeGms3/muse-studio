import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/repertorio")({
  beforeLoad: () => {
    throw redirect({ to: "/musicas", replace: true });
  },
});
