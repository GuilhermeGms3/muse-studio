package com.musicos.repository;

import com.musicos.domain.LibraryContent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryContentRepository extends JpaRepository<LibraryContent, String> {
    List<LibraryContent> findAllByOrderByCategoryAscFriendlyTitleAsc();
    List<LibraryContent> findByCategoryIgnoreCaseOrderByFriendlyTitleAsc(String category);
}
