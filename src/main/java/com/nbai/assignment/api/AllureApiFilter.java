package com.nbai.assignment.api;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Attaches one short text attachment per API call to the current Allure test.
 * Credentials never reach the report: cookies and headers are not recorded, and the /auth call (whose request
 * carries the password and whose response carries the token) is attached without either body.
 */
final class AllureApiFilter implements Filter {

    private static final String AUTH_PATH = "/auth";
    private static final int MAX_BODY_LENGTH = 10_000;

    @Override
    public Response filter(FilterableRequestSpecification request, FilterableResponseSpecification response,
            FilterContext context) {
        Response result = context.next(request, response);
        boolean sensitive = request.getURI().endsWith(AUTH_PATH);

        StringBuilder text = new StringBuilder()
                .append(request.getMethod()).append(' ').append(request.getURI()).append('\n');
        if (!sensitive && request.getBody() != null) {
            text.append("Request body: ").append(truncate(String.valueOf(request.<Object>getBody()))).append('\n');
        }
        text.append("Status: ").append(result.getStatusLine()).append('\n');
        if (!sensitive) {
            text.append("Response body: ").append(truncate(result.getBody().asString()));
        }
        Allure.addAttachment(request.getMethod() + " " + request.getURI(), "text/plain", text.toString());
        return result;
    }

    private static String truncate(String body) {
        if (body.length() <= MAX_BODY_LENGTH) {
            return body;
        }
        return body.substring(0, MAX_BODY_LENGTH) + "... [truncated, " + (body.length() - MAX_BODY_LENGTH)
                + " more characters]";
    }
}
