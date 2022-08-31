package ga.problems.knapsack;


import ga.framework.model.NoSolutionException;
import ga.framework.model.Problem;
import ga.framework.model.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class KnapsackProblem implements Problem {

    //Fields
    int capacity;
    List<Item> itemsToPack;

    public KnapsackProblem(int capacity, List<Item> itemsToPack){
        this.capacity = capacity;
        this.itemsToPack = itemsToPack;
    }

    @Override
    public Solution createNewSolution() throws NoSolutionException {
        List<Item> itemsInKnapsack = new ArrayList<>();
        List<Item> itemsNotInKnapsack = new ArrayList<>(this.itemsToPack);
        int capacityLeft = this.capacity;

        //check if all items are too heavy
        boolean allItemsTooHeavy = true;
        for(Item item : itemsToPack){
            if(item.weight < this.capacity){
                allItemsTooHeavy = false;
            }
        }
        if(allItemsTooHeavy){
            throw new NoSolutionException("All items are too heavy");
        }

        //Fill Backpack with random items
        while(true){

            //Get possible items
            List<Item> possibleItems = new ArrayList<>();
            for(Item item : itemsNotInKnapsack){
                if(item.weight <= capacityLeft){
                    possibleItems.add(item);
                }
            }
            if(possibleItems.isEmpty()){
                break;
            }

            //Add random item to Backpack
            Random random = new Random();
            int randomIndex = random.nextInt(0, possibleItems.size());
            Item item = possibleItems.get(randomIndex);
            itemsInKnapsack.add(item);

            //Calculate new capacity
            capacityLeft = capacityLeft - item.weight;

            //Update list of items not in backpack
            itemsNotInKnapsack.remove(item);
        }
        return new KnapsackSolution(this, itemsInKnapsack, itemsNotInKnapsack);
    }

}
