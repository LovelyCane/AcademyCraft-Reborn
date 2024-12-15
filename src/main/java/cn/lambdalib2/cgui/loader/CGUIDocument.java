package cn.lambdalib2.cgui.loader;

import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.WidgetContainer;
import cn.lambdalib2.cgui.component.Component;
import cn.lambdalib2.cgui.component.Transform;
import cn.lambdalib2.s11n.xml.DOMS11n;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ResourceUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CGUI Doc reader and writer.
 */
@SideOnly(Side.CLIENT)
public enum CGUIDocument {
    instance;
    // API
    /**
     * Reads a CGUI Document from given InputStream.
     */
    public static WidgetContainer read(InputStream in) {
        try {
            return instance.readInternal(instance.db.parse(in));
        } catch (IOException|SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a CGUI Document from given ResourceLocation.
     */
    public static WidgetContainer read(ResourceLocation location) {
        return read(ResourceUtils.getResourceStream(location));
    }

    /**
     * Writes the given CGUI document to the output stream. The stream is to be closed by the user.
     */
    public static void write(WidgetContainer container, OutputStream out) {
        Document doc = instance.db.newDocument();
        instance.writeInternal(container, doc);
        instance.writeDoc(out, doc);
    }

    /**
     * Writes the given CGUI document to the given file.
     */
    public static void write(WidgetContainer container, File dest) {
        try (FileOutputStream ofs = new FileOutputStream(dest)) {
            write(container, ofs);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // IMPL
    public static final String TAG_WIDGET = "Widget", TAG_COMPONENT = "Component";

    private final DOMS11n s11n = DOMS11n.instance;
    private final DocumentBuilder db;

    CGUIDocument() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        try {
            db = dbf.newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Node> toStdList(NodeList l) {
        List<Node> ret = new ArrayList<>();
        for (int i = 0; i < l.getLength(); ++i) {
            ret.add (l.item(i));
        }
        return ret;
    }

    private WidgetContainer readInternal(Document doc) {
        WidgetContainer ret = new WidgetContainer();
        Node root = doc.getFirstChild();
        if (root == null || !root.getNodeName().equals("Root"))
            throw new RuntimeException("Root widget invalid");
        toStdList(root.getChildNodes())
                .stream()
                .filter(n -> n.getNodeName().equalsIgnoreCase(TAG_WIDGET))
                .forEach(n -> readWidget(ret, (Element) n));
        return ret;
    }

    /**
     * Reads a widget from given node and add it into the container.
     */
    private void readWidget(WidgetContainer container, Element node) {
        Widget w = new Widget();
        String name = node.getAttribute("name");
        toStdList(node.getChildNodes())
                .forEach(n ->
                {
                    switch (n.getNodeName()) {
                    case TAG_WIDGET:
                        readWidget(w, (Element) n);
                        break;
                    case TAG_COMPONENT:
                        Optional<Component> comp = readComponent((Element) n);
                        comp.ifPresent(c -> {
                            if (c.name.equals("Transform")) { // Currently Transform needs special treatment
                                w.removeComponent(Transform.class);
                                w.transform = (Transform) c;
                            }
                            w.addComponent(c);
                        });
                        break;
                    }
                });

        if (!container.addWidget(name, w)) {
            Debug.warnFormat("Name clash while reading widget: %s, it is ignored.", name);
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<Component> readComponent(Element node) {
        try {
            Class<? extends Component> klass = (Class<? extends Component>) Class.forName(node.getAttribute("class"));
            return Optional.of(s11n.deserialize(klass, node));
        } catch (Exception e) {
            Debug.error("Failed reading component", e);
            return Optional.empty();
        }
    }

    private void writeInternal(WidgetContainer container, Document doc) {
        Element root = doc.createElement("Root");
        // Use drawList to preserve order
        container.getDrawList().forEach(widget -> {
            Element elem = doc.createElement(TAG_WIDGET);
            writeWidget(widget.getName(), widget, elem);
            root.appendChild(elem);
        });
        doc.appendChild(root);
    }

    private void writeWidget(String name, Widget w, Element dst) {
        Document doc = dst.getOwnerDocument();
        dst.setAttribute("name", name);
        w.getComponentList().forEach(c -> dst.appendChild(writeComponent(c, doc)));
        w.getDrawList().forEach(child -> {
            Element elem = doc.createElement(TAG_WIDGET);
            writeWidget(child.getName(), child, elem);
            dst.appendChild(elem);
        });
    }

    public Node writeComponent(Component component, Document doc) {
        Element ret = (Element) s11n.serialize(doc, TAG_COMPONENT, component);
        ret.setAttribute("class", component.getClass().getCanonicalName());
        return ret;
    }

    private void writeDoc(OutputStream dst, Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(dst);
            transformer.transform(source, result);
        } catch (Exception e) {
            Debug.error("Can't write CGUI document", e);
        }
    }


}
