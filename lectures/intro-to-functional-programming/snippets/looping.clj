(defn factorial 
  "computes n * (n-1) * ... * 1"
  [n]
  (if (= n 1)
    1
    (* n (factorial (- n 1)))))

(factorial 21)
; => ArithmeticException integer overflow  
;    clojure.lang.Numbers.throwIntOverflow (Numbers.java:1501)

(defn factorial 
  "computes n * (n-1) * ... * 1"
  [n]
  (if (= n 1)
    1N
    (* n (factorial (- n 1)))))

(defn factorial 
  "computes n * (n-1) * ... * 1"
  [n]
  (if (= n 1)
    1
    (*' n (factorial (- n 1)))))

(factorial 10000)
; => StackOverflowError   clojure.lang.Numbers.equal (Numbers.java:216)

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (loop [result 1
         ivals (range 2 (+ n 1))]
    (if (seq ivals)
      (recur (* result (first ivals))
             (rest ivals))
      result)))

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (reduce ;; inputs: current result and next value 
          (fn [result i]
            ;; output: result
            (* result i))
          1
          (range (+ n 1))))

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (reduce * 1 (range 2 (+ n 1))))


(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (reduce * (range 1 (+ n 1))))
