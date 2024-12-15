package cn.lambdalib2.s11n.xml;

import cn.lambdalib2.s11n.SerializationHelper;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.render.font.Fonts;
import cn.lambdalib2.render.font.IFont;
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

    private class SerializerItem {
        final Predicate<Object> pred;
        final Serializer<Object> serializer;

        @SuppressWarnings("unchecked")
        private SerializerItem(Predicate<Object> pred, Serializer<?> serializer) {
            this.pred = pred;
            this.serializer = (Serializer<Object>) serializer;
        }
    }

    private final List<SerializerItem> _serializers = new ArrayList<>();
    private final Map<Class<?>, Deserializer<?>> _deserializers = new HashMap<>();
    private final SerializationHelper serHelper = new SerializationHelper();

    {
        // Primitive types
        addSerializerType((obj, node) -> addText(node, obj.toString()),
            char.class, Character.class,
            int.class, Integer.class,
            float.class, Float.class,
            double.class, Double.class,
            boolean.class, Boolean.class,
            String.class, ResourceLocation.class);

        addSerializer((obj) -> obj.getClass().isEnum(), (obj, node) -> addText(node, obj.toString()));

        addSerializerType((obj, node) -> {
            addText(node, Fonts.getName((IFont) obj));
        }, IFont.class);

        addSerializer((obj) -> obj instanceof Color, (obj, node) -> {
            Color c = (Color) obj;
            Document d = node.getOwnerDocument();

            node.appendChild(serialize(d, "red", c.getRed()));
            node.appendChild(serialize(d, "green", c.getGreen()));
            node.appendChild(serialize(d, "blue", c.getBlue()));
            node.appendChild(serialize(d, "alpha", c.getAlpha()));
        });

        // Literal value parsings
        addDeserializerStr(char.class, str -> str.charAt(0));

        addDeserializerStr(int.class, Integer::parseInt);
        addDeserializerStr(Integer.class, Integer::valueOf);

        addDeserializerStr(float.class, Float::parseFloat);
        addDeserializerStr(Float.class, Float::valueOf);

        addDeserializerStr(double.class, Double::parseDouble);
        addDeserializerStr(Double.class, Double::valueOf);

        addDeserializerStr(boolean.class, Boolean::parseBoolean);
        addDeserializerStr(Boolean.class, Boolean::new);

        addDeserializerStr(String.class, str -> str);
        addDeserializerStr(ResourceLocation.class, ResourceLocation::new);

        addDeserializerStr(IFont.class, str -> {
            if (Fonts.exists(str)) {
                return Fonts.get(str);
            } else {
                Debug.warn("Can't find font " + str);
                return Fonts.getDefault();
            }
        });

        addDeserializer(Color.class, (type, node) -> {
            NodeList ls = node.getChildNodes();
            int r = 0, g = 0, b = 0, a = 0;
            for (int i = 0; i < ls.getLength(); ++i) {
                if (ls instanceof Element) {
                    Element elem = ((Element) ls.item(i));
                    int value = Integer.parseInt(elem.getTextContent());
                    String t = elem.getTagName();
                    if (t.equals("red")) {
                        r = value;
                    } else if (t.equals("green")) {
                        g = value;
                    } else if (t.equals("blue")) {
                        b = value;
                    } else if (t.equals("alpha")) {
                        a = value;
                    }
                }
            }
            return new Color(r, g, b, a);
        });
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
        _serializers.add(new SerializerItem(pred, serializer));
    }

    public <T> void addSerializerType(Serializer<T> serializer, Class<?> ...args) {
        addSerializer(obj -> Arrays.stream(args).anyMatch(it -> it.isInstance(obj)), serializer);
    }

    public <T> void addDeserializer(Class<T> type, Deserializer<T> deserializer) {
        _deserializers.put(type, deserializer);
        serHelper.regS11nType(type);
    }

    public <T> void addDeserializerStr(Class<T> type, Function<String, T> parseMethod) {
        addDeserializer(type, (__, node) -> parseMethod.apply(node.getTextContent()));
    }

    public void registerS11nType(Class<?> type) {
        serHelper.regS11nType(type);
    }

    public Node serialize(Document doc, String name, Object obj) {
        Serializer<Object> serializer = null;
        for (SerializerItem item : _serializers) {
            if (item.pred.test(obj)) {
                serializer = item.serializer;
                break;
            }
        }

        if (serializer != null) {
            Element elem = doc.createElement(name);
            serializer.serialize(obj, elem);
            return elem;
        } else {
            return serializeDefault(doc, obj, name);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(Class<T> type, Node node) {
        Deserializer<T> deserializer = (Deserializer<T>) _deserializers.get(type);
        if (deserializer != null) {
            return deserializer.deserialize(type, node);
        } else {
            return deserializeDefault(type, node);
        }
    }

    public SerializationHelper getSerHelper() {
        return serHelper;
    }

    private Node serializeDefault(Document doc, Object obj, String name) {
        Element ret = doc.createElement(name);
        List<Field> fields = serHelper.getExposedFields(obj.getClass());
        try {
            for (Field f : fields) {
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
            String content = node.getTextContent();
            return Arrays.stream(type.getEnumConstants())
                .filter(it -> content.equals(it.toString())).findAny().get();
        } else {
            try {
                T ret = type.newInstance();
                List<Field> fields = serHelper.getExposedFields(type);
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    Node item = childNodes.item(i);
                    if (item instanceof Element) {
                        Element elem = ((Element) item);
                        fields.stream()
                            .filter(it -> it.getName().equals(elem.getNodeName()))
                            .findAny()
                            .ifPresent(field -> {
                                boolean isNull = !elem.getAttribute("isNull").isEmpty();
                                try {
                                    field.set(ret, isNull ? null : deserialize(field.getType(), elem));
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                    }
                }
                return ret;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Node addText(Node node, String content) {
        return node.appendChild(node.getOwnerDocument().createTextNode(content));
    }

}
