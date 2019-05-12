# Quizzicle
The problem we are trying solve with our Domain Specific Language (DSL) is a quick method to generate study tools for a typical class exam. Most exams involve memorization of terms and their definitions. Thus, our DSL will take a configurable text file with `term:definition` pairs and convert it into multiple study tools including flash cards and practice quizzes. One of our practice quizzes will be a matching quiz where the terms and definitions will be randomized.

## Usage
Create a text file containing the necessary options according to the examples and grammar.

Make sure the FileInputStream object uses that text file.

Output will be an HTML page with the necessary study tools.

## Grammar
```ANTLR
grammar Quizzicle;

file        : config? terms quizzes EOF;
config      : 'config' '{' settings '}';
settings    : MODE
                | ANSWER_KEY
                | MODE ANSWER_KEY
                | ANSWER_KEY MODE;

terms       : 'terms' '{' items '}';
items       : item+;
item        : key_phrase ':' definition ';';

quizzes     : quiz+;
quiz        : TYPE '{' key_phrases ';' '}';
key_phrases : key_phrase
                | key_phrase ',' key_phrases;

key_phrase : CHARACTERS+;
definition : CHARACTERS+;


MODE       : 'mode:' SPACES ('teacher' | 'student') ';';
TYPE       : ('flash_cards' | 'matching');
ANSWER_KEY : 'include_answer_key:' SPACES ('true' | 'false') ';';
CHARACTERS : [A-Za-z0-9]+;
WS         : [\n\t\r ]+ -> skip;
SPACES     : [ ]*;
```

## Examples
Input:
```
config {
    mode: student;
    include_answer_key: true;
}

terms {
    apple: a red fruit;
    orange: an orange fruit;
    banana: a yellow fruit;
    kiwi: a green, exotic fruit;
}

flash_cards {
    apple, orange, banana;
}

matching {
    orange, banana, kiwi;
}
```

The output will be determined based upon the _config_ section of the config file as well as seeing which sections (e.g. flashcard, matching, etc.) exist. The _config_ section will allow the user to set flags based upon their desired output.
