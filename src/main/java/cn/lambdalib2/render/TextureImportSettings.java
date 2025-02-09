package cn.lambdalib2.render;

public class TextureImportSettings {
    public enum FilterMode {
        Point, Blinear, Trilinear
    }

    public enum WrapMode {
        Repeat, Clamp
    }

    public final FilterMode filterMode;
    public final WrapMode wrapMode;

    public TextureImportSettings(FilterMode filterMode, WrapMode wrapMode) {
        this.filterMode = filterMode;
        this.wrapMode = wrapMode;
    }
}
