package com.musicos.repository;

import com.musicos.domain.MusicProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicProjectRepository extends JpaRepository<MusicProject, String> {
}
