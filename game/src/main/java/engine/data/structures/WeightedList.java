package engine.data.structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WeightedList<T> {

    private class WeightedNode {
        double weight;
        T value;

        public WeightedNode(T value, double weight) {
            this.value = value;
            this.weight = weight;
        }
    }

    private List<WeightedNode> nodes;
    private int size = 0;

    public WeightedList() {
        nodes = new LinkedList<>();
    }

    public void add(T value, double weight) {
        size++;

        int pos = 0;
        for (WeightedNode node : nodes) {
            if (weight > node.weight) {
                nodes.add(pos, new WeightedNode(value, weight));
                return;
            }
            pos++;
        }
        nodes.add(pos, new WeightedNode(value, weight));
    }

    public List<T> list() {
        List<T> list = new ArrayList<>(size);
        nodes.forEach(node -> list.add(node.value));
        return list;
    }

}
