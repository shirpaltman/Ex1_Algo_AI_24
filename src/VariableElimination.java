import java.util.ArrayList;


public class VariableElimination {
    String query;
    String[] hidden;
    String [] evidence;
    bayesianNetwork Bayesian_Network;


    public VariableElimination(String query,String[] hidden,String[] evidence,bayesianNetwork bayesian_Network){
        this.query=query;
        this.hidden =hidden;
        this.evidence = evidence;
        this.Bayesian_Network =bayesian_Network;
    }

    public String VariableElimination(){

        return  "Variable Elimination Result";
    }
}
