package cn.academy;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AcademyConfig {
    @SerializedName("ability")
    private Ability ability;

    public Ability getAbility() {
        return ability;
    }

    public static class Ability {
        @SerializedName("metalEntities")
        private Map<String, List<String>> metalEntities;

        public Map<String, List<String>> getMetalEntities() {
            return metalEntities;
        }

        @SerializedName("metalBlocks")
        private Map<String, List<String>> metalBlocks;

        public Map<String, List<String>> getMetalBlocks() {
            return metalBlocks;
        }
    }

    public static AcademyConfig loadConfig(String filePath) throws IOException {
        try (FileReader fileReader = new FileReader(filePath)) {
            Gson gson = new Gson();
            return gson.fromJson(fileReader, AcademyConfig.class);
        }
    }
}