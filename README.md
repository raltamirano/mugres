# mugres-core
MUGRES is a tool to ease music composition and performance

### REPL
MUGRES comes with a basic REPL so you can play with it, test features and more.

To start the REPL: 

    mvn clean package
    java -cp ./target/mugres-core-XXXXXXXXX.jar mugres.core.ui.REPL
    
That should leave you with the `mugres>` prompt. You can execute commands following the syntax:

    command-name param1-value param2-value paramN-value
    
Some example commands you can issue:

    mugres> random-song        
    mugres> stop    
    mugres> calls-party DRUMS
    mugres> call halfTime(len=4)
    mugres> stop    
    mugres> calls-party STRINGS1
    mugres> call random(len=2,scale='Minor Pentatonic',root=G)
    
Here you have the commands:

###### call
###### calls-key
###### calls-party
###### calls-show-context
###### calls-show-functions
###### calls-show-parties
###### calls-tempo
###### calls-ts
###### help
###### load-song
###### loop-section
###### play-section
###### play-song
###### quit
###### random-song
###### sections
###### status
###### stop    
    
