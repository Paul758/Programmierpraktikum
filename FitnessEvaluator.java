package ga.problems.knapsack;

import ga.framework.model.Solution;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FitnessEvaluator implements ga.framework.operators.FitnessEvaluator {

    @Override
    public void evaluate(List<Solution> population) {
        population.stream()
                .map(s -> (KnapsackSolution) s)
                .forEach(s -> s.setFitness(s.itemsInKnapsack.stream().reduce(0d, (sum, item) -> sum + item.value, Double::sum)));
    }
}
