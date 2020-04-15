package mugres.core.live.processors.drummer.commands;

import mugres.core.common.Context;
import mugres.core.live.processors.drummer.Drummer;
import mugres.core.live.processors.drummer.config.Action;

import java.util.HashMap;
import java.util.Map;

public interface Command {
    /** Returns this command's name. */
    String getName();

    /** Executes this command. */
    void execute(final Context context,
                 final Drummer drummer,
                 final Map<String, Object> parameters);

    default Action action() {
        return new Action().addStep(this);
    }

    default Action action(final Map<String, Object> parameters) {
        return new Action().addStep(this, parameters);
    }

    default Action action(final String parameter1, final Object value1) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        return action(parameters);
    }

    default Action action(final String parameter1, final Object value1,
                          final String parameter2, final Object value2) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        return action(parameters);
    }

    default Action action(final String parameter1, final Object value1,
                          final String parameter2, final Object value2,
                          final String parameter3, final Object value3) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        parameters.put(parameter3, value3);
        return action(parameters);
    }

    default Action action(final String parameter1, final Object value1,
                          final String parameter2, final Object value2,
                          final String parameter3, final Object value3,
                          final String parameter4, final Object value4) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        parameters.put(parameter3, value3);
        parameters.put(parameter4, value4);
        return action(parameters);
    }
}
