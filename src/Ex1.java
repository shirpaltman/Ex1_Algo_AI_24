import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Ex1 {

    public static void main(String[] args) {
        String xmlFilePath = "alarm_net.xml";
        String textFilePath = "input.txt"; ;


        File out = new File("output.txt");
        try(FileWriter writer = new FileWriter(out)){
            bayesianNetwork network = xmlFile.read_net(xmlFilePath);
            if (network != null) {
                System.out.println("Network successfully loaded.");
                String result = readTextfile.readfile(textFilePath, network);
                writer.append(result);
            } else {
                System.out.println("Failed to load the network.");
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}