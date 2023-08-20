package mugres.common.lsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lindermayer System
 */
public class LSystem {
    private final Set<Symbol> alphabet;
    private final RuleSet rules;
    private final List<Symbol> axiom;

    private LSystem(final Set<Symbol> alphabet,
                    final RuleSet rules,
                    final List<Symbol> axiom) {
        if (alphabet == null || alphabet.isEmpty())
            throw new IllegalArgumentException("alphabet");
        if (rules == null)
            throw new IllegalArgumentException("rules");

        rules.rules().keySet().forEach(symbol -> {
            if (!alphabet.contains(symbol))
                throw new IllegalArgumentException("Rules can only refer to alphabet symbols! Unknown symbol: " + symbol);
        });

        if (axiom != null)
            axiom.forEach(symbol -> {
                if (!alphabet.contains(symbol))
                    throw new IllegalArgumentException("Axiom can only refer to alphabet symbols! Unknown symbol: " + symbol);
            });

        this.alphabet = alphabet;
        this.rules = rules;
        this.axiom = axiom == null ? Collections.emptyList() : new ArrayList<>(axiom);
    }

    public static LSystem of(final Set<Symbol> alphabet,
                             final RuleSet rules,
                             final List<Symbol> axiom) {
        return new LSystem(alphabet, rules, axiom);
    }

    public static LSystem of(final Set<Symbol> alphabet,
                             final RuleSet rules) {
        return new LSystem(alphabet, rules, null);
    }

    public static LSystem of(final String alphabet,
                             final RuleSet rules) {
        return of(alphabet, rules, null);
    }

    public static LSystem of(final String alphabet,
                             final RuleSet rules,
                             final String axiom) {
        if (alphabet == null || alphabet.trim().isEmpty())
            throw new IllegalArgumentException("alphabet");

        final Set<Symbol> alphabetSymbols = new HashSet<>();
        for(char c : alphabet.toCharArray())
            alphabetSymbols.add(Symbol.of(c));
        if (alphabet.length() != alphabetSymbols.size())
            throw new IllegalArgumentException("'alphabet' can not contain duplicated elements!");

        final List<Symbol> axiomSymbols = new ArrayList<>();
        if (axiom != null)
            for(char c : axiom.toCharArray())
                axiomSymbols.add(Symbol.of(c));

        return new LSystem(alphabetSymbols, rules, axiomSymbols);
    }

    public boolean hasSymbol(Symbol symbol) {
        return alphabet.contains(symbol);
    }

    public List<Symbol> generate(final int iterations) {
        final List<List<Symbol>> generated = generateAll(iterations);
        return generated.get(generated.size() - 1);
    }

    public List<Symbol> generate(final int iterations, final List<Symbol> axiom) {
        final List<List<Symbol>> generated = generateAll(iterations, axiom);
        return generated.get(generated.size() - 1);
    }

    public List<List<Symbol>> generateAll(final int iterations) {
        if (axiom.isEmpty())
            throw new UnsupportedOperationException("No default axiom defined for this L-System!");

        return generateAll(iterations, axiom);
    }

    public List<List<Symbol>> generateAll(final int iterations, final List<Symbol> axiom) {
        if (axiom == null || axiom.isEmpty())
            throw new IllegalArgumentException("axiom");

        return doGenerate(iterations, axiom);
    }

    private List<List<Symbol>> doGenerate(final int iterations, final List<Symbol> axiom) {
        if (iterations <= 0)
            throw new IllegalArgumentException("iterations");

        final List<List<Symbol>> result = new ArrayList<>();
        result.add(new ArrayList<>(axiom));

        List<Symbol> input = axiom;
        for(int i=1; i<iterations; i++) {
            List<Symbol> produced = new ArrayList<>();
            for(int a=0; a<input.size(); a++) {
                final Symbol symbol = input.get(a);
                final Production rule = rules.ruleFor(symbol);
                if (rule == null) {
                    produced.add(symbol);
                } else {
                    final List<Symbol> produceByIteration = rule.produce();
                    produceByIteration.forEach(s -> {
                        if (!alphabet.contains(s))
                            throw new IllegalArgumentException("Productions can only refer alphabet symbols! " +
                                    "Unknown symbol: " + s);
                    });
                    produced.addAll(produceByIteration);
                }
            }
            result.add(produced);
            input = produced;
        }
        
        return result;
    }
}
