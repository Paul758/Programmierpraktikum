package ga.problems.knapsack;

import ga.framework.GeneticAlgorithm;
import ga.framework.model.NoSolutionException;
import ga.framework.operators.EvolutionException;
import ga.framework.operators.SurvivalException;
import ga.framework.operators.TopKSurvival;
import ga.framework.operators.TournamentSelection;

import java.util.ArrayList;
import java.util.List;

public class ConcreteProblem {

    public static void main(String[] args) throws NoSolutionException, SurvivalException, EvolutionException {
        ArrayList<Item> items = new ArrayList<>(List.of(
                new Item(5, 10),
                new Item(4, 8),
                new Item(4, 6),
                new Item(4, 4),
                new Item(3, 7),
                new Item(3, 4),
                new Item(2, 6),
                new Item(2, 3),
                new Item(1, 3),
                new Item(1, 1)

        ));

        KnapsackProblem knapsackProblem = new KnapsackProblem(11, items);

        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.solve(knapsackProblem).withPopulationOfSize(4)
                .evolvingSolutionsWith(new KnapsackMutation())
                .evaluatingSolutionsBy(new FitnessEvaluator())
                .performingSelectionsWith(new TournamentSelection(), new TopKSurvival(2))
                .stoppingAtEvolution(10)
                .runOptimization().forEach(System.out::println);
    }
}
