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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SideOnly(Side.CLIENT)
public enum CGUIDocument {
    instance;

    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static final DocumentBuilder db;

    static {
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
        dbf.setIgnoringElementContentWhitespace(true);

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error initializing document builder", e);
        }
    }

    private final DOMS11n s11n = DOMS11n.instance;

    public static WidgetContainer read(InputStream in) {
        try {
            Document doc = db.parse(in);
            return instance.readInternal(doc);
        } catch (IOException | SAXException e) {
            throw new RuntimeException("Error reading CGUI document", e);
        }
    }

    public static WidgetContainer read(ResourceLocation location) {
        return read(ResourceUtils.getResourceStream(location));
    }

    public static void write(WidgetContainer container, OutputStream out) {
        try {
            Document doc = db.newDocument();
            instance.writeInternal(container, doc);
            instance.writeDoc(out, doc);
        } catch (Exception e) {
            Debug.error("Error writing CGUI document", e);
            throw new RuntimeException("Error writing CGUI document", e);
        }
    }

    public static void write(WidgetContainer container, File dest) {
        try (FileOutputStream ofs = new FileOutputStream(dest)) {
            write(container, ofs);
        } catch (IOException ex) {
            throw new RuntimeException("Error writing CGUI document", ex);
        }
    }

    private List<Node> toStdList(NodeList nodeList) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(nodeList.item(i));
        }
        return nodes;
    }

    private WidgetContainer readInternal(Document doc) {
        WidgetContainer container = new WidgetContainer();
        Node root = doc.getFirstChild();
        if (root == null || !root.getNodeName().equals("Root")) {
            throw new RuntimeException("Root widget invalid");
        }

        toStdList(root.getChildNodes()).stream()
                .filter(n -> n.getNodeName().equalsIgnoreCase("Widget"))
                .forEach(n -> readWidget(container, (Element) n));

        return container;
    }

    private void readWidget(WidgetContainer container, Element node) {
        Widget widget = new Widget();
        String name = node.getAttribute("name");

        toStdList(node.getChildNodes()).forEach(n -> {
            switch (n.getNodeName()) {
                case "Widget":
                    readWidget(widget, (Element) n);
                    break;
                case "Component":
                    readComponent((Element) n).ifPresent(c -> {
                        if (c.name.equals("Transform")) {
                            widget.removeComponent(Transform.class);
                            widget.transform = (Transform) c;
                        }
                        widget.addComponent(c);
                    });
                    break;
            }
        });

        if (!container.addWidget(name, widget)) {
            Debug.warnFormat("Name clash while reading widget: %s, it is ignored.", name);
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<Component> readComponent(Element node) {
        try {
            Class<? extends Component> clazz = (Class<? extends Component>) Class.forName(node.getAttribute("class"));
            return Optional.of(s11n.deserialize(clazz, node));
        } catch (Exception e) {
            Debug.error("Failed reading component", e);
            return Optional.empty();
        }
    }

    private void writeInternal(WidgetContainer container, Document doc) {
        Element root = doc.createElement("Root");
        container.getDrawList().forEach(widget -> {
            Element widgetElement = doc.createElement("Widget");
            writeWidget(widget.getName(), widget, widgetElement);
            root.appendChild(widgetElement);
        });
        doc.appendChild(root);
    }

    private void writeWidget(String name, Widget widget, Element element) {
        Document doc = element.getOwnerDocument();
        element.setAttribute("name", name);

        widget.getComponentList().forEach(component -> element.appendChild(writeComponent(component, doc)));
        widget.getDrawList().forEach(child -> {
            Element widgetElem = doc.createElement("Widget");
            writeWidget(child.getName(), child, widgetElem);
            element.appendChild(widgetElem);
        });
    }

    public Node writeComponent(Component component, Document doc) {
        Element componentElement = (Element) s11n.serialize(doc, "Component", component);
        componentElement.setAttribute("class", component.getClass().getCanonicalName());
        return componentElement;
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