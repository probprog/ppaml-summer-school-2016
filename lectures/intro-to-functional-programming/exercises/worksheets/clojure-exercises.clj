;; gorilla-repl.fileformat = 1

;; **
;;; # Clojure Exercises
;; **

;; @@
 (ns clojure-exercises
   (:require [clojure.repl :as repl])
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
	  ...complete-this...)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/cumsum</span>","value":"#'clojure-exercises/cumsum"}
;; <=

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
;;; Again, test your function
;; **

;; @@
(cumsum [1 2 3 4])
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>6</span>","value":"6"},{"type":"html","content":"<span class='clj-long'>10</span>","value":"10"}],"value":"(1 3 6 10)"}
;; <=

;; **
;;; ## Exercise 2: Higher-order functions
;; **

;; **
;;; In the `getting-started` worksheet, we used the `map` function. This function is an example of a higher-order function, which is to say that it is a function that accepts a function as an argument. In this exercise we will look at some of clojure's higher-order functions, and write implementations of our own.
;; **

;; **
;;; ### Exercise 2a: Map
;;; 
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

;; **
;;; ### Exercise 2b: Comp
;;; 
;;; We will now define a function `comp`, which takes two functions `f` and `g` as an argument and returns a function `h` such that `(h x) -> (f (g x))`. 
;;; 
;;; Let's start by considering the case where `f` and `g` accept a single argument. Complete the following code
;; **

;; @@
(defn my-comp [f g]
  (fn [x]
    ...complete-this...))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/my-comp</span>","value":"#'clojure-exercises/my-comp"}
;; <=

;; @@
;; solution
(defn my-comp [f g]
  (fn [x]
    (f (g x))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/my-comp</span>","value":"#'clojure-exercises/my-comp"}
;; <=

;; **
;;; Test this code by composing `sqrt` and `sqr`:
;; **

;; @@
(let [f sqrt
      g (fn [x] 
         (* x x))
      h (my-comp f g)]
  (h -10))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>10.0</span>","value":"10.0"}
;; <=

;; **
;;; Now let's generalize this function to accept a variable number of arguments. The following code defines a function with an `args` that accepts a variable number of arguments
;; **

;; @@
(let [f (fn [& args]
          (prn args))]
  (f 1)
  (f 2 3 4))
;; @@
;; ->
;;; (1)
;;; (2 3 4)
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; In order to pass a variable length set of arguments to a function, we will need the `apply` function, which can be called `(apply f args)` to call a function with a (variable length) sequence of arguments `args`.  
;; **

;; @@
(let [args [1 2 3]]
  ;; this is equivalent to (+ 1 2 3)
  (apply + args))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>6</span>","value":"6"}
;; <=

;; **
;;; Use the `apply` function to define a `comp` function that is agnostic of the number of input arguments
;; **

;; @@
(defn my-comp [f g]
  ...complete-this...)
;; @@

;; **
;;; We can look up how the Clojure function `comp` is defined using the `source` command from the `clojure.repl` namespace
;; **

;; @@
(repl/source comp)
;; @@
;; ->
;;; (defn comp
;;;   &quot;Takes a set of functions and returns a fn that is the composition
;;;   of those fns.  The returned fn takes a variable number of args,
;;;   applies the rightmost of fns to the args, the next
;;;   fn (right-to-left) to the result, etc.&quot;
;;;   {:added &quot;1.0&quot;
;;;    :static true}
;;;   ([] identity)
;;;   ([f] f)
;;;   ([f g] 
;;;      (fn 
;;;        ([] (f (g)))
;;;        ([x] (f (g x)))
;;;        ([x y] (f (g x y)))
;;;        ([x y z] (f (g x y z)))
;;;        ([x y z &amp; args] (f (apply g x y z args)))))
;;;   ([f g &amp; fs]
;;;      (reduce1 comp (list* f g fs))))
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; We see that this function has 4 signatures:
;;; 
;;; 1. `(comp)` with no arguments returns the identity function `(fn [x] x)`
;;; 2. `(comp f)` with a single argument returns the function `f`
;;; 3. `(comp f g)` returns the composition of `f` and `g`
;;; 4. `(comp f g & fs)` returns the composition of a variable number of functions
;;; 
;;; The implementation for `(comp f g)` itself specifies a function with 5 signatures. 
;;; 
;;; - Why do you think the Clojure developers have gone through the trouble of defining these call syntaxes?
;;; - Can you infer what the syntax `(apply g x y z args)` does?
;; **

;; **
;;; ### Exercise 2c Reduce
;;; 
;;; As a final exercise, let's implement the `reduce` function. This function has two signatures
;;; 
;;; 1. `(reduce f init values)`: repeatedly call `(f result value)` for each `value` in `values` where `result` is the result of the previous function call, and is initialized to `init`.
;;; 2. `(reduce f values)`: perform the above operation, initializing `init` to `(first values)` and `values` to `(rest values)`. 
;;; 
;;; Complete the following code (*hint*: look at the `loop` and `recur` patterns from Exercise 1)
;; **

;; @@
(defn my-reduce 
  ([f init values]
   ...complete-this...)
  ([f values]
   ...complete-this...))
;; @@

;; @@
;; solution

(defn my-reduce 
  ([f init values]
   (loop [result init
          values values]
     (if (seq values)
       (recur (f result 
                 (first values))
              (rest values))
       result)))
  ([f values]
   (when (seq values)
     (my-reduce f 
                (first values) 
                (rest values)))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;clojure-exercises/my-reduce</span>","value":"#'clojure-exercises/my-reduce"}
;; <=

;; **
;;; Test your code
;; **

;; @@
(my-reduce + [1 2 3 4])

; => 6

(my-reduce (fn [sums v]
             (conj sums
                   (if (seq sums)
                     (+ (peek sums) v)
                     v)))
           []
           [1 2 3 4])

; => [1 3 6 10]
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>6</span>","value":"6"},{"type":"html","content":"<span class='clj-long'>10</span>","value":"10"}],"value":"[1 3 6 10]"}
;; <=

;; **
;;; *Bonus*: Clojure implements a function called `reductions`, which performs a `reduce` operation and returns a lazy sequence of intermediate results. In other words `(reductions + [1 2 3 4])` returns a lazy sequence `(1 3 6 10)`. Write this function.
;; **

;; @@
(defn my-reductions
  ([f init values]
   ...complete-this...)
  ([f values]
   ...complete-this...))
;; @@

;; **
;;; and test your code	
;; **

;; @@
(my-reductions +
               [1 2 3 4])

; => (1 3 6 10)
;; @@
