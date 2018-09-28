(defproject clojure-gorilla-tsp "0.1.0-SNAPSHOT"
  :description "Gorilla notebook for solving Travelling Salesman Problem (made for TAI2151 Artificial Intelligence)"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0"]]
                [org.clojure/math.combinatorics "0.1.4"]
                [org.clojure/math.numeric-tower "0.0.4"]
  :plugins [[lein-gorilla "0.4.0"]]
  :main ^:skip-aot clojure-gorilla-tsp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
