package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.InstrumentId;
import com.musicos.service.HomeService;
import com.musicos.repository.CompetencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class HomeServiceIntegrationTest {

    @Autowired
    private HomeService homeService;

    @Autowired
    private CompetencyRepository competencies;

    @Test
    void homeKeepsTheProgressiveDisclosureContract() {
        var home = homeService.home(InstrumentId.GUITAR);

        assertThat(home.todayPlan()).isEmpty();
        assertThat(home.expectedMinutes()).isEqualTo(45);
        assertThat(home.currentObjective()).isNull();
        assertThat(home.continueFrom()).isNull();
        assertThat(home.coach()).isNotNull();
        assertThat(home.coach().profile().instrument()).isEqualTo(InstrumentId.GUITAR);
        assertThat(home.coach().recommendations()).allSatisfy(recommendation ->
                assertThat(recommendation.evidence()).isNotEmpty());
    }

    @Test
    void homeKeepsCoachInsideTheSelectedInstrumentProfileWithoutInventingContinuation() {
        var home = homeService.home(InstrumentId.ACOUSTIC);

        assertThat(home.currentObjective()).isNull();
        assertThat(home.continueFrom()).isNull();
        assertThat(home.coach().profile().instrument()).isEqualTo(InstrumentId.ACOUSTIC);
    }

    @Test
    void curriculumCoversTheMainLearningAreas() {
        var skills = competencies.findAll().stream()
                .filter(item -> item.getInstruments().contains(InstrumentId.GUITAR))
                .toList();

        assertThat(skills).hasSizeGreaterThan(50);
        assertThat(skills).extracting(skill -> skill.getTechnicalName())
                .contains("Escala Maior", "Pentatônica Menor", "Menor Harmônica", "Campo Harmônico",
                        "Intervalos de Ouvido", "Leitura à Primeira Vista", "Improvisação Avançada",
                        "Arranjo", "Performance");
    }
}
