package ar.com.qsy.model.objects;

import java.util.ArrayList;
import java.util.Iterator;

public class Routine implements Iterator<Step>{
    private int index;
    ArrayList<Step> steps;
    int numberOfNodes;
    public Routine(int numberOfNodes){
        index=0;
        this.numberOfNodes=numberOfNodes;
        //TODO cargar los steps
    }

    @Override
    public boolean hasNext() {
        return (index<steps.size());
    }

    @Override
    public Step next() {
        return steps.get(index++);
    }
    public int getNumberOfNodes(){
        return numberOfNodes;
    }
}
