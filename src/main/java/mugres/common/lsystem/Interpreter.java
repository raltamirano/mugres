package mugres.common.lsystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mugres.common.Context;

public class Interpreter {
    private final String name;
    private final LSystem lSystem;
    private final Object target;
    private final Map<Symbol, Action> actions;
    private final Map<String, Object> parameters;

    private Interpreter(final String name, final LSystem lSystem, final Object target,
                        final Map<Symbol, Action> actions, final Map<String, Object> parameters) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name");
        if (lSystem == null)
            throw new IllegalArgumentException("lSystem");

        this.name = name;
        this.lSystem = lSystem;
        this.target = target;
        this.actions = actions == null ? Collections.emptyMap() : new HashMap<>(actions);
        this.parameters = parameters == null ? Collections.emptyMap() : new HashMap<>(parameters);
    }

    public static Builder builder(final String name) {
        return new Builder(name);
    }

    public String name() {
        return name;
    }

    public LSystem lSystem() {
        return lSystem;
    }

    public Object target() {
        return target;
    }

    public Map<Symbol, Action> actions() {
        return Collections.unmodifiableMap(actions);
    }

    public Map<String, Object> parameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public void interpret(final Context context, final int iterations) {
        doInterpret(context, target, lSystem.generate(iterations));
    }

    public void interpret(final Context context, final int iterations, final List<Symbol> axiom) {
        doInterpret(context, target, lSystem.generate(iterations, axiom));
    }

    public void interpret(final Context context, final int iterations,
                          final Object target) {
        doInterpret(context, target, lSystem.generate(iterations));
    }

    public void interpret(final Context context, final int iterations,
                          final Object target, final List<Symbol> axiom) {
        doInterpret(context, target, lSystem.generate(iterations, axiom));
    }

    private void doInterpret(final Context context, final Object target, final List<Symbol> production) {
        final Context interpretationContext = Context.ComposableContext.of(context);
        production.forEach(symbol -> {
            final Action action = actions.get(symbol);
            if (action != null)
                action.apply(interpretationContext, target, parameters());
        });
    }

    public interface Action {
        void apply(final Context context, final Object target, final Map<String, Object> parameters);
    }

    public static class Builder {
        private final String name;
        private LSystem lSystem;
        private Object target;
        private Map<Symbol, Action> actions = new HashMap<>();
        private Map<String, Object> parameters = new HashMap<>();

        public Builder(final String name) {
            this.name = name;
        }

        public Builder lSystem(final LSystem lSystem) {
            this.lSystem = lSystem;
            return this;
        }

        public Builder target(final Object target) {
            this.target = target;
            return this;
        }

        public Builder action(final String symbol, final Action action) {
            return action(Symbol.of(symbol), action);
        }

        public Builder action(final Symbol symbol, final Action action) {
            actions.put(symbol, action);
            return this;
        }

        public Builder parameter(final String name, final Object value) {
            parameters.put(name, value);
            return this;
        }

        public Interpreter build() {
            return new Interpreter(name, lSystem, target, actions, parameters);
        }
    }
}
