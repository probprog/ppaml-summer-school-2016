;; gorilla-repl.fileformat = 1

;; **
;;; # Bonus Exercise 2: Schelling coordination game
;;; 
;;; In this example, we will explore using mutually recursive inference to model theory of mind.
;;; 
;;; Authors:
;;; 
;;;  - Brooks Paige [brooks@robots.ox.ac.uk](mailto:brooks@robots.ox.ac.uk)
;;;  - Frank Wood [fwood@robots.ox.ac.uk](mailto:fwood@robots.ox.ac.uk)
;; **

;; @@
(ns coordination-game
  (:require [anglican.importance]
            [anglican.stat :as stat])
  (:use [anglican core emit runtime]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; Amy and Bob want to meet up -- but they have no way of communicating ahead of time whether to go to the local pub, or to the Starbucks. They would both mildly prefer the pub, selecting it with probability 0.6.
;;; 
;;; * Where do they go?
;;; * Do they meet up?
;;; 
;;; This is an example of a [coordination game](https://en.wikipedia.org/wiki/Coordination_game).
;;; This particular formulation follows two example models at [forestdb.org](http://forestdb.org).
;;; 
;;; 
;;; We'll also use this example to explore additional Anglican language features.
;;; The distribution objects included in Anglican (for example `normal` and `flip`), are written in Clojure and implement the methods `sample` and `observe`.
;;; A call to `sample` should draw a random variate, and a call to `observe` should return a log-probability.
;;; 
;;; There's a macro shortcut `defdist` which can be used to define additional distributions.
;;; For example, we can create a distribution object in Anglican representing the prior distribution over their location preferences, which takes a `pub-preference` parameter between 0 and 1:
;; **

;; @@
(defdist location [pub-preference] []
  (sample* [this] 
          (if (sample* (flip pub-preference)) 
            :pub 
            :starbucks))
  (observe* [this value] 
           (observe* (flip pub-preference) 
                     (= value :pub))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>#multifn[print-method 0x13e7f685]</span>","value":"#multifn[print-method 0x13e7f685]"}
;; <=

;; **
;;; We can then sample from this distribution just as any of the Anglican builtins:
;; **

;; @@
(repeatedly 10 #(sample* (location 0.6)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-keyword'>:starbucks</span>","value":":starbucks"},{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"}],"value":"(:pub :pub :pub :pub :pub :pub :pub :starbucks :pub :pub)"}
;; <=

;; **
;;; If they both choose at random, where do they end up? How often do they meet each other?
;;; 
;;; We can write this as an Anglican query. The new distribution constructor `location` must be included as a primitive procedure:
;; **

;; @@
(with-primitive-procedures [location]
  (defquery meet-by-chance []
    (let [amy-location (sample (location 0.6))
          bob-location (sample (location 0.6))]
      {:amy amy-location
       :meet (= amy-location bob-location)})))

(def samples
  (repeatedly 1000
              #(sample* ((conditional meet-by-chance)))))

(println "p(Amy at Pub) ="
         (mean (map #(if (= (:amy %) :pub) 1.0 0.0)
                    samples)))


(println "p(Amy meets Bob) ="
         (mean (map #(if (:meet %) 1.0 0.0) 
                    samples)))
;; @@
;; ->
;;; p(Amy at Pub) = 0.5740000000000001
;;; p(Amy meets Bob) = 0.527
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; ## Rejection sampling and hard constraints
;;; 
;;; What if we wanted to consider the posterior probability of the two being at the pub (the best possible outcome for both of them), conditioned on them successfully meeting up?
;;; 
;;; One way of writing this query would involve effectively adding a hard constraint.
;;; Adding hard constraints makes inference difficult, as we are then effectively performing rejection sampling;
;;; this is not recommended!
;;; 
;;; We can add a hard constraint with `(observe (flip 1.0) value)`, which has log-probability 0 when `value` is `true`, and log-probability `-Infinity` when `value` is `false`.
;;; 
;;; Here's an inefficient way of writing this query:
;;; 
;; **

;; @@
(with-primitive-procedures [location]
  (defquery meet-hard-constraint []
    (let [amy-location (sample (location 0.6))
          bob-location (sample (location 0.6))]
      (observe (flip 1.0) (= amy-location bob-location))
      amy-location)))

(def samples
  (repeatedly 1000
              #(sample* ((conditional meet-hard-constraint)))))

(frequencies samples)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-long'>643</span>","value":"643"}],"value":"[:pub 643]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:starbucks</span>","value":":starbucks"},{"type":"html","content":"<span class='clj-long'>357</span>","value":"357"}],"value":"[:starbucks 357]"}],"value":"{:pub 643, :starbucks 357}"}
;; <=

;; **
;;; This model is simple enough that rejection sampling is feasible: we get a valid sample with p(Both at same location), which we computed above. However, for more complicated queries this quickly becomes unreasonable.
;;; 
;;; Fortunately, we can re-write the model above such that it no longer has a hard constraint. Instead of sampling both values and having a deterministic observation, we can `observe` instead of `sample` one of the locations directly.
;;; 
;;; Exercise: convince yourself that both these programs define the exact same distribution over the result value.
;; **

;; @@
(with-primitive-procedures [location]
  (defquery meet-soft-constraint []
    (let [amy-location (sample (location 0.6))
          bob-location-dist (location 0.6)]
      (observe bob-location-dist amy-location)
      amy-location)))

(def samples
  (repeatedly 1000
              #(sample* ((conditional meet-soft-constraint)))))

(frequencies samples)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:starbucks</span>","value":":starbucks"},{"type":"html","content":"<span class='clj-long'>327</span>","value":"327"}],"value":"[:starbucks 327]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:pub</span>","value":":pub"},{"type":"html","content":"<span class='clj-long'>673</span>","value":"673"}],"value":"[:pub 673]"}],"value":"{:starbucks 327, :pub 673}"}
;; <=

;; **
;;; The true distribution here can be computed analytically. The probability they both go to the pub is 0.36, and the probability that they both go to the starbucks is 0.16, so:
;;; 
;;; $$p(pub|meet) = \frac{0.36}{0.16 + 0.36} = 0.6923$$
;;; 
;;; Whenever possible, performing this sort of transformation on queries to avoid specifying programs with rejection sample semantics is highly recommended.
;; **

;; **
;;; ## A mutually recursive model
;;; 
;;; Now, suppose Amy and Bob are a bit more clever about it. Rather than choosing independently at random, they each choose by simulating what they think the other would do.
;;; 
;;; We do this by defining mutually recursive functions for sampling the location of Amy and Bob, conditioned on what they each think the other is thinking. Roughly:
;;; 
;;; * Amy wants to go where Bob goes
;;; * Amy knows Bob wants to go where Amy goes
;;; * Amy knows Bob knows that Amy wants to go where Bob goes
;;; 
;;; and so on. This recursion would continue indefinitely, so we add a `depth` parameter explicitly limiting the amount of meta-reasoning which takes place.
;;; 
;;; In the base case of the recursion, one of the actors (here, Bob) eventually chooses to simply pick a location.
;;; 
;;; The behaviour of the two actors are defined in the Anglican functions `amy` and `bob`, which each call one another, and return a location. We use the same reparameterization trick as above to specify our model in a way which avoids conditioning on hard constraints.
;; **

;; @@
(declare simulate-amy simulate-bob)

(with-primitive-procedures [location]
  (defm simulate-amy [depth]
    (let [amy-location-dist (location 0.6)
          bob-location (simulate-bob (dec depth))]
      (observe amy-location-dist bob-location)
      bob-location))

  (defm simulate-bob [depth]
    (let [bob-location-dist (location 0.6)]
      (if (> depth 0)
        (let [amy-location (simulate-amy depth)]
          (observe bob-location-dist amy-location)
          amy-location)
        (sample bob-location-dist)))))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;coordination-game/simulate-bob</span>","value":"#'coordination-game/simulate-bob"}
;; <=

;; **
;;; Here is a helper function which runs both Amy's and Bob's location selection procedures for a given recursion depth. We want to predict:
;;; 
;;; * What is the probability Amy is at the pub?
;;; * What is the probability Bob is at the pub?
;;; 
;;; The answers to these will vary according to the supplied `meta-reasoning-depth`:
;; **

;; @@
(defn coordinate [meta-reasoning-depth]
  (let [N 1000
        amy-conditional (conditional (query [depth] (simulate-amy depth)) :lmh)
        bob-conditional (conditional (query [depth] (simulate-bob depth)) :lmh)
        amy-dist (amy-conditional meta-reasoning-depth)
        bob-dist (bob-conditional meta-reasoning-depth)
        amy-samples (repeatedly N #(sample* amy-dist))
        bob-samples (repeatedly N #(sample* bob-dist))
        pub-probability (fn [outcomes] (/ (count (filter #(= :pub %) outcomes)) (float N)))]
    (println "recursion depth: " meta-reasoning-depth)
    (println "p(Amy at pub): " (pub-probability amy-samples))
    (println "p(Bob at pub): " (pub-probability bob-samples))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;coordination-game/coordinate</span>","value":"#'coordination-game/coordinate"}
;; <=

;; **
;;; At a recursion depth of zero, Bob just picks at random, so is at the pub with probabilty 0.6.
;;; Amy knows this, so is more likely to go to the pub herself:
;; **

;; @@
(def depth 0)
(coordinate depth)
;; @@
;; ->
;;; recursion depth:  0
;;; p(Amy at pub):  0.702
;;; p(Bob at pub):  0.586
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; At a recursion depth of 1, Bob also reasons about what Amy will do.
;;; Bob is now much more likely to go to the pub, too:
;; **

;; @@
(def depth 1)
(coordinate depth)
;; @@
;; ->
;;; recursion depth:  1
;;; p(Amy at pub):  0.721
;;; p(Bob at pub):  0.741
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; At higher recursion depths, they both become more likely to go to the pub.
;;; By depth 3, this is greater than 90% for both of them: as they both "think harder", they are more likely to reach the best outcome -- both going to the pub:
;; **

;; @@
(def depth 2)
(coordinate depth)

(def depth 3)
(coordinate depth)
;; @@
;; ->
;;; recursion depth:  2
;;; p(Amy at pub):  0.842
;;; p(Bob at pub):  0.878
;;; recursion depth:  3
;;; p(Amy at pub):  0.914
;;; p(Bob at pub):  0.954
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; ## Exercise: Coordination game with false beliefs
;;; 
;;; Now consider a modification of this coordination game:
;;; 
;;; Suppose Amy is actually trying to avoid Bob, but Bob doesn't know this, and Amy knows that Bob doesn't know this.
;;; 
;;; We can write this as an Anglican program where Bob simulates from an incorrect model for Amy, using the `amy-true-model`, `amy-false-model`, and `bob-confused` functions below:
;; **

;; @@
(declare simulate-true-amy simulate-false-amy simulate-true-bob)

(with-primitive-procedures [location]
  (defm simulate-true-amy [depth]
    (let [amy-location-dist (location 0.6)
          bob-location (simulate-true-bob (dec depth))
          not-bob-location (if (= bob-location :pub)
                             :starbucks
                             :pub)]
      (observe amy-location-dist not-bob-location)
      not-bob-location))

  (defm simulate-false-amy [depth]
    (let [amy-location-dist (location 0.6)
          bob-location (simulate-true-bob (dec depth))]
      (observe amy-location-dist bob-location)
      bob-location))

  (defm simulate-true-bob [depth]
    (let [bob-location-dist (location 0.6)]
      (if (> depth 0)
        (let [amy-location (simulate-false-amy depth)]
          (observe bob-location-dist amy-location)
          amy-location)
        (sample bob-location-dist)))))


;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;coordination-game/simulate-true-bob</span>","value":"#'coordination-game/simulate-true-bob"}
;; <=

;; **
;;; Here is a similar helper function as before, to estimate the probability of each of them selecting the pub:
;; **

;; @@
(defn coordinate-false-belief [meta-reasoning-depth]
  (let [N 1000
        amy-conditional (conditional (query [depth] (simulate-true-amy depth)) :lmh)
        bob-conditional (conditional (query [depth] (simulate-true-bob depth)) :lmh)
        amy-dist (amy-conditional meta-reasoning-depth)
        bob-dist (bob-conditional meta-reasoning-depth)
        amy-samples (repeatedly N #(sample* amy-dist))
        bob-samples (repeatedly N #(sample* bob-dist))
        pub-probability (fn [outcomes] (/ (count (filter #(= :pub %) outcomes)) (float N)))]
    (println "recursion depth: " meta-reasoning-depth)
    (println "p(Amy at pub): " (pub-probability amy-samples))
    (println "p(Bob at pub): " (pub-probability bob-samples))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;coordination-game/coordinate-false-belief</span>","value":"#'coordination-game/coordinate-false-belief"}
;; <=

;; **
;;; The more meta-reasoning involved, the more likely Amy will go to Starbucks (and therefore successfully dodge Bob), and the more Bob will believe that going to the pub will lead to meeting Amy:
;; **

;; @@
(coordinate-false-belief 0)
(coordinate-false-belief 1)
(coordinate-false-belief 2)
(coordinate-false-belief 3)
;; @@
;; ->
;;; recursion depth:  0
;;; p(Amy at pub):  0.497
;;; p(Bob at pub):  0.598
;;; recursion depth:  1
;;; p(Amy at pub):  0.497
;;; p(Bob at pub):  0.754
;;; recursion depth:  2
;;; p(Amy at pub):  0.307
;;; p(Bob at pub):  0.891
;;; recursion depth:  3
;;; p(Amy at pub):  0.143
;;; p(Bob at pub):  0.947
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
