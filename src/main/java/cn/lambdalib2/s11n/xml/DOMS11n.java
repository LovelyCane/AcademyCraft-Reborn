package cn.lambdalib2.s11n.xml;

import cn.lambdalib2.render.font.Fonts;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.s11n.SerializationHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public enum DOMS11n {
    instance;

    @SuppressWarnings("unchecked")
    private static class SerializerItem {
        final Predicate<Object> pred;
        final Serializer<Object> serializer;

        private SerializerItem(Predicate<Object> pred, Serializer<?> serializer) {
            this.pred = pred;
            this.serializer = (Serializer<Object>) serializer;
        }
    }

    private final List<SerializerItem> serializers = new ArrayList<>();
    private final Map<Class<?>, Deserializer<?>> deserializers = new HashMap<>();
    private final SerializationHelper serHelper = new SerializationHelper();

    {
        initializeSerializers();
        initializeDeserializers();
    }

    @FunctionalInterface
    public interface Serializer<T> {
        void serialize(T obj, Node node);
    }

    @FunctionalInterface
    public interface Deserializer<T> {
        T deserialize(Class<T> type, Node node);
    }

    public <T> void addSerializer(Predicate<Object> pred, Serializer<T> serializer) {
        serializers.add(new SerializerItem(pred, serializer));
    }

    public <T> void addSerializerType(Serializer<T> serializer, Class<?>... args) {
        addSerializer(obj -> Arrays.stream(args).anyMatch(it -> it.isInstance(obj)), serializer);
    }

    public <T> void addDeserializer(Class<T> type, Deserializer<T> deserializer) {
        deserializers.put(type, deserializer);
        serHelper.regS11nType(type);
    }

    public <T> void addDeserializerStr(Class<T> type, Function<String, T> parseMethod) {
        addDeserializer(type, (__, node) -> parseMethod.apply(node.getTextContent()));
    }

    public Node serialize(Document doc, String name, Object obj) {
        Serializer<Object> serializer = findSerializer(obj);
        if (serializer != null) {
            Element elem = doc.createElement(name);
            serializer.serialize(obj, elem);
            return elem;
        }
        return serializeDefault(doc, obj, name);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(Class<T> type, Node node) {
        Deserializer<T> deserializer = (Deserializer<T>) deserializers.get(type);
        return deserializer != null ? deserializer.deserialize(type, node) : deserializeDefault(type, node);
    }

    private void initializeSerializers() {
        addSerializerType((obj, node) -> addText(node, obj.toString()),
                char.class, Character.class,
                int.class, Integer.class,
                float.class, Float.class,
                double.class, Double.class,
                boolean.class, Boolean.class,
                String.class, ResourceLocation.class);
        addSerializer((obj) -> obj.getClass().isEnum(), (obj, node) -> addText(node, obj.toString()));
        addSerializerType((obj, node) -> addText(node, Fonts.getName((IFont) obj)), IFont.class);
        addSerializer((obj) -> obj instanceof Color, (obj, node) -> {
            Color c = (Color) obj;
            Document d = node.getOwnerDocument();
            node.appendChild(serialize(d, "red", c.getRed()));
            node.appendChild(serialize(d, "green", c.getGreen()));
            node.appendChild(serialize(d, "blue", c.getBlue()));
            node.appendChild(serialize(d, "alpha", c.getAlpha()));
        });
    }

    private void initializeDeserializers() {
        addDeserializerStr(char.class, str -> str.charAt(0));
        addDeserializerStr(int.class, Integer::parseInt);
        addDeserializerStr(float.class, Float::parseFloat);
        addDeserializerStr(double.class, Double::parseDouble);
        addDeserializerStr(boolean.class, Boolean::parseBoolean);
        addDeserializerStr(String.class, str -> str);
        addDeserializerStr(ResourceLocation.class, ResourceLocation::new);
        addDeserializerStr(IFont.class, str -> Fonts.exists(str) ? Fonts.get(str) : Fonts.getDefault());
        addDeserializer(Color.class, (type, node) -> {
            NodeList ls = node.getChildNodes();
            int r = 0, g = 0, b = 0, a = 0;
            for (int i = 0; i < ls.getLength(); ++i) {
                if (ls.item(i) instanceof Element) {
                    Element elem = (Element) ls.item(i);
                    int value = Integer.parseInt(elem.getTextContent());
                    switch (elem.getTagName()) {
                        case "red": r = value; break;
                        case "green": g = value; break;
                        case "blue": b = value; break;
                        case "alpha": a = value; break;
                    }
                }
            }
            return new Color(r, g, b, a);
        });
    }

    private Serializer<Object> findSerializer(Object obj) {
        return serializers.stream()
                .filter(item -> item.pred.test(obj))
                .map(item -> item.serializer)
                .findFirst()
                .orElse(null);
    }

    private Node serializeDefault(Document doc, Object obj, String name) {
        Element ret = doc.createElement(name);
        try {
            for (Field f : serHelper.getExposedFields(obj.getClass())) {
                Object fieldValue = f.get(obj);
                if (fieldValue != null) {
                    ret.appendChild(serialize(doc, f.getName(), fieldValue));
                } else {
                    Element nullNode = doc.createElement(f.getName());
                    nullNode.setAttribute("isNull", "true");
                    ret.appendChild(nullNode);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return ret;
    }

    private <T> T deserializeDefault(Class<T> type, Node node) {
        if (type.isEnum()) {
            return Arrays.stream(type.getEnumConstants())
                    .filter(it -> node.getTextContent().equals(it.toString()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Enum value not found"));
        }
        try {
            T ret = type.getDeclaredConstructor().newInstance();
            for (Field field : serHelper.getExposedFields(type)) {
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    if (childNodes.item(i) instanceof Element) {
                        Element elem = (Element) childNodes.item(i);
                        if (elem.getNodeName().equals(field.getName())) {
                            field.setAccessible(true);
                            boolean isNull = !elem.getAttribute("isNull").isEmpty();
                            field.set(ret, isNull ? null : deserialize(field.getType(), elem));
                        }
                    }
                }
            }
            return ret;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void addText(Node node, String content) {
        node.appendChild(node.getOwnerDocument().createTextNode(content));
    }
}