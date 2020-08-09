package mugres.core.function.common;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.function.Function.EventsFunction;

import java.util.*;

import static mugres.core.utils.Randoms.random;

public abstract class ByStrategiesFunction extends EventsFunction {
    protected ByStrategiesFunction(final String name, final String description) {
        super(name, description,
            Parameter.of("totalMeasures", "Total measures to generate", Parameter.DataType.INTEGER,
                    true, null));
    }
    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final int totalMeasures = (int) ((arguments.get("totalMeasures") == null) ?
                arguments.get(LENGTH_PARAMETER.getName()) : arguments.get("totalMeasures"));

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
                event.offset(offset);
            events.addAll(generated);
            offset = offset.plus(context.getTimeSignature().measuresLength(strategy.getMeasures()));
        }

        return events;
    }

    protected void addStrategy(final Strategy strategy) {
        synchronized (STRATEGIES) {
            if (!STRATEGIES.containsKey(strategy.getMeasures()))
                STRATEGIES.put(strategy.getMeasures(), new ArrayList<>());
            STRATEGIES.get(strategy.getMeasures()).add(strategy);
        }
    }

    private final Map<Integer, List<Strategy>> STRATEGIES = new HashMap<>();

    public interface Strategy {
        int getMeasures();
        List<Event> execute(final Context context);
    }
}
