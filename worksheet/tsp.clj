;; gorilla-repl.fileformat = 1

;; **
;;; # Travelling Salesman Problem
;; **

;; @@
(ns tsp-notes
  (:require [gorilla-plot.core :as plot]
            [clojure.math.combinatorics :as combo]
            [clojure.math.numeric-tower :as math]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; ## Introduction
;;; 
;;; > Given a list of cities and the distances between each pair of cities, what is the shortest possible route that visits each city and returns to the origin city?
;;; 
;;; Before tackling our problem, we will first define the data and structures required for representing the problem itself, the algorithms and their associated solutions.
;; **

;; **
;;; ## Preparation
;;; 
;;; ### Representing cities
;;; 
;;; Cities are representing using `x` and `y` coordinates.
;;; 
;;; > `defrecord`: https://clojuredocs.org/clojure.core/defrecord
;; **

;; @@
(defrecord City [x y])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-class'>tsp_notes.City</span>","value":"tsp_notes.City"}
;; <=

;; **
;;; Examples of using `City`:
;; **

;; @@
(City. 10 20)
(City. 1 2)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:x 1]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[:y 2]"}],"value":"#tsp_notes.City{:x 1, :y 2}"}
;; <=

;; **
;;; For accessing the individual fields of a `City`:
;; **

;; @@
(let [a (City. 10 20)]
  [(:x a) (:y a)])
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>10</span>","value":"10"},{"type":"html","content":"<span class='clj-long'>20</span>","value":"20"}],"value":"[10 20]"}
;; <=

;; **
;;; ### Calculating distance between cities
;;; 
;;; The straight-line distance or "_Euclidean distance_" between 2 cities can be calculated using their `x` and `y` coordinates.
;; **

;; @@
(defn distance [a b]
  (let [dist-x (- (:x a) (:x b))
        dist-y (- (:y a) (:y b))]
    (math/sqrt (+ (math/expt dist-x 2)
                  (math/expt dist-y 2)))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;tsp-notes/distance</span>","value":"#'tsp-notes/distance"}
;; <=

;; **
;;; Example of using `distance`:
;; **

;; @@
(distance (City. 0 0) (City. 10 10))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>14.142135623730951</span>","value":"14.142135623730951"}
;; <=

;; **
;;; ### Generating random cities
;;; 
;;; Now we need to generate some cities that are contained within `max-x` and `max-y`, which by default to `900` and `600` respectively.
;;; 
;;; > `defn`: https://clojuredocs.org/clojure.core/defn
;; **

;; @@
(defn random-cities 
  [count & {:keys [max-x max-y] :or {max-x 900 max-y 600}}]
  (for [i (range count)]
    (City. (rand-int max-x) (rand-int max-y))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;tsp-notes/random-cities</span>","value":"#'tsp-notes/random-cities"}
;; <=

;; **
;;; Example of using `random-cities`:
;; **

;; @@
(random-cities 10)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>205</span>","value":"205"}],"value":"[:x 205]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>358</span>","value":"358"}],"value":"[:y 358]"}],"value":"#tsp_notes.City{:x 205, :y 358}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>481</span>","value":"481"}],"value":"[:x 481]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>564</span>","value":"564"}],"value":"[:y 564]"}],"value":"#tsp_notes.City{:x 481, :y 564}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>851</span>","value":"851"}],"value":"[:x 851]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>365</span>","value":"365"}],"value":"[:y 365]"}],"value":"#tsp_notes.City{:x 851, :y 365}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>281</span>","value":"281"}],"value":"[:x 281]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>188</span>","value":"188"}],"value":"[:y 188]"}],"value":"#tsp_notes.City{:x 281, :y 188}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>424</span>","value":"424"}],"value":"[:x 424]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>248</span>","value":"248"}],"value":"[:y 248]"}],"value":"#tsp_notes.City{:x 424, :y 248}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>760</span>","value":"760"}],"value":"[:x 760]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>396</span>","value":"396"}],"value":"[:y 396]"}],"value":"#tsp_notes.City{:x 760, :y 396}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>312</span>","value":"312"}],"value":"[:x 312]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>307</span>","value":"307"}],"value":"[:y 307]"}],"value":"#tsp_notes.City{:x 312, :y 307}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>556</span>","value":"556"}],"value":"[:x 556]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>472</span>","value":"472"}],"value":"[:y 472]"}],"value":"#tsp_notes.City{:x 556, :y 472}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>324</span>","value":"324"}],"value":"[:x 324]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>573</span>","value":"573"}],"value":"[:y 573]"}],"value":"#tsp_notes.City{:x 324, :y 573}"},{"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:x</span>","value":":x"},{"type":"html","content":"<span class='clj-unkown'>390</span>","value":"390"}],"value":"[:x 390]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:y</span>","value":":y"},{"type":"html","content":"<span class='clj-unkown'>525</span>","value":"525"}],"value":"[:y 525]"}],"value":"#tsp_notes.City{:x 390, :y 525}"}],"value":"(#tsp_notes.City{:x 205, :y 358} #tsp_notes.City{:x 481, :y 564} #tsp_notes.City{:x 851, :y 365} #tsp_notes.City{:x 281, :y 188} #tsp_notes.City{:x 424, :y 248} #tsp_notes.City{:x 760, :y 396} #tsp_notes.City{:x 312, :y 307} #tsp_notes.City{:x 556, :y 472} #tsp_notes.City{:x 324, :y 573} #tsp_notes.City{:x 390, :y 525})"}
;; <=

;; **
;;; ### Plotting cities
;;; 
;;; `Gorilla` (the engine running behind this notebook) has a simple plotting API (http://gorilla-repl.org/plotting.html) which can be used to illustrate the positions of every cities. But before plotting, we need to map each city to a vector of `x` and `y` coordinates, which will then be fed into the `list-plot` provided by `Gorilla`.
;; **

;; @@
(defn city-to-point [city]
  [(:x city) (:y city)])

(defn plot-cities [cities]
  (let [points (map city-to-point cities)]
    (plot/list-plot points)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;tsp-notes/plot-cities</span>","value":"#'tsp-notes/plot-cities"}
;; <=

;; **
;;; Example:
;; **

;; @@
(plot-cities (random-cities 10))
;; @@
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"data":[{"name":"e6e4aa5e-796d-4fbc-85ab-43adcf41f9d2","values":[{"x":758,"y":388},{"x":525,"y":560},{"x":386,"y":12},{"x":279,"y":48},{"x":301,"y":297},{"x":190,"y":131},{"x":604,"y":200},{"x":833,"y":76},{"x":469,"y":38},{"x":553,"y":138}]}],"marks":[{"type":"symbol","from":{"data":"e6e4aa5e-796d-4fbc-85ab-43adcf41f9d2"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"fill":{"value":"steelblue"},"fillOpacity":{"value":1}},"update":{"shape":"circle","size":{"value":70},"stroke":{"value":"transparent"}},"hover":{"size":{"value":210},"stroke":{"value":"white"}}}}],"scales":[{"name":"x","type":"linear","range":"width","zero":false,"domain":{"data":"e6e4aa5e-796d-4fbc-85ab-43adcf41f9d2","field":"data.x"}},{"name":"y","type":"linear","range":"height","nice":true,"zero":false,"domain":{"data":"e6e4aa5e-796d-4fbc-85ab-43adcf41f9d2","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :data [{:name \"e6e4aa5e-796d-4fbc-85ab-43adcf41f9d2\", :values ({:x 758, :y 388} {:x 525, :y 560} {:x 386, :y 12} {:x 279, :y 48} {:x 301, :y 297} {:x 190, :y 131} {:x 604, :y 200} {:x 833, :y 76} {:x 469, :y 38} {:x 553, :y 138})}], :marks [{:type \"symbol\", :from {:data \"e6e4aa5e-796d-4fbc-85ab-43adcf41f9d2\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :fill {:value \"steelblue\"}, :fillOpacity {:value 1}}, :update {:shape \"circle\", :size {:value 70}, :stroke {:value \"transparent\"}}, :hover {:size {:value 210}, :stroke {:value \"white\"}}}}], :scales [{:name \"x\", :type \"linear\", :range \"width\", :zero false, :domain {:data \"e6e4aa5e-796d-4fbc-85ab-43adcf41f9d2\", :field \"data.x\"}} {:name \"y\", :type \"linear\", :range \"height\", :nice true, :zero false, :domain {:data \"e6e4aa5e-796d-4fbc-85ab-43adcf41f9d2\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}]}}"}
;; <=

;; **
;;; ### Representing a tour
;;; 
;;; A tour is a vector of cities starting from and ending at the `origin`. However, most of the algorithm does not connect the end of a tour to the start of the tour, thus, we will need to do it manually for visualization purpose.
;;; 
;;; Here, the first city of a tour is assumed to be the `origin` city, and will be marked red.
;; **

;; @@
(defn plot-tour [tour]
  (let [points (map city-to-point (lazy-cat tour (take 1 tour)))]
    (plot/compose (plot/list-plot points)
                  (plot/list-plot points :joined true)
                  (plot/list-plot [(first points)] :colour "#FF0000"))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;tsp-notes/plot-tour</span>","value":"#'tsp-notes/plot-tour"}
;; <=

;; **
;;; Example:
;; **

;; @@
(plot-tour (random-cities 20))
;; @@
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"scales":[{"name":"x","type":"linear","range":"width","zero":false,"domain":{"data":"54e3c96c-cec8-4b6e-9d6e-b632fc326bb0","field":"data.x"}},{"name":"y","type":"linear","range":"height","nice":true,"zero":false,"domain":{"data":"54e3c96c-cec8-4b6e-9d6e-b632fc326bb0","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}],"data":[{"name":"54e3c96c-cec8-4b6e-9d6e-b632fc326bb0","values":[{"x":858,"y":1},{"x":817,"y":204},{"x":288,"y":68},{"x":376,"y":74},{"x":531,"y":508},{"x":476,"y":41},{"x":55,"y":486},{"x":533,"y":129},{"x":636,"y":357},{"x":352,"y":321},{"x":424,"y":329},{"x":85,"y":146},{"x":387,"y":260},{"x":599,"y":530},{"x":631,"y":404},{"x":866,"y":172},{"x":67,"y":510},{"x":639,"y":588},{"x":382,"y":253},{"x":42,"y":550},{"x":858,"y":1}]},{"name":"511056cc-b3ba-4c28-93c1-250c472678d6","values":[{"x":858,"y":1},{"x":817,"y":204},{"x":288,"y":68},{"x":376,"y":74},{"x":531,"y":508},{"x":476,"y":41},{"x":55,"y":486},{"x":533,"y":129},{"x":636,"y":357},{"x":352,"y":321},{"x":424,"y":329},{"x":85,"y":146},{"x":387,"y":260},{"x":599,"y":530},{"x":631,"y":404},{"x":866,"y":172},{"x":67,"y":510},{"x":639,"y":588},{"x":382,"y":253},{"x":42,"y":550},{"x":858,"y":1}]},{"name":"1b45488d-70cc-4091-a850-77735b723ba4","values":[{"x":858,"y":1}]}],"marks":[{"type":"symbol","from":{"data":"54e3c96c-cec8-4b6e-9d6e-b632fc326bb0"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"fill":{"value":"steelblue"},"fillOpacity":{"value":1}},"update":{"shape":"circle","size":{"value":70},"stroke":{"value":"transparent"}},"hover":{"size":{"value":210},"stroke":{"value":"white"}}}},{"type":"line","from":{"data":"511056cc-b3ba-4c28-93c1-250c472678d6"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"stroke":{"value":"#FF29D2"},"strokeWidth":{"value":2},"strokeOpacity":{"value":1}}}},{"type":"symbol","from":{"data":"1b45488d-70cc-4091-a850-77735b723ba4"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"fill":{"value":"#FF0000"},"fillOpacity":{"value":1}},"update":{"shape":"circle","size":{"value":70},"stroke":{"value":"transparent"}},"hover":{"size":{"value":210},"stroke":{"value":"white"}}}}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :scales [{:name \"x\", :type \"linear\", :range \"width\", :zero false, :domain {:data \"54e3c96c-cec8-4b6e-9d6e-b632fc326bb0\", :field \"data.x\"}} {:name \"y\", :type \"linear\", :range \"height\", :nice true, :zero false, :domain {:data \"54e3c96c-cec8-4b6e-9d6e-b632fc326bb0\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}], :data ({:name \"54e3c96c-cec8-4b6e-9d6e-b632fc326bb0\", :values ({:x 858, :y 1} {:x 817, :y 204} {:x 288, :y 68} {:x 376, :y 74} {:x 531, :y 508} {:x 476, :y 41} {:x 55, :y 486} {:x 533, :y 129} {:x 636, :y 357} {:x 352, :y 321} {:x 424, :y 329} {:x 85, :y 146} {:x 387, :y 260} {:x 599, :y 530} {:x 631, :y 404} {:x 866, :y 172} {:x 67, :y 510} {:x 639, :y 588} {:x 382, :y 253} {:x 42, :y 550} {:x 858, :y 1})} {:name \"511056cc-b3ba-4c28-93c1-250c472678d6\", :values ({:x 858, :y 1} {:x 817, :y 204} {:x 288, :y 68} {:x 376, :y 74} {:x 531, :y 508} {:x 476, :y 41} {:x 55, :y 486} {:x 533, :y 129} {:x 636, :y 357} {:x 352, :y 321} {:x 424, :y 329} {:x 85, :y 146} {:x 387, :y 260} {:x 599, :y 530} {:x 631, :y 404} {:x 866, :y 172} {:x 67, :y 510} {:x 639, :y 588} {:x 382, :y 253} {:x 42, :y 550} {:x 858, :y 1})} {:name \"1b45488d-70cc-4091-a850-77735b723ba4\", :values ({:x 858, :y 1})}), :marks ({:type \"symbol\", :from {:data \"54e3c96c-cec8-4b6e-9d6e-b632fc326bb0\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :fill {:value \"steelblue\"}, :fillOpacity {:value 1}}, :update {:shape \"circle\", :size {:value 70}, :stroke {:value \"transparent\"}}, :hover {:size {:value 210}, :stroke {:value \"white\"}}}} {:type \"line\", :from {:data \"511056cc-b3ba-4c28-93c1-250c472678d6\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :stroke {:value \"#FF29D2\"}, :strokeWidth {:value 2}, :strokeOpacity {:value 1}}}} {:type \"symbol\", :from {:data \"1b45488d-70cc-4091-a850-77735b723ba4\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :fill {:value \"#FF0000\"}, :fillOpacity {:value 1}}, :update {:shape \"circle\", :size {:value 70}, :stroke {:value \"transparent\"}}, :hover {:size {:value 210}, :stroke {:value \"white\"}}}})}}"}
;; <=

;; **
;;; ### Calculating the length of a tour
;;; 
;;; The length of the tour `[1 2 3]` is the sum of the distance between each pair of consecutive cities in the tour, including the distance from the last city to the origin city: `[1 2]`, `[2 3]` and `[3 1]`.
;;; 
;;; Pair of consecutive cities can be obtained by _zipping_ a list of starting cities and a list of ending cities. A list of ending cities can be obtained by left shifting the list of starting cities by one city.
;;; 
;;; starting city: `1 2 3`  
;;; ending city: `2 3 1`  
;;; zipped: `[1 2] [2 3] [3 1]`
;;; 
;;; > `lazy-cat`: https://clojuredocs.org/clojure.core/lazy-cat  
;;; > `map`: https://clojuredocs.org/clojure.core/map  
;;; > `apply`: https://clojuredocs.org/clojure.core/apply  
;;; > `partial`: https://clojuredocs.org/clojure.core/partial
;; **

;; @@
(defn tour-length [tour]
  (let [destinations (lazy-cat (drop 1 tour) (take 1 tour))
        pairs (map vector tour destinations)]
  	(apply + (map (partial apply distance) pairs))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;tsp-notes/tour-length</span>","value":"#'tsp-notes/tour-length"}
;; <=

;; **
;;; Example:
;; **

;; @@
(tour-length (random-cities 10))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>3343.488577429193</span>","value":"3343.488577429193"}
;; <=

;; **
;;; ### ... and a helper method
;;; 
;;; This will execute each algorithm, time it and plot the result of the algorithm.
;; **

;; @@
(defn execute [cities & algorithms]
  (for [algorithm algorithms]
    (time (let [tour (algorithm cities)]
            [(str (count cities) " cities") (str "distance: "(tour-length tour)) (plot-tour tour)]))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;tsp-notes/execute</span>","value":"#'tsp-notes/execute"}
;; <=

;; **
;;; ... but we do not have any algorithms yet, so we will define one next: `Brute force algorithm`.
;; **
