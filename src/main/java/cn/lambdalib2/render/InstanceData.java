package cn.lambdalib2.render;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class InstanceData {

    final Map<String, Object> objectMap = new HashMap<>();

    public void setVec2(String key, Vector2f v) {
        objectMap.put(key, v);
    }

    public void setVec3(String key, Vector3f v) {
        objectMap.put(key, v);
    }

    public void setFloat(String key, float v) {
        objectMap.put(key, v);
    }

}
