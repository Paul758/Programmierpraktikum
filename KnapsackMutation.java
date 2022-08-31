package ga.problems.knapsack;

import ga.framework.model.Solution;
import ga.framework.operators.EvolutionException;
import ga.framework.operators.EvolutionaryOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KnapsackMutation implements EvolutionaryOperator {

    @Override
    public Solution evolve(Solution solution) throws EvolutionException {
        KnapsackSolution evolvedSolution = new KnapsackSolution(solution);

        boolean versionOneIsPossible = checkVersionOneIsPossible(evolvedSolution);
        boolean versionTwoIsPossible = checkVersionTwoIsPossible(evolvedSolution);

        if(!versionOneIsPossible && !versionTwoIsPossible){
            throw new EvolutionException("no version is possible");
        } else if(versionOneIsPossible && !versionTwoIsPossible){
            return VariantOne.evolveByRemovingItem(evolvedSolution);
        } else if(!versionOneIsPossible && versionTwoIsPossible){
            return VariantTwo.evolveByAddingItem(evolvedSolution);
        } else {
            double random = Math.random();
            if(random < 0.5){
                return VariantOne.evolveByRemovingItem(evolvedSolution);
            } else {
                return VariantTwo.evolveByAddingItem(evolvedSolution);
            }
        }
    }

    private boolean checkVersionOneIsPossible(KnapsackSolution solution) {
        return !solution.itemsInKnapsack.isEmpty();
    }

    private boolean checkVersionTwoIsPossible(KnapsackSolution solution) {
        //Check if capacity is left
        KnapsackProblem currentProblem = (KnapsackProblem) solution.getProblem();
        int maxCapacity = currentProblem.capacity;

        int currentKnapsackWeight = 0;
        for(Item item : solution.itemsInKnapsack){
            currentKnapsackWeight += item.weight;
        }
        int capacityLeft = maxCapacity - currentKnapsackWeight;

        return capacityLeft > 0;
    }

    public static class VariantOne{
        public static Solution evolveByRemovingItem(KnapsackSolution solution){

            int randomNumber = getRandomNumber(0, solution.itemsInKnapsack.size());
            Item item = solution.itemsInKnapsack.get(randomNumber);
            solution.removeItemFromKnapsack(item);
            return solution;
        }
    }

    public static class VariantTwo{
        public static Solution evolveByAddingItem(KnapsackSolution solution){
            List<Item> possibleItems = new ArrayList<>();

            KnapsackProblem currentProblem = (KnapsackProblem) solution.getProblem();
            int maxCapacity = currentProblem.capacity;

            int currentKnapsackWeight = 0;
            for(Item item : solution.itemsInKnapsack){
                currentKnapsackWeight += item.weight;
            }
            int capacityLeft = maxCapacity - currentKnapsackWeight;

            for(Item item : solution.itemsNotInKnapsack){
                if(item.weight <= capacityLeft){
                    possibleItems.add(item);
                }
            }

            if(possibleItems.isEmpty()){
                return solution;
            }

            int randomNumber = getRandomNumber(0,possibleItems.size());
            Item item = possibleItems.get(randomNumber);

            solution.addItemToKnapsack(item);

            return solution;
        }
    }

    public static int getRandomNumber(int lowerBound, int upperBound){
        Random random = new Random();
        return random.nextInt(lowerBound, upperBound);
    }
}
