import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/sessao")({
  beforeLoad: () => {
    throw redirect({ to: "/pratica", replace: true });
  },
});
