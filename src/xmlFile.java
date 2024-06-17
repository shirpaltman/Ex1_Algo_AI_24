import java.util.*;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import  javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;



public class xmlFile{

    public static void main(String[]args){
        try{
            bayesianNetwork BN =read_net("../network.xml");
            System.out.println(BN);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Helper method to parse outcomes from VARIABLE element
    private static  List<String > parseOutcomes(Element varElement){
        List<String> outcomes =new ArrayList<>();
        NodeList outcomeNodes= varElement.getElementsByTagName("OUTCOME");
        for(int i =0 ;i<outcomeNodes.getLength();i++){
            outcomes.add(outcomeNodes.item(i).getTextContent());   // adding each outcome to the list
        }
        return  outcomes;
    }



    //Helper method to parse parent variables from DEFINITION element
    private static List<String> parseGivens (Element defElement){
        List <String> givens = new ArrayList<>();
        NodeList givensNodes =defElement.getElementsByTagName("GIVEN");
        for (int i =0; i<givensNodes.getLength(); i++){
            givens.add(givensNodes.item(i).getTextContent());
        }
        return  givens;
    }

    private  static String[]parseTable(Element defElement){
        String tableString = defElement.getElementsByTagName("TABLE").item(0).getTextContent();
        return  tableString.split(" ");   //Spliting the table string into an array of probs
    }

    public static bayesianNetwork read_net(String filename) throws Exception{
        //ArrayList<String[]> conditional_prob_table = new ArrayList<>();  //list which holds the CPTs
        DocumentBuilderFactory doc_builder_factory = DocumentBuilderFactory.newInstance();
        bayesianNetwork BN = new bayesianNetwork();    //creating a new Bayesian Network

        DocumentBuilder db = doc_builder_factory.newDocumentBuilder(); //creating a document builder
        Document net = db.parse(new File (filename));

        NodeList variableNodes = net.getElementsByTagName("VARIABLE");
        NodeList definitionNodes = net.getElementsByTagName("DEFINITION");

        //loop through all VARIABLE elements
        for(int i=0; i< variableNodes.getLength(); i++) {
            Node varNode = variableNodes.item(i);
            if (varNode.getNodeType() == Node.ELEMENT_NODE) {
                Element varElement = (Element) varNode;
                String name = varElement.getElementsByTagName("NAME").item(0).getTextContent();  //getting the variable's NAME
                List<String> outcomes = parseOutcomes(varElement);
                bayesianNode node = new bayesianNode(name ,outcomes);
                //node.outcomes.addAll(outcomes);
                BN.add_set(node);
            }
        }
        for(int i=0;i< definitionNodes.getLength(); i++) {
            Node defNode = definitionNodes.item(i);
            if (defNode.getNodeType() == Node.ELEMENT_NODE) {
                Element defElement = (Element) defNode;
                String nodeName = defElement.getElementsByTagName("FOR").item(0).getTextContent();
                List<String> given = parseGivens(defElement);
                String[] table = parseTable(defElement);
                bayesianNode node = BN.returnByName(nodeName);
                for (String parentName : given) {
                    bayesianNode parentNode = BN.returnByName(parentName);
                    node.parents.add(parentNode);
                }
                node.buildCPT(table);
            }
        }
        BN.fixNet();
        return BN;

                    //Debugging with print statements
//                    System.out.println("Variable Name: " +name);
//                    System.out.println("Outcomes: " +outcomes);
//                    System.out.println("Givens: " +givens);
//                    System.out.println("Table: " +Arrays.toString(table));


        }

}



