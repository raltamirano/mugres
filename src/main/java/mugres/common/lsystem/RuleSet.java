package mugres.common.lsystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuleSet {
    private final Map<Symbol, Production> rules;

    private RuleSet(final Map<Symbol, Production> rules) {
        this.rules = rules == null ? Collections.emptyMap() : new HashMap<>(rules);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Production ruleFor(final Symbol symbol) {
        return rules.get(symbol);
    }

    public Map<Symbol, Production> rules() {
        return Collections.unmodifiableMap(rules);
    }

    public static class Builder {
        private final Map<Symbol, Production> rules = new HashMap<>();

        public Builder add(final String symbol, final String production) {
            return add(Symbol.of(symbol), Production.of(production));
        }

        public Builder add(final String symbol, final Production production) {
            return add(Symbol.of(symbol), production);
        }

        public Builder add(final Symbol symbol, final Production production) {
            if (symbol == null)
                throw new IllegalArgumentException("symbol");
            if (production == null)
                throw new IllegalArgumentException("production");

            rules.put(symbol, production);
            return this;
        }

        public RuleSet build() {
            return new RuleSet(rules);
        }
    }
}
