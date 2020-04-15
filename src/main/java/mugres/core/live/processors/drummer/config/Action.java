package mugres.core.live.processors.drummer.config;

import mugres.core.common.Context;
import mugres.core.live.processors.drummer.commands.Command;
import mugres.core.live.processors.drummer.Drummer;

import java.util.*;

/** Commands to be executed when a pedal is pressed. */
public class Action {
    private final List<Step> steps = new ArrayList<>();

    public Action addStep(final Command command) {
        return addStep(command, Collections.EMPTY_MAP);
    }

    public Action addStep(final Command command, final Map<String, Object> parameters) {
        steps.add(new Step(command, parameters));
        return this;
    }

    public void execute(final Context context, final Drummer drummer) {
        for(int index = 0; index < steps.size(); index++) {
            final Step step = steps.get(index);
            step.getCommand().execute(context, drummer, step.getParameters());
        }
    }

    public Action then(final Action next) {
        final Action newAction = new Action();

        for(int index = 0; index < steps.size(); index++)
            newAction.addStep(steps.get(index).command, steps.get(index).parameters);

        for(int index = 0; index < next.steps.size(); index++)
            newAction.addStep(next.steps.get(index).command, next.steps.get(index).parameters);

        return newAction;
    }

    class Step {
        private Command command;
        private Map<String, Object> parameters;

        public Step(final Command command, final Map<String, Object> parameters) {
            this.command = command;
            this.parameters = parameters;
        }

        public Command getCommand() {
            return this.command;
        }

        public Map<String, Object> getParameters() {
            return this.parameters;
        }

        public void setCommand(final Command command) {
            this.command = command;
        }

        public void setParameters(final Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
 }