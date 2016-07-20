;; gorilla-repl.fileformat = 1

;; **
;;; # Exercise 2: Gaussian posterior estimation
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
  (let [;; define the observation noise
        sigma2 2
        ;; sample observation mean from prior
        mu (sample (normal 1.0 (sqrt 5)))
        ;; define the observation likelihood
        like (normal mu (sqrt sigma2))]
    ;; observe y1 and y2 acccording to the liklihood
    (observe like y1)
    (observe like y2)
    ;; return the observation mean
    mu))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;gaussian-estimation/gaussian-model</span>","value":"#'gaussian-estimation/gaussian-model"}
;; <=

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
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"scales":[{"name":"x","type":"linear","range":"width","zero":false,"domain":[-10,10]},{"name":"y","type":"linear","range":"height","nice":true,"zero":false,"domain":[0,0.8]}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}],"data":[{"name":"e49858c3-8366-4943-be6d-d5b322498f34","values":[{"x":-10.0,"y":0},{"x":-9.5,"y":0.0},{"x":-9.0,"y":0.0},{"x":-8.5,"y":0.0},{"x":-8.0,"y":0.0},{"x":-7.5,"y":0.0},{"x":-7.0,"y":2.0E-4},{"x":-6.5,"y":4.0E-4},{"x":-6.0,"y":8.0E-4},{"x":-5.5,"y":0.001},{"x":-5.0,"y":0.0018},{"x":-4.5,"y":0.006},{"x":-4.0,"y":0.0124},{"x":-3.5,"y":0.0208},{"x":-3.0,"y":0.0284},{"x":-2.5,"y":0.0488},{"x":-2.0,"y":0.0566},{"x":-1.5,"y":0.0794},{"x":-1.0,"y":0.115},{"x":-0.5,"y":0.13},{"x":0.0,"y":0.156},{"x":0.5,"y":0.1734},{"x":1.0,"y":0.1744},{"x":1.5,"y":0.1786},{"x":2.0,"y":0.166},{"x":2.5,"y":0.1554},{"x":3.0,"y":0.1288},{"x":3.5,"y":0.1096},{"x":4.0,"y":0.0842},{"x":4.5,"y":0.0568},{"x":5.0,"y":0.0456},{"x":5.5,"y":0.0234},{"x":6.0,"y":0.0188},{"x":6.5,"y":0.0138},{"x":7.0,"y":0.006},{"x":7.5,"y":0.0034},{"x":8.0,"y":0.0014},{"x":8.5,"y":0.0014},{"x":9.0,"y":6.0E-4},{"x":9.5,"y":0.0},{"x":10.0,"y":6.0E-4},{"x":10.5,"y":0.0},{"x":11.0,"y":0}]},{"name":"8175e170-b059-427d-bc1b-08a729f55781","values":[{"x":4.308118011083451,"y":0},{"x":4.456534988070035,"y":0.015496879445320457},{"x":4.604951965056619,"y":0.006063996304690614},{"x":4.7533689420432035,"y":0.01010666050781769},{"x":4.901785919029788,"y":0.019539543648447535},{"x":5.050202896016372,"y":0.01347554734375692},{"x":5.198619873002956,"y":0.03503642309376799},{"x":5.34703684998954,"y":0.04716441570314922},{"x":5.495453826976124,"y":0.06333507251565752},{"x":5.643870803962709,"y":0.0761368424922266},{"x":5.792287780949293,"y":0.1010666050781769},{"x":5.940704757935877,"y":0.12127992609381227},{"x":6.089121734922461,"y":0.19539543648447533},{"x":6.237538711909045,"y":0.2243678632735527},{"x":6.3859556888956295,"y":0.2789438300157682},{"x":6.534372665882214,"y":0.3166753625782876},{"x":6.682789642868798,"y":0.33015090992204454},{"x":6.831206619855382,"y":0.3624922235470611},{"x":6.979623596841966,"y":0.4446930623439784},{"x":7.12804057382855,"y":0.4022450882111441},{"x":7.276457550815135,"y":0.4864672591096248},{"x":7.424874527801719,"y":0.4925312554143154},{"x":7.573291504788303,"y":0.37596777089081806},{"x":7.721708481774887,"y":0.34834289883611635},{"x":7.870125458761471,"y":0.3416051251642379},{"x":8.018542435748056,"y":0.2775962752813925},{"x":8.166959412734641,"y":0.3382362383282987},{"x":8.315376389721227,"y":0.23784341061730963},{"x":8.463793366707812,"y":0.21021853856260794},{"x":8.612210343694397,"y":0.12464881292975151},{"x":8.760627320680982,"y":0.09904527297661336},{"x":8.909044297667567,"y":0.09163372193754706},{"x":9.057461274654152,"y":0.06198751778128183},{"x":9.205878251640737,"y":0.04312175150002214},{"x":9.354295228627322,"y":0.03031998152345307},{"x":9.502712205613907,"y":0.037731532562519376},{"x":9.651129182600492,"y":0.020887098382823224},{"x":9.799546159587077,"y":0.02021332101563538},{"x":9.947963136573662,"y":0.028298649421889532},{"x":10.096380113560247,"y":0.004716441570314922},{"x":10.244797090546832,"y":0.002695109468751384},{"x":10.393214067533417,"y":0}]}],"marks":[{"type":"line","from":{"data":"e49858c3-8366-4943-be6d-d5b322498f34"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"interpolate":{"value":"step-before"},"fill":{"value":"steelblue"},"fillOpacity":{"value":0.4},"stroke":{"value":"steelblue"},"strokeWidth":{"value":2},"strokeOpacity":{"value":1}}}},{"type":"line","from":{"data":"8175e170-b059-427d-bc1b-08a729f55781"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"interpolate":{"value":"step-before"},"fill":{"value":"green"},"fillOpacity":{"value":0.4},"stroke":{"value":"green"},"strokeWidth":{"value":2},"strokeOpacity":{"value":1}}}}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :scales [{:name \"x\", :type \"linear\", :range \"width\", :zero false, :domain [-10 10]} {:name \"y\", :type \"linear\", :range \"height\", :nice true, :zero false, :domain [0 0.8]}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}], :data ({:name \"e49858c3-8366-4943-be6d-d5b322498f34\", :values ({:x -10.0, :y 0} {:x -9.5, :y 0.0} {:x -9.0, :y 0.0} {:x -8.5, :y 0.0} {:x -8.0, :y 0.0} {:x -7.5, :y 0.0} {:x -7.0, :y 2.0E-4} {:x -6.5, :y 4.0E-4} {:x -6.0, :y 8.0E-4} {:x -5.5, :y 0.001} {:x -5.0, :y 0.0018} {:x -4.5, :y 0.006} {:x -4.0, :y 0.0124} {:x -3.5, :y 0.0208} {:x -3.0, :y 0.0284} {:x -2.5, :y 0.0488} {:x -2.0, :y 0.0566} {:x -1.5, :y 0.0794} {:x -1.0, :y 0.115} {:x -0.5, :y 0.13} {:x 0.0, :y 0.156} {:x 0.5, :y 0.1734} {:x 1.0, :y 0.1744} {:x 1.5, :y 0.1786} {:x 2.0, :y 0.166} {:x 2.5, :y 0.1554} {:x 3.0, :y 0.1288} {:x 3.5, :y 0.1096} {:x 4.0, :y 0.0842} {:x 4.5, :y 0.0568} {:x 5.0, :y 0.0456} {:x 5.5, :y 0.0234} {:x 6.0, :y 0.0188} {:x 6.5, :y 0.0138} {:x 7.0, :y 0.006} {:x 7.5, :y 0.0034} {:x 8.0, :y 0.0014} {:x 8.5, :y 0.0014} {:x 9.0, :y 6.0E-4} {:x 9.5, :y 0.0} {:x 10.0, :y 6.0E-4} {:x 10.5, :y 0.0} {:x 11.0, :y 0})} {:name \"8175e170-b059-427d-bc1b-08a729f55781\", :values ({:x 4.308118011083451, :y 0} {:x 4.456534988070035, :y 0.015496879445320457} {:x 4.604951965056619, :y 0.006063996304690614} {:x 4.7533689420432035, :y 0.01010666050781769} {:x 4.901785919029788, :y 0.019539543648447535} {:x 5.050202896016372, :y 0.01347554734375692} {:x 5.198619873002956, :y 0.03503642309376799} {:x 5.34703684998954, :y 0.04716441570314922} {:x 5.495453826976124, :y 0.06333507251565752} {:x 5.643870803962709, :y 0.0761368424922266} {:x 5.792287780949293, :y 0.1010666050781769} {:x 5.940704757935877, :y 0.12127992609381227} {:x 6.089121734922461, :y 0.19539543648447533} {:x 6.237538711909045, :y 0.2243678632735527} {:x 6.3859556888956295, :y 0.2789438300157682} {:x 6.534372665882214, :y 0.3166753625782876} {:x 6.682789642868798, :y 0.33015090992204454} {:x 6.831206619855382, :y 0.3624922235470611} {:x 6.979623596841966, :y 0.4446930623439784} {:x 7.12804057382855, :y 0.4022450882111441} {:x 7.276457550815135, :y 0.4864672591096248} {:x 7.424874527801719, :y 0.4925312554143154} {:x 7.573291504788303, :y 0.37596777089081806} {:x 7.721708481774887, :y 0.34834289883611635} {:x 7.870125458761471, :y 0.3416051251642379} {:x 8.018542435748056, :y 0.2775962752813925} {:x 8.166959412734641, :y 0.3382362383282987} {:x 8.315376389721227, :y 0.23784341061730963} {:x 8.463793366707812, :y 0.21021853856260794} {:x 8.612210343694397, :y 0.12464881292975151} {:x 8.760627320680982, :y 0.09904527297661336} {:x 8.909044297667567, :y 0.09163372193754706} {:x 9.057461274654152, :y 0.06198751778128183} {:x 9.205878251640737, :y 0.04312175150002214} {:x 9.354295228627322, :y 0.03031998152345307} {:x 9.502712205613907, :y 0.037731532562519376} {:x 9.651129182600492, :y 0.020887098382823224} {:x 9.799546159587077, :y 0.02021332101563538} {:x 9.947963136573662, :y 0.028298649421889532} {:x 10.096380113560247, :y 0.004716441570314922} {:x 10.244797090546832, :y 0.002695109468751384} {:x 10.393214067533417, :y 0})}), :marks ({:type \"line\", :from {:data \"e49858c3-8366-4943-be6d-d5b322498f34\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :interpolate {:value \"step-before\"}, :fill {:value \"steelblue\"}, :fillOpacity {:value 0.4}, :stroke {:value \"steelblue\"}, :strokeWidth {:value 2}, :strokeOpacity {:value 1}}}} {:type \"line\", :from {:data \"8175e170-b059-427d-bc1b-08a729f55781\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :interpolate {:value \"step-before\"}, :fill {:value :green}, :fillOpacity {:value 0.4}, :stroke {:value :green}, :strokeWidth {:value 2}, :strokeOpacity {:value 1}}}})}}"}
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
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"data":[{"name":"b7427337-9f01-4801-af1e-a1e71d8f4852","values":[{"x":1,"y":-27.02},{"x":2,"y":3.57},{"x":3,"y":8.191},{"x":4,"y":9.898},{"x":5,"y":9.603},{"x":6,"y":9.945},{"x":7,"y":10.056}]}],"marks":[{"type":"rect","from":{"data":"b7427337-9f01-4801-af1e-a1e71d8f4852"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"width":{"scale":"x","band":true,"offset":-1},"y":{"scale":"y","field":"data.y"},"y2":{"scale":"y","value":0}},"update":{"fill":{"value":"steelblue"},"opacity":{"value":1}},"hover":{"fill":{"value":"#FF29D2"}}}}],"scales":[{"name":"x","type":"ordinal","range":"width","domain":{"data":"b7427337-9f01-4801-af1e-a1e71d8f4852","field":"data.x"}},{"name":"y","range":"height","nice":true,"domain":{"data":"b7427337-9f01-4801-af1e-a1e71d8f4852","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :data [{:name \"b7427337-9f01-4801-af1e-a1e71d8f4852\", :values ({:x 1, :y -27.02} {:x 2, :y 3.57} {:x 3, :y 8.191} {:x 4, :y 9.898} {:x 5, :y 9.603} {:x 6, :y 9.945} {:x 7, :y 10.056})}], :marks [{:type \"rect\", :from {:data \"b7427337-9f01-4801-af1e-a1e71d8f4852\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :width {:scale \"x\", :band true, :offset -1}, :y {:scale \"y\", :field \"data.y\"}, :y2 {:scale \"y\", :value 0}}, :update {:fill {:value \"steelblue\"}, :opacity {:value 1}}, :hover {:fill {:value \"#FF29D2\"}}}}], :scales [{:name \"x\", :type \"ordinal\", :range \"width\", :domain {:data \"b7427337-9f01-4801-af1e-a1e71d8f4852\", :field \"data.x\"}} {:name \"y\", :range \"height\", :nice true, :domain {:data \"b7427337-9f01-4801-af1e-a1e71d8f4852\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}]}}"}
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
  (let [;; sample the mean (same for each scientist)
        mu (sample (normal 0 50))
        sigmas (map (fn [y]
                      (let [;; sample the measurement noise 
                            ;; (specific to scientist)
                            sigma (sample (uniform-continuous 0 25))]
                        ;; observe the measured value
                        (observe (normal mu sigma) y)
                        ;; return the sampled noise
                        sigma))
                    measurements)]
    ;; return mean and measurement noise for each scientist
    {:mu mu :sigmas sigmas}))
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
(println "Expected value of measured quantity:" 
         (stat/mean (map :mu scientist-samples)))

(plot/histogram (map :mu scientist-samples)
                :normalize :probability
                :bins 50)
;; @@
;; ->
;;; Expected value of measured quantity: 9.177622325529656
;;; 
;; <-
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"data":[{"name":"8417d97d-ea3d-43c2-9624-3c55bd61766e","values":[{"x":0.846013073554386,"y":0},{"x":1.1251720307518212,"y":0.0027},{"x":1.4043309879492563,"y":0.0},{"x":1.6834899451466914,"y":0.0},{"x":1.9626489023441265,"y":0.0021},{"x":2.2418078595415616,"y":0.0},{"x":2.520966816738997,"y":0.0},{"x":2.800125773936432,"y":0.0072},{"x":3.0792847311338667,"y":0.0083},{"x":3.3584436883313016,"y":8.0E-4},{"x":3.6376026455287365,"y":8.0E-4},{"x":3.9167616027261714,"y":0.0012},{"x":4.195920559923606,"y":0.0},{"x":4.475079517121041,"y":0.0098},{"x":4.754238474318476,"y":0.0048},{"x":5.033397431515911,"y":2.0E-4},{"x":5.312556388713346,"y":0.011},{"x":5.591715345910781,"y":0.0084},{"x":5.870874303108216,"y":0.006},{"x":6.1500332603056505,"y":0.0052},{"x":6.429192217503085,"y":1.0E-4},{"x":6.70835117470052,"y":0.002},{"x":6.987510131897955,"y":0.0037},{"x":7.26666908909539,"y":0.0018},{"x":7.545828046292825,"y":0.0303},{"x":7.82498700349026,"y":0.0291},{"x":8.104145960687696,"y":0.0366},{"x":8.38330491788513,"y":0.0263},{"x":8.662463875082565,"y":0.0421},{"x":8.94162283228,"y":0.0487},{"x":9.220781789477435,"y":0.0434},{"x":9.49994074667487,"y":0.1063},{"x":9.779099703872305,"y":0.0967},{"x":10.05825866106974,"y":0.2736},{"x":10.337417618267175,"y":0.0923},{"x":10.61657657546461,"y":0.0533},{"x":10.895735532662044,"y":0.0145},{"x":11.17489448985948,"y":0.0061},{"x":11.454053447056914,"y":0.0051},{"x":11.733212404254349,"y":0.0042},{"x":12.012371361451784,"y":0.0},{"x":12.291530318649219,"y":0.0013},{"x":12.570689275846654,"y":2.0E-4},{"x":12.849848233044089,"y":0.0},{"x":13.129007190241524,"y":0.0052},{"x":13.408166147438958,"y":0.0},{"x":13.687325104636393,"y":0.0018},{"x":13.966484061833828,"y":0.0},{"x":14.245643019031263,"y":0.0037},{"x":14.524801976228698,"y":0.0},{"x":14.803960933426133,"y":6.0E-4},{"x":15.083119890623568,"y":0.0025},{"x":15.362278847821003,"y":0}]}],"marks":[{"type":"line","from":{"data":"8417d97d-ea3d-43c2-9624-3c55bd61766e"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"interpolate":{"value":"step-before"},"fill":{"value":"steelblue"},"fillOpacity":{"value":0.4},"stroke":{"value":"steelblue"},"strokeWidth":{"value":2},"strokeOpacity":{"value":1}}}}],"scales":[{"name":"x","type":"linear","range":"width","zero":false,"domain":{"data":"8417d97d-ea3d-43c2-9624-3c55bd61766e","field":"data.x"}},{"name":"y","type":"linear","range":"height","nice":true,"zero":false,"domain":{"data":"8417d97d-ea3d-43c2-9624-3c55bd61766e","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :data [{:name \"8417d97d-ea3d-43c2-9624-3c55bd61766e\", :values ({:x 0.846013073554386, :y 0} {:x 1.1251720307518212, :y 0.0027} {:x 1.4043309879492563, :y 0.0} {:x 1.6834899451466914, :y 0.0} {:x 1.9626489023441265, :y 0.0021} {:x 2.2418078595415616, :y 0.0} {:x 2.520966816738997, :y 0.0} {:x 2.800125773936432, :y 0.0072} {:x 3.0792847311338667, :y 0.0083} {:x 3.3584436883313016, :y 8.0E-4} {:x 3.6376026455287365, :y 8.0E-4} {:x 3.9167616027261714, :y 0.0012} {:x 4.195920559923606, :y 0.0} {:x 4.475079517121041, :y 0.0098} {:x 4.754238474318476, :y 0.0048} {:x 5.033397431515911, :y 2.0E-4} {:x 5.312556388713346, :y 0.011} {:x 5.591715345910781, :y 0.0084} {:x 5.870874303108216, :y 0.006} {:x 6.1500332603056505, :y 0.0052} {:x 6.429192217503085, :y 1.0E-4} {:x 6.70835117470052, :y 0.002} {:x 6.987510131897955, :y 0.0037} {:x 7.26666908909539, :y 0.0018} {:x 7.545828046292825, :y 0.0303} {:x 7.82498700349026, :y 0.0291} {:x 8.104145960687696, :y 0.0366} {:x 8.38330491788513, :y 0.0263} {:x 8.662463875082565, :y 0.0421} {:x 8.94162283228, :y 0.0487} {:x 9.220781789477435, :y 0.0434} {:x 9.49994074667487, :y 0.1063} {:x 9.779099703872305, :y 0.0967} {:x 10.05825866106974, :y 0.2736} {:x 10.337417618267175, :y 0.0923} {:x 10.61657657546461, :y 0.0533} {:x 10.895735532662044, :y 0.0145} {:x 11.17489448985948, :y 0.0061} {:x 11.454053447056914, :y 0.0051} {:x 11.733212404254349, :y 0.0042} {:x 12.012371361451784, :y 0.0} {:x 12.291530318649219, :y 0.0013} {:x 12.570689275846654, :y 2.0E-4} {:x 12.849848233044089, :y 0.0} {:x 13.129007190241524, :y 0.0052} {:x 13.408166147438958, :y 0.0} {:x 13.687325104636393, :y 0.0018} {:x 13.966484061833828, :y 0.0} {:x 14.245643019031263, :y 0.0037} {:x 14.524801976228698, :y 0.0} {:x 14.803960933426133, :y 6.0E-4} {:x 15.083119890623568, :y 0.0025} {:x 15.362278847821003, :y 0})}], :marks [{:type \"line\", :from {:data \"8417d97d-ea3d-43c2-9624-3c55bd61766e\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :interpolate {:value \"step-before\"}, :fill {:value \"steelblue\"}, :fillOpacity {:value 0.4}, :stroke {:value \"steelblue\"}, :strokeWidth {:value 2}, :strokeOpacity {:value 1}}}}], :scales [{:name \"x\", :type \"linear\", :range \"width\", :zero false, :domain {:data \"8417d97d-ea3d-43c2-9624-3c55bd61766e\", :field \"data.x\"}} {:name \"y\", :type \"linear\", :range \"height\", :nice true, :zero false, :domain {:data \"8417d97d-ea3d-43c2-9624-3c55bd61766e\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}]}}"}
;; <=

;; @@
(def noise-estimate 
  (stat/mean (map :sigmas scientist-samples)))

(plot/bar-chart (range 1 8) noise-estimate)
;; @@
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"data":[{"name":"bf351357-d7f6-4f61-8880-99f7e9b081b9","values":[{"x":1,"y":20.26561417740721},{"x":2,"y":11.615240915165462},{"x":3,"y":8.497871750964958},{"x":4,"y":6.349088031770878},{"x":5,"y":6.796952487241856},{"x":6,"y":7.459956606232633},{"x":7,"y":6.761950502447058}]}],"marks":[{"type":"rect","from":{"data":"bf351357-d7f6-4f61-8880-99f7e9b081b9"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"width":{"scale":"x","band":true,"offset":-1},"y":{"scale":"y","field":"data.y"},"y2":{"scale":"y","value":0}},"update":{"fill":{"value":"steelblue"},"opacity":{"value":1}},"hover":{"fill":{"value":"#FF29D2"}}}}],"scales":[{"name":"x","type":"ordinal","range":"width","domain":{"data":"bf351357-d7f6-4f61-8880-99f7e9b081b9","field":"data.x"}},{"name":"y","range":"height","nice":true,"domain":{"data":"bf351357-d7f6-4f61-8880-99f7e9b081b9","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :data [{:name \"bf351357-d7f6-4f61-8880-99f7e9b081b9\", :values ({:x 1, :y 20.26561417740721} {:x 2, :y 11.615240915165462} {:x 3, :y 8.497871750964958} {:x 4, :y 6.349088031770878} {:x 5, :y 6.796952487241856} {:x 6, :y 7.459956606232633} {:x 7, :y 6.761950502447058})}], :marks [{:type \"rect\", :from {:data \"bf351357-d7f6-4f61-8880-99f7e9b081b9\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :width {:scale \"x\", :band true, :offset -1}, :y {:scale \"y\", :field \"data.y\"}, :y2 {:scale \"y\", :value 0}}, :update {:fill {:value \"steelblue\"}, :opacity {:value 1}}, :hover {:fill {:value \"#FF29D2\"}}}}], :scales [{:name \"x\", :type \"ordinal\", :range \"width\", :domain {:data \"bf351357-d7f6-4f61-8880-99f7e9b081b9\", :field \"data.x\"}} {:name \"y\", :range \"height\", :nice true, :domain {:data \"bf351357-d7f6-4f61-8880-99f7e9b081b9\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}]}}"}
;; <=

;; **
;;; * Are these noise levels what you would expect?
;;; * How sensitive is this to the prior on @@\mu@@ and @@\sigma\_i@@?
;; **

;; **
;;; 
;; **
