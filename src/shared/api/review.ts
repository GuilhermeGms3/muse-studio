import { useApiQuery } from "./client";
import type { JournalEntry } from "./contracts";

export const useJournal = () => useApiQuery<JournalEntry[]>(["journal"], "/journal");
