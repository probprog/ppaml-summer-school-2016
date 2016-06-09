;; integers, doubles, ratios
1234, 1.234, 1.234M, 12/34

;; strings, characters
"ada", \a \d \a

;; booleans, null
true, false, nil

;; symbols, keywords
ada, :ada  

;; regular expressions
#"a*b"

;; lists
(list 1 2 3), (1 2 3)

;; vectors
(vector 1 2 3), [1 2 3]

;; hash maps
(hash-map :a 1 :b 2), {:a 1 :b 2}

;; sets
(set 1 2 3), #{1 2 3}

;; everything nests
{:a [[1 2] [3 4]] :b #{1 2 3}}