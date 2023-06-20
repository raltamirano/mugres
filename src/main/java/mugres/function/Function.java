package mugres.function;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.function.builtin.literal.Literal;
import mugres.tracker.Event;
import mugres.common.Length;
import mugres.common.TimeSignature;
import mugres.function.builtin.arp.Arp;
import mugres.function.builtin.arp.Arp2;
import mugres.function.builtin.bm.BlackMetal;
import mugres.function.builtin.chords.Chords;
import mugres.function.builtin.drums.BlastBeat;
import mugres.function.builtin.drums.Drums;
import mugres.function.builtin.drums.HalfTime;
import mugres.function.builtin.drums.HipHopBeat;
import mugres.function.builtin.euclides.Euclides;
import mugres.function.builtin.random.Random;
import mugres.function.builtin.riffer.Riffer;
import mugres.function.builtin.song.LoFiHipHopSongGenerator;
import mugres.function.builtin.text.TextMelody;
import mugres.tracker.Song;
import mugres.parametrizable.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static mugres.common.Context.PATTERN_LENGTH;

/** Function that generates musical artifacts. */
public abstract class Function<T> {
    private final String name;
    private final String description;
    private final Set<Parameter> parameters = new HashSet<>();

    public Function(final String name,
                    final String description,
                    final Parameter... parameters) {
        this.name = name;
        this.description = description;
        for(Parameter parameter : parameters)
            addParameter(parameter);

        register(this);
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Set<Parameter> parameters() {
        return Collections.unmodifiableSet(parameters);
    }

    public Parameter parameter(final String name) {
        for(Parameter parameter : parameters)
            if (parameter.name().equals(name))
                return parameter;
        return null;
    }

    protected void addParameter(final Parameter parameter) {
        if (parameters.contains(parameter.name()))
            throw new IllegalArgumentException(String.format("Parameter '%s' already exists!", parameter.name()));

        parameters.add(parameter);
    }

    public abstract Artifact artifact();

    public T execute(final Context context, final Map<String, Object> arguments) {
        if (!arguments.containsKey(LENGTH_PARAMETER.name()))
            if (context.has(PATTERN_LENGTH))
                arguments.put(LENGTH_PARAMETER.name(), context.get(PATTERN_LENGTH));

        final T result = doExecute(context, prepareArguments(arguments));
        // TODO: Validate length/complete to length with rests / etc.
        return result;
    }

    public T executeNoArgs(final Context context) {
        return execute(context, Collections.emptyMap());
    }

    /** This methods is useful for functions that either have only a single parameter or
     * a single mandatory parameter (besides {@link #LENGTH_PARAMETER}) among all of its parameters. */
    public T executeSingleArg(final Context context, final Object argument) {
        final List<Parameter> parameterList = new ArrayList<>(this.parameters);
        final long mandatoryParameters = parameterList.stream().filter(p -> !p.name().equals(LENGTH_PARAMETER.name())
                && !p.isOptional()).count();
        String parameterName = null;
        if (parameterList.size() == 1) {
            parameterName = parameterList.get(0).name();
        } else if (mandatoryParameters == 1) {
            parameterName = parameterList.stream().filter(p -> !p.name().equals(LENGTH_PARAMETER.name())
                    && !p.isOptional()).findFirst().get().name();
        }

        if (parameterName == null)
            throw new IllegalArgumentException("Could not determine the parameter to use");

        final Map<String, Object> arguments = new HashMap<>();
        arguments.put(parameterName, argument);
        return execute(context, arguments);
    }

    @Override
    public String toString() {
        return name;
    }

    protected abstract T doExecute(final Context context, final Map<String, Object> arguments);

    protected Length lengthFromNumberOfMeasures(final Context context, final Map<String, Object> arguments) {
        final int measures = (Integer) arguments.get(LENGTH_PARAMETER.name());
        final TimeSignature timeSignature = context.timeSignature();
        return timeSignature.measuresLength(measures);
    }

    protected Result<T> getComposedCallResult(final Map<String, Object> arguments) {
        return (Result<T>) arguments.get(COMPOSED_CALL_RESULT_PARAMETER.name());
    }

    private Map<String, Object> prepareArguments(final Map<String, Object> arguments) {
        final Map<String, Object> preparedArguments = new HashMap<>();

        if (arguments.containsKey(COMPOSED_CALL_RESULT_PARAMETER.name()))
            preparedArguments.put(COMPOSED_CALL_RESULT_PARAMETER.name(), arguments.get(COMPOSED_CALL_RESULT_PARAMETER.name()));

        if (parameters.isEmpty()) {
            if (arguments.isEmpty())
                return arguments;
            else
                throw new IllegalArgumentException(String.format("No arguments expected for function '%s'. " +
                        "Provided: '%s'", name, arguments));
        } else {
            for(Parameter parameter : parameters) {
                Object argument = arguments.get(parameter.name());
                if (argument == null) {
                    if (parameter.isOptional())
                        argument = parameter.defaultValue();
                    else
                        throw new IllegalArgumentException(String.format("No value provided for parameter '%s' " +
                                        "while calling function '%s'", parameter.name(), name));
                }

                preparedArguments.put(parameter.name(), argument);
            }

            for(String argumentName : arguments.keySet())
                if (!allowedInternalParameter(argumentName) && !parameters.stream().anyMatch(p -> p.name().equals(argumentName)))
                    throw new IllegalArgumentException(String.format("Unexpected argument '%s' " +
                            "while calling function '%s'. Value: '%s'",
                            argumentName, name, arguments.get(argumentName)));
        }

        // Validate common parameters
        final int length = (Integer)preparedArguments.get(LENGTH_PARAMETER.name());
        if (length <= 0)
            throw new IllegalArgumentException(String.format("'%s' parameter must be always > 0",
                    LENGTH_PARAMETER.name()));

        return preparedArguments;
    }

    private boolean allowedInternalParameter(String argumentName) {
        if (COMPOSED_CALL_RESULT_PARAMETER.name().equals(argumentName)) return true;
        return false;
    }

    public static abstract class EventsFunction extends Function<List<Event>> {
        public EventsFunction(final String name,
                        final String description,
                        final Parameter... parameters) {
            super(name, description, parameters);

            // Every function must specify these mandatory parameters
            addParameter(LENGTH_PARAMETER);
        }

        @Override
        public Artifact artifact() {
            return Artifact.EVENTS;
        }
    }

    public static abstract class SongFunction extends Function<Song> {
        public SongFunction(final String name,
                        final String description,
                        final Parameter... parameters) {
            super(name, description, parameters);
        }

        @Override
        public Artifact artifact() {
            return Artifact.SONG;
        }
    }

    /** Mandatory length parameter some functions must have */
    public static final Parameter LENGTH_PARAMETER = Parameter.of("len", "Length", -1,
            "Length in measures", DataType.INTEGER, false);
    public static final Parameter COMPOSED_CALL_RESULT_PARAMETER = Parameter.of("composedCallResult", "CCR",
            Integer.MAX_VALUE, "Result of composed Call", DataType.UNKNOWN, false);

    private static final Map<String, Function> REGISTRY = new HashMap<>();

    static {
        new Random();
        new Drums();
        new HalfTime();
        new BlastBeat();
        new HipHopBeat();
        new Riffer();
        new Chords();
        new Arp();
        new Arp2();
        new BlackMetal();
        new LoFiHipHopSongGenerator();
        new TextMelody();
        new Euclides();
        new Literal();
    }

    private static synchronized void register(final Function function) {
        final String name = function.name();
        if (REGISTRY.containsKey(name))
            throw new IllegalArgumentException("Already registered function: " + name);
        REGISTRY.put(name, function);
    }

    public static <R, F extends Function<R>> F forName(final String name) {
        return (F) REGISTRY.get(name);
    }

    public static Set<Function> forArtifact(final Artifact artifact) {
        return Collections.unmodifiableSet(REGISTRY.values().stream()
                .filter(f -> f.artifact().equals(artifact))
                .collect(Collectors.toSet()));
    }

    public static Set<Function> allFunctions() {
        return Collections.unmodifiableSet(new HashSet<>(REGISTRY.values()));
    }

    public static Set<EventsFunction> allEventsFunctions() {
        return Collections.unmodifiableSet(new HashSet<>(REGISTRY.values()
                .stream()
                .filter(f -> f instanceof EventsFunction)
                .map(EventsFunction.class::cast)
                .collect(Collectors.toList()))
        );
    }

    /** Musical artifacts a Function can produce. */
    public enum Artifact {
        EVENTS("Events"),
        SONG("Song");

        private final String label;

        Artifact(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

}
