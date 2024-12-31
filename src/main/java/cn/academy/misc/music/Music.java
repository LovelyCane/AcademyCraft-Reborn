package cn.academy.misc.music;

import com.google.gson.annotations.SerializedName;

public class Music {
    @SerializedName("name")
    private String name;

    @SerializedName("file_name")
    private String fileName;

    @SerializedName("file_path")
    private String filePath;

    @SerializedName("icon_path")
    private String iconPath;

    @SerializedName("description")
    private String description;

    public Music(String name, String fileName, String filePath, String iconPath, String description) {
        this.name = name;
        this.fileName = fileName;
        this.filePath = filePath;
        this.iconPath = iconPath;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getDescription() {
        return description;
    }
}
