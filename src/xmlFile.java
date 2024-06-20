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

public class xmlFile {
    public static bayesianNetwork read_net(String filename) {
        HashMap<String,String[]> cpts = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        bayesianNetwork BN = new bayesianNetwork();
        ArrayList<String> variables = new ArrayList<>();
        ArrayList<String> outcomes = new ArrayList<>();
        ArrayList<String> givens = new ArrayList<>();
        ArrayList<String> tables = new ArrayList<>();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document net = db.parse(new File(filename));
            net.getDocumentElement().normalize();

            NodeList variable = net.getElementsByTagName("VARIABLE");
            NodeList definition = net.getElementsByTagName("DEFINITION");

            for (int i = 0; i < variable.getLength(); i++) {
                variables = new ArrayList<>();
                outcomes = new ArrayList<>();
                givens = new ArrayList<>();
                tables = new ArrayList<>();

                Node var = variable.item(i);
                if (var.getNodeType() == Node.ELEMENT_NODE) {
                    Element outcome_var = (Element) var;

                    String name = outcome_var.getElementsByTagName("NAME").item(0).getTextContent();
                    variables.add(name);
                    for (int j = 0; j < outcome_var.getElementsByTagName("OUTCOME").getLength(); j++) {
                        outcomes.add(outcome_var.getElementsByTagName("OUTCOME").item(j).getTextContent());
                    }
                }

                Node def = definition.item(i);
                if (def.getNodeType() == Node.ELEMENT_NODE) {
                    Element outcome_def = (Element) def;

                    String for_def = outcome_def.getElementsByTagName("FOR").item(0).getTextContent();
                    for (int j = 0; j < outcome_def.getElementsByTagName("GIVEN").getLength(); j++) {
                        givens.add(outcome_def.getElementsByTagName("GIVEN").item(j).getTextContent());
                    }
                    for (int j = 0; j < outcome_def.getElementsByTagName("TABLE").getLength(); j++) {
                        tables.add(outcome_def.getElementsByTagName("TABLE").item(j).getTextContent());
                    }
                }
                String[] table = tables.get(0).split(" ");
                bayesianNode bn = new bayesianNode(variables.get(0), givens, outcomes,BN);
                cpts.put(bn.getName(),table);
                BN.addNode(bn);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        BN.fixNet();
        List<String> keys = BN.getNodes().keySet().stream().toList();
        for (int i=0;i<keys.size();i++) {
            String nodeName = keys.get(i);
            bayesianNode node = BN.getNode(nodeName);
            if (node != null) {
                node.build(cpts.get(nodeName));
            } else {
                System.out.println("Error: Node is null for name " + nodeName);
            }
        }
        return BN;
    }
}