package mugres.function.common;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.tracker.Event;
import mugres.common.Length;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;

import java.util.*;

import static mugres.utils.Randoms.random;

public abstract class ByStrategiesFunction extends EventsFunction {
    protected ByStrategiesFunction(final String name, final String description) {
        super(name, description,
            Parameter.of("totalMeasures", "Total measures", 1,
                    "Total measures to generate", DataType.INTEGER, true, null));
    }
    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final int totalMeasures = (int) ((arguments.get("totalMeasures") == null) ?
                arguments.get(LENGTH_PARAMETER.name()) : arguments.get("totalMeasures"));

        int repetitions = 1;
        Strategy strategy = null;
        if (STRATEGIES.containsKey(totalMeasures)) {
            strategy = random(STRATEGIES.get(totalMeasures));
        } else {
            if (totalMeasures > 1 && totalMeasures % 2 != 0)
                throw new RuntimeException("Generating riffs by repetition only supported por even " +
                        "number of measures to generate");
            int tryThis = totalMeasures / 2;
            do {
                if (STRATEGIES.containsKey(tryThis))
                    strategy = random(STRATEGIES.get(tryThis));
                tryThis /= 2;
                repetitions *= 2;
                if (strategy != null)
                    break;
            } while(tryThis != 1);
        }

        Length offset = Length.ZERO;
        for (int index = 0; index < repetitions; index++) {
            final List<Event> generated = strategy.execute(context);
            for(final Event event : generated)
                events.add(event.offset(offset));
            offset = offset.plus(context.timeSignature().measuresLength(strategy.measures()));
        }

        return events;
    }

    protected void addStrategy(final Strategy strategy) {
        synchronized (STRATEGIES) {
            if (!STRATEGIES.containsKey(strategy.measures()))
                STRATEGIES.put(strategy.measures(), new ArrayList<>());
            STRATEGIES.get(strategy.measures()).add(strategy);
        }
    }

    private final Map<Integer, List<Strategy>> STRATEGIES = new HashMap<>();

    public interface Strategy {
        int measures();
        List<Event> execute(final Context context);
    }
}
