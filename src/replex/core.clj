(ns replex.core
  (:require [replex.cmd :as cmd]))

(defn set-repl-ns
  "Sets the default REPL namespace to use for bound symbols
   when evaluating extensions commands. Usually this is the
   initial namespace when starting up the REPL (`user` by
   default). This function **must** be called during the REPL
   startup, before using any extensions macros.

   Example:
   ```clj
   (ns user
     (:require [replex.core :as replex]))

   ;; do some other init stuff...

   (replex/set-repl-ns 'user)
   ```"
  [ns-name]
  {:pre [(simple-symbol? ns-name)]}
  (reset! cmd/repl-ns ns-name))

(defn set-injected-globals
  "Sets the globals that should be injected to the current namespace when
   evaluating `replex.cmd/inject-globals`. Globals are given as a map
   where keys are name of the 'injected' symbols and values are the fully
   qualified names of the actual values.

   This function **should** be called once, during the repl startup in
   the repl init namespace.

   Example:
   ```clj
   (ns user
     (:require [replex.core :as replex]))

   ;; do some other init stuff...
   (def repl-ctx ...)
   (defonce app (start-app repl-ctx))

   ;; set namespace first
   (replex/set-repl-ns 'user)
   ;; then define which globals to inject
   (replex/set-injected-globals
     '{ctx user/repl-ctx
       app user/app})
   ```"
  [inject-map]
  (reset! cmd/globals-to-inject inject-map))
