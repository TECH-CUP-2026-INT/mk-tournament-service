package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MatchesServiceFeignClientTest {

    @Test
    void feignClient_apuntaALaPropiedadDeBaseUrlDeMatchesConfigurableSinHardcodear() {
        FeignClient annotation = MatchesServiceFeignClient.class.getAnnotation(FeignClient.class);

        assertNotNull(annotation);
        assertEquals("matches-service", annotation.name());
        assertEquals("${matches-service.base-url}", annotation.url());
    }
}
