package org.yebigun.hrbank.global.seeder;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@RequiredArgsConstructor
public class AllDataSeederRunner {

    private final List<DataSeeder> seeders;

    @PostConstruct
    public void runAllSeeders() {
        seeders.forEach(DataSeeder::seed);
    }

}
