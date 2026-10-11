-- ====================================================================
-- Seed Data for European Universities
-- NOTE: This dataset represents illustrative sample data across
-- all 5 European regions with realistic benchmark thresholds.
-- ====================================================================

-- Western Europe
INSERT INTO universities (name, country, region, min_gre, min_toefl, min_cgpa) VALUES
('University of Oxford', 'United Kingdom', 'Western Europe', 326, 110, 8.8),
('University of Cambridge', 'United Kingdom', 'Western Europe', 328, 110, 9.0),
('Imperial College London', 'United Kingdom', 'Western Europe', 322, 105, 8.5),
('Sorbonne University', 'France', 'Western Europe', 308, 92, 7.6),
('KU Leuven', 'Belgium', 'Western Europe', 312, 96, 7.9),
('University of Amsterdam', 'Netherlands', 'Western Europe', 316, 100, 8.2),
('Trinity College Dublin', 'Ireland', 'Western Europe', 310, 95, 7.8);

-- Northern Europe
INSERT INTO universities (name, country, region, min_gre, min_toefl, min_cgpa) VALUES
('Karolinska Institute', 'Sweden', 'Northern Europe', 318, 100, 8.3),
('KTH Royal Institute of Technology', 'Sweden', 'Northern Europe', 314, 95, 8.0),
('University of Copenhagen', 'Denmark', 'Northern Europe', 312, 94, 7.9),
('University of Helsinki', 'Finland', 'Northern Europe', 306, 90, 7.5),
('University of Oslo', 'Norway', 'Northern Europe', 310, 92, 7.7);

-- Central Europe
INSERT INTO universities (name, country, region, min_gre, min_toefl, min_cgpa) VALUES
('ETH Zurich', 'Switzerland', 'Central Europe', 325, 108, 8.9),
('Technical University of Munich', 'Germany', 'Central Europe', 320, 100, 8.4),
('Heidelberg University', 'Germany', 'Central Europe', 315, 98, 8.1),
('University of Vienna', 'Austria', 'Central Europe', 305, 90, 7.4),
('Charles University', 'Czech Republic', 'Central Europe', 300, 85, 7.2),
('University of Warsaw', 'Poland', 'Central Europe', 298, 85, 7.0);

-- Southern Europe
INSERT INTO universities (name, country, region, min_gre, min_toefl, min_cgpa) VALUES
('Politecnico di Milano', 'Italy', 'Southern Europe', 310, 92, 7.8),
('University of Bologna', 'Italy', 'Southern Europe', 304, 88, 7.4),
('Autonomous University of Barcelona', 'Spain', 'Southern Europe', 306, 90, 7.5),
('University of Lisbon', 'Portugal', 'Southern Europe', 298, 84, 7.1),
('National Technical University of Athens', 'Greece', 'Southern Europe', 302, 86, 7.3);

-- Eastern Europe
INSERT INTO universities (name, country, region, min_gre, min_toefl, min_cgpa) VALUES
('Jagiellonian University', 'Poland', 'Eastern Europe', 295, 82, 6.9),
('Eötvös Loránd University', 'Hungary', 'Eastern Europe', 292, 80, 6.8),
('University of Bucharest', 'Romania', 'Eastern Europe', 290, 78, 6.6),
('Sofia University', 'Bulgaria', 'Eastern Europe', 288, 75, 6.5);
