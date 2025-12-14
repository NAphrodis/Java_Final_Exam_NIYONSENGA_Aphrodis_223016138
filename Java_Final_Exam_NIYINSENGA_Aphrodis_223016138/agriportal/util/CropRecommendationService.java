package com.agriportal.util;

import java.util.ArrayList;
import java.util.List;


public class CropRecommendationService {

    public static class Recommendation {
        public final String cropName;
        public final String reason;

        public Recommendation(String cropName, String reason) {
            this.cropName = cropName;
            this.reason = reason;
        }
    }

    
    
    public List<Recommendation> recommend(String climate, String soilType, double areaHectares) {
        List<Recommendation> out = new ArrayList<Recommendation>();
        // Very naive rules — expand later
        if (climate == null) climate = "";
        if (soilType == null) soilType = "";

        if (climate.toLowerCase().contains("trop") && soilType.toLowerCase().contains("loam")) {
            out.add(new Recommendation("Maize", "Suitable for warm climates & loamy soils"));
            out.add(new Recommendation("Cassava", "Drought tolerant and high yield in region"));
        } else {
            out.add(new Recommendation("Beans", "Good general-purpose legume"));
            out.add(new Recommendation("Sorghum", "Tolerates semi-arid conditions"));
        }
        // Use area heuristics
        if (areaHectares < 1.0) out.add(new Recommendation("Herbs", "High-value small plot"));
        return out;
    }
}
