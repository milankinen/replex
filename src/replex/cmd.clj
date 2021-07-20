(ns replex.cmd
  (:require [clojure.string :as string]
            [clojure.walk :refer [postwalk]])
  (:import (java.util.regex Pattern)))

(defonce repl-ns (atom nil))
(defonce globals-to-inject (atom {}))

(defn- get-repl-ns []
  (or @repl-ns (throw (IllegalStateException. "Project REPL namespace not set"))))

(defmacro inject-globals [target-ns]
  `(do ~@(for [[name var-sym] @globals-to-inject]
           `(intern '~target-ns '~name ~var-sym))
       '~(set (keys @globals-to-inject))))

(defn- normalize-form [form]
  (postwalk
    #(cond
       ;; Normalize generated symbols (best effort...)
       (and (simple-symbol? %)
            (re-find #"__\d+#$" (name %)))
       (symbol (string/replace (name %) #"__\d+#$" ""))
       ;; Clojure reader reads regex patterns as java.util.regex.Pattern
       ;; instances that do not implement clojure equiv, hence using this
       ;; "private" tagged literal for comparison
       (instance? Pattern %)
       (tagged-literal 'replex/pattern (str %))
       :else %)
    form))

(defmacro capture-bindings [target-ns bindings]
  (assert (even? (count bindings)))
  (let [destructured-bindings (destructure bindings)
        bound-names (->> (partition-all 2 destructured-bindings)
                         (map first)
                         (remove #(re-find #"(__\d+|^_)$" (str %))))]
    `(do (in-ns '~target-ns)
         (let ~destructured-bindings
           ~@(for [name bound-names]
               `(intern '~target-ns '~name ~name)))
         (in-ns '~(get-repl-ns))
         ~(->> (for [name bound-names]
                 [`'~name (symbol (str target-ns) (str name))])
               (into {})))))

;; We need this "private" macro the get form's local
;; bindings from &env
(defmacro -capture-form [form target-ns]
  (let [captured-symbols (->> (map first &env)
                              (remove #(re-find #"(__\d+|^_)$" (str %)))
                              (set))]
    (println "Capturing symbols:" captured-symbols)
    `(do ~@(map (fn [s] `(intern '~target-ns '~s ~s)) captured-symbols)
         ~form)))

(defmacro capture-locals [target-ns form-to-capture top-form]
  `(do (in-ns '~target-ns)
       ~(postwalk
          (fn [form]
            (if (= (normalize-form form-to-capture)
                   (normalize-form form))
              `(-capture-form ~form ~target-ns)
              form))
          top-form)
       (in-ns '~(get-repl-ns))
       nil))

(defmacro reset-namespace [target-ns]
  `(let [interns# (ns-interns '~target-ns)]
     (doseq [[name#] interns#]
       (ns-unmap '~target-ns name#))
     (require '~target-ns :reload)))
