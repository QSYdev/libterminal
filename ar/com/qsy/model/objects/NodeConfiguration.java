package ar.com.qsy.model.objects;

import java.awt.Color;

public class NodeConfiguration {
    int id;
    int delay;
    Color color;

    public NodeConfiguration(int id, int delay, Color color){
        this.id=id;
        this.delay=delay;
        this.color=color;
    }

    public int getId() {
        return id;
    }

    public int getDelay() {
        return delay;
    }

    public Color getColor() {
        return color;
    }
}
