# Clojure REPL extensions

Some convenience macros helping to utilize 120% of your Clojure REPL.

[![Clojars](https://img.shields.io/clojars/v/com.github.milankinen/replex?style=flat-square)](https://clojars.org/com.github.milankinen/replex)

## Motivation

Clojure REPL has changed my way of writing software. Being able to inspect
and modify running applications and test modifications in-place has significantly
improved my productivity and given a lot of joy that other development tools 
haven't been able to provide.

That said, there is still some room to improve. Evaluating top level functions 
is easy but sometimes there is a need to dig deeper into application internals. 
Perhaps some specific HTTP request in your development server causes an 
exception but don't known how to replicate that behaviour with REPL? Would it 
be possible to "capture" the input somehow and then continue debugging the app 
with REPL? Would it be nice to evaluate single forms inside functions but can't 
because they bind some local symbols from `let`?

This project introduces some convenience macros and utilities that have brought
my REPL development experience to a whole new level. Using these utilities, I 
can capture almost any temporal state of my application and evaluate almost 
**any form** from my codebase **within seconds**. No more `println` debugging.

This project is meant as development time tooling only, do not use in production 
environments!

## Installation and editor setup

TODO

## License

MIT
