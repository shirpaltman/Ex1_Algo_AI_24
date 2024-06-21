import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class xmlFile {

    public static bayesianNetwork read_net(String filename) {
//        List<String[]> cpts = new ArrayList<>();
        Map<String,String[]> cpts = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        bayesianNetwork BN = new bayesianNetwork();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document net = db.parse(new File(filename));
            net.getDocumentElement().normalize();

            NodeList variableList = net.getElementsByTagName("VARIABLE");
            NodeList definitionList = net.getElementsByTagName("DEFINITION");

            for (int i = 0; i < variableList.getLength(); i++) {
                Node variableNode = variableList.item(i);
                if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element variableElement = (Element) variableNode;
                    String varName = variableElement.getElementsByTagName("NAME").item(0).getTextContent();
                    List<String> outcomes = new ArrayList<>();
                    NodeList outcomeList = variableElement.getElementsByTagName("OUTCOME");
                    for (int j = 0; j < outcomeList.getLength(); j++) {
                        outcomes.add(outcomeList.item(j).getTextContent());
                    }
                    BN.addNode(new bayesianNode(varName, outcomes));
                }
            }

            for (int i = 0; i < definitionList.getLength(); i++) {
                Node definitionNode = definitionList.item(i);
                if (definitionNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element definitionElement = (Element) definitionNode;
                    String forNode = definitionElement.getElementsByTagName("FOR").item(0).getTextContent();
                    bayesianNode node = BN.getNode(forNode);
                    NodeList givenList = definitionElement.getElementsByTagName("GIVEN");
                    List<String> parents = new ArrayList<>();
                    for (int j = 0; j < givenList.getLength(); j++) {
                        parents.add(givenList.item(j).getTextContent());
                    }
                    node.setParents(parents);
                    node.setNetwork(BN);
                    String table = definitionElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    cpts.put(node.getName(),table.split(" "));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        BN.fixNet();
        for (String i: BN.getNodes().keySet()) {
            bayesianNode node = BN.getNodes().get(i);
            String[] table = cpts.get(i);
            node.build(table);
        }

        for (bayesianNode node : BN.getNodes().values()) {
            System.out.println("CPT for node " + node.getName() + ":");
            node.printCptTable();
        }

        return BN;
    }
}