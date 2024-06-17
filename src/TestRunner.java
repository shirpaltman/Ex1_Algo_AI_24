public class TestRunner {

    public static void main(String[] args) {
        // Specify the paths to the input and XML configuration files
        String inputTextPath = "input.txt";  // Ensure this path is correctly set to where your input.txt file is located
        String xmlFilePath = "alarm_net.xml";  // Ensure this path is correctly set to where your XML file is located

        // Attempt to read and initialize the Bayesian Network from the XML file
        bayesianNetwork bn = null;
        try {
            bn = xmlFile.read_net(xmlFilePath);  // Ensure this method correctly reads the XML file and constructs the network
            System.out.println("Network successfully loaded.");
        } catch (Exception e) {
            System.out.println("Failed to read the XML file:");
            e.printStackTrace();
            return;  // Exit if there is an error in loading the network
        }

        // Read the input file and process the queries using the Bayesian Network
        readTextfile readTextFile = new readTextfile(inputTextPath);  // Make sure the constructor and methods of ReadTextfile are correctly implemented
        String result = readTextFile.readfile(bn);  // This method should process all lines from the input file and perform necessary queries on the Bayesian Network

        // Output the results of processing the queries
        System.out.println("Processed Queries Output:");
        System.out.println(result);
    }
}
