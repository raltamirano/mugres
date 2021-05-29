package mugres.core.function;

import mugres.core.common.*;
import mugres.core.function.builtin.arp.Arp;
import mugres.core.function.builtin.bm.BlackMetal;
import mugres.core.function.builtin.chords.Chords;
import mugres.core.function.builtin.drums.BlastBeat;
import mugres.core.function.builtin.drums.Drums;
import mugres.core.function.builtin.drums.HalfTime;
import mugres.core.function.builtin.drums.HipHopBeat;
import mugres.core.function.builtin.random.Random;
import mugres.core.function.builtin.riffer.Riffer;
import mugres.core.function.builtin.song.LoFiHipHopSongGenerator;
import mugres.core.notation.Song;

import java.util.*;
import java.util.stream.Collectors;

import static mugres.core.common.Context.SECTION_LENGTH;

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Parameter> getParameters() {
        return Collections.unmodifiableSet(parameters);
    }

    public Parameter getParameter(final String name) {
        for(Parameter parameter : parameters)
            if (parameter.name.equals(name))
                return parameter;
        return null;
    }

    public abstract Artifact getArtifact();

    protected void addParameter(final Parameter parameter) {
        if (parameters.contains(parameter.name))
            throw new IllegalArgumentException(String.format("Parameter '%s' already exists!", parameter.name));

        parameters.add(parameter);
    }

    public T execute(final Context context, final Map<String, Object> arguments) {
        if (!arguments.containsKey(LENGTH_PARAMETER.getName()))
            if (context.has(SECTION_LENGTH))
                arguments.put(LENGTH_PARAMETER.getName(), context.get(SECTION_LENGTH));

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
        final long mandatoryParameters = parameterList.stream().filter(p -> !p.getName().equals(LENGTH_PARAMETER.getName())
                && !p.optional).count();
        String parameterName = null;
        if (parameterList.size() == 1) {
            parameterName = parameterList.get(0).name;
        } else if (mandatoryParameters == 1) {
            parameterName = parameterList.stream().filter(p -> !p.getName().equals(LENGTH_PARAMETER.getName())
                    && !p.optional).findFirst().get().name;
        }

        if (parameterName == null)
            throw new IllegalArgumentException("Could not determine the parameter to use");

        final Map<String, Object> arguments = new HashMap<>();
        arguments.put(parameterName, argument);
        return execute(context, arguments);
    }

    protected abstract T doExecute(final Context context, final Map<String, Object> arguments);

    protected Length lengthFromNumberOfMeasures(final Context context, final Map<String, Object> arguments) {
        final int measures = (Integer) arguments.get(LENGTH_PARAMETER.getName());
        final TimeSignature timeSignature = context.getTimeSignature();
        return timeSignature.measuresLength(measures);
    }

    protected Result<T> getComposedCallResult(final Map<String, Object> arguments) {
        return (Result<T>) arguments.get(COMPOSED_CALL_RESULT_PARAMETER.getName());
    }

    private Map<String, Object> prepareArguments(final Map<String, Object> arguments) {
        final Map<String, Object> preparedArguments = new HashMap<>();

        if (arguments.containsKey(COMPOSED_CALL_RESULT_PARAMETER.getName()))
            preparedArguments.put(COMPOSED_CALL_RESULT_PARAMETER.getName(), arguments.get(COMPOSED_CALL_RESULT_PARAMETER.getName()));

        if (parameters.isEmpty()) {
            if (arguments.isEmpty())
                return arguments;
            else
                throw new IllegalArgumentException(String.format("No arguments expected for function '%s'. " +
                        "Provided: '%s'", name, arguments));
        } else {
            for(Parameter parameter : parameters) {
                Object argument = arguments.get(parameter.getName());
                if (argument == null) {
                    if (parameter.optional)
                        argument = parameter.defaultValue;
                    else
                        throw new IllegalArgumentException(String.format("No value provided for parameter '%s' " +
                                        "while calling function '%s'", parameter.name, name));
                }

                preparedArguments.put(parameter.name, argument);
            }

            for(String argumentName : arguments.keySet())
                if (!allowedInternalParameter(argumentName) && !parameters.stream().anyMatch(p -> p.name.equals(argumentName)))
                    throw new IllegalArgumentException(String.format("Unexpected argument '%s' " +
                            "while calling function '%s'. Value: '%s'",
                            argumentName, name, arguments.get(argumentName)));
        }

        // Validate common parameters
        final int length = (Integer)preparedArguments.get(LENGTH_PARAMETER.name);
        if (length <= 0)
            throw new IllegalArgumentException(String.format("'%s' parameter must be always > 0",
                    LENGTH_PARAMETER.name));

        return preparedArguments;
    }

    private boolean allowedInternalParameter(String argumentName) {
        if (COMPOSED_CALL_RESULT_PARAMETER.getName().equals(argumentName)) return true;
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
        public Artifact getArtifact() {
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
        public Artifact getArtifact() {
            return Artifact.SONG;
        }
    }

    /** Mandatory length parameter some functions must have */
    public static final Parameter LENGTH_PARAMETER = new Parameter("len", "Length in measures",
            Parameter.DataType.INTEGER);
    public static final Parameter COMPOSED_CALL_RESULT_PARAMETER = new Parameter("composedCallResult", "Result of composed Call",
            Parameter.DataType.UNKNOWN);

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
        new BlackMetal();
        new LoFiHipHopSongGenerator();
    }

    private static synchronized void register(final Function function) {
        final String name = function.getName();
        if (REGISTRY.containsKey(name))
            throw new IllegalArgumentException("Already registered function: " + name);
        REGISTRY.put(name, function);
    }

    public static <R, F extends Function<R>> F forName(final String name) {
        return (F) REGISTRY.get(name);
    }

    public static Set<Function> forArtifact(final Artifact artifact) {
        return Collections.unmodifiableSet(REGISTRY.values().stream()
                .filter(f -> f.getArtifact().equals(artifact))
                .collect(Collectors.toSet()));
    }

    public static Set<Function> allFunctions() {
        return Collections.unmodifiableSet(new HashSet<>(REGISTRY.values()));
    }

    /** Musical artifacts a Function can produce. */
    public enum Artifact {
        EVENTS("Events"),
        SONG("Song");

        private final String name;

        Artifact(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Parameter {
        private final String name;
        private final String documentation;
        private final DataType dataType;
        private final boolean optional;
        private final Object defaultValue;

        private Parameter(String name, String documentation, DataType dataType) {
            this(name, documentation, dataType, false, null);
        }

        private Parameter(String name, String documentation, DataType dataType, boolean optional, Object defaultValue) {
            this.name = name;
            this.documentation = documentation;
            this.dataType = dataType;
            this.optional = optional;
            this.defaultValue = defaultValue;
        }

        public static Parameter of(String name, String documentation, DataType dataType) {
            return new Parameter(name, documentation, dataType);
        }

        public static Parameter of(String name, String documentation, DataType dataType, boolean optional, Object defaultValue) {
            return new Parameter(name, documentation, dataType, optional, defaultValue);
        }

        public String getName() {
            return name;
        }

        public String getDocumentation() {
            return documentation;
        }

        public DataType getDataType() {
            return dataType;
        }

        public boolean isOptional() {
            return optional;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Parameter parameter = (Parameter) o;
            return Objects.equals(name, parameter.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        public enum DataType {
            /** {@link Length} */
            LENGTH,
            /** {@link Value} */
            VALUE,
            /** {@link Note} */
            NOTE,
            /** {@link Pitch} */
            PITCH,
            /** {@link Scale} */
            SCALE,
            /** Plain text */
            TEXT,
            /** Integer numbers */
            INTEGER,
            /** A DrumKit piece*/
            DRUM_KIT,
            /** Variants of something */
            VARIANT,
            /** True/False values */
            BOOLEAN,
            /** Unknown */
            UNKNOWN
        }

        public enum Variant {
            /** Not specified */
            NONE,
            /** Pick one of the available variants at random */
            RANDOM,
            /** Builtin variant 0 (default)*/
            V0,
            /** Builtin variant 1 */
            V1,
            /** Builtin variant 2 */
            V2,
            /** Builtin variant 3 */
            V3,
            /** Builtin variant 4 */
            V4,
            /** Builtin variant 5 */
            V5,
            /** Builtin variant 6 */
            V6,
            /** Builtin variant 7 */
            V7,
            /** Builtin variant 8 */
            V8,
            /** Builtin variant 9 */
            V9,
            /** User variant 00 */
            U00,
            /** User variant 01 */
            U01,
            /** User variant 02 */
            U02,
            /** User variant 03 */
            U03,
            /** User variant 04 */
            U04,
            /** User variant 05 */
            U05,
            /** User variant 06 */
            U06,
            /** User variant 07 */
            U07,
            /** User variant 08 */
            U08,
            /** User variant 09 */
            U09,
            /** User variant 10 */
            U10,
            /** User variant 11 */
            U11,
            /** User variant 12 */
            U12,
            /** User variant 13 */
            U13,
            /** User variant 14 */
            U14,
            /** User variant 15 */
            U15,
            /** User variant 16 */
            U16,
            /** User variant 17 */
            U17,
            /** User variant 18 */
            U18,
            /** User variant 19 */
            U19,
            /** User variant 20 */
            U20,
            /** User variant 21 */
            U21,
            /** User variant 22 */
            U22,
            /** User variant 23 */
            U23,
            /** User variant 24 */
            U24,
            /** User variant 25 */
            U25,
            /** User variant 26 */
            U26,
            /** User variant 27 */
            U27,
            /** User variant 28 */
            U28,
            /** User variant 29 */
            U29,
            /** User variant 30 */
            U30,
            /** User variant 31 */
            U31,
            /** User variant 32 */
            U32,
            /** User variant 33 */
            U33,
            /** User variant 34 */
            U34,
            /** User variant 35 */
            U35,
            /** User variant 36 */
            U36,
            /** User variant 37 */
            U37,
            /** User variant 38 */
            U38,
            /** User variant 39 */
            U39,
            /** User variant 40 */
            U40,
            /** User variant 41 */
            U41,
            /** User variant 42 */
            U42,
            /** User variant 43 */
            U43,
            /** User variant 44 */
            U44,
            /** User variant 45 */
            U45,
            /** User variant 46 */
            U46,
            /** User variant 47 */
            U47,
            /** User variant 48 */
            U48,
            /** User variant 49 */
            U49,
            /** User variant 50 */
            U50,
            /** User variant 51 */
            U51,
            /** User variant 52 */
            U52,
            /** User variant 53 */
            U53,
            /** User variant 54 */
            U54,
            /** User variant 55 */
            U55,
            /** User variant 56 */
            U56,
            /** User variant 57 */
            U57,
            /** User variant 58 */
            U58,
            /** User variant 59 */
            U59,
            /** User variant 60 */
            U60,
            /** User variant 61 */
            U61,
            /** User variant 62 */
            U62,
            /** User variant 63 */
            U63,
            /** User variant 64 */
            U64,
            /** User variant 65 */
            U65,
            /** User variant 66 */
            U66,
            /** User variant 67 */
            U67,
            /** User variant 68 */
            U68,
            /** User variant 69 */
            U69,
            /** User variant 70 */
            U70,
            /** User variant 71 */
            U71,
            /** User variant 72 */
            U72,
            /** User variant 73 */
            U73,
            /** User variant 74 */
            U74,
            /** User variant 75 */
            U75,
            /** User variant 76 */
            U76,
            /** User variant 77 */
            U77,
            /** User variant 78 */
            U78,
            /** User variant 79 */
            U79,
            /** User variant 80 */
            U80,
            /** User variant 81 */
            U81,
            /** User variant 82 */
            U82,
            /** User variant 83 */
            U83,
            /** User variant 84 */
            U84,
            /** User variant 85 */
            U85,
            /** User variant 86 */
            U86,
            /** User variant 87 */
            U87,
            /** User variant 88 */
            U88,
            /** User variant 89 */
            U89,
            /** User variant 90 */
            U90,
            /** User variant 91 */
            U91,
            /** User variant 92 */
            U92,
            /** User variant 93 */
            U93,
            /** User variant 94 */
            U94,
            /** User variant 95 */
            U95,
            /** User variant 96 */
            U96,
            /** User variant 97 */
            U97,
            /** User variant 98 */
            U98,
            /** User variant 99 */
            U99
        }
    }
}
