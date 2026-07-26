package com.hostelchatbot.hostelchatbot.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ComplaintAiService {

    private final RestTemplate restTemplate;
    private final String provider;
    private final String apiKey;
    private final String model;
    private final String endpoint;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class AiComplaintDraft {
        private final String description;
        private final String type;

        public AiComplaintDraft(String description, String type) {
            this.description = description;
            this.type = type;
        }

        public String description() {
            return description;
        }

        public String type() {
            return type;
        }
    }

    public ComplaintAiService() {
        this(new RestTemplate(), "google", "", "gemini-2.0-flash", "", "microsoft/Phi-3-mini-4k-instruct", "",
                "llama-3.1-8b-instant");
    }

    @Autowired
    public ComplaintAiService(@Value("${ai.provider:google}") String provider,
            @Value("${ai.google.api-key:}") String googleApiKey,
            @Value("${ai.google.model:gemini-2.0-flash}") String googleModel,
            @Value("${ai.huggingface.api-key:}") String huggingFaceApiKey,
            @Value("${ai.huggingface.model:microsoft/Phi-3-mini-4k-instruct}") String huggingFaceModel,
            @Value("${ai.groq.api-key:}") String groqApiKey,
            @Value("${ai.groq.model:llama-3.1-8b-instant}") String groqModel) {
        this(new RestTemplate(), provider, googleApiKey, googleModel, huggingFaceApiKey, huggingFaceModel, groqApiKey,
                groqModel);
    }

    ComplaintAiService(RestTemplate restTemplate,
            String provider,
            String googleApiKey,
            String googleModel,
            String huggingFaceApiKey,
            String huggingFaceModel,
            String groqApiKey,
            String groqModel) {
        this.restTemplate = restTemplate == null ? new RestTemplate() : restTemplate;
        this.provider = provider == null ? "google" : provider.toLowerCase(Locale.ROOT);

        if ("google".equals(this.provider)) {
            this.apiKey = googleApiKey;
            this.model = googleModel;
            this.endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + this.model
                    + ":generateContent?key=" + this.apiKey;
        } else if ("huggingface".equals(this.provider)) {
            this.apiKey = huggingFaceApiKey;
            this.model = huggingFaceModel;
            this.endpoint = "https://api-inference.huggingface.co/models/" + this.model;
        } else if ("groq".equals(this.provider)) {
            this.apiKey = groqApiKey;
            this.model = groqModel;
            this.endpoint = "https://api.groq.com/openai/v1/chat/completions";
        } else {
            throw new IllegalArgumentException("Unsupported AI provider: " + provider);
        }
    }

    public AiComplaintDraft parse(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return new AiComplaintDraft("General complaint", "other");
        }

        if (apiKey == null || apiKey.isBlank()) {
            return new AiComplaintDraft(userMessage.trim(), "other");
        }

        try {
            if ("google".equals(provider)) {
                return callGoogle(userMessage);
            }

            if ("groq".equals(provider)) {
                return callGroq(userMessage);
            }

            return callHuggingFace(userMessage);
        } catch (Exception ex) {
            return new AiComplaintDraft(userMessage.trim(), "other");
        }
    }

    AiComplaintDraft parseStructuredResponse(String rawResponse) {
        try {
            String cleanedResponse = rawResponse == null ? "" : rawResponse.trim();
            if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.replaceFirst("```json", "").replaceFirst("```", "").trim();
            }

            JsonNode node = objectMapper.readTree(cleanedResponse);
            JsonNode contentNode = node.path("choices").path(0).path("message").path("content");
            if (contentNode.isTextual()) {
                String nestedContent = contentNode.asText();
                String unwrappedContent = unwrapJsonPayload(nestedContent);
                if (!unwrappedContent.isBlank()) {
                    JsonNode parsedNode = objectMapper.readTree(unwrappedContent);
                    if (parsedNode.isObject()) {
                        node = parsedNode;
                    }
                }
            }

            String description = node.path("description").asText("General complaint");
            String type = ComplaintCategory.normalize(node.path("type").asText("other"));
            return new AiComplaintDraft(description, type);
        } catch (Exception ex) {
            return new AiComplaintDraft("General complaint", "other");
        }
    }

    private AiComplaintDraft callGoogle(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", buildPrompt(userMessage))))));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        String responseText = restTemplate.postForObject(endpoint, request, String.class);
        return parseStructuredResponse(extractGoogleText(responseText));
    }

    private AiComplaintDraft callHuggingFace(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of("inputs", buildPrompt(userMessage));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        String responseText = restTemplate.postForObject(endpoint, request, String.class);
        return parseStructuredResponse(extractHuggingFaceText(responseText));
    }

    private AiComplaintDraft callGroq(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", buildPrompt(userMessage))));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        String responseText = restTemplate.postForObject(endpoint, request, String.class);
        return parseStructuredResponse(extractGroqText(responseText));
    }

    private String buildPrompt(String userMessage) {
        return "You are a hostel complaint parser for student complaints. " +
                "Return ONLY valid JSON with keys description and type. " +
                "The description should be a short plain-English summary of the complaint. " +
                "Choose the type carefully from these exact values only: electricity, room-cleaning, furniture, plumbing, sanitation, other. "
                +
                "Use electricity for AC, fan, lights, sockets, switches, electrical appliances. " +
                "Use room-cleaning for dirty room, cleaning, housekeeping, brooming, garbage in room. " +
                "Use furniture for bed, chair, table, cupboard, broken furniture or any furniture releated thing " +
                "Use plumbing for water, tap, pipe, leakage, toilet, bathroom issue. " +
                "Use sanitation for hygiene, garbage, pest, washroom cleanliness, bad smell. " +
                "Use other for anything that does not match the categories. " +
                "User message: " + userMessage;
    }

    private String extractGoogleText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText("");
                }
            }
        } catch (Exception ignored) {
        }
        return response;
    }

    private String extractHuggingFaceText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            if (root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);
                if (first.has("generated_text")) {
                    return first.path("generated_text").asText("");
                }
            }
        } catch (Exception ignored) {
        }
        return response;
    }

    private String extractGroqText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                if (message.has("content")) {
                    return message.path("content").asText("");
                }
            }
        } catch (Exception ignored) {
        }
        return response;
    }

    private String unwrapJsonPayload(String payload) {
        if (payload == null || payload.isBlank()) {
            return "";
        }

        String cleanedPayload = payload.trim();
        if (cleanedPayload.startsWith("```")) {
            cleanedPayload = cleanedPayload.replaceFirst("```json", "").replaceFirst("```", "").trim();
        }

        if ((cleanedPayload.startsWith("\"") && cleanedPayload.endsWith("\"")) || cleanedPayload.contains("\\\"")) {
            try {
                return objectMapper.readValue(cleanedPayload, String.class);
            } catch (Exception ignored) {
            }
        }

        return cleanedPayload;
    }
}
