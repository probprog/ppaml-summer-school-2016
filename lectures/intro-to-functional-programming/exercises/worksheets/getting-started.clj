;; gorilla-repl.fileformat = 1

;; **
;;; # Getting Started with Clojure
;;; 
;;; This file is a Gorilla Repl worksheet. This is a notebook format which allows writing Clojure (and Anglican) code in cells within a document. Conceptually this is quite similar to [jupyter](http://jupyter.org) notebooks.
;;; 
;;; Pressing `shift+enter` evaluates a code segment. You can access more commands via the menu, either by clicking the icon in the upper-right corner, or by pressing `ctrl+g ctrl+g` (i.e. pressing `ctrl+g` twice in quick succession).
;; **

;; **
;;; ## Namespaces 
;;; 
;;; The following cell defines a namespace, and imports some functions we will need. This is a Clojure concept somewhat analogous to a class in Java, or a module in Python. For now, take this as given; we will supply necessary imports at the top of the document for all the examples.
;;; 
;;; Run it by clicking on it and hitting `shift+enter`. This block of code often takes 10 seconds or more to run while Clojure unpacks and initializes dependencies.
;;; 
;;; Output will appear just below the cell; in this case we expect `nil`.
;; **

;; @@
(ns getting-started
  ;; this allows objects in the namespace gorilla-plot.core to
  ;; be referenced via the shorthand plot, e.g. plot/list-plot
  ;;
  ;; the equivalent command in python would be
  ;;
  ;;   import gorilla-plot.core as plot
  (:require [gorilla-plot.core :as plot])
  ;; this is equivalent to the python command
  ;;
  ;;  from anglican.runtime import *
  (:use [anglican.runtime]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; ## Clojure Basics
;;; 
;;; Syntactically, Clojure is a type of LISP. This means that parenthesis are used for function application: the first element in a parenthesized sequence is a function, and the following elements are its arguments.
;;; 
;;; It can take a few minutes to become accustomed to this sort of prefix notation. The following cell demonstrates a series of standard arithmetic and mathematical expressions. 
;;; 
;;; Run it by clicking on it and hitting `shift+enter`. Output will appear just below the cell. Comments in Clojure begin with semicolons, and describe the code segments below.
;; **

;; @@
;; Add two numbers
(+ 1 1)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}
;; <=

;; @@
;; Subtract: "10 - 3"
(- 10 3)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>7</span>","value":"7"}
;; <=

;; @@
;; Multiply, divide
(* 2 5)
(/ 10.0 3.3)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>3.0303030303030303</span>","value":"3.0303030303030303"}
;; <=

;; @@
;; Compound arithmetic expressions: "(10 * (2.1 + 4.3) / 2)"
(/ (* 10 (+ 2.1 4.3)) 2)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>32.0</span>","value":"32.0"}
;; <=

;; @@
;; Anglican supplies functions for `log`, `exp`, and more:
(exp -2)
(log (+ 1 1))
(sqrt 5)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>2.23606797749979</span>","value":"2.23606797749979"}
;; <=

;; **
;;; Clojure is dynamically _typed_ and performs type conversion behind the scenes, almost always nicely.  It has types for floating-point numbers, integers, fractions, and booleans.  Clojure also has matrix types, but we won't need them for these exercises, though Anglican supports them (e.g. [Kalman smoother](http://www.robots.ox.ac.uk/~fwood/anglican/examples/viewer/?worksheet=kalman).)
;;; 
;;; Comparison operators `<`, `>`, `=`, `<=`, `>=` behave as one would expect, and can be used within an `if` statement. The `if` statement takes the form
;;; 
;;; `(if bool? expr-if-true expr-if-false)`
;;; 
;;; That is, an `if` expression will itself be a list with four elements: the first is `if`, the second evaluates to a boolean, and the last two are any arbitrary expressions. Here are a few examples. Please ask if you have any questions about these!
;; **

;; @@
;; this evaluates to true
(< 4 10)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>true</span>","value":"true"}
;; <=

;; @@
;; this evaluates to 1
(if (> 3 2) 1 -1)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}
;; <=

;; @@
;; this evaluates to 20
(if (<= 3 3) (+ 10 10) 0)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>20</span>","value":"20"}
;; <=

;; @@
;; this evaluates to 4
(+ (if (< 4 5) 1 2) 3)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}
;; <=

;; **
;;; A `let` block is a bit of Clojure which can be used to define variables within a local scope. A `let` block takes an initial argument which defines a sequence of _bindings_, followed by a sequence of statements.
;;; 
;;; Bindings are a list in square-brackets `[]` of name-value pairs. In `(let [x 1 y 2] expr)`, the `expr` is evaluated with `x` set equal to 1, and `y` equal to 2.
;;; 
;;; If a `let` block includes multiple expressions, the return value of the entire block is the last expression. For more info see http://clojuredocs.org/clojure.core/let.
;; **

;; @@
;; evaluates to 12
(let [x 10
      y 2]
  (+ x y))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>12</span>","value":"12"}
;; <=

;; @@
;; also evaluates to 12!
(let [x 10
      y 2]
  (* x 3)
  (+ x y))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>12</span>","value":"12"}
;; <=

;; @@
;; ... but this evaluates to 32
(let [x 10
      y 2]
  (+ (* x 3) y))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>32</span>","value":"32"}
;; <=

;; @@
;; ... and so does this
(let [x 10
      y 2
      x (* x 3)]
  (+ x y))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>32</span>","value":"32"}
;; <=

;; @@
;; this has a side-effect, printing to the console,
;; which is carried out within the let block
(let [x 10
      y 2]
  (println "x times 3 =" (* x 3))
  (+ x y))
;; @@
;; ->
;;; x times 3 = 30
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>12</span>","value":"12"}
;; <=

;; @@
;; there is also the `do` block, which is like let, but has no bindings
(do 
  (println "10 =" 10)
  (println "1 + 1 ="  (+ 1 1))
  (+ (* 10 3) 2))
;; @@
;; ->
;;; 10 = 10
;;; 1 + 1 = 2
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>32</span>","value":"32"}
;; <=

;; **
;;; ## Functions
;;; 
;;; There are several ways to define a function in  Clojure. The basic way is to use `fn`, which takes a list of argument names (in square brackets) and then a sequence of expressions. It actually looks a lot like a `let` block! However, values for the arguments are passed in when the function is called. Here's an example:
;; **

;; @@
;; define a function which takes x, y as inputs, then returns 2x + y + 3
;; then call that function on values x=5 and y=10, and return the result
(let [my-fn (fn [x y] 
              (+ (* 2 x) y 3))]
  (my-fn 5 10))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>23</span>","value":"23"}
;; <=

;; **
;;; In addition to `fn`, you can use `defn` and `#` 
;; **

;; @@
;; this defines a function as a global variable
(defn my-fn [x y]
  (+ (* 2 x) y 3))

(my-fn 5 10)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>23</span>","value":"23"}
;; <=

;; @@
;; for short functions, you can use the # macro
(let [f #(+ (* 2 %1) %2 3)]
  (f 5 10))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>23</span>","value":"23"}
;; <=

;; **
;;; ## Lists and Vectors
;; **

;; **
;;; Here is some example usage of lists in Clojure:
;; **

;; @@
;; Create a list, explicitly
(list 1 2 3)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(1 2 3)"}
;; <=

;; @@
;; Get the first element of a list
;; This returns `1`, a number
(first (list 1 2 3))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}
;; <=

;; @@
;; Get a list containing the "rest" of the list (all but the first element)
;; This returns `(2 3)`, a list
(rest (list 1 2 3))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(2 3)"}
;; <=

;; @@
;; Check the length of a list using `count`
(count (list 1 2 3 4))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>4</span>","value":"4"}
;; <=

;; @@
;; Add an element to the FRONT of list with `conj`
;; This returns `(0 1 2 3)`
(conj (list 1 2 3) 0)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(0 1 2 3)"}
;; <=

;; @@
;; Create a list of 5 elements, all of which are the output of "1 + 1"
(repeat 5 (+ 1 1))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>(2 2 2 2 2)</span>","value":"(2 2 2 2 2)"}
;; <=

;; @@
;; Create a list of integers in a certain range
(range 5)
(range 2 8)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>(2 3 4 5 6 7)</span>","value":"(2 3 4 5 6 7)"}
;; <=

;; @@
;; Create a list by repeatedly calling a function
(repeatedly 3 (fn [] (+ 10 20)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>30</span>","value":"30"},{"type":"html","content":"<span class='clj-long'>30</span>","value":"30"},{"type":"html","content":"<span class='clj-long'>30</span>","value":"30"}],"value":"(30 30 30)"}
;; <=

;; @@
;; Looking up an element in a list requires linear time
(let [numbers (doall (range 0 20000002 2))]
  (time (nth numbers 1000000))
  (time (nth numbers 10000000)))
;; @@
;; ->
;;; &quot;Elapsed time: 10.936 msecs&quot;
;;; &quot;Elapsed time: 105.635 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>20000000</span>","value":"20000000"}
;; <=

;; **
;;; 
;;; Here is some example usage of vectors in Clojure:
;; **

;; @@
;; Create a vector by using square brackets
[1 2 3]
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[1 2 3]"}
;; <=

;; @@
;; When using `conj` on a vector, the element is appended to the END of the vector
;; This creates [1 2 3 4]
(conj [1 2 3] 4)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"[1 2 3 4]"}
;; <=

;; @@
;; Looking up an element in a vector requires constant time
(let [numbers (vec (range 0 20000002 2))]
  (time (nth numbers 1000000))
  (time (nth numbers 10000000)))
;; @@
;; ->
;;; &quot;Elapsed time: 0.032 msecs&quot;
;;; &quot;Elapsed time: 0.001 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>20000000</span>","value":"20000000"}
;; <=

;; **
;;; ## map, reduce, and loop
;;; 
;;; One thing which will be useful are the `map` and `reduce` functions for performing operations on lists.
;;; 
;;; - `map` takes a function and a list (or lists), and applies the function to every element in the list.
;;; 
;;; - `reduce` takes a function and a list, applies the function recursively to the pairs of elements in the list; see the examples below, or its documentation [here](http://clojuredocs.org/clojure.core/reduce).
;; **

;; @@
;; Apply the function f(x) = x*x to every element of the list `(1 2 3 4)`
(map (fn [x] 
       (* x x)) 
     (list 1 2 3 4))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"},{"type":"html","content":"<span class='clj-long'>9</span>","value":"9"},{"type":"html","content":"<span class='clj-long'>16</span>","value":"16"}],"value":"(1 4 9 16)"}
;; <=

;; @@
;; Here's a different way of writing the above:
(map #(pow % 2)
     (range 1 5))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-double'>1.0</span>","value":"1.0"},{"type":"html","content":"<span class='clj-double'>4.0</span>","value":"4.0"},{"type":"html","content":"<span class='clj-double'>9.0</span>","value":"9.0"},{"type":"html","content":"<span class='clj-double'>16.0</span>","value":"16.0"}],"value":"(1.0 4.0 9.0 16.0)"}
;; <=

;; @@
;; Apply the function f(x,y) = x + 2y to the x values `(1 2 3)` and the y values `(10 9 8)`
(map (fn [x y] 
       (+ x (* 2 y)))
     [1 2 3]   ; these are values x1, x2, x3
     [10 9 8]) ; these are values y1, y2, y3
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>21</span>","value":"21"},{"type":"html","content":"<span class='clj-long'>20</span>","value":"20"},{"type":"html","content":"<span class='clj-long'>19</span>","value":"19"}],"value":"(21 20 19)"}
;; <=

;; @@
;; Calculate the sum of elements
(reduce + 0.0 [1 2 3])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>6.0</span>","value":"6.0"}
;; <=

;; @@
;; This does the same thing. Note that the result is now a long, not a double because we have not provied the initial value 0.0.
(reduce + [1 2 3])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>6</span>","value":"6"}
;; <=

;; @@
;; This creates a vector containing only the positive elements
(reduce (fn [y x]
          ;; append x to y if larger than 0
          (if (> x 0)
            (conj y x)
            y))
        ;; initial value for y
        []
        ;; values for x
        [-1 1 -2 2 -3 3])
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[1 2 3]"}
;; <=

;; @@
;; Clojure provides a number of other functions that loop over
;; elements. We could also write the above example using
;; `filter`, which returns a list instead of a vector
(filter #(> % 0) 
        [-1 1 -2 2 -3 3])
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(1 2 3)"}
;; <=

;; **
;;; The final essential Clojure construct we will want for the exercises is `loop ... recur`. This allows us to easily write looping code.
;;; 
;;; `loop` specifies initial values for a set of names (similar to a `let`-block) and then `recur` passes new values in when running the next loop iteration. This is best demonstrated by example. There are some examples http://clojuredocs.org/clojure.core/loop, and below:
;; **

;; @@
;; loop from x=1 until x=10, printing each x
(loop [x 1]
  (if (<= x 10)
    (let [next-x (+ x 1)]
      (println x)
      (recur next-x))))
;; @@
;; ->
;;; 1
;;; 2
;;; 3
;;; 4
;;; 5
;;; 6
;;; 7
;;; 8
;;; 9
;;; 10
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
;; this code loops from x=10 down to x=0, 
;; and builds up a vector y containing the values of 2x.
(loop [x 10
       y []]
  (if (= x 0)
    y
    (recur (- x 1)
           (conj y (* 2 x)))))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>20</span>","value":"20"},{"type":"html","content":"<span class='clj-long'>18</span>","value":"18"},{"type":"html","content":"<span class='clj-long'>16</span>","value":"16"},{"type":"html","content":"<span class='clj-long'>14</span>","value":"14"},{"type":"html","content":"<span class='clj-long'>12</span>","value":"12"},{"type":"html","content":"<span class='clj-long'>10</span>","value":"10"},{"type":"html","content":"<span class='clj-long'>8</span>","value":"8"},{"type":"html","content":"<span class='clj-long'>6</span>","value":"6"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[20 18 16 14 12 10 8 6 4 2]"}
;; <=

;; **
;;; ## Keyboard Shortcuts
;;; 
;;; You can use the following keyboard shortcuts inside a Gorilla Repl worksheet. These shortcuts are for **OS X** and **Linux**. Users on **Windows** should replace `ctrl` with `alt`. 
;;; 
;;; | Key | Action |
;;; |:-|:-|
;;; | `ctrl+g  ctrl+g` | Open menu |
;;; | `ctrl+space` | Activate autocompletion |
;;; | `shift+enter` | Evaluate current segment |
;;; | `ctrl+shift+enter` | Evaluate entire work sheet |
;;; | `ctrl+g  ctrl+l` | Load worksheet |                                             
;;; | `ctrl+g  ctrl+s` | Save worksheet |
;;; | `ctrl+g  ctrl+n` | Create new segment *below* current segment |
;;; | `ctrl+g  ctrl+b` | Create new segment *above* current segment |
;;; | `ctrl+g  ctrl+x` | Delete current segment |
;;; | `ctrl+g  ctrl+u` | Move segment *up* |
;;; | `ctrl+g  ctrl+d` | Move segment *down* |
;;; | `ctrl+g  ctrl+m` | Convert current segment to markdown |
;;; | `ctrl+g  ctrl+j` | Convert the current segment to a Clojure segment |
;; **
