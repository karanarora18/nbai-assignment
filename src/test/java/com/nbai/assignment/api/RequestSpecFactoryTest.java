package com.nbai.assignment.api;

import static org.testng.Assert.assertEquals;

import com.nbai.assignment.config.ConfigManager;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.testng.annotations.Test;

/** Inspects the built spec only; no request is sent. */
public class RequestSpecFactoryTest {

    @Test
    public void specUsesExplicitBaseUri() {
        RequestSpecification spec = RequestSpecFactory.create("http://localhost:3001");

        assertEquals(SpecificationQuerier.query(spec).getBaseUri(), "http://localhost:3001");
    }

    @Test
    public void specUsesBaseUriFromConfigManager() {
        RequestSpecification spec = RequestSpecFactory.create();

        assertEquals(SpecificationQuerier.query(spec).getBaseUri(), ConfigManager.getBaseUri());
    }
}
