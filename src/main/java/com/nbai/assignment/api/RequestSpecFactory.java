package com.nbai.assignment.api;

import com.nbai.assignment.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/** Builds the shared REST Assured request specification used by API clients. */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    /** Spec pointing at the configured base URI (see ConfigManager). */
    public static RequestSpecification create() {
        return create(ConfigManager.getBaseUri());
    }

    /** Spec pointing at an explicit base URI. */
    static RequestSpecification create(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .addFilter(new AllureApiFilter())
                .build();
    }
}
