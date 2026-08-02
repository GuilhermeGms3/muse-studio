import { useApiQuery } from "./client";
import type { HomeData, Instrument, InstrumentId, UserPreferences } from "./contracts";

export const useHomeData = (instrument: InstrumentId) =>
  useApiQuery<HomeData>(["home", instrument], `/home?instrument=${instrument}`);

export const useInstruments = () => useApiQuery<Instrument[]>(["instruments"], "/instruments");

export const usePreferences = () => useApiQuery<UserPreferences>(["preferences"], "/preferences");
