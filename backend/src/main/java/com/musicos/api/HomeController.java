package com.musicos.api;

import com.musicos.api.ApiModels.HomeView;
import com.musicos.domain.InstrumentId;
import com.musicos.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {
    private final HomeService service;

    public HomeController(HomeService service) {
        this.service = service;
    }

    @GetMapping
    public HomeView get(@RequestParam(defaultValue = "guitar") InstrumentId instrument) {
        return service.home(instrument);
    }
}
