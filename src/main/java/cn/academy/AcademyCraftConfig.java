package cn.academy;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcademyCraftConfig {
    @SerializedName("ability")
    private Ability ability = new Ability();

    public Ability getAbility() {
        return ability;
    }

    @SerializedName("gui")
    private Map<String, Node> gui = new HashMap<>();

    public Map<String, Node> getGui() {
        return gui;
    }

    public static class Ability {
        @SerializedName("cpRecoverSpeed")
        private float cpRecoverSpeed;

        @SerializedName("metalEntities")
        private Map<String, List<String>> metalEntities = new HashMap<>();

        @SerializedName("metalBlocks")
        private Map<String, List<String>> metalBlocks = new HashMap<>();

        public float getCpRecoverSpeed() {
            return cpRecoverSpeed;
        }

        public Map<String, List<String>> getMetalEntities() {
            return metalEntities;
        }

        public Map<String, List<String>> getMetalBlocks() {
            return metalBlocks;
        }

        public void setCpRecoverSpeed(int cpRecoverSpeed) {
            this.cpRecoverSpeed = cpRecoverSpeed;
            saveConfig();
        }
    }

    public static class Node {
        @SerializedName("pos")
        double[] pos;

        private Node(double[] pos) {
            this.pos = pos;
        }

        public double[] getPos() {
            return pos;
        }

        public void setPos(double[] pos) {
            this.pos = pos;
            saveConfig();
        }
    }

    public static AcademyCraftConfig loadConfig(File file) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!isValidConfig(file)) {
            return writeDefaultConfig(gson, file);
        }

        try (FileReader fileReader = new FileReader(file)) {
            return gson.fromJson(fileReader, AcademyCraftConfig.class);
        } catch (JsonSyntaxException | IOException e) {
            return writeDefaultConfig(gson, file);
        }
    }

    private static boolean isValidConfig(File file) {
        Gson gson = new GsonBuilder().create();
        try (FileReader fileReader = new FileReader(file)) {
            JsonObject jsonObject;
            try {
                jsonObject = gson.fromJson(fileReader, JsonObject.class);
            } catch (JsonSyntaxException e) {
                return false;
            }

            Field[] fields = AcademyCraftConfig.class.getDeclaredFields();
            AcademyCraftConfig defaultConfig = getDefaultConfig();

            for (Field field : fields) {
                String fieldName = field.getName();

                if (!jsonObject.has(fieldName)) {
                    return false;
                }

                JsonElement element = jsonObject.get(fieldName);
                JsonObject nestedObject = element.getAsJsonObject();
                // the inner class's field
                for (Field nestedField : field.getType().getDeclaredFields()) {
                    String nestedFieldName = nestedField.getName();
                    if (!nestedObject.has(nestedFieldName)) {
                        return false;
                    }
                }

                if (field.getType().equals(Map.class)) {
                    Map<String, Node> gui = defaultConfig.getGui();
                    JsonObject guiObject = element.getAsJsonObject();
                    for (String string : gui.keySet()) {
                        if (!guiObject.has(string)) {
                            return false;
                        }
                        // node,like cpbar
                        JsonObject nodeObject = nestedObject.get(string).getAsJsonObject();
                        Field[] nodeFields = AcademyCraftConfig.Node.class.getDeclaredFields();
                        for (Field nodeFild : nodeFields) {
                            String nodeFieldName = nodeFild.getName();
                            if (!nodeObject.has(nodeFieldName)) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static AcademyCraftConfig writeDefaultConfig(Gson gson, File file) throws IOException {
        AcademyCraftConfig defaultConfig = getDefaultConfig();
        try (FileWriter fileWriter = new FileWriter(file)) {
            String json = gson.toJson(defaultConfig);
            fileWriter.write(json);
            return defaultConfig;
        }
    }

    private static AcademyCraftConfig getDefaultConfig() {
        AcademyCraftConfig defaultConfig = new AcademyCraftConfig();
        defaultConfig.getAbility().cpRecoverSpeed = 1.0f;
        defaultConfig.getGui().put("cpbar", new Node(new double[]{-12, 12}));
        defaultConfig.getGui().put("keyhint", new Node(new double[]{0, 30}));
        defaultConfig.getGui().put("media", new Node(new double[]{-6, -6}));
        defaultConfig.getGui().put("notification", new Node(new double[]{0, 15}));
        return defaultConfig;
    }

    private static void saveConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fileWriter = new FileWriter(AcademyCraft.configFile)) {
            if (AcademyCraft.academyCraftConfig != null) {
                String json = gson.toJson(AcademyCraft.academyCraftConfig);
                fileWriter.write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}