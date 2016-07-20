;; gorilla-repl.fileformat = 1

;; **
;;; # Getting Started with Clojure
;;; 
;;; This file is a Gorilla Repl worksheet. This is a notebook format which allows writing Clojure (and Anglican) code in cells within a document. Conceptually this is quite similar to [jupyter](http://jupyter.org) notebooks.
;;; 
;;; Pressing `shift+enter` evaluates a code segment. You can access more commands via the menu, either by clicking the icon in the upper-right corner, or by pressing `alt+g alt+g` (i.e. pressing `alt+g` twice in quick succession). If you are using OS X, you have to use `ctrl` instead of `alt`.
;;; 
;;; This environment supports autocompletion. Whenever you are typing the name of a function, you can hit `ctrl+space` to complete the function name and see its documentation.
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
;;; When using a new function, it can be helpful to view the Clojure documentation and source code. We do this using the `doc` and `source` commands, defined in the "`clojure.repl`" namespace:
;; **

;; @@
;; This returns the documentation for the `+` operator:
(clojure.repl/doc +)
;; @@
;; ->
;;; -------------------------
;;; clojure.core/+
;;; ([] [x] [x y] [x y &amp; more])
;;;   Returns the sum of nums. (+) returns 0. Does not auto-promote
;;;   longs, will throw on overflow. See also: +&#x27;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
;; and this returns the source code:
(clojure.repl/source +)
;; @@
;; ->
;;; (defn +
;;;   &quot;Returns the sum of nums. (+) returns 0. Does not auto-promote
;;;   longs, will throw on overflow. See also: +&#x27;&quot;
;;;   {:inline (nary-inline &#x27;add &#x27;unchecked_add)
;;;    :inline-arities &gt;1?
;;;    :added &quot;1.2&quot;}
;;;   ([] 0)
;;;   ([x] (cast Number x))
;;;   ([x y] (. clojure.lang.Numbers (add x y)))
;;;   ([x y &amp; more]
;;;      (reduce1 + (+ x y) more)))
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; Clojure is dynamically _typed_ and performs type conversion behind the scenes, almost always nicely.  It has types for floating-point numbers, integers, fractions, and booleans.  Clojure also has matrix types, but we won't need them for these exercises, though Anglican supports them (e.g. [Kalman smoother](http://www.robots.ox.ac.uk/~fwood/anglican/examples/viewer/?worksheet=kalman).)
;;; 
;;; Comparison operators `<`, `>`, `=`, `<=`, `>=` behave as one would expect, and can be used within an `if` statement. The `if` statement takes the form
;;; 
;;; ```clojure
;;;    (if boolean-expr 
;;;      expr-if-true 
;;;      expr-if-false)
;;; ```
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

;; @@
;; nil is equivalent to a logical false
(if nil true false)

;; all other values are equivalent to a logical true
(if [] true false)
(if "" true false)
(if 0 true false)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>true</span>","value":"true"}
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

;; @@
;; and the `when` block, which compiles into an `if` statement followed by a do block. The `if` evaluates the first argument, and then `do`s the rest if true:

;; This is expanded to read "(if 1 (do (println 2) 3)) 
(when 1 (println 2) 3)
;; @@
;; ->
;;; 2
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}
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
;;; In addition to `fn`, you can use `defn` to define a function in the global namespace
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

;; **
;;; Clojure also provides the `#(...)` shorthand for defining anonymous functions, in which `%1`, `%2`, etc, can be used to refer to the function arguments.
;; **

;; @@
;; for short functions, you can use the # macro
(let [f #(+ (* 2 %1) %2 3)]
  (f 5 10))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>23</span>","value":"23"}
;; <=

;; **
;;; For inline functions with exactly one argument you can also simply use `%` to refer to the argument.
;; **

;; @@
(let [f #(* % %)]
  (f 4))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>16</span>","value":"16"}
;; <=

;; **
;;; ## Macros
;;; 
;;; Sometimes it is preferable, instead of writing a function to operate on some inputs in a predefined way, to instead write a *macro* which actually transforms the code that is inputted to it. We can use the `macroexpand` function to explicitly see what macros are doing. To do this, we must pass the macro and it's arguments to `macroexpand` as a `quote`, by using the quote operator "`'`". A good example is the `when` block we defined earlier, which is actually a macro:
;; **

;; @@
;; If you try this without the quote "'" it will evaulate the when block before trying to macroexpand, and then throw an error!
(macroexpand '(when 1 2 3))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>if</span>","value":"if"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>do</span>","value":"do"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(do 2 3)"}],"value":"(if 1 (do 2 3))"}
;; <=

;; **
;;; We see that `when` literally extracts the first argument and passes it to an `if` statement, and pass the remaining arguments to a `do` block. One of the interesting things about Clojure is that a large chunk of the language is actually defined in terms of macros which perform simplifying code transformations to save effort. An example is the `thread-first` macro: "`->`" which recursively inserts each expression as the first argument of the next expression:
;; **

;; @@
(let [c 5]

;; actually evaluates (- (\ (+ c 3) 2) 1)
(-> c (+ 3) (/ 2) (- 1)))

;; We can see this by using macroexpand (don't forget to quote!)
(let [c 5]
(macroexpand '(-> c (+ 3) (/ 2) (- 1))))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>-</span>","value":"-"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>/</span>","value":"/"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-symbol'>+</span>","value":"+"},{"type":"html","content":"<span class='clj-symbol'>c</span>","value":"c"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(+ c 3)"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"(/ (+ c 3) 2)"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"(- (/ (+ c 3) 2) 1)"}
;; <=

;; @@
;; There is also a thread-last macro, "->>", which inserts the expression as the last argument of the next expression:
(let [c 5]
(println "Using ->> gives: " (macroexpand '(->> c (+ 3) (/ 2) (- 1))))
(->> c (+ 3) (/ 2) (- 1)))
;; @@
;; ->
;;; Using -&gt;&gt; gives:  (- 1 (/ 2 (+ 3 c)))
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-ratio'>3/4</span>","value":"3/4"}
;; <=

;; **
;;; ## Datastructures
;; **

;; **
;;; The core data structures in Clojure are
;;; 
;;; - lists: `(1 2 3)`
;;; - vectors: `[1 2 3]`
;;; - hashmaps: `{:a 1 :b 2}`
;;; - sets: `#{1 2 3}`
;;; 
;;; Each of these data structures is a collection, which means that it supports the operations
;;; 
;;; - `(count collection)`: returns the number of elements
;;; - `(conj collection element)`: inserts an element into the collection
;;; - `(seq collection)`: converts the collection into an iterable sequence
;;; 
;;; Functions defined on sequences also apply to collections
;;; 
;;; - `(first sequence)`: returns the first element in a collection
;;; - `(rest sequence)`: returns a sequence with everything but the first element
;;; - `(cons element sequence)`: prepends an element to a sequence
;; **

;; **
;;; ### Lists and Sequences
;; **

;; @@
;; Create a list, explicitly
(list 1 2 3)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(1 2 3)"}
;; <=

;; @@
;; all lists are sequences
(seq? (list 1 2 3))

;; but not all sequences are lists
(list? (seq [1 2 3]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>false</span>","value":"false"}
;; <=

;; @@
;; first extracts the first element of sequence
(first (list 1 2 3))

;; rest returns the remainder of the sequence
(rest (list 1 2 3))

;; cons prepends an item to a list
(cons 0 (list 1 2 3))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(0 1 2 3)"}
;; <=

;; @@
;; for lists conj prepends an item 
(conj (list 1 2 3) 0)

;; peek extracts the first element
(peek (list 1 2 3))

;; pop removes the first element
(pop (list 1 2 3))
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
;;; &quot;Elapsed time: 13.452546 msecs&quot;
;;; &quot;Elapsed time: 113.157086 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>20000000</span>","value":"20000000"}
;; <=

;; **
;;; ### Vectors
;; **

;; @@
;; Create a vector by using square brackets
[1 2 3]
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[1 2 3]"}
;; <=

;; @@
;; conj appends to the end for vectors
(conj [1 2 3] 4)

;; peek extracts the last element
(peek [1 2 3])

;; pop removes the last element
(pop [1 2 3])
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[1 2]"}
;; <=

;; @@
;; seq casts the vector as a sequence 
(seq [1 2 3])

;; you can use first on vectors
;; (as well as any other collections)
(first [1 2 3])

;; rest returns the remainder of the
;; vector as a sequence
(rest [1 2 3])

;; cons prepends an item, again turning
;; the vector into a sequence
(cons 0 [1 2 3])
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(0 1 2 3)"}
;; <=

;; @@
;; Looking up an element in a vector requires constant time
(let [numbers (vec (range 0 20000002 2))]
  (time (nth numbers 1000000))
  (time (nth numbers 10000000)))
;; @@
;; ->
;;; &quot;Elapsed time: 0.032317 msecs&quot;
;;; &quot;Elapsed time: 6.74E-4 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>20000000</span>","value":"20000000"}
;; <=

;; @@
;; vectors can be used as functions
(let [v [1 2 3]]
  (v 2))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}
;; <=

;; **
;;; ### Hash maps
;; **

;; @@
;; this is a hash map literal
{:a 1 :b 2 :c 3}
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:a</span>","value":":a"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:a 1]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:b</span>","value":":b"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[:b 2]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:c</span>","value":":c"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[:c 3]"}],"value":"{:a 1, :b 2, :c 3}"}
;; <=

;; @@
;; entries in hash maps are customarily labeled with keywords
(keyword "a")
(class :a)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-class'>clojure.lang.Keyword</span>","value":"clojure.lang.Keyword"}
;; <=

;; @@
;; however, keys in a hash map can be any clojure object
{:a 1 "b" 2 [3 4] 5}
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:a</span>","value":":a"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:a 1]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-string'>&quot;b&quot;</span>","value":"\"b\""},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[\"b\" 2]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"[3 4]"},{"type":"html","content":"<span class='clj-long'>5</span>","value":"5"}],"value":"[[3 4] 5]"}],"value":"{:a 1, \"b\" 2, [3 4] 5}"}
;; <=

;; @@
;; get can be used to extract an entry from a hash map
(let [m {:a 1 :b 2 :c 3}]
  (get m :a))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}
;; <=

;; @@
;; a get command can be given a value for missing entries
(let [m {:a 1 :b 2 :c 3}]
  (get m :d 4))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}
;; <=

;; @@
;; the assoc command inserts an entry into a hash map
(let [m {:a 1 :b 2 :c 3}]
  (assoc m :d 4))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:a</span>","value":":a"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:a 1]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:b</span>","value":":b"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[:b 2]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:c</span>","value":":c"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[:c 3]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:d</span>","value":":d"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"[:d 4]"}],"value":"{:a 1, :b 2, :c 3, :d 4}"}
;; <=

;; @@
;; calling seq on a hash map produces a sequence of pairs
(let [m {:a 1 :b 2 :c 3}]
  (seq m))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>([:a 1] [:b 2] [:c 3])</span>","value":"([:a 1] [:b 2] [:c 3])"}
;; <=

;; @@
;; conj can be used to insert into a hash map
(let [m {:a 1 :b 2 :c 3}]
  (conj m [:d 4]))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:a</span>","value":":a"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:a 1]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:b</span>","value":":b"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[:b 2]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:c</span>","value":":c"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[:c 3]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:d</span>","value":":d"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"[:d 4]"}],"value":"{:a 1, :b 2, :c 3, :d 4}"}
;; <=

;; @@
;; like vectors, hash maps can be used as functions
(let [m {:a 1 :b 2 :c 3}]
  (m :c))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}
;; <=

;; @@
;; keywords start with :, and can be used as functions as well
(let [m {:a 1 :b 2 :c 3}]
  (:c m))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}
;; <=

;; @@
;; under the hood, hash maps can have two different types
(array-map :a 1 :b 2 :c 3)
(hash-map :a 1 :b 2 :c 3)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:c</span>","value":":c"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[:c 3]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:b</span>","value":":b"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[:b 2]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:a</span>","value":":a"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:a 1]"}],"value":"{:c 3, :b 2, :a 1}"}
;; <=

;; @@
;; array maps are ordered
(let [m (array-map :a 1 :b 2 :c 3)]
  (first m))

(let [m (array-map :a 1 :b 2 :c 3)]
  (assoc m :d 4))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:a</span>","value":":a"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:a 1]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:b</span>","value":":b"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[:b 2]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:c</span>","value":":c"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[:c 3]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:d</span>","value":":d"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"[:d 4]"}],"value":"{:a 1, :b 2, :c 3, :d 4}"}
;; <=

;; @@
;; hash maps are unordered
(let [m (hash-map :a 1 :b 2 :c 3)]
  (first m))

(let [m (hash-map :a 1 :b 2 :c 3)]
  (assoc m :d 4))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:c</span>","value":":c"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[:c 3]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:b</span>","value":":b"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[:b 2]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:d</span>","value":":d"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"[:d 4]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:a</span>","value":":a"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:a 1]"}],"value":"{:c 3, :b 2, :d 4, :a 1}"}
;; <=

;; @@
;; small literals are array maps by default
{:a 1 :b 2 :c 3 :d 4 
 :e 5 :f 6 :g 7 :h 8}

;; larger literals are hash maps
{:a 1 :b 2 :c 3 :d 4 
 :e 5 :f 6 :g 7 :h 8
 :i 9}
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:e</span>","value":":e"},{"type":"html","content":"<span class='clj-long'>5</span>","value":"5"}],"value":"[:e 5]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:g</span>","value":":g"},{"type":"html","content":"<span class='clj-long'>7</span>","value":"7"}],"value":"[:g 7]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:c</span>","value":":c"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[:c 3]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:h</span>","value":":h"},{"type":"html","content":"<span class='clj-long'>8</span>","value":"8"}],"value":"[:h 8]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:b</span>","value":":b"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"[:b 2]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:d</span>","value":":d"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"[:d 4]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:f</span>","value":":f"},{"type":"html","content":"<span class='clj-long'>6</span>","value":"6"}],"value":"[:f 6]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:i</span>","value":":i"},{"type":"html","content":"<span class='clj-long'>9</span>","value":"9"}],"value":"[:i 9]"},{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:a</span>","value":":a"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"[:a 1]"}],"value":"{:e 5, :g 7, :c 3, :h 8, :b 2, :d 4, :f 6, :i 9, :a 1}"}
;; <=

;; **
;;; ## Lazy sequences
;; **

;; **
;;; The following function defines an infinite sequence of numbers as a so-called lazy sequence. In a lazy sequence, elements are not evaluated until the are accessed for the first time, but are stored in memory afterwards.
;; **

;; @@
(defn inf-range 
  "returns an infinite range as a lazy sequence"
  ;; this defines the default call syntax
  ([start step]
  	(lazy-seq 
      (cons start 
            (inf-range (+ start step) 
                       step))))
  ;; this defines a call syntax with 
  ;; the start argument omitted
  ([step] 
   (inf-range 0 step))
  ;; this defines a call syntax with
  ;; no arguments
  ([]
   (inf-range 0 1)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;getting-started/inf-range</span>","value":"#'getting-started/inf-range"}
;; <=

;; **
;;; Don't *ever* try to evaluate lazy sequence in the repl, since this will try to print out an infinite sequence
;; **

;; @@
;; (inf-range)
;; @@

;; **
;;; But you can bind infinite sequences to variables just fine
;; **

;; @@
(def my-range (inf-range))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;getting-started/my-range</span>","value":"#'getting-started/my-range"}
;; <=

;; **
;;; Elements are not evaluated until you accessed them the first time, but are then stored until they need to be garbage collected. To see this, let's write a version of range that is artificially slowed down
;; **

;; @@
(defn slow-range 
  ([start]
  	(Thread/sleep 100)
  	(lazy-seq 
    	  (cons start 
        	    (slow-range (inc start)))))
  ([] (slow-range 0)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;getting-started/slow-range</span>","value":"#'getting-started/slow-range"}
;; <=

;; @@
(def my-range (slow-range))

(time (first (drop 10 my-range)))
(time (first (drop 10 my-range)))
;; @@
;; ->
;;; &quot;Elapsed time: 1101.491567 msecs&quot;
;;; &quot;Elapsed time: 0.115022 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>10</span>","value":"10"}
;; <=

;; **
;;; ## Map, Reduce, and Loop
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
;; This does the same thing, but produces a long
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
;;; `loop` specifies initial values for a set of names (similar to a `let`-block) and then `recur` passes new values in when running the next loop iteration. Another way to put this is that `recur` takes the same arguments as the original loop, and effectively calls the loop again but this time starting with the updated values. It is important to ensure that at least one of the parameters is converging towards a value which will trigger the end of the loop (usually via an `if` statement). This is best demonstrated by example. There are some examples http://clojuredocs.org/clojure.core/loop, and below:
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
;;; You can use the following keyboard shortcuts inside a Gorilla Repl worksheet. These shortcuts are for **Linux** and **Windows**. **OS X** users should replace `alt` with `ctrl`. 
;;; 
;;; | Key | Action |
;;; |:-|:-|
;;; | `alt+g  alt+g` | Open menu |
;;; | `ctrl+space` | Activate autocompletion |
;;; | `shift+enter` | Evaluate current segment |
;;; | `alt+shift+enter` | Evaluate entire work sheet |
;;; | `alt+g  alt+l` | Load worksheet |                                             
;;; | `alt+g  alt+s` | Save worksheet |
;;; | `alt+g  alt+n` | Create new segment *below* current segment |
;;; | `alt+g  alt+b` | Create new segment *above* current segment |
;;; | `alt+g  alt+x` | Delete current segment |
;;; | `alt+g  alt+u` | Move segment *up* |
;;; | `alt+g  alt+d` | Move segment *down* |
;;; | `alt+g  alt+m` | Convert current segment to markdown |
;;; | `alt+g  alt+j` | Convert the current segment to a Clojure segment |
;; **

;; @@

;; @@
