package com.universitypredictor.model;

import com.universitypredictor.exception.InvalidInputException;

/**
 * Encapsulates the institutional profile and minimum admission requirements of a European university.
 * 
 * In an interview, explain:
 * "The University model encapsulates institutional data and ensures that minimum criteria
 * (minGre, minToefl, minCgpa) remain valid and immutable once retrieved from the database.
 * Encapsulation guarantees that business objects maintain a consistent internal state."
 */
public class University {

    private final int id;
    private final String name;
    private final String country;
    private final Region region;
    private final int minGre;
    private final int minToefl;
    private final double minCgpa;

    /**
     * Constructs a University object with validation.
     * 
     * @param id Database primary key identifier
     * @param name Institution name (e.g. "University of Oxford")
     * @param country Sovereign nation (e.g. "United Kingdom")
     * @param region Geographic European region enum
     * @param minGre Minimum typical GRE benchmark (260 - 340)
     * @param minToefl Minimum typical TOEFL benchmark (0 - 120)
     * @param minCgpa Minimum typical CGPA benchmark (0.0 - 10.0)
     */
    public University(int id, String name, String country, Region region, int minGre, int minToefl, double minCgpa) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("University name cannot be blank.");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new InvalidInputException("University country cannot be blank.");
        }
        if (region == null) {
            throw new InvalidInputException("University region must be defined.");
        }
        if (minGre < 260 || minGre > 340) {
            throw new InvalidInputException("University min GRE must be between 260 and 340.");
        }
        if (minToefl < 0 || minToefl > 120) {
            throw new InvalidInputException("University min TOEFL must be between 0 and 120.");
        }
        if (minCgpa < 0.0 || minCgpa > 10.0) {
            throw new InvalidInputException("University min CGPA must be between 0.0 and 10.0.");
        }

        this.id = id;
        this.name = name.trim();
        this.country = country.trim();
        this.region = region;
        this.minGre = minGre;
        this.minToefl = minToefl;
        this.minCgpa = Math.round(minCgpa * 100.0) / 100.0;
    }

    // --- Accessor (Getter) Methods ---

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String country() {
        return country;
    }

    public String getCountry() {
        return country;
    }

    public Region getRegion() {
        return region;
    }

    public String getRegionName() {
        return region.getDisplayName();
    }

    public int getMinGre() {
        return minGre;
    }

    public int getMinToefl() {
        return minToefl;
    }

    public double getMinCgpa() {
        return minCgpa;
    }

    @Override
    public String toString() {
        return "University{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", region=" + region.getDisplayName() +
                ", minGre=" + minGre +
                ", minToefl=" + minToefl +
                ", minCgpa=" + minCgpa +
                '}';
    }
}
