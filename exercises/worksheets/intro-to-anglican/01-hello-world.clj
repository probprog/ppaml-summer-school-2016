;; gorilla-repl.fileformat = 1

;; **
;;; # An Anglican Probabilistic Hello World
;;; 
;;; This file is a [Gorilla Repl](http://gorilla-repl.org/index.html) worksheet. This is a notebook format which allows writing Clojure (and Anglican) code in cells within a document. Conceptually this is quite similar to (e.g.) iPython notebooks.
;;; 
;;; Shift + enter evaluates a code block. Hit ctrl+g twice in quick succession or click the menu icon (upper-right corner) for more commands.
;; **

;; **
;;; This worksheet, written by
;;; 
;;; - Frank Wood : [fwood@robots.ox.ac.uk](mailto:fwood@robots.ox.ac.uk)
;;; - Brooks Paige : [brooks@robots.ox.ac.uk](mailto:brooks@robots.ox.ac.uk)
;;; - Jan-Willem van de Meent : [jwvdm@robots.ox.ac.uk](mailto:jwvdm@robots.ox.ac.uk)
;;; 
;;; is designed to introduce you to the basics of Clojure and Anglican.
;;; It introduces probabilistic programming inference in a "Hello World!" Beta-Bernoulli model, illustrates different ways of writing queries, and shows different ways of consuming their output.
;;; 
;;; 
;;; The following cell defines a _namespace_, and imports some functions we will need. This is a Clojure concept somewhat analogous to a class in Java, or a module in Python. For now, take this as given; we will supply necessary imports at the top of the document for all the examples.
;;; 
;;; Run it by clicking on it and hitting shift+enter. This block of code often takes 10 seconds or more to run while Clojure unpacks and initializes dependencies.
;;; 
;;; Output will appear just below the cell; in this case we expect `nil`.
;; **

;; @@
(ns hello-world
  (:require [gorilla-plot.core :as plot])
  (:use [anglican core runtime emit stat]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; ## Anglican overview
;;; 
;;; The Anglican system consists of three main components:
;;; 
;;; 1. A [language](http://www.robots.ox.ac.uk/~fwood/anglican/language/index.html) for defining probabilistic programs. This language implements a large subset of the language features in Clojure. We refer to an Anglican program as a *query*.
;;; 
;;; 2. An inference backend that implements a number of different [inference methods](http://www.robots.ox.ac.uk/~fwood/anglican/inference/index.html) for Anglican queries. 
;;; 
;;; 3. A [library](https://crossclj.info/ns/anglican/1.0.0/anglican.runtime.html) of functions such as basic math operations and constructors for *distribution* objects, all of which can be used both in Anglican and in Clojure programs.
;;; 
;;; 
;;; ## Distributions 
;;; 
;;; Anglican provides a number of distriubtion primitives to the language, for example `normal`. Calling `(normal mu std)`, with arguments `mu` and `std`, creates a _distribution object_. A distribution object can be used both in Clojure programs and in Anglican programs, but behaves a little differently in each case.
;;; 
;;; In a Clojure program, a distribution object implements two methods
;;; 
;;; 1. `sample*` generates a sample from the distribution. For example `(sample* (normal 0.0 1.0))` draws a standard normal random variate.
;;; 
;;; 2. `observe*` computes the log probability of a sample. For example `(observe (normal 0.0 1.0) 3.0)` returns the log probability of the value `3.0` under the distribution `(normal 0.0 1.0)`.
;;; 
;;; In an Anglican program, there are two *special forms* that interact with distribution objects
;;; 
;;; 1. `sample` asks the inference backend to generate a sample from the distribution. By default the backend does this by simply calling the Clojure function `sample*`. However in some inference algorithms the backend may resuse a previously sampled value, or sample from a learned proposal.
;;; 
;;; 2. `observe` askes the inference backend to update the log probability of the current execution according the the log probability that can be calculated using the Clojure function `observe*`.
;;; 
;;; Below are some example distribution primitives; these are sufficient to solve the exercises.  A full list of built-in primitives can be found [here](http://www.robots.ox.ac.uk/~fwood/anglican/language/index.html).
;; **

;; @@
;; Draw from a normal distribution with mean 1 and standard deviation 2:
(sample* (normal 1 2))

;; Flip a coin, which comes up `true` with probability 0.7, and false with probabilty 0.3:
(sample* (flip 0.7))

;; Sample from a uniform distribution on the open interval (3, 10):
(sample* (uniform-continuous 3 10))

;; Sample from a beta distribution with parameters a=2, b=3:
(sample* (beta 2 3))

;; Sample from a binomial distribution with n=10 and p=0.4:
(sample* (binomial 10 0.4))

;; Sample from a discrete distribution with probabilities [0.3 0.2 0.5] on 0, 1, 2:
(sample* (discrete [0.3 0.2 0.5]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}
;; <=

;; @@
;; `repeatedly` can be pretty useful, here.
;; Suppose we want to draw 10 samples from the same normal distribution:
(let [normal-dist (normal 1 2.2)]
  (repeatedly 10 (fn [] (sample* normal-dist))))

;; The # symbol can be used as a shorthand for function definition.
;; The same code as the previous line can also be written like so:
(let [normal-dist (normal 1 2.2)]
  (repeatedly 10 #(sample* normal-dist)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-double'>-0.6089063750002284</span>","value":"-0.6089063750002284"},{"type":"html","content":"<span class='clj-double'>1.5268745319621093</span>","value":"1.5268745319621093"},{"type":"html","content":"<span class='clj-double'>5.677406578535351</span>","value":"5.677406578535351"},{"type":"html","content":"<span class='clj-double'>1.1344290522840268</span>","value":"1.1344290522840268"},{"type":"html","content":"<span class='clj-double'>1.3688634899415546</span>","value":"1.3688634899415546"},{"type":"html","content":"<span class='clj-double'>-1.8668847052088822</span>","value":"-1.8668847052088822"},{"type":"html","content":"<span class='clj-double'>1.6997996979769423</span>","value":"1.6997996979769423"},{"type":"html","content":"<span class='clj-double'>2.5362611979408687</span>","value":"2.5362611979408687"},{"type":"html","content":"<span class='clj-double'>1.9843325729708183</span>","value":"1.9843325729708183"},{"type":"html","content":"<span class='clj-double'>-0.8164700554931028</span>","value":"-0.8164700554931028"}],"value":"(-0.6089063750002284 1.5268745319621093 5.677406578535351 1.1344290522840268 1.3688634899415546 -1.8668847052088822 1.6997996979769423 2.5362611979408687 1.9843325729708183 -0.8164700554931028)"}
;; <=

;; @@
;; Using observe: log p(x=3), where x ~ Normal(0, 1):
(observe* (normal 0 1) 3)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>-5.418938533204672</span>","value":"-5.418938533204672"}
;; <=

;; **
;;; ## A First Anglican Query
;; **

;; **
;;; Let's use Anglican (and what we've learned) to pose a simple statistical query under the model
;;; 
;;; $$\begin{align}\theta &\sim \mathrm{Beta}(5,3) \\\\
;;; y &\sim \mathrm{Bernoulli}(\theta)\end{align}$$
;;; 
;;; and ask 
;;; 
;;; $$p(\theta>0.7 | y = true).$$
;;; 
;;; For this we can easily look up and/or compute the ground truth = 
;;; 
;;; $$p(\theta>0.7 | y = true) = .448 = 1-\mathrm{betacdf}(6,3,0.7)$$
;;; 
;;; Probabilistic models written in Anglican are called `queries`, and are defined using `defquery`.
;;; 
;;; The following program defines the statistical model above:
;; **

;; @@
(defquery one-flip [y]
  (let [theta (sample (beta 5 3))]
    (observe (flip theta) y)
    (> theta 0.7)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;hello-world/one-flip</span>","value":"#'hello-world/one-flip"}
;; <=

;; **
;;; Take a moment to make sure that code block makes sense! `defquery` looks a lot like a function definition, except the contents of the `defquery` are actually Anglican code, which is then _compiled_ into a computable representation of the posterior (think sampler).
;;; 
;;; The query is named `one-flip`, and it takes a single argument `y`, which is the observed value.
;;; 
;;; The `let` block defines `theta` as a random sample from the distribution `(beta 5 3)`.
;;; 
;;; The `observe` statement asserts that we see `y` as data generated from `(flip theta)`.
;;; 
;;; The final statement defines the return value for the program, which is equal to the `true/false` value of the expression `(> theta 0.7)`.
;;; 
;;; Together, these four lines define our first Anglican program/query/model.
;; **

;; **
;;; ### Posterior sampling from queries
;;; 
;;; 
;;; The `conditional` function takes a query and returns a distribution object constructor (think of the returned object as a factory for conditional/parameterized distributions). It takes various optional arguments which are used to specify the algorithm used for posterior sampling. Sensible values for these are provided in all exercises, and all different options are described in the [inference algorithms documentation](http://www.robots.ox.ac.uk/~fwood/anglican/language/index.html).
;;; 
;;; The following line defines `one-flip-posterior` as a distribution constructor which will draw posterior samples from the distribution defined by our query above, using the Lightweight Metropolis-Hastings (`:lmh`) algorithm.
;; **

;; @@
(def one-flip-posterior (conditional one-flip :lmh))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;hello-world/one-flip-posterior</span>","value":"#'hello-world/one-flip-posterior"}
;; <=

;; **
;;; The object we just created plays the same role as `normal`, `flip`, or other built-in distribution constructors (except one can only `sample` but not `observe` from distributions created using `conditional`).
;;; 
;;; To actually create the posterior distribution itself, we create a distribution object which takes the query argument `outcome`. 
;;; 
;;; This is analogous to how when creating a normal distribution we must specify the mean and standard deviation, e.g. `(normal 0 1)`. Here, we specify whether our one outcome was true or false.
;; **

;; @@
(def true-flip-posterior (one-flip-posterior true))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;hello-world/true-flip-posterior</span>","value":"#'hello-world/true-flip-posterior"}
;; <=

;; **
;;; Now, we can draw samples just as we would draw samples from a distribution created by calling `(normal 0 1)`. A sample from a conditional distribution defined in this way returns a key-value map, where the keys are the same as those specified in the `predict` statements.
;;; 
;;; To index into a hashmap in Clojure, just use the key as a function.
;; **

;; @@
;; Draw one sample (returns true or false):
(sample* true-flip-posterior)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>true</span>","value":"true"}
;; <=

;; **
;;; Sampling repeatedly from this distribution object characterizes the distribution.
;;; 
;;; Here, we're using the clojure builtin `frequencies`, and drawing 1000 samples.
;; **

;; @@
(frequencies (repeatedly 
               1000
               #(sample* true-flip-posterior)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>true</span>","value":"true"},{"type":"html","content":"<span class='clj-long'>448</span>","value":"448"}],"value":"[true 448]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>false</span>","value":"false"},{"type":"html","content":"<span class='clj-long'>552</span>","value":"552"}],"value":"[false 552]"}],"value":"{true 448, false 552}"}
;; <=

;; **
;;; A rudimentary plotting capability comes as part of [Gorilla REPL](http://gorilla-repl.org/).  Here we use a histogram plot to show the estimated distribution.
;; **

;; @@
(plot/histogram 
  (->> (repeatedly 10000 #(sample* true-flip-posterior))
       (map (fn [x] (if x 1 0))))
  :bins 100 :normalize :probability)
;; @@
;; =>
;;; {"type":"vega","content":{"width":400,"height":247.2187957763672,"padding":{"top":10,"left":55,"bottom":40,"right":10},"data":[{"name":"f9d04e4e-5635-4b2f-9aeb-038d71768d9f","values":[{"x":0.0,"y":0},{"x":0.010000000000000002,"y":0.5446},{"x":0.020000000000000004,"y":0.0},{"x":0.030000000000000006,"y":0.0},{"x":0.04000000000000001,"y":0.0},{"x":0.05000000000000001,"y":0.0},{"x":0.06000000000000001,"y":0.0},{"x":0.07,"y":0.0},{"x":0.08000000000000002,"y":0.0},{"x":0.09000000000000002,"y":0.0},{"x":0.10000000000000003,"y":0.0},{"x":0.11000000000000004,"y":0.0},{"x":0.12000000000000005,"y":0.0},{"x":0.13000000000000006,"y":0.0},{"x":0.14000000000000007,"y":0.0},{"x":0.15000000000000008,"y":0.0},{"x":0.1600000000000001,"y":0.0},{"x":0.1700000000000001,"y":0.0},{"x":0.1800000000000001,"y":0.0},{"x":0.1900000000000001,"y":0.0},{"x":0.20000000000000012,"y":0.0},{"x":0.21000000000000013,"y":0.0},{"x":0.22000000000000014,"y":0.0},{"x":0.23000000000000015,"y":0.0},{"x":0.24000000000000016,"y":0.0},{"x":0.25000000000000017,"y":0.0},{"x":0.2600000000000002,"y":0.0},{"x":0.2700000000000002,"y":0.0},{"x":0.2800000000000002,"y":0.0},{"x":0.2900000000000002,"y":0.0},{"x":0.3000000000000002,"y":0.0},{"x":0.3100000000000002,"y":0.0},{"x":0.32000000000000023,"y":0.0},{"x":0.33000000000000024,"y":0.0},{"x":0.34000000000000025,"y":0.0},{"x":0.35000000000000026,"y":0.0},{"x":0.36000000000000026,"y":0.0},{"x":0.3700000000000003,"y":0.0},{"x":0.3800000000000003,"y":0.0},{"x":0.3900000000000003,"y":0.0},{"x":0.4000000000000003,"y":0.0},{"x":0.4100000000000003,"y":0.0},{"x":0.4200000000000003,"y":0.0},{"x":0.4300000000000003,"y":0.0},{"x":0.44000000000000034,"y":0.0},{"x":0.45000000000000034,"y":0.0},{"x":0.46000000000000035,"y":0.0},{"x":0.47000000000000036,"y":0.0},{"x":0.48000000000000037,"y":0.0},{"x":0.4900000000000004,"y":0.0},{"x":0.5000000000000003,"y":0.0},{"x":0.5100000000000003,"y":0.0},{"x":0.5200000000000004,"y":0.0},{"x":0.5300000000000004,"y":0.0},{"x":0.5400000000000004,"y":0.0},{"x":0.5500000000000004,"y":0.0},{"x":0.5600000000000004,"y":0.0},{"x":0.5700000000000004,"y":0.0},{"x":0.5800000000000004,"y":0.0},{"x":0.5900000000000004,"y":0.0},{"x":0.6000000000000004,"y":0.0},{"x":0.6100000000000004,"y":0.0},{"x":0.6200000000000004,"y":0.0},{"x":0.6300000000000004,"y":0.0},{"x":0.6400000000000005,"y":0.0},{"x":0.6500000000000005,"y":0.0},{"x":0.6600000000000005,"y":0.0},{"x":0.6700000000000005,"y":0.0},{"x":0.6800000000000005,"y":0.0},{"x":0.6900000000000005,"y":0.0},{"x":0.7000000000000005,"y":0.0},{"x":0.7100000000000005,"y":0.0},{"x":0.7200000000000005,"y":0.0},{"x":0.7300000000000005,"y":0.0},{"x":0.7400000000000005,"y":0.0},{"x":0.7500000000000006,"y":0.0},{"x":0.7600000000000006,"y":0.0},{"x":0.7700000000000006,"y":0.0},{"x":0.7800000000000006,"y":0.0},{"x":0.7900000000000006,"y":0.0},{"x":0.8000000000000006,"y":0.0},{"x":0.8100000000000006,"y":0.0},{"x":0.8200000000000006,"y":0.0},{"x":0.8300000000000006,"y":0.0},{"x":0.8400000000000006,"y":0.0},{"x":0.8500000000000006,"y":0.0},{"x":0.8600000000000007,"y":0.0},{"x":0.8700000000000007,"y":0.0},{"x":0.8800000000000007,"y":0.0},{"x":0.8900000000000007,"y":0.0},{"x":0.9000000000000007,"y":0.0},{"x":0.9100000000000007,"y":0.0},{"x":0.9200000000000007,"y":0.0},{"x":0.9300000000000007,"y":0.0},{"x":0.9400000000000007,"y":0.0},{"x":0.9500000000000007,"y":0.0},{"x":0.9600000000000007,"y":0.0},{"x":0.9700000000000008,"y":0.0},{"x":0.9800000000000008,"y":0.0},{"x":0.9900000000000008,"y":0.0},{"x":1.0000000000000007,"y":0.4554},{"x":1.0100000000000007,"y":0}]}],"marks":[{"type":"line","from":{"data":"f9d04e4e-5635-4b2f-9aeb-038d71768d9f"},"properties":{"enter":{"x":{"scale":"x","field":"data.x"},"y":{"scale":"y","field":"data.y"},"interpolate":{"value":"step-before"},"fill":{"value":"steelblue"},"fillOpacity":{"value":0.4},"stroke":{"value":"steelblue"},"strokeWidth":{"value":2},"strokeOpacity":{"value":1}}}}],"scales":[{"name":"x","type":"linear","range":"width","zero":false,"domain":{"data":"f9d04e4e-5635-4b2f-9aeb-038d71768d9f","field":"data.x"}},{"name":"y","type":"linear","range":"height","nice":true,"zero":false,"domain":{"data":"f9d04e4e-5635-4b2f-9aeb-038d71768d9f","field":"data.y"}}],"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}]},"value":"#gorilla_repl.vega.VegaView{:content {:width 400, :height 247.2188, :padding {:top 10, :left 55, :bottom 40, :right 10}, :data [{:name \"f9d04e4e-5635-4b2f-9aeb-038d71768d9f\", :values ({:x 0.0, :y 0} {:x 0.010000000000000002, :y 0.5446} {:x 0.020000000000000004, :y 0.0} {:x 0.030000000000000006, :y 0.0} {:x 0.04000000000000001, :y 0.0} {:x 0.05000000000000001, :y 0.0} {:x 0.06000000000000001, :y 0.0} {:x 0.07, :y 0.0} {:x 0.08000000000000002, :y 0.0} {:x 0.09000000000000002, :y 0.0} {:x 0.10000000000000003, :y 0.0} {:x 0.11000000000000004, :y 0.0} {:x 0.12000000000000005, :y 0.0} {:x 0.13000000000000006, :y 0.0} {:x 0.14000000000000007, :y 0.0} {:x 0.15000000000000008, :y 0.0} {:x 0.1600000000000001, :y 0.0} {:x 0.1700000000000001, :y 0.0} {:x 0.1800000000000001, :y 0.0} {:x 0.1900000000000001, :y 0.0} {:x 0.20000000000000012, :y 0.0} {:x 0.21000000000000013, :y 0.0} {:x 0.22000000000000014, :y 0.0} {:x 0.23000000000000015, :y 0.0} {:x 0.24000000000000016, :y 0.0} {:x 0.25000000000000017, :y 0.0} {:x 0.2600000000000002, :y 0.0} {:x 0.2700000000000002, :y 0.0} {:x 0.2800000000000002, :y 0.0} {:x 0.2900000000000002, :y 0.0} {:x 0.3000000000000002, :y 0.0} {:x 0.3100000000000002, :y 0.0} {:x 0.32000000000000023, :y 0.0} {:x 0.33000000000000024, :y 0.0} {:x 0.34000000000000025, :y 0.0} {:x 0.35000000000000026, :y 0.0} {:x 0.36000000000000026, :y 0.0} {:x 0.3700000000000003, :y 0.0} {:x 0.3800000000000003, :y 0.0} {:x 0.3900000000000003, :y 0.0} {:x 0.4000000000000003, :y 0.0} {:x 0.4100000000000003, :y 0.0} {:x 0.4200000000000003, :y 0.0} {:x 0.4300000000000003, :y 0.0} {:x 0.44000000000000034, :y 0.0} {:x 0.45000000000000034, :y 0.0} {:x 0.46000000000000035, :y 0.0} {:x 0.47000000000000036, :y 0.0} {:x 0.48000000000000037, :y 0.0} {:x 0.4900000000000004, :y 0.0} {:x 0.5000000000000003, :y 0.0} {:x 0.5100000000000003, :y 0.0} {:x 0.5200000000000004, :y 0.0} {:x 0.5300000000000004, :y 0.0} {:x 0.5400000000000004, :y 0.0} {:x 0.5500000000000004, :y 0.0} {:x 0.5600000000000004, :y 0.0} {:x 0.5700000000000004, :y 0.0} {:x 0.5800000000000004, :y 0.0} {:x 0.5900000000000004, :y 0.0} {:x 0.6000000000000004, :y 0.0} {:x 0.6100000000000004, :y 0.0} {:x 0.6200000000000004, :y 0.0} {:x 0.6300000000000004, :y 0.0} {:x 0.6400000000000005, :y 0.0} {:x 0.6500000000000005, :y 0.0} {:x 0.6600000000000005, :y 0.0} {:x 0.6700000000000005, :y 0.0} {:x 0.6800000000000005, :y 0.0} {:x 0.6900000000000005, :y 0.0} {:x 0.7000000000000005, :y 0.0} {:x 0.7100000000000005, :y 0.0} {:x 0.7200000000000005, :y 0.0} {:x 0.7300000000000005, :y 0.0} {:x 0.7400000000000005, :y 0.0} {:x 0.7500000000000006, :y 0.0} {:x 0.7600000000000006, :y 0.0} {:x 0.7700000000000006, :y 0.0} {:x 0.7800000000000006, :y 0.0} {:x 0.7900000000000006, :y 0.0} {:x 0.8000000000000006, :y 0.0} {:x 0.8100000000000006, :y 0.0} {:x 0.8200000000000006, :y 0.0} {:x 0.8300000000000006, :y 0.0} {:x 0.8400000000000006, :y 0.0} {:x 0.8500000000000006, :y 0.0} {:x 0.8600000000000007, :y 0.0} {:x 0.8700000000000007, :y 0.0} {:x 0.8800000000000007, :y 0.0} {:x 0.8900000000000007, :y 0.0} {:x 0.9000000000000007, :y 0.0} {:x 0.9100000000000007, :y 0.0} {:x 0.9200000000000007, :y 0.0} {:x 0.9300000000000007, :y 0.0} {:x 0.9400000000000007, :y 0.0} {:x 0.9500000000000007, :y 0.0} {:x 0.9600000000000007, :y 0.0} {:x 0.9700000000000008, :y 0.0} {:x 0.9800000000000008, :y 0.0} {:x 0.9900000000000008, :y 0.0} {:x 1.0000000000000007, :y 0.4554} {:x 1.0100000000000007, :y 0})}], :marks [{:type \"line\", :from {:data \"f9d04e4e-5635-4b2f-9aeb-038d71768d9f\"}, :properties {:enter {:x {:scale \"x\", :field \"data.x\"}, :y {:scale \"y\", :field \"data.y\"}, :interpolate {:value \"step-before\"}, :fill {:value \"steelblue\"}, :fillOpacity {:value 0.4}, :stroke {:value \"steelblue\"}, :strokeWidth {:value 2}, :strokeOpacity {:value 1}}}}], :scales [{:name \"x\", :type \"linear\", :range \"width\", :zero false, :domain {:data \"f9d04e4e-5635-4b2f-9aeb-038d71768d9f\", :field \"data.x\"}} {:name \"y\", :type \"linear\", :range \"height\", :nice true, :zero false, :domain {:data \"f9d04e4e-5635-4b2f-9aeb-038d71768d9f\", :field \"data.y\"}}], :axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}]}}"}
;; <=

;; **
;;; ## A Second Query: Multiple Observes
;;; 
;;; How would we modify this model to return, instead of a one-flip posterior, the posterior distribution given a sequence of flips? That is, we keep the basic model
;;; 
;;; $$\begin{align}\theta &\sim \mathrm{Beta}(5,3) \\\\
;;; y\_i &\sim \mathrm{Bernoulli}(\theta)\end{align}$$
;;; 
;;; and ask 
;;; 
;;; $$p(\theta>0.7 | x\_i)$$
;;; 
;;; for some sequence @@x\_i@@. Now, we let `outcomes`, the argument to our query, be a sequence, and we can use `map` (or `loop` and `recur`) to `observe` all different outcomes.
;;; 
;;; Here's one way of writing this:
;; **

;; @@
(defquery many-flips [y-values]
  (let [theta (sample (beta 5 3))
        outcome-dist (flip theta)]
    (map (fn [y] 
           (observe outcome-dist y)) 
         y-values)
    (> theta 0.7)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;hello-world/many-flips</span>","value":"#'hello-world/many-flips"}
;; <=

;; **
;;; We can use `conditional` to estimate the posterior distribution of @@\theta > 0.7@@ given the sequence `[true, false, false, true]`, just as before (the analytical answer is 0.21).
;; **

;; @@
(def many-flip-posterior (conditional many-flips :lmh))


(frequencies 
  (repeatedly 1000 
              #(sample* (many-flip-posterior [true false false true]))))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>false</span>","value":"false"},{"type":"html","content":"<span class='clj-long'>785</span>","value":"785"}],"value":"[false 785]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>true</span>","value":"true"},{"type":"html","content":"<span class='clj-long'>215</span>","value":"215"}],"value":"[true 215]"}],"value":"{false 785, true 215}"}
;; <=

;; **
;;; That's it! Now move onto the exercises. Keep this worksheet open in a separate tab or window, and refer to it for language reference.
;; **

;; **
;;; ## Advanced usage (not necessary for exercises)
;;; 
;;; If you are familiar with sampling techniques both in general and in Anglican you may directly interact with the sampler output that is hidden behind the `conditional` function. Bearing in mind that some samplers return _weighted samples_ which should be accounted for in subsequent use and some will return samples with weight `-Infinity` indicating that not all constraints have been satisfied yet; `conditional` does this and more under the covers
;;; 
;; **

;; @@
(def one-flip-samples
  (doquery :importance one-flip [true]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;hello-world/one-flip-samples</span>","value":"#'hello-world/one-flip-samples"}
;; <=

;; **
;;; This command defines an infinite lazy sequence of samples. Each sample is a hashmap with three entries
;; **

;; @@
(first one-flip-samples)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:log-weight</span>","value":":log-weight"},{"type":"html","content":"<span class='clj-double'>-1.732447605679859</span>","value":"-1.732447605679859"}],"value":"[:log-weight -1.732447605679859]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:result</span>","value":":result"},{"type":"html","content":"<span class='clj-unkown'>false</span>","value":"false"}],"value":"[:result false]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:predicts</span>","value":":predicts"},{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[],"value":"[]"}],"value":"[:predicts []]"}],"value":"{:log-weight -1.732447605679859, :result false, :predicts []}"}
;; <=

;; **
;;; The `:result` entry contains the return value for each program execution. The `:log-weight` entry contains the corresponding log probability. We will return to the `:predict` value in a moment. 
;;; 
;;; You can now get a posterior estimate, by extracting the `:result` value from each sample, e.g.	
;; **

;; @@
(frequencies
  (map :result
       (take 1000 one-flip-samples)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>false</span>","value":"false"},{"type":"html","content":"<span class='clj-long'>662</span>","value":"662"}],"value":"[false 662]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>true</span>","value":"true"},{"type":"html","content":"<span class='clj-long'>338</span>","value":"338"}],"value":"[true 338]"}],"value":"{false 662, true 338}"}
;; <=

;; **
;;; You can also use the `collect-by` helper function to calculate the cumulative log weight associated with each unique return value in a sequence of samples
;; **

;; @@
(require '[anglican.inference :refer [collect-by]])

(collect-by :result 
            (take 10000 one-flip-samples))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>false</span>","value":"false"},{"type":"html","content":"<span class='clj-double'>-1.0527953790953903</span>","value":"-1.0527953790953903"}],"value":"[false -1.0527953790953903]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>true</span>","value":"true"},{"type":"html","content":"<span class='clj-double'>-1.2902037253292633</span>","value":"-1.2902037253292633"}],"value":"[true -1.2902037253292633]"}],"value":"{false -1.0527953790953903, true -1.2902037253292633}"}
;; <=

;; **
;;; The output of `collect-by` is a map `{value log-weight}`, which can be post-processed using functions in `anglican.stat`. For example we use `empirical-distribution` to normalize log weights into probabilities that sum to 1.0:
;; **

;; @@
(require '[anglican.stat :refer [empirical-distribution]])

(empirical-distribution
  (collect-by :result 
              (take 10000 one-flip-samples)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>false</span>","value":"false"},{"type":"html","content":"<span class='clj-double'>0.5590748784403028</span>","value":"0.5590748784403028"}],"value":"[false 0.5590748784403028]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>true</span>","value":"true"},{"type":"html","content":"<span class='clj-double'>0.4409251215596972</span>","value":"0.4409251215596972"}],"value":"[true 0.4409251215596972]"}],"value":"{false 0.5590748784403028, true 0.4409251215596972}"}
;; <=

;; **
;;; Finally, you can use the `(predict ...)` special form to define additional outputs in the program. Unlike the `:result` value, these outputs do not have to be define at the end of the program. For example:
;; **

;; @@
(defquery many-flips [y-values]
  (let [theta (sample (beta 5 3))
        outcome-dist (flip theta)]
    (predict :theta theta)
    (map (fn [y] 
           (observe outcome-dist y)) 
         y-values)
    (> theta 0.7)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;hello-world/many-flips</span>","value":"#'hello-world/many-flips"}
;; <=

;; **
;;; You will now see an entry under `:predicts` in the returned samples
;; **

;; @@
(first 
  (doquery :importance many-flips 
           [[true true true]]))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:log-weight</span>","value":":log-weight"},{"type":"html","content":"<span class='clj-double'>-1.9289274005111863</span>","value":"-1.9289274005111863"}],"value":"[:log-weight -1.9289274005111863]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:result</span>","value":":result"},{"type":"html","content":"<span class='clj-unkown'>false</span>","value":"false"}],"value":"[:result false]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:predicts</span>","value":":predicts"},{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:theta</span>","value":":theta"},{"type":"html","content":"<span class='clj-double'>0.5257256395327461</span>","value":"0.5257256395327461"}],"value":"[:theta 0.5257256395327461]"}],"value":"[[:theta 0.5257256395327461]]"}],"value":"[:predicts [[:theta 0.5257256395327461]]]"}],"value":"{:log-weight -1.9289274005111863, :result false, :predicts [[:theta 0.5257256395327461]]}"}
;; <=

;; **
;;; Anglican provides a function called `get-predicts`, in `anglican.state`, which turns the predicts as a hashmap
;; **

;; @@
(require '[anglican.state :refer [get-predicts]])

(get-predicts
  (first 
    (doquery :importance 
             many-flips [[true true true]])))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:theta</span>","value":":theta"},{"type":"html","content":"<span class='clj-double'>0.7972006043653663</span>","value":"0.7972006043653663"}],"value":"[:theta 0.7972006043653663]"}],"value":"{:theta 0.7972006043653663}"}
;; <=
