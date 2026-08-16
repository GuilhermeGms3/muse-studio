package com.musicos.integration.reaper;

import static com.musicos.api.StudioApiModels.*;

import com.musicos.domain.StudioProject;

public interface ReaperBridge {
    ReaperStatusView status();
    ReaperStatusView configure(ReaperConfigurationRequest request);
    ReaperStatusView testConnection();
    ReaperStatusView disconnect();
    OpenInReaperView open(StudioProject project);
}
