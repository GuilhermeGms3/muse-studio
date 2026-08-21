ALTER TABLE studio_clips ADD COLUMN IF NOT EXISTS external_item_id VARCHAR(255);
ALTER TABLE studio_regions ADD COLUMN IF NOT EXISTS boundary_confidence VARCHAR(32) DEFAULT 'DEFINED' NOT NULL;
ALTER TABLE studio_regions ADD COLUMN IF NOT EXISTS external_region_id VARCHAR(255);
ALTER TABLE studio_markers ADD COLUMN IF NOT EXISTS external_marker_id VARCHAR(255);

ALTER TABLE reaper_integration_settings ALTER COLUMN executable_path DROP NOT NULL;
ALTER TABLE reaper_integration_settings ALTER COLUMN workspace_path DROP NOT NULL;
ALTER TABLE reaper_integration_settings ADD COLUMN IF NOT EXISTS agent_base_url VARCHAR(1000);
ALTER TABLE reaper_integration_settings ADD COLUMN IF NOT EXISTS container_media_root VARCHAR(2000);
ALTER TABLE reaper_integration_settings ADD COLUMN IF NOT EXISTS host_media_root VARCHAR(2000);

ALTER TABLE studio_regions ADD CONSTRAINT IF NOT EXISTS ck_studio_region_confidence
    CHECK (boundary_confidence IN ('DEFINED', 'ESTIMATED', 'UNKNOWN'));
