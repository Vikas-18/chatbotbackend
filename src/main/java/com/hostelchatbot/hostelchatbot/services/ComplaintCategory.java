package com.hostelchatbot.hostelchatbot.services;

import java.util.Locale;

public enum ComplaintCategory {
    ELECTRICITY("electricity"),
    ROOM_CLEANING("room-cleaning"),
    FURNITURE("furniture"),
    PLUMBING("plumbing"),
    SANITATION("sanitation"),
    OTHER("other");

    private final String value;

    ComplaintCategory(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ComplaintCategory fromValue(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT)
                .replace("_", "-")
                .replace(" ", "-");

        return switch (normalized) {
            case "electrical", "electricity", "power", "light", "lights", "fan", "fans", "ac", "air-conditioner",
                    "switch", "socket", "plug", "appliance", "appliances", "wiring" ->
                ELECTRICITY;
            case "room-cleaning", "roomcleaning", "cleaning", "clean", "housekeeping", "room-clean",
                    "room-cleaning-service", "room-cleaning-issue", "dirty-room", "dirty", "mess", "garbage-in-room",
                    "room-dirty" ->
                ROOM_CLEANING;
            case "furniture", "chair", "table", "bed", "cupboard", "fixture", "broken-furniture", "almirah", "desk",
                    "rack" ->
                FURNITURE;
            case "plumbing", "water", "tap", "taps", "pipe", "pipes", "drain", "drains", "toilet", "toilets",
                    "washroom", "bathroom", "leakage", "leak" ->
                PLUMBING;
            case "sanitation", "hygiene", "garbage", "dust", "washroom-cleaning", "washroom-cleanliness", "bad-smell",
                    "odor", "pest", "pests" ->
                SANITATION;
            default -> OTHER;
        };
    }

    public static String normalize(String value) {
        return fromValue(value).value();
    }
}
