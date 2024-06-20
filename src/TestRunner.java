import java.util.*;

public class TestRunner {

    public static void main(String[] args) {
        String xmlFilePath = "alarm_net.xml";
        String textFilePath = "input.txt"; ;

        bayesianNetwork network = xmlFile.readNetwork(xmlFilePath);
        if (network != null) {
            System.out.println("Network successfully loaded.");
            String result=readTextfile.readfile(textFilePath, network);
            System.out.println(result);
        } else {
            System.out.println("Failed to load the network.");
        }
    }
}