;; gorilla-repl.fileformat = 1

;; **
;;; # Clojure Exercises
;; **

;; @@
(ns clojure-exercises
  (:use [anglican.runtime]))

(def ...complete-this... nil)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/...complete-this...</span>","value":"#'clojure-exercises/...complete-this..."}
;; <=

;; **
;;; ## Exercise 1: Summing values
;;; 
;;; If you've gone through the `getting-started.clj` worksheet, you will have seen the `loop` construct. We will use `loop` to define a `sum` function.
;;; 
;;; Complete the function below by replacing `...complete-this...` with the correct expressions
;; **

;; @@
(defn sum 
  "returns the sum of values in a collection"
  [values]
  (loop [result 0.0
         values values]
    (if (seq values)
      (recur ...complete-this...
             ...complete-this...)
      result)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/sum</span>","value":"#'clojure-exercises/sum"}
;; <=

;; @@
;; solution
(defn sum 
  "returns the sum of values in a collection"
  [values]
  (loop [result 0.0
         values values]
    (if (seq values)
      (recur (+ result (first values))
             (rest values))
      result)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/sum</span>","value":"#'clojure-exercises/sum"}
;; <=

;; **
;;; You can test your function using the command
;; **

;; @@
(sum [1 2 3])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>6.0</span>","value":"6.0"}
;; <=

;; **
;;; Note that the sum function returns a double, whereas the inputs are longs. Can you explain why this happens?
;;; 
;;; Rewrite the sum function so it preserves the type of the input
;; **

;; @@
(defn sum 
  "returns the sum of values in a collection"
  [values]
  (loop [result nil
         values values]
    (if (seq values)
      (if result
        (recur ...complete-this...
               ...complete-this...)
        (recur ...complete-this...
               ...complete-this...))
      result)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/sum</span>","value":"#'clojure-exercises/sum"}
;; <=

;; @@
;; solution
(defn sum 
  "returns the sum of values in a collection"
  [values]
  (loop [result nil
         values values]
    (if (seq values)
      (if result
        (recur (+ result
                  (first values))
               (rest values))
        (recur (first values)
               (rest values)))
      result)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/sum</span>","value":"#'clojure-exercises/sum"}
;; <=

;; **
;;; Now test that the sum function preserves input types
;; **

;; @@
(sum [1 2 3])
(sum [1.0 2.0 3.0])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>6.0</span>","value":"6.0"}
;; <=

;; **
;;; Rewrite the `sum` function using the `reduce` command
;; **

;; @@
(defn sum [values]
  ...complete-this...)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/sum</span>","value":"#'clojure-exercises/sum"}
;; <=

;; @@
;; solution
(defn sum [values]
  (reduce + values))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/sum</span>","value":"#'clojure-exercises/sum"}
;; <=

;; **
;;; Now let's write a cumulative sum function that maps a collection of numbers `[1 2 3 4]` onto the partial sums `[1 3 6 10]`
;; **

;; @@
(defn cumsum 
  "returns a vector of partial sums"
  [values]
  (loop [results nil
         values values]
    (if (seq values)
      (if results
        (recur ...complete-this...
               ...complete-this...)
        (recur ...complete-this...
               ...complete-this...))
      results)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/cumsum</span>","value":"#'clojure-exercises/cumsum"}
;; <=

;; @@
;; solution
(defn cumsum 
  "returns a vector of partial sums"
  [values]
  (loop [results nil
         values values]
    (if (empty? values)
      results
      (if results
	      (recur (conj results
                       (+ (peek results)
                          (first values)))
    	         (rest values))
          (recur [(first values)]
                 (rest values))))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/cumsum</span>","value":"#'clojure-exercises/cumsum"}
;; <=

;; **
;;; Test your function
;; **

;; @@
(cumsum (list 1 2 3 4))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>6</span>","value":"6"},{"type":"html","content":"<span class='clj-long'>10</span>","value":"10"}],"value":"[1 3 6 10]"}
;; <=

;; **
;;; Bonus: can you write `cumsum` as a function that returns a lazy sequence?
;; **

;; @@
(defn cumsum 
  "returns a vector of partial sums"
  ([values]
   (if (seq values)
      ...complete-this...
     (lazy-seq)))
  ([init values]
   (lazy-seq
	  ...complete-this...))
;; @@

;; @@
;; solution
(defn cumsum 
  "returns a vector of partial sums"
  ([values]
   (lazy-seq
     (when (seq values)
       (cumsum (first values)
               (rest values)))))
  ([init values]
   (lazy-seq
     (cons init
           (when (seq values)
             (cumsum (+ init 
                        (first values))
                     (rest values)))))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/cumsum</span>","value":"#'clojure-exercises/cumsum"}
;; <=

;; **
;;; ## Exercise 2: Higher-order functions
;; **

;; **
;;; In the `getting-started` worksheet, we used the `map` function. This function is an example of a higher-order function, which is to say that it is a function that accepts a function as an argument. In this exercise we will look at some of clojure's higher-order functions, and write implementations of our own.
;; **

;; **
;;; Let's start with `mapv`, which is a variant of `map` that returns a vector. 
;; **

;; @@
(defn my-mapv 
  [f values]
  (loop [results []
         values values]
    ...complete-this...))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/my-mapv</span>","value":"#'clojure-exercises/my-mapv"}
;; <=

;; @@
;; solution
(defn my-mapv 
  [f values]
  (loop [results []
         values values]
    (if (seq values)
      (recur (conj results
                   (f (first values)))
             (rest values))
      results)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/my-mapv</span>","value":"#'clojure-exercises/my-mapv"}
;; <=

;; **
;;; Test your `mapv` function
;; **

;; @@
(my-mapv #(* % %) 
         (range 5))

;; => [0 1 4 9 16]

(my-mapv #(* % %) nil)

;; => []
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[],"value":"[]"}
;; <=

;; **
;;; Now write the `map` function (which should return a lazy sequence)
;; **

;; @@
(defn my-map 
  [f values]	
  (lazy-seq
    (when (seq values)
      ...complete-this...)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/my-map</span>","value":"#'clojure-exercises/my-map"}
;; <=

;; @@
;; solution
(defn my-map 
  [f values]	
  (lazy-seq
    (when (seq values)
      (cons (f (first values))
            (map f (rest values))))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/my-map</span>","value":"#'clojure-exercises/my-map"}
;; <=

;; @@
(my-map #(* % %) (range 5))

;; => (0 1 4 9 16)

(my-map #(* % %) nil)

;; => ()
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[],"value":"()"}
;; <=

;; @@
(lazy-seq nil)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[],"value":"()"}
;; <=

;; @@

;; @@
