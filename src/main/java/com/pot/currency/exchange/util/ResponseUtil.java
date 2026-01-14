package com.pot.currency.exchange.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class ResponseUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void sendResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setStatus(status);
        MAPPER.writeValue(response.getWriter(), data);
    }

    public static void sendError(HttpServletResponse resp, int status, String message) {
        resp.setStatus(status);
        try {
            MAPPER.writeValue(resp.getWriter(), Map.of("message", message));
        } catch (IOException e) {
            System.err.println("Failed to send error response: " + e.getMessage());
            if (!resp.isCommitted()) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
