# mugres
MUGRES is a tool to ease music composition and performance

### Programmatic usage

Try making some noise by using the following code:

    Song.of(Call.of("random", 4)).play();

### REPL
MUGRES comes with a basic REPL so you can play with it, test features and more.

To start the REPL:

    mvn clean compile assembly:single
    java -cp ./target/mugres-*.jar mugres.utils.repl.REPL

That should leave you with the `mugres>` prompt. You can execute commands following the syntax:

    command-name param1-value param2-value paramN-value

Some example commands you can issue:

    mugres> random-song        
    mugres> stop    
    mugres> calls-track DRUMS
    mugres> call halfTime(len=4)
    mugres> stop    
    mugres> calls-track STRINGS1
    mugres> call random(len=2,scale='Minor Pentatonic',root=G)

Here you have the commands:

###### call
###### calls-key
###### calls-track
###### calls-show-context
###### calls-show-functions
###### calls-show-tracks
###### calls-tempo
###### calls-ts
###### help
###### load-song
###### loop-pattern
###### play-pattern
###### play-song
###### quit
###### random-song
###### patterns
###### status
###### stop    
