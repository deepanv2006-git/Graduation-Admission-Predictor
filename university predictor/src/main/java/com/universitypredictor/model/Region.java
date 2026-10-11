package com.universitypredictor.model;

import com.universitypredictor.exception.InvalidInputException;

/**
 * Enumeration representing the five geographic regions of Europe.
 * 
 * In an interview, explain:
 * "The Region enum provides type safety, preventing invalid arbitrary strings
 * from being used as regions in our business logic. Enums in Java can have fields,
 * constructors, and helper methods, making them much richer than basic constants."
 */
public enum Region {
    WESTERN_EUROPE("Western Europe"),
    NORTHERN_EUROPE("Northern Europe"),
    CENTRAL_EUROPE("Central Europe"),
    SOUTHERN_EUROPE("Southern Europe"),
    EASTERN_EUROPE("Eastern Europe");

    private final String displayName;

    /**
     * Enum constructor associating each constant with its official display title.
     * 
     * @param displayName Human-readable label (e.g. "Western Europe")
     */
    Region(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name of the region.
     * 
     * @return Formatted region name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a string representation (either enum constant name or display name)
     * into a corresponding Region enum value.
     * 
     * @param text Raw input string, e.g. "Western Europe" or "WESTERN_EUROPE"
     * @return Matching Region enum value
     * @throws InvalidInputException if the input doesn't match any known European region
     */
    public static Region fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidInputException("Region name cannot be empty.");
        }
        String clean = text.trim();
        for (Region region : values()) {
            if (region.displayName.equalsIgnoreCase(clean) || region.name().equalsIgnoreCase(clean.replace(' ', '_'))) {
                return region;
            }
        }
        throw new InvalidInputException("Unknown European region: '" + text + "'. Valid options are: Western Europe, Northern Europe, Central Europe, Southern Europe, Eastern Europe.");
    }
}
