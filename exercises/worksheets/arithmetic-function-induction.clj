;; gorilla-repl.fileformat = 1

;; **
;;; # Arithmetic functions induction
;; **

;; @@
(ns arithmetic-functions
   (:require [clojure.repl :as repl])
   (:use [anglican core emit runtime]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(defn get-int-constant [] 
  (sample (uniform-discrete 0 10)))

(defn safe-div [x y] (if (= y 0) 0 (/ x y)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;arithmetic-functions/safe-div</span>","value":"#'arithmetic-functions/safe-div"}
;; <=

;; @@
(def operations ['+ '- '* 'safe-div])


(defn make-fn [body-expr]
  (list 'fn ['x] body-expr))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;arithmetic-functions/make-fn</span>","value":"#'arithmetic-functions/make-fn"}
;; <=

;; **
;;; Simple version: no recursion, single operation
;; **

;; @@
(defn sample-operation [] 
  (get operations (sample (uniform-discrete 0 (count operations)))))

(defn sample-symbol []
  (if (sample (flip 0.5)) (get-int-constant) 'x))

(defn sample-from-prior []
  (list (sample-operation) (sample-symbol) (sample-symbol)))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;arithmetic-functions/sample-from-prior</span>","value":"#'arithmetic-functions/sample-from-prior"}
;; <=

;; @@
(make-fn (sample-from-prior))
(make-fn (sample-from-prior))
(make-fn (sample-from-prior))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>fn</span>","value":"fn"},{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>x</span>","value":"x"}],"value":"[x]"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>safe-div</span>","value":"safe-div"},{"type":"html","content":"<span class='clj-symbol'>x</span>","value":"x"},{"type":"html","content":"<span class='clj-unkown'>6</span>","value":"6"}],"value":"(safe-div x 6)"}],"value":"(fn [x] (safe-div x 6))"}
;; <=

;; @@
(defn logp-simple [body]
  (let [lp-operator (log 0.5)
        lp-lhs (if (number? (nth body 1)) (+ (log 0.5) (log 0.1)) (log 0.5))
        lp-rhs (if (number? (nth body 2)) (+ (log 0.5) (log 0.1)) (log 0.5))]
    (+ lp-operator lp-lhs lp-rhs)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;arithmetic-functions/logp-simple</span>","value":"#'arithmetic-functions/logp-simple"}
;; <=

;; @@
(defn propose [body]
  (let [num-choices (+ 3 
                       (if (number? (nth body 1)) 1 0)
                       (if (number? (nth body 2)) 1 0))
        which-choice (sample (uniform-discrete 0 num-choices))
        proposal (cond (= which-choice 0) (list (sample-operation) (nth body 1) (nth body 2))
                       (= which-choice 1) (list (nth body 0) (sample-symbol) (nth body 2))
                       (= which-choice 2) (list (nth body 0) (nth body 1) (sample-symbol))
                       (and (= which-choice 3) (number? (nth body 1))) (list (nth body 0) (get-int-constant) (nth body 2))
                       :else (list (nth body 0) (nth body 1) (get-int-constant)))]
    proposal))

;; TODO: this needs to also compute log probability of the proposal
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;arithmetic-functions/propose</span>","value":"#'arithmetic-functions/propose"}
;; <=

;; @@
(let [init (sample-from-prior)]
  (println init)
  (println (propose init)))
;; @@
;; ->
;;; (safe-div x x)
;;; (- x x)
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; Full (recursive) production rules:
;; **

;; @@
(defn productions []
  (let [expression-type (sample (discrete [0.4 0.3 0.3]))]
    (cond (= expression-type 0) (get-int-constant)
          (= expression-type 1) 'x
          :else 
          (let [operation (get operations (sample (uniform-discrete 0 (count operations))))]
            (list operation (productions) (productions))))))


(let [expr (list 'fn ['x] (productions))]
  (print expr)
  ((eval expr) 1))
;; @@
;; ->
;;; (fn [x] (+ x (* (* x (+ 8 7)) (* (safe-div x 4) (* 1 (* x (- (* (* (* 6 9) 2) 2) 0)))))))
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-bigint'>811N</span>","value":"811N"}
;; <=

;; **
;;; ## In Anglican
;; **

;; @@
(defdist noisy-eval 
  "noisy evaluation of quoted functions"
  [code input] [compiled-fn (eval code)
                noise-dist (normal 0 0.01)]
  (sample [this] (+ (compiled-fn input) (sample noise-dist)))
  (observe [this value] (observe noise-dist (- (compiled-fn input) value))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>#object[clojure.lang.MultiFn 0x5435957b &quot;clojure.lang.MultiFn@5435957b&quot;]</span>","value":"#object[clojure.lang.MultiFn 0x5435957b \"clojure.lang.MultiFn@5435957b\"]"}
;; <=

;; @@

(defm productions-defm []
  (let [expression-type (sample (discrete [0.4 0.3 0.3]))]
    (cond (= expression-type 0) (sample (uniform-discrete 0 10))
          (= expression-type 1) 'x
          :else 
          (let [operation (get operations (sample (uniform-discrete 0 (count operations))))]
            (list operation (productions-defm) (productions-defm))))))

(defm gen-proc-defm [] (list 'fn ['x] (productions-defm)))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;arithmetic-functions/gen-proc-defm</span>","value":"#'arithmetic-functions/gen-proc-defm"}
;; <=

;; @@
(with-primitive-procedures [noisy-eval]
  (defquery induce-procedure [ins outs]
    (let [proc (gen-proc-defm)]
      (loop [ins ins
             outs outs]
        (if (empty? ins)
          (predict :procedure proc)
          (do
            (observe (noisy-eval proc (first ins)) (first outs))
            (recur (rest ins) (rest outs))))))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;arithmetic-functions/induce-procedure</span>","value":"#'arithmetic-functions/induce-procedure"}
;; <=

;; **
;;; Simple example works:
;;; 
;;; (correct answer is `(fn [x] (+ x 1))`)
;;; 
;; **

;; @@
(time (->> (doquery :lmh induce-procedure [[1 2 3] [2 3 4]])
    (drop 5000)
    first))
;; @@
;; ->
;;; &quot;Elapsed time: 10470.855 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:log-weight</span>","value":":log-weight"},{"type":"html","content":"<span class='clj-double'>0.0</span>","value":"0.0"}],"value":"[:log-weight 0.0]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:predicts</span>","value":":predicts"},{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:procedure</span>","value":":procedure"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>fn</span>","value":"fn"},{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>x</span>","value":"x"}],"value":"[x]"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>+</span>","value":"+"},{"type":"html","content":"<span class='clj-symbol'>x</span>","value":"x"},{"type":"html","content":"<span class='clj-unkown'>1</span>","value":"1"}],"value":"(+ x 1)"}],"value":"(fn [x] (+ x 1))"}],"value":"[:procedure (fn [x] (+ x 1))]"}],"value":"[[:procedure (fn [x] (+ x 1))]]"}],"value":"[:predicts [[:procedure (fn [x] (+ x 1))]]]"}],"value":"{:log-weight 0.0, :predicts [[:procedure (fn [x] (+ x 1))]]}"}
;; <=

;; **
;;; Harder example very very rarely finds something reasonable:
;;; 
;;; (correct answer is something like `(fn [x] (- 7 (* 2 x)))`)
;; **

;; @@
(def est-fn
  (time (->> (doquery :lmh induce-procedure [[1 2 3] [5 3 1]])
    (drop 5000)
	first)))

(:procedure (into {} (:predicts est-fn)))
;; @@
;; ->
;;; &quot;Elapsed time: 10441.103 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>fn</span>","value":"fn"},{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>x</span>","value":"x"}],"value":"[x]"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>+</span>","value":"+"},{"type":"html","content":"<span class='clj-symbol'>x</span>","value":"x"},{"type":"html","content":"<span class='clj-symbol'>x</span>","value":"x"}],"value":"(+ x x)"}],"value":"(fn [x] (+ x x))"}
;; <=

;; @@
(def predicted-proc (:procedure (into {} (:predicts est-fn))))

(println "Inputs:" [1 2 3])
(println "Target outputs:" [5 3 1])
(println "Procedure:" predicted-proc)
(println "Estimated outputs:" (mapv float (map (eval predicted-proc) [1 2 3])))
;; @@
;; ->
;;; Inputs: [1 2 3]
;;; Target outputs: [5 3 1]
;;; Procedure: (fn [x] (+ x x))
;;; Estimated outputs: [2.0 4.0 6.0]
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@

;; @@
