package com.nbai.assignment;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import java.util.Map;
import org.testng.annotations.Test;

/** Local-only check that the build toolchain and key libraries are wired up. No network calls. */
public class SmokeTest {

    @Test
    public void toolchainAndLibrariesAreOnClasspath() throws Exception {
        // REST Assured is resolvable.
        assertNotNull(RestAssured.given());

        // Jackson can round-trip a simple JSON string.
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue("{\"status\":\"ok\"}", Map.class);
        assertEquals(parsed.get("status"), "ok");
    }
}
