package ga.problems.knapsack;

import ga.framework.model.Problem;
import ga.framework.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class KnapsackSolution extends Solution {

    public List<Item> itemsInKnapsack;
    public List<Item> itemsNotInKnapsack;

    public KnapsackSolution(Problem problem, List<Item> itemsInKnapsack,
                            List<Item> itemsNotInKnapsack){
        super(problem);
        this.itemsInKnapsack = itemsInKnapsack;
        this.itemsNotInKnapsack = itemsNotInKnapsack;
    }

    public KnapsackSolution(Problem problem) {
        super(problem);
    }

    public KnapsackSolution(Solution toCopy){
        super(toCopy);
        KnapsackSolution knapsackSolution = (KnapsackSolution) toCopy;
        this.itemsInKnapsack = new ArrayList<>(knapsackSolution.itemsInKnapsack);
        this.itemsNotInKnapsack = new ArrayList<>(knapsackSolution.itemsNotInKnapsack);
    }

    @Override
    public String toString() {
        return "KnapsackSolution{" +
                "itemsInKnapsack=" + itemsInKnapsack + "fitness =" + getFitness() +
                '}';
    }

    public void addItemToKnapsack(Item item){
        itemsInKnapsack.add(item);
        itemsNotInKnapsack.remove(item);
    }

    public void removeItemFromKnapsack(Item item){
        itemsInKnapsack.remove(item);
        itemsNotInKnapsack.add(item);

    }
}
