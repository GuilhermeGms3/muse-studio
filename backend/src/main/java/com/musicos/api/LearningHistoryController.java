package com.musicos.api;

import com.musicos.api.ApiModels.LearningHistoryItemView;
import com.musicos.domain.InstrumentId;
import com.musicos.service.LearningHistoryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
public class LearningHistoryController {
    private final LearningHistoryService service;

    public LearningHistoryController(LearningHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<LearningHistoryItemView> history(
            @RequestParam(defaultValue = "guitar") InstrumentId instrument) {
        return service.history(instrument);
    }
}
