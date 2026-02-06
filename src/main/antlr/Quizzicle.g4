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


MODE : 'mode:' SPACES ('teacher' | 'student') ';';
TYPE : ('flash_cards' | 'matching');
ANSWER_KEY : 'include_answer_key:' SPACES ('true' | 'false') ';';
CHARACTERS : [A-Za-z0-9,]+;
WS : [\n\t\r ]+ -> skip;
SPACES : [ ]+;
