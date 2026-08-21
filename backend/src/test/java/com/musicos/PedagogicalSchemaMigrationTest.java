package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;

class PedagogicalSchemaMigrationTest {
    @Test
    void migratesARepresentativeLegacySchemaIncrementally() throws Exception {
        var url = "jdbc:h2:mem:legacy-pedagogical-migration;DB_CLOSE_DELAY=-1";
        try (var connection = DriverManager.getConnection(url, "sa", "");
             var statement = connection.createStatement()) {
            statement.execute("create table exercises (id varchar(255) primary key)");
            statement.execute("insert into exercises (id) values ('legacy-exercise')");
        }

        Flyway.configure()
                .dataSource(url, "sa", "")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion(MigrationVersion.fromVersion("0"))
                .load()
                .migrate();

        try (var connection = DriverManager.getConnection(url, "sa", "")) {
            var metadata = connection.getMetaData();
            try (var tables = metadata.getTables(null, "PUBLIC", "LEARNING_COMPETENCIES", null)) {
                assertThat(tables.next()).isTrue();
            }
            try (var columns = metadata.getColumns(null, "PUBLIC", "EXERCISES", "OBSERVABLE_OBJECTIVE")) {
                assertThat(columns.next()).isTrue();
            }
            try (var columns = metadata.getColumns(null, "PUBLIC", "LEARNING_INSTRUMENT_PROFILES", "PRIMARY_PROFILE")) {
                assertThat(columns.next()).isTrue();
            }
            try (var tables = metadata.getTables(null, "PUBLIC", "LEARNING_PATHS", null)) {
                assertThat(tables.next()).isTrue();
            }
            try (var tables = metadata.getTables(null, "PUBLIC", "LEARNING_CONTENT_RELATIONS", null)) {
                assertThat(tables.next()).isTrue();
            }
            try (var statement = connection.createStatement();
                 var rows = statement.executeQuery("select count(*) from exercises where id = 'legacy-exercise'")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getInt(1)).isEqualTo(1);
            }
            try (var statement = connection.createStatement();
                 var rows = statement.executeQuery(
                         "select \"version\" from \"flyway_schema_history\" where \"success\" = true "
                                 + "order by \"installed_rank\" desc limit 1")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getString(1)).isEqualTo("12");
            }
            try (var tables = metadata.getTables(null, "PUBLIC", "STUDIO_PROJECTS", null)) {
                assertThat(tables.next()).isTrue();
            }
        }
    }
}
