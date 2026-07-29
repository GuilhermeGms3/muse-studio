package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.config.WebConfig;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.SkillState;
import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;

class WebConfigTest {

    @Test
    void convertsFrontendIdsFromQueryParameters() {
        var conversion = new DefaultFormattingConversionService();
        new WebConfig().addFormatters(conversion);

        assertThat(conversion.convert("guitar", InstrumentId.class)).isEqualTo(InstrumentId.GUITAR);
        assertThat(conversion.convert("practicing", SkillState.class)).isEqualTo(SkillState.PRACTICING);
    }
}
