public class TestRunner {


    public static void main(String[] args) {


        String inputText ="input.txt";
        String xmlFileText = "alarm_net.xml";
        bayesianNetwork bn =null;
        try{
            bn = xmlFile.read_net(xmlFileText);
        }
        catch (Exception error){
            System.out.println("failed to read the XML file");
            error.printStackTrace();
            return;
        }

        readTextfile readTextFile=new readTextfile(inputText);
        String result = readTextFile.readfile(bn);
    }
}