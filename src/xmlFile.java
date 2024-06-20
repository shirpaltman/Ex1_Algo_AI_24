import java.util.*;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;




public class xmlFile{

    public static bayesianNetwork readNetwork(String filePath) {
        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("VARIABLE");
            bayesianNetwork network = new bayesianNetwork();
            List<bayesianNode> nodes = new ArrayList<>();

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node nNode = nodeList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getElementsByTagName("NAME").item(0).getTextContent();
                    NodeList outcomeList = eElement.getElementsByTagName("OUTCOME");
                    List<String> outcomes = new ArrayList<>();
                    for (int i = 0; i < outcomeList.getLength(); i++) {
                        outcomes.add(outcomeList.item(i).getTextContent());
                    }
                    bayesianNode node = new bayesianNode(name, outcomes);
                    nodes.add(node);
                    network.addNode(node);
                }
            }

            NodeList definitionList = doc.getElementsByTagName("DEFINITION");
            for (int temp = 0; temp < definitionList.getLength(); temp++) {
                Node dNode = definitionList.item(temp);
                if (dNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element dElement = (Element) dNode;
                    String forNode = dElement.getElementsByTagName("FOR").item(0).getTextContent();
                    bayesianNode node = findNodeByName(nodes, forNode);
                    NodeList givenList = dElement.getElementsByTagName("GIVEN");
                    for (int i = 0; i < givenList.getLength(); i++) {
                        String givenNode = givenList.item(i).getTextContent();
                        node.addParent(givenNode);
                    }

                    NodeList tableList = dElement.getElementsByTagName("TABLE");
                    for (int i = 0; i < tableList.getLength(); i++) {
                        HashMap<String, String> row = new HashMap<>();
                        String[] entries = tableList.item(i).getTextContent().split(" ");
                        int index = 0;
                        for (String parentName : node.getParents()) {
                            row.put(parentName, entries[index++]);
                        }
                        row.put(node.getName(), entries[index++]);
                        row.put("P", entries[index]);
                        node.getCPT().add(row);
                    }
                }
            }

            network.fixNet();
            return network;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static bayesianNode findNodeByName(List<bayesianNode> nodes, String name) {
        for (bayesianNode node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }
}



