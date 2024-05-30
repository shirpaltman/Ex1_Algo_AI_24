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
            bayesianNetwork BN =read_net("network.xml");
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

    private  static String[]parseTable(Element element){
        String tableString = defElement.getElementByTagName("TABLE").item(0).getTextContent();
        return  tableString.split(" ");   //Spliting the table string into an array of probs
    }

    static  class bayesianNode{
        String name;
        List<String> outcomes;
        List <String> parents;
        bayesianNetwork network;
        List <bayesianNode> children;
        Map<String,Double> cpt;

        bayesianNode(String name,List<String>parents, bayesianNetwork network, List<String> outcomes){
            this.name =name;
            this.outcomes =outcomes;
            this.parents = parents;
            this.network =network;
            this.cpt = new HashMap<>(); //Initialize the CPT
            this.children = new ArrayList<>(); //Initialize the children list
        }

        void build (String[] table){
            int outcomeCount =outcomes.size();
            int parentCombinations =table.length /outcomeCount;
            for (int i =0 ;i<parentCombinations; i++){
                for (int j=0;j<outcomeCount; j++){
                    String key= generateKey(i,j,outcomeCount);
                    cpt.put(key,Double.parseDouble(table[i *outcomeCount +j]));
                }
            }
        }


        private  String generateKey(int parentCombination,int outcomeIndex, int outcomeCount){
            StringBuilder key = new StringBuilder();

            //convert parent comination index to binary  representation
            for ( int i = parents.size()-1; i>=0 ; i--){
                int val = (parentCombination/ (1<<i))%2;
                key.append(val).append(",");
            }
            //Append the outcome index
            key.append(outcomeIndex);
            return  key.toString();
        }



        void  addChild(bayesianNode child){
            children.add(child);
        }
        @Override
        public String toString(){
            return "Node{name" + name + ",outcomes=" + outcomes + ",parents=" +parents + ",CPT=" +cpt + "}" ;
        }
    }

    static class bayesianNetwork{
        List<bayesianNode> nodes;

        bayesianNetwork(){
            this.nodes = new ArrayList<>();
        }
        void add_set(bayesianNode node){
            nodes.add(node);
        }


        //Method to fix ant issues with the network structure
        void fixNet(){
            Map<String,bayesianNode> nodeMap = new HashMap<>();
            for (bayesianNode node : nodes){
                for(String parentName : node.parents){
                    bayesianNode parent = nodeMap.get(parentName);
                    if(parent != null){
                        parent.addChild(node);
                    }
                }
            }
        }

        @Override
        public String toString(){
            return "Network{nodes=" + nodes + "}";
        }
    }






    public static bayesianNetwork read_net(String filename) throws Exception{
        ArrayList<String[]> conditional_prob_table = new ArrayList<>();  //list which holds the CPTs
        DocumentBuilderFactory doc_builder_factory = DocumentBuilderFactory.newInstance();
        bayesianNetwork BN = new bayesianNetwork();    //creating a new Bayesian Network


        doc_builder_factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
        DocumentBuilder db = doc_builder_factory.newDocumentBuilder(); //creating a document builder
        Document net = db.parse(new File (filename));
        net.getDocumentElement().normalize();  //Normalizing the XML structure
        NodeList variableNodes = net.getElementsByTagName("VARIABLE");
        NodeList definitionNodes = net.getElementsByTagName("DEFINITION");

        //loop through all VARIABLE elements
        for(int i=0; i< variableNodes.getLength(); i++){
            Node varNode = variableNodes.item(i);
            if(varNode.getNodeType() == Node.ELEMENT_NODE){
                Element varElement = (Element) varNode;
                String name = varElement.getElementsByTagName("NAME").item(0).getTextContent();  //getting the variable's NAME
                List<String> outcomes = parseOutcomes(varElement);

                Node defNode = definitionNodes.item(i);
                if(defNode.getNodeType() == Node.ELEMENT_NODE){
                    Element defElement =(Element) defNode;
                    List<String> givens = parseGivens(defElement);  //get the parent's variables
                    String [] table =parseTable(defElement); //get the CPT

                    conditional_prob_table.add(table);
                    bayesianNode bn = new bayesianNode (name,givens,BN,outcomes); //creating a new BN
                    BN.add_set(bn);    //adding the node to the BNetwork
                }
            }
        }
        BN.fixNet();
        for( int i =0; i<BN.nodes.size();i++){
            BN.nodes.get(i).build(conditional_prob_table.get(i));
        }
        return BN; //return the Bnetwork
    }





}



