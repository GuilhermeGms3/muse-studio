import { apiRequest, useApiQuery } from "./client";
import type { LibraryContent } from "./contracts";

export const useLibrary = () => useApiQuery<LibraryContent[]>(["library"], "/library");

export const saveLesson = (lesson: LibraryContent) =>
  apiRequest<LibraryContent>(`/library/${lesson.id}`, {
    method: "PUT",
    body: JSON.stringify(lesson),
  });
