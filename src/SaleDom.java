import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Scanner;

public class SaleDom {
    public static void crearNuevoXML(Document doc) {
        try {
            // Crear un nuevo Documento
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document newDoc = db.newDocument();

            // Crear un elemento raíz para el nuevo documento
            Element rootElement = newDoc.createElement("sales_data");
            newDoc.appendChild(rootElement);

            // Guardar el nuevo documento en un archivo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newDoc);
            StreamResult result = new StreamResult(new FileOutputStream("new-sales.xml"));

            transformer.transform(source, result);

            System.out.println("Nuevo archivo XML 'new-sales.xml' creado con éxito.");
        } catch (ParserConfigurationException | IOException | TransformerException e) {
            salesDOM.LOG.severe("Error al crear el nuevo archivo XML: " + e.getMessage());
        }
    }
    public static double obtenerPorcentajeIncremento() {
        Scanner scanner = new Scanner(System.in);
        double porcentaje = 0.0;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print("Ingrese un valor de incremento entre 5% y 15%: ");
            porcentaje = scanner.nextDouble();

            if (porcentaje >= 5 && porcentaje <= 15) {
                entradaValida = true;
            } else {
                System.out.println("El valor debe estar entre 5% y 15%.");
            }
        }

        return porcentaje;
    }
    public static void salesState(Document doc, String s) {
        Element root = doc.getDocumentElement();

        NodeList salesData = root.getElementsByTagName("sale_record");

        int n = salesData.getLength();
        double incremento = obtenerPorcentajeIncremento() / 100.0; // Obtiene el incremento como fracción

        for (int index = 0; index < n; index++) {
            Node node = salesData.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = element.getElementsByTagName("id").item(0).getTextContent();
                String first_name = element.getElementsByTagName("first_name").item(0).getTextContent();
                String last_name = element.getElementsByTagName("last_name").item(0).getTextContent();
                String sales = element.getElementsByTagName("sales").item(0).getTextContent();
                String state = element.getElementsByTagName("state").item(0).getTextContent();
                String department = element.getElementsByTagName("department").item(0).getTextContent();

                if (state.equals(s)) {
                    double ventasAnteriores = Double.parseDouble(sales);
                    double ventasIncrementadas = ventasAnteriores * (1 + incremento);
                    System.out.printf("%4.4s %10.10s %-10.10s %,7.2f %-10.10s %-15.15s\n",
                            id, first_name, last_name, ventasIncrementadas, state, department);
                }
            }
        }
    }
    public static void main(String argv[]) {
        if (argv.length != 1) {
            salesDOM.LOG.severe("Falta archivo XML como argumento.");
            System.exit(1);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(argv[0]));

            doc.getDocumentElement().normalize();

            double ventasTotales = salesDOM.salesTotal(doc);

            System.out.printf("\nVentas totales: $%,8.2f \n", ventasTotales);

            // Llamada a la función salesState para ingresar el porcentaje de incremento
            salesState(doc, "Arizona");

            // Llamada a la función reporteVentas
            salesDOM.reporteVentas(doc);
            // Llamada a la función crearNuevoXML
            crearNuevoXML(doc);

        } catch (ParserConfigurationException e) {
            salesDOM.LOG.severe(e.getMessage());
        } catch (IOException e) {
            salesDOM.LOG.severe(e.getMessage());
        } catch (SAXException e) {
            salesDOM.LOG.severe(e.getMessage());
        }
    }


}
