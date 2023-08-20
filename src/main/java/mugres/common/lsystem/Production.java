package mugres.common.lsystem;

import mugres.common.ProbabilityMap;

import java.util.ArrayList;
import java.util.List;

public class Production {
    private final ProbabilityMap<List<Symbol>> successor;

    private Production(final ProbabilityMap<List<Symbol>> successor) {
        if (successor == null)
            throw new IllegalArgumentException("successor");

        this.successor = successor;
    }

    public static Production of(final String elements) {
        final ProbabilityMap.Builder<List<Symbol>> builder = ProbabilityMap.builder();
        final List<Symbol> successorSymbols = new ArrayList<>();
        for(char c : elements.toCharArray())
            successorSymbols.add(Symbol.of(c));
        builder.add(1.0, successorSymbols);
        return new Production(builder.build());
    }

    public static Production of(final ProbabilityMap<List<Symbol>> successor) {
        return new Production(successor);
    }

    public ProbabilityMap<List<Symbol>> successor() {
        return successor;
    }

    public List<Symbol> produce() {
        return successor.value();
    }
}
