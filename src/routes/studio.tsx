import { createFileRoute, Outlet } from "@tanstack/react-router";

export const Route = createFileRoute("/studio")({
  head: () => ({ meta: [{ title: "Studio - Muse Studio" }] }),
  component: Outlet,
});
