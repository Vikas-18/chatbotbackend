package com.hostelchatbot.hostelchatbot.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplaintAiServiceTest {

    private final ComplaintAiService complaintAiService = new ComplaintAiService(null, "google", "", "", "", "", "", "");

    @Test
    void parsesStructuredJsonResponse() {
        ComplaintAiService.AiComplaintDraft draft = complaintAiService.parseStructuredResponse("""
                {"description":"AC is not working","type":"electrical"}
                """);

        assertEquals("electricity", draft.type());
        assertTrue(draft.description().contains("AC"));
    }

    @Test
    void defaultsToGeneralWhenResponseIsMissingFields() {
        ComplaintAiService.AiComplaintDraft draft = complaintAiService.parseStructuredResponse("{}");

        assertEquals("other", draft.type());
        assertEquals("General complaint", draft.description());
    }

    @Test
    void extractsCategoryFromGroqStyleResponse() {
        ComplaintAiService.AiComplaintDraft draft = complaintAiService.parseStructuredResponse("""
                {"choices":[{"message":{"content":"{\\\"description\\\":\\\"Room needs cleaning\\\",\\\"type\\\":\\\"room-cleaning\\\"}"}}]}
                """);

        assertEquals("room-cleaning", draft.type());
        assertTrue(draft.description().contains("Room"));
    }
}
