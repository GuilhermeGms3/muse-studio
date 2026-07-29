package com.musicos.repository;

import com.musicos.domain.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, String> {
}
