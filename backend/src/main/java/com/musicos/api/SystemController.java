package com.musicos.api;

import com.musicos.service.BuildIdentityService;
import com.musicos.service.BuildIdentityService.BuildIdentity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {
    private final BuildIdentityService builds;

    public SystemController(BuildIdentityService builds) { this.builds = builds; }

    @GetMapping("/build")
    public BuildIdentity build() { return builds.identity(); }
}
