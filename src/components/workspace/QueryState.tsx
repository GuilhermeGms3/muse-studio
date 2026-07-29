export function QueryState({ error }: { error?: Error | null }) {
  return (
    <div className="flex h-full min-h-48 items-center justify-center bg-panel p-4 text-center">
      <div>
        <p className="text-sm">{error ? "Não foi possível carregar os dados." : "Carregando..."}</p>
        {error && <p className="mt-1 text-2xs text-muted-foreground">{error.message}</p>}
      </div>
    </div>
  );
}
