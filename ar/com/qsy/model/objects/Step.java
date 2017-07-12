package ar.com.qsy.model.objects;

import javax.security.auth.login.Configuration;
import java.util.ArrayList;
import java.util.Set;


public final class Step {
    ArrayList<NodeConfiguration> nodesConfiguration;
    int timeout;
    ExpressionTree expression;

    public Step(ArrayList<NodeConfiguration> n, int t, String expr){
        this.nodesConfiguration=n;
        this.timeout=t;
        this.expression= new ExpressionTree(expr);
    }
    public ArrayList<NodeConfiguration> getNodes(){
        return nodesConfiguration;
    }
    public int getTimeout(){
        return timeout;
    }
    public boolean isFinished(Set<Integer> ids){
        return expression.evaluateExpressionTree(ids);
    }
    
}
