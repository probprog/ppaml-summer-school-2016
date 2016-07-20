;; gorilla-repl.fileformat = 1

;; **
;;; # Gaussian posterior estimation
;; **

;; @@
(ns gaussian-estimation
  (:require [gorilla-plot.core :as plot]
            [anglican.stat :as stat])
  (:use [anglican core emit runtime]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; We can use Anglican for posterior estimation in Bayesian models.
;;; 
;;; Suppose we are trying to estimate the mean of a Gaussian distribution, given some observed data @@y\_i@@.
;;; We'll assume that the variance is known, and focus on learning the posterior distribution of the mean @@\mu@@.
;;; We put a Gaussian prior on @@\mu@@, yielding a model:
;;; 
;;; $$\begin{align}
;;; \sigma^2 &= 2 \\\\
;;; \mu &\sim \mathrm{Normal}(1, \sqrt 5) \\\\
;;; y\_i|\mu &\sim \mathrm{Normal}(\mu, \sigma).
;;; \end{align}$$
;;; 
;;; Now suppose we observe two data points, @@y\_1 = 9@@ and @@y\_2 = 8@@.
;;; This will be passed as an input to the query as a vector with two elements.
;; **

;; @@
(def y1 9)
(def y2 8)
(def dataset [y1 y2])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;gaussian-estimation/dataset</span>","value":"#'gaussian-estimation/dataset"}
;; <=

;; **
;;; Write this model as a simple Anglican program:
;; **

;; @@
(defquery gaussian-model [y1 y2]

  ;;; *** YOUR CODE HERE ***

  ;;; *** change the following line if needed ***
  mu)
;; @@

;; **
;;; The following line now draws 20,000 samples using Metropolis-Hastings sampling
;; **

;; @@
(def posterior-samples  
  (take 10000
        (drop 10000
              (map :result
                   (doquery :rmh gaussian-model [y1 y2] 
                            :number-of-particles 1000)))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;gaussian-estimation/posterior-samples</span>","value":"#'gaussian-estimation/posterior-samples"}
;; <=

;; **
;;; We can plot a histogram of these samples to see the posterior.
;;; 
;;; Here we have chosen the [conjugate prior](http://en.wikipedia.org/wiki/Conjugate_prior) for @@\mu@@, making this a rare model in that we can actually compute the posterior distribution analytically
;;; &mdash; when we run our sampler, we expect to find
;;; 
;;; $$\begin{align}
;;; \mu|y\_{1:2} &\sim \mathrm{Normal}(7.25, 0.9129).
;;; \end{align}$$
;;; 
;;; We can also draw samples from the prior distribution @@\mathrm{Normal}(1,\sqrt 5)@@, to see how the posterior differs from the prior:
;; **

;; @@
(def prior-samples 
  (repeatedly 10000 #(sample* (normal 1 (sqrt 5)))))


(println "Prior on mu (blue) and posterior (green)")
(plot/compose
 (plot/histogram prior-samples
                 :normalize :probability-density :bins 40
                 :plot-range [[-10 10] [0 0.8]])
 (plot/histogram posterior-samples
                 :normalize :probability-density :bins 40
                 :color :green))
;; @@
;; ->
;;; Prior on mu (blue) and posterior (green)
;;; 
;; <-
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"scales":[{"name":"x","type":"linear","range":"width","zero":false,"domain":[-10,10]},{"name":"y","type":"linear","range":"height","nice":true,"zero":false,"domain":[0,0.8]}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}],"data":[{"name":"6600628c-6ac6-439d-ab6d-a3454e34c338","values":[{"x":-10.0,"y":0},{"x":-9.5,"y":0.0},{"x":-9.0,"y":0.0},{"x":-8.5,"y":0.0},{"x":-8.0,"y":0.0},{"x":-7.5,"y":2.0E-4},{"x":-7.0,"y":4.0E-4},{"x":-6.5,"y":6.0E-4},{"x":-6.0,"y":6.0E-4},{"x":-5.5,"y":0.001},{"x":-5.0,"y":0.0052},{"x":-4.5,"y":0.0062},{"x":-4.0,"y":0.0112},{"x":-3.5,"y":0.0188},{"x":-3.0,"y":0.0262},{"x":-2.5,"y":0.0376},{"x":-2.0,"y":0.0654},{"x":-1.5,"y":0.0858},{"x":-1.0,"y":0.1112},{"x":-0.5,"y":0.1374},{"x":0.0,"y":0.1542},{"x":0.5,"y":0.1662},{"x":1.0,"y":0.1794},{"x":1.5,"y":0.1758},{"x":2.0,"y":0.1698},{"x":2.5,"y":0.1512},{"x":3.0,"y":0.1282},{"x":3.5,"y":0.1058},{"x":4.0,"y":0.0842},{"x":4.5,"y":0.0604},{"x":5.0,"y":0.046},{"x":5.5,"y":0.0266},{"x":6.0,"y":0.0192},{"x":6.5,"y":0.009},{"x":7.0,"y":0.0076},{"x":7.5,"y":0.0056},{"x":8.0,"y":0.0022},{"x":8.5,"y":2.0E-4},{"x":9.0,"y":2.0E-4},{"x":9.5,"y":2.0E-4},{"x":10.0,"y":2.0E-4},{"x":10.5,"y":0.0},{"x":11.0,"y":0}]},{"name":"0899a971-aab8-4c01-bf15-40edb5c6da07","values":[{"x":3.9811210776148878,"y":0},{"x":4.129999126562134,"y":6.716906938741147E-4},{"x":4.27887717550938,"y":0.0},{"x":4.427755224456626,"y":0.004701834857118803},{"x":4.576633273403872,"y":0.0},{"x":4.725511322351118,"y":0.002686762775496459},{"x":4.874389371298364,"y":0.028211009142712816},{"x":5.0232674202456105,"y":0.03425622538757985},{"x":5.172145469192857,"y":0.037614678856950426},{"x":5.321023518140103,"y":0.02149410220397167},{"x":5.469901567087349,"y":0.06246723453029267},{"x":5.618779616034595,"y":0.11956094350959241},{"x":5.767657664981841,"y":0.11284403657085126},{"x":5.916535713929087,"y":0.12896461322383002},{"x":6.065413762876333,"y":0.1968053733051156},{"x":6.214291811823579,"y":0.18068479665213685},{"x":6.363169860770825,"y":0.26397444269252707},{"x":6.5120479097180715,"y":0.3130078633453375},{"x":6.660925958665318,"y":0.3291284399983162},{"x":6.809804007612564,"y":0.34995085150841376},{"x":6.95868205655981,"y":0.38689383967149005},{"x":7.107560105507056,"y":0.3593545212226514},{"x":7.256438154454302,"y":0.4130897767325805},{"x":7.405316203401548,"y":0.4668250322425097},{"x":7.554194252348794,"y":0.46077981599764267},{"x":7.70307230129604,"y":0.47824377403836965},{"x":7.851950350243286,"y":0.36942988163076307},{"x":8.000828399190532,"y":0.3882372210592383},{"x":8.149706448137778,"y":0.3197247702840786},{"x":8.298584497085024,"y":0.20822411510097555},{"x":8.44746254603227,"y":0.2015072081622344},{"x":8.596340594979516,"y":0.14978702473392758},{"x":8.745218643926762,"y":0.08530471812201257},{"x":8.894096692874008,"y":0.06582568799966323},{"x":9.042974741821254,"y":0.05709370897929975},{"x":9.1918527907685,"y":0.04701834857118803},{"x":9.340730839715746,"y":0.018807339428475213},{"x":9.489608888662993,"y":0.006716906938741147},{"x":9.638486937610239,"y":0.017463958040726982},{"x":9.787364986557485,"y":0.02015072081622344},{"x":9.93624303550473,"y":0.009403669714237606},{"x":10.085121084451977,"y":0}]}],"marks":[{"type":"line","from":{"data":"6600628c-6ac6-439d-ab6d-a3454e34c338"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"interpolate":{"value":"step-before"},"fill":{"value":"steelblue"},"fillOpacity":{"value":0.4},"stroke":{"value":"steelblue"},"strokeWidth":{"value":2},"strokeOpacity":{"value":1}}}},{"type":"line","from":{"data":"0899a971-aab8-4c01-bf15-40edb5c6da07"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"interpolate":{"value":"step-before"},"fill":{"value":"green"},"fillOpacity":{"value":0.4},"stroke":{"value":"green"},"strokeWidth":{"value":2},"strokeOpacity":{"value":1}}}}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :scales [{:name \"x\", :type \"linear\", :range \"width\", :zero false, :domain [-10 10]} {:name \"y\", :type \"linear\", :range \"height\", :nice true, :zero false, :domain [0 0.8]}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}], :data ({:name \"6600628c-6ac6-439d-ab6d-a3454e34c338\", :values ({:x -10.0, :y 0} {:x -9.5, :y 0.0} {:x -9.0, :y 0.0} {:x -8.5, :y 0.0} {:x -8.0, :y 0.0} {:x -7.5, :y 2.0E-4} {:x -7.0, :y 4.0E-4} {:x -6.5, :y 6.0E-4} {:x -6.0, :y 6.0E-4} {:x -5.5, :y 0.001} {:x -5.0, :y 0.0052} {:x -4.5, :y 0.0062} {:x -4.0, :y 0.0112} {:x -3.5, :y 0.0188} {:x -3.0, :y 0.0262} {:x -2.5, :y 0.0376} {:x -2.0, :y 0.0654} {:x -1.5, :y 0.0858} {:x -1.0, :y 0.1112} {:x -0.5, :y 0.1374} {:x 0.0, :y 0.1542} {:x 0.5, :y 0.1662} {:x 1.0, :y 0.1794} {:x 1.5, :y 0.1758} {:x 2.0, :y 0.1698} {:x 2.5, :y 0.1512} {:x 3.0, :y 0.1282} {:x 3.5, :y 0.1058} {:x 4.0, :y 0.0842} {:x 4.5, :y 0.0604} {:x 5.0, :y 0.046} {:x 5.5, :y 0.0266} {:x 6.0, :y 0.0192} {:x 6.5, :y 0.009} {:x 7.0, :y 0.0076} {:x 7.5, :y 0.0056} {:x 8.0, :y 0.0022} {:x 8.5, :y 2.0E-4} {:x 9.0, :y 2.0E-4} {:x 9.5, :y 2.0E-4} {:x 10.0, :y 2.0E-4} {:x 10.5, :y 0.0} {:x 11.0, :y 0})} {:name \"0899a971-aab8-4c01-bf15-40edb5c6da07\", :values ({:x 3.9811210776148878, :y 0} {:x 4.129999126562134, :y 6.716906938741147E-4} {:x 4.27887717550938, :y 0.0} {:x 4.427755224456626, :y 0.004701834857118803} {:x 4.576633273403872, :y 0.0} {:x 4.725511322351118, :y 0.002686762775496459} {:x 4.874389371298364, :y 0.028211009142712816} {:x 5.0232674202456105, :y 0.03425622538757985} {:x 5.172145469192857, :y 0.037614678856950426} {:x 5.321023518140103, :y 0.02149410220397167} {:x 5.469901567087349, :y 0.06246723453029267} {:x 5.618779616034595, :y 0.11956094350959241} {:x 5.767657664981841, :y 0.11284403657085126} {:x 5.916535713929087, :y 0.12896461322383002} {:x 6.065413762876333, :y 0.1968053733051156} {:x 6.214291811823579, :y 0.18068479665213685} {:x 6.363169860770825, :y 0.26397444269252707} {:x 6.5120479097180715, :y 0.3130078633453375} {:x 6.660925958665318, :y 0.3291284399983162} {:x 6.809804007612564, :y 0.34995085150841376} {:x 6.95868205655981, :y 0.38689383967149005} {:x 7.107560105507056, :y 0.3593545212226514} {:x 7.256438154454302, :y 0.4130897767325805} {:x 7.405316203401548, :y 0.4668250322425097} {:x 7.554194252348794, :y 0.46077981599764267} {:x 7.70307230129604, :y 0.47824377403836965} {:x 7.851950350243286, :y 0.36942988163076307} {:x 8.000828399190532, :y 0.3882372210592383} {:x 8.149706448137778, :y 0.3197247702840786} {:x 8.298584497085024, :y 0.20822411510097555} {:x 8.44746254603227, :y 0.2015072081622344} {:x 8.596340594979516, :y 0.14978702473392758} {:x 8.745218643926762, :y 0.08530471812201257} {:x 8.894096692874008, :y 0.06582568799966323} {:x 9.042974741821254, :y 0.05709370897929975} {:x 9.1918527907685, :y 0.04701834857118803} {:x 9.340730839715746, :y 0.018807339428475213} {:x 9.489608888662993, :y 0.006716906938741147} {:x 9.638486937610239, :y 0.017463958040726982} {:x 9.787364986557485, :y 0.02015072081622344} {:x 9.93624303550473, :y 0.009403669714237606} {:x 10.085121084451977, :y 0})}), :marks ({:type \"line\", :from {:data \"6600628c-6ac6-439d-ab6d-a3454e34c338\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :interpolate {:value \"step-before\"}, :fill {:value \"steelblue\"}, :fillOpacity {:value 0.4}, :stroke {:value \"steelblue\"}, :strokeWidth {:value 2}, :strokeOpacity {:value 1}}}} {:type \"line\", :from {:data \"0899a971-aab8-4c01-bf15-40edb5c6da07\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :interpolate {:value \"step-before\"}, :fill {:value :green}, :fillOpacity {:value 0.4}, :stroke {:value :green}, :strokeWidth {:value 2}, :strokeOpacity {:value 1}}}})}}"}
;; <=

;; **
;;; # The seven scientists
;;; 
;;; Here's an interesting variation on estimating the mean of a Gaussian. This example is from [MacKay 2003, exercise 22.15] and [Lee & Wagenmaker 2013, section 4.2].
;;; 
;;; Suppose seven scientists all go and perform the same experiment, each collecting a measurement @@y\_i@@ for @@i = 1,\dots,7@@. 
;;; 
;;; These scientists are varyingly good at their job, and while we can assume each scientist would estimate @@y@@ correctly _on average_, some of them may have much more error in their measurements than others.
;;; 
;;; They come back with the following seven observations:
;; **

;; @@
(def measurements [-27.020 3.570 8.191 9.898 9.603 9.945 10.056])

(plot/bar-chart (range 1 8) measurements)
;; @@
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"data":[{"name":"923a05e3-f2bb-48ea-ab63-caa62b8006be","values":[{"x":1,"y":-27.02},{"x":2,"y":3.57},{"x":3,"y":8.191},{"x":4,"y":9.898},{"x":5,"y":9.603},{"x":6,"y":9.945},{"x":7,"y":10.056}]}],"marks":[{"type":"rect","from":{"data":"923a05e3-f2bb-48ea-ab63-caa62b8006be"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"width":{"scale":"x","band":true,"offset":-1},"y":{"scale":"y","field":"data.y"},"y2":{"scale":"y","value":0}},"update":{"fill":{"value":"steelblue"},"opacity":{"value":1}},"hover":{"fill":{"value":"#FF29D2"}}}}],"scales":[{"name":"x","type":"ordinal","range":"width","domain":{"data":"923a05e3-f2bb-48ea-ab63-caa62b8006be","field":"data.x"}},{"name":"y","range":"height","nice":true,"domain":{"data":"923a05e3-f2bb-48ea-ab63-caa62b8006be","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :data [{:name \"923a05e3-f2bb-48ea-ab63-caa62b8006be\", :values ({:x 1, :y -27.02} {:x 2, :y 3.57} {:x 3, :y 8.191} {:x 4, :y 9.898} {:x 5, :y 9.603} {:x 6, :y 9.945} {:x 7, :y 10.056})}], :marks [{:type \"rect\", :from {:data \"923a05e3-f2bb-48ea-ab63-caa62b8006be\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :width {:scale \"x\", :band true, :offset -1}, :y {:scale \"y\", :field \"data.y\"}, :y2 {:scale \"y\", :value 0}}, :update {:fill {:value \"steelblue\"}, :opacity {:value 1}}, :hover {:fill {:value \"#FF29D2\"}}}}], :scales [{:name \"x\", :type \"ordinal\", :range \"width\", :domain {:data \"923a05e3-f2bb-48ea-ab63-caa62b8006be\", :field \"data.x\"}} {:name \"y\", :range \"height\", :nice true, :domain {:data \"923a05e3-f2bb-48ea-ab63-caa62b8006be\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}]}}"}
;; <=

;; **
;;; Clearly scientist 1 does not know what he is doing (and 2 and 3 are probably a little suspect too)!
;;; 
;;; To model this situation, we place simple priors on the mean @@\mu@@ of the measurements, and the error standard deviation @@\sigma\_i@@ for each of the @@i@@ scientists.
;;; 
;;; As a starting point, consider placing uninformative priors on these parameters; a suggestion is:
;;; $$\begin{align}
;;; \mu &\sim \mathrm{Normal}(0, 50) \\\\
;;; \sigma\_i &\sim \mathrm{Uniform}(0, 25)
;;; \end{align}$$
;;; 
;;; The uniform distribution over real numbers on the open interval @@(a, b)@@ can be constructed in Anglican with `(uniform-continuous a b)`.
;;; 
;;; We can ask two questions here:
;;; 
;;; * Given these measurements, what is the posterior distribution of @@y@@?
;;; * What distribution over noise level @@\sigma_i@@ do we infer for each of these scientists' estimates?
;;; 
;;; Write the model in Anglican such that it returns a hashmap with two entries: one for the value `:mu` and one for the vector of seven noise levels `:sigmas`.
;; **

;; @@
(defquery scientists [measurements]
  
  ;;; *** YOUR CODE HERE ***
  
  ;;; *** modify the next line as needed ***
  {:mu 0.0 :sigmas (repeat 7 1.0)})
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;gaussian-estimation/scientists</span>","value":"#'gaussian-estimation/scientists"}
;; <=

;; **
;;; Given the measurements, we sample from the conditional distribution and plot a histogram of the results.
;; **

;; @@
(def scientist-samples 
  (take 10000
        (drop 10000
              (map :result
                   (doquery :rmh 
                            scientists [measurements])))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;gaussian-estimation/scientist-samples</span>","value":"#'gaussian-estimation/scientist-samples"}
;; <=

;; @@
(println "Expected value of measured quantity:" (stat/mean (map :mu scientist-samples)))

(plot/histogram (map :mu scientist-samples)
                :normalize :probability
                :bins 50)
;; @@
;; ->
;;; Expected value of measured quantity: 9.222200323386152
;;; 
;; <-
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"data":[{"name":"be796fb4-00bb-4b72-b76a-a8e7aeb84f35","values":[{"x":-1.2833390116344767,"y":0},{"x":-1.0038481960208998,"y":0.0013},{"x":-0.7243573804073229,"y":0.002},{"x":-0.4448665647937459,"y":0.002},{"x":-0.16537574918016895,"y":0.0},{"x":0.11411506643340802,"y":0.0},{"x":0.393605882046985,"y":0.0},{"x":0.6730966976605619,"y":0.0},{"x":0.9525875132741388,"y":0.0},{"x":1.2320783288877157,"y":0.0015},{"x":1.5115691445012926,"y":5.0E-4},{"x":1.7910599601148696,"y":0.0},{"x":2.0705507757284467,"y":0.0},{"x":2.350041591342024,"y":0.0032},{"x":2.629532406955601,"y":0.0},{"x":2.909023222569178,"y":0.005},{"x":3.1885140381827553,"y":0.0},{"x":3.4680048537963324,"y":0.0},{"x":3.7474956694099095,"y":0.0031},{"x":4.026986485023486,"y":0.0094},{"x":4.306477300637063,"y":0.0},{"x":4.58596811625064,"y":0.0023},{"x":4.865458931864216,"y":0.0011},{"x":5.144949747477793,"y":0.0047},{"x":5.42444056309137,"y":0.0029},{"x":5.703931378704946,"y":0.0},{"x":5.983422194318523,"y":0.0},{"x":6.2629130099321,"y":2.0E-4},{"x":6.5424038255456765,"y":0.0059},{"x":6.821894641159253,"y":0.0049},{"x":7.10138545677283,"y":0.0088},{"x":7.3808762723864065,"y":0.0048},{"x":7.660367087999983,"y":0.0064},{"x":7.93985790361356,"y":0.0438},{"x":8.219348719227137,"y":0.0187},{"x":8.498839534840714,"y":0.0473},{"x":8.778330350454292,"y":0.041},{"x":9.05782116606787,"y":0.0688},{"x":9.337311981681447,"y":0.0852},{"x":9.616802797295025,"y":0.1258},{"x":9.896293612908602,"y":0.2028},{"x":10.17578442852218,"y":0.1575},{"x":10.455275244135757,"y":0.0623},{"x":10.734766059749335,"y":0.0245},{"x":11.014256875362912,"y":0.0265},{"x":11.29374769097649,"y":0.0098},{"x":11.573238506590068,"y":0.0011},{"x":11.852729322203645,"y":0.0108},{"x":12.132220137817223,"y":0.0019},{"x":12.4117109534308,"y":0.001},{"x":12.691201769044378,"y":0.0012},{"x":12.970692584657955,"y":0}]}],"marks":[{"type":"line","from":{"data":"be796fb4-00bb-4b72-b76a-a8e7aeb84f35"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"interpolate":{"value":"step-before"},"fill":{"value":"steelblue"},"fillOpacity":{"value":0.4},"stroke":{"value":"steelblue"},"strokeWidth":{"value":2},"strokeOpacity":{"value":1}}}}],"scales":[{"name":"x","type":"linear","range":"width","zero":false,"domain":{"data":"be796fb4-00bb-4b72-b76a-a8e7aeb84f35","field":"data.x"}},{"name":"y","type":"linear","range":"height","nice":true,"zero":false,"domain":{"data":"be796fb4-00bb-4b72-b76a-a8e7aeb84f35","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :data [{:name \"be796fb4-00bb-4b72-b76a-a8e7aeb84f35\", :values ({:x -1.2833390116344767, :y 0} {:x -1.0038481960208998, :y 0.0013} {:x -0.7243573804073229, :y 0.002} {:x -0.4448665647937459, :y 0.002} {:x -0.16537574918016895, :y 0.0} {:x 0.11411506643340802, :y 0.0} {:x 0.393605882046985, :y 0.0} {:x 0.6730966976605619, :y 0.0} {:x 0.9525875132741388, :y 0.0} {:x 1.2320783288877157, :y 0.0015} {:x 1.5115691445012926, :y 5.0E-4} {:x 1.7910599601148696, :y 0.0} {:x 2.0705507757284467, :y 0.0} {:x 2.350041591342024, :y 0.0032} {:x 2.629532406955601, :y 0.0} {:x 2.909023222569178, :y 0.005} {:x 3.1885140381827553, :y 0.0} {:x 3.4680048537963324, :y 0.0} {:x 3.7474956694099095, :y 0.0031} {:x 4.026986485023486, :y 0.0094} {:x 4.306477300637063, :y 0.0} {:x 4.58596811625064, :y 0.0023} {:x 4.865458931864216, :y 0.0011} {:x 5.144949747477793, :y 0.0047} {:x 5.42444056309137, :y 0.0029} {:x 5.703931378704946, :y 0.0} {:x 5.983422194318523, :y 0.0} {:x 6.2629130099321, :y 2.0E-4} {:x 6.5424038255456765, :y 0.0059} {:x 6.821894641159253, :y 0.0049} {:x 7.10138545677283, :y 0.0088} {:x 7.3808762723864065, :y 0.0048} {:x 7.660367087999983, :y 0.0064} {:x 7.93985790361356, :y 0.0438} {:x 8.219348719227137, :y 0.0187} {:x 8.498839534840714, :y 0.0473} {:x 8.778330350454292, :y 0.041} {:x 9.05782116606787, :y 0.0688} {:x 9.337311981681447, :y 0.0852} {:x 9.616802797295025, :y 0.1258} {:x 9.896293612908602, :y 0.2028} {:x 10.17578442852218, :y 0.1575} {:x 10.455275244135757, :y 0.0623} {:x 10.734766059749335, :y 0.0245} {:x 11.014256875362912, :y 0.0265} {:x 11.29374769097649, :y 0.0098} {:x 11.573238506590068, :y 0.0011} {:x 11.852729322203645, :y 0.0108} {:x 12.132220137817223, :y 0.0019} {:x 12.4117109534308, :y 0.001} {:x 12.691201769044378, :y 0.0012} {:x 12.970692584657955, :y 0})}], :marks [{:type \"line\", :from {:data \"be796fb4-00bb-4b72-b76a-a8e7aeb84f35\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :interpolate {:value \"step-before\"}, :fill {:value \"steelblue\"}, :fillOpacity {:value 0.4}, :stroke {:value \"steelblue\"}, :strokeWidth {:value 2}, :strokeOpacity {:value 1}}}}], :scales [{:name \"x\", :type \"linear\", :range \"width\", :zero false, :domain {:data \"be796fb4-00bb-4b72-b76a-a8e7aeb84f35\", :field \"data.x\"}} {:name \"y\", :type \"linear\", :range \"height\", :nice true, :zero false, :domain {:data \"be796fb4-00bb-4b72-b76a-a8e7aeb84f35\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}]}}"}
;; <=

;; @@
(def noise-estimate (mean (map :sigmas scientist-samples)))

(plot/bar-chart (range 1 8) noise-estimate)
;; @@
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"data":[{"name":"682589ac-158c-4973-8b08-5b8e722d162e","values":[{"x":1,"y":20.017395733119653},{"x":2,"y":12.101351279236129},{"x":3,"y":7.265266383799016},{"x":4,"y":5.865732590085473},{"x":5,"y":6.589522486132321},{"x":6,"y":6.196043436742876},{"x":7,"y":6.834466249434729}]}],"marks":[{"type":"rect","from":{"data":"682589ac-158c-4973-8b08-5b8e722d162e"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"width":{"scale":"x","band":true,"offset":-1},"y":{"scale":"y","field":"data.y"},"y2":{"scale":"y","value":0}},"update":{"fill":{"value":"steelblue"},"opacity":{"value":1}},"hover":{"fill":{"value":"#FF29D2"}}}}],"scales":[{"name":"x","type":"ordinal","range":"width","domain":{"data":"682589ac-158c-4973-8b08-5b8e722d162e","field":"data.x"}},{"name":"y","range":"height","nice":true,"domain":{"data":"682589ac-158c-4973-8b08-5b8e722d162e","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :data [{:name \"682589ac-158c-4973-8b08-5b8e722d162e\", :values ({:x 1, :y 20.017395733119653} {:x 2, :y 12.101351279236129} {:x 3, :y 7.265266383799016} {:x 4, :y 5.865732590085473} {:x 5, :y 6.589522486132321} {:x 6, :y 6.196043436742876} {:x 7, :y 6.834466249434729})}], :marks [{:type \"rect\", :from {:data \"682589ac-158c-4973-8b08-5b8e722d162e\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :width {:scale \"x\", :band true, :offset -1}, :y {:scale \"y\", :field \"data.y\"}, :y2 {:scale \"y\", :value 0}}, :update {:fill {:value \"steelblue\"}, :opacity {:value 1}}, :hover {:fill {:value \"#FF29D2\"}}}}], :scales [{:name \"x\", :type \"ordinal\", :range \"width\", :domain {:data \"682589ac-158c-4973-8b08-5b8e722d162e\", :field \"data.x\"}} {:name \"y\", :range \"height\", :nice true, :domain {:data \"682589ac-158c-4973-8b08-5b8e722d162e\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}]}}"}
;; <=

;; **
;;; * Are these noise levels what you would expect?
;;; * How sensitive is this to the prior on @@\mu@@ and @@\sigma\_i@@?
;; **

;; **
;;; 
;; **
