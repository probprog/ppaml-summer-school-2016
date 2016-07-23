;; gorilla-repl.fileformat = 1

;; **
;;; # Bonus Exercise 1: Captcha
;; **

;; @@
(ns captcha
  (:require [gorilla-plot.core :as plot]
            [anglican.rmh :as rmh]
            [anglican.smc :as smc]
            [clojure.core.matrix :as m]
            [gorilla-repl.image :as image]
            [clojure.java.io :as io])
  (:use [anglican runtime emit core inference]
        [exercises captcha])
  (:import [javax.imageio ImageIO]
           [java.io File]
           [robots.OxCaptcha OxCaptcha]))

(def ...complete-this... nil)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/...complete-this...</span>","value":"#'captcha/...complete-this..."}
;; <=

;; **
;;; ## Captcha renderer
;;; 
;;; We will try to break some captchas by doing inference over letter identities in a generative model containing both letter identities and the captcha image (2D matrix of 0-255 numbers). Let's explore the renderer...
;;; 
;;; Remember that the image is 50 pixels high and 150 pixels wide. x-axis goes from left to right and y-axis goes from top to bottom.
;;; 
;;; Play around with the parameters to get a feel for the renderer...
;; **

;; @@
(def xs [1 20 40])
(def ys [30 30 30])
(def letters "abc")
(def salt-and-pepper true)
(def render-mode OxCaptcha/ABSOLUTE) ; either OxCaptcha/ABSOLUTE or OxCaptcha/RELATIVE
(def filename "tmp/captcha/test.png")
(render-to-file xs ys letters salt-and-pepper filename :mode render-mode)
(image/image-view (ImageIO/read (File. filename)) :type "png" :alt "captcha")
;; @@
;; =>
;;; {"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAM2klEQVR42u3ch5MVRRcFcP4IrLKKwlCKCUUtMScMYMCcI+acQVQMYM45I5gx5yyYcxYVM2aSCuaI6X71u1W9td+6rGl32YXuqq43O/OmZ967Z84993S/7RKt2Lp06RK11ZZYaO0Bf/jhh3j77bfjzjvvjNNOOy2OOeaYOP/88+Ohhx6Kjz/+OH799ddZ44urD1GL30WrfzvfffddvPLKK3HVVVfFoEGDYo899oihQ4fGbbfdFu+++2788ssvFVCzAdBa/Vv6+uuv46mnnoozzjgjNt1001hjjTVip512ipEjR8Zrr70W06dPr8GcDR6MNgHW448/HieddFJ+iOWXXz622mqruOiii5LJ/g2warA63/21KbDWWWed2QJYlQH/JbD++OOP+P3331N400hedfsca3yRloDlOGCV8Rr3xuP83VbG+e233xruyfa/Ha+2NmQsAREcIPjmm2/is88+y2qO8H7jjTdSJ+lvvvlmfPDBB/Hpp5/Gt99+m4AR0BkBS2X47LPPxueffx6TJ0+O9957L6vHd955J8eZMmVKnvvzzz/n9VsCE3B///33OdZHH32U47z++uvZbdvnmPd4b1Pw19bOwAKMn376KaZOnRrjx4+P5557LsaMGRO33HJLjBo1Ki699NIYPnx49ssuuyxuvPHGPP7yyy/HJ598kgCbNm3an4C12WabxSmnnBJ33HFHPP3003HvvffGddddF1deeWVWjzfccEPcf//98eKLLyYoAKwpIGxjJFXnpEmTEtwsjJtvvjnHGTFiRHbb7vfhhx+OcePGJYgBrCWw1taGwAIqAZgwYUICSsDOOeectAoGDhwYe++9d+y6666x4447xoABA2LnnXeOfffdN48L6IMPPpisht0eeeSRBmAtvfTSsdZaa8X++++f4DLmUUcdlefuvvvu2ffbb7/cd+GFF8Y999yToME42AugCkt9+eWXyUiuBeTHHntsHHDAAbHbbrvlfak+jXfQQQfF8ccfH1dccUU8+uij8f777yf7VnC1X1HQcOTHH39M1sE2l1xySRxyyCGxww47xBZbbBGbb755pjPbm2yySfTv3z/WXHPNtBJs77LLLnH66acn62Cv0aNHx4knnpjA6tWrVyy11FKx/vrr53h8rW233TbH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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x35bf95c2 \"BufferedImage@35bf95c2: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}
;; <=

;; **
;;; You can also check the images in the folder `tmp/captcha`. We can also get the actual matrix of values:
;; **

;; @@
(def test-captcha (render xs ys letters salt-and-pepper :mode render-mode))
(m/shape test-captcha)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>50</span>","value":"50"},{"type":"html","content":"<span class='clj-unkown'>150</span>","value":"150"}],"value":"[50 150]"}
;; <=

;; **
;;; ## Approximate Bayesian computation (ABC) likelihood
;;; 
;;; Our generative model will have the form of
;;; 
;;; \begin{align}
;;; 	\theta = (\text{x-offsets}, \text{y-offsets}, \text{letters}, \text{other-params}) &\sim p(\theta) \\\\
;;;     y = \text{captcha-image} &\sim p(y | \theta).
;;; \end{align}
;;; 
;;; Using the backend inference algorithms, we would like to find the posterior @@p(\theta | y)@@.
;;; 
;;; In order to do this, we need to specify the prior @@p(\theta)@@ and the likelihood @@p(y | \theta)@@ in our probabilistic program. We want the likelihood to be a distribution which is high when the rendered captcha render(@@\theta@@) is similar to the observed one @@y@@ and low otherwise. This will force the posterior to concentrate around the correct letters positions and identities that might have generated the observed captcha. This way of designing likelihoods to encode similarity is called approximate Bayesian computation (ABC) and the likelihood function is called an ABC likelihood.
;;; 
;;; Recalling that render(@@\theta@@) and @@y@@ are both @@50 \times 150@@ integer matrices, we can for example design an ABC likelihood as follows:
;;; \begin{align}
;;; 	p(y | \theta) = \text{Normal}(\text{flatten}(y); \text{flatten}(\text{render}(\theta)), \sigma^2 I)
;;; \end{align}
;;; where `flatten` flattens matrices into a vector and @@\sigma@@ is a parameter designed by us.
;;; 
;;; In this example, we will use a very similar ABC likelihood:
;;; \begin{align}
;;; 	p(y | \theta) = \text{Normal}(\text{reduce-dim}(\text{flatten}(y)); \text{reduce-dim}(\text{flatten}(\text{render}(\theta))), \sigma^2 I).
;;; \end{align}
;;; The difference is that we use a function `reduce-dim` which additionally reduces the dimension of the flattened, @@7500@@-dimensional vector, to a @@500@@-dimensional one through a linear projection by a random projection matrix @@R \in \mathbb R^{500 \times 7500}@@ (see section 2.1 in [here](http://www.ime.unicamp.br/~wanderson/Artigos/randon_projection_kdd.pdf)). The reason for this is to make the posterior space "smoother" so that it is easier for our hill-climbing based algorithms to arrive at the right answer.
;;; 
;;; The ABC likelihood which takes two @@50 \times 150@@ integer matrices has been implemented for you. You can try to generate some captchas and calculate the (log-)likelihood.
;; **

;; @@
;; First captcha
(def xs-1 [1 20 40]) ;...complete-me...)
(def ys-1 [30 30 30]) ;...complete-me...)
(def letters-1 "abc") ;...complete-me...)
(def salt-and-pepper false)
(def test-captcha-1 (render xs-1 ys-1 letters-1 salt-and-pepper :mode render-mode))
(def filename-1 "tmp/captcha/test-1.png")
(render-to-file xs-1 ys-1 letters-1 salt-and-pepper filename-1 :mode render-mode)

;; Second captcha
(def xs-2 [1 20 40]) ;...complete-me...)
(def ys-2 [30 30 30]) ;...complete-me...)
(def letters-2 "bcd") ;...complete-me...)
(def salt-and-pepper false)
(def test-captcha-2 (render xs-2 ys-2 letters-2 salt-and-pepper :mode render-mode))
(def filename-2 "tmp/captcha/test-2.png")
(render-to-file xs-2 ys-2 letters-2 salt-and-pepper filename-2 :mode render-mode)

;; View the two captchas
[(image/image-view (ImageIO/read (File. filename-1)) :type "png" :alt "captcha-1")
 (image/image-view (ImageIO/read (File. filename-2)) :type "png" :alt "captcha-2")]

;; Inspect the log-likelihood value of the ABC likelihood described above for different abc-sigma's
(def abc-sigma 1) ;...complete-me...) ; Standard deviation calculated from each pixel (pixels range from 0 to 255)
(observe* (abc-dist test-captcha-1 abc-sigma) test-captcha-2)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>-562.8459442023368</span>","value":"-562.8459442023368"}
;; <=

;; **
;;; What are the typical values of log-likelihood?
;;; How does the choice of @@\sigma@@ (`abc-sigma`) affect the variability of the log-likelihood? Why?
;;; 
;;; The choice of @@\sigma@@ needs to be calibrated in such a way that the variability of the log-likelihood is just right. If it is too low then the posterior space will not be peaked enough for inference to zoom in onto the right answer (a valid sample from a posterior might not be the right answer). If it is too high then the posterior space will be too peaked for inference to move from one posterior mode (e.g. wrong one) to another (e.g. right one).
;; **

;; **
;;; ## The captcha solving probabilistic program
;;; 
;;; Fill in the necessary blanks in the program in order to form a generative model. You can use the following fixed values to make sure the prior doesn't generate letters outside the captcha image:
;; **

;; @@
WIDTH ; width of the captcha image
HEIGHT ; height of the captcha image
avg-width ; average width of a letter
avg-height ; average height of a letter
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>25</span>","value":"25"}
;; <=

;; @@
;; Model specific
(def abc-sigma 1) ;...complete-me...) ; Standard deviation calculated from each pixel (pixels range from 0 to 255)
(def letter-dict "abcdeghk") ; Captcha letter dictionary (keep it reasonably small for good inference)

(with-primitive-procedures [render abc-dist overlap-abc-dist index-of-sorted retain-visible]
  (defquery captcha [baseline-image letter-dict abc-sigma]
    (let [;; prior for number of letters
          num-letters (sample (uniform-discrete 3 6))

          ;; prior for the letter positions and identities
          [xs ys letter-ids visible?] (loop [xs [] ys [] letter-ids [] visible? []]
                                        (if (= (count xs) num-letters)
                                          [xs ys letter-ids visible?]
                                          (let [x (round (sample (uniform-continuous 0 (- WIDTH avg-width)))) 
                                                y (round (sample (uniform-continuous avg-height HEIGHT)))
                                                letter-id (sample (uniform-discrete 0 (count letter-dict)))
                                                v (sample (flip 0.5))]
                                            (recur (conj xs x)
                                                   (conj ys y)
                                                   (conj letter-ids letter-id)
                                                   (conj visible? v)))))

          ;; Reorder xs, ys, letter-ids according to xs
          indices (index-of-sorted xs)

          ;; Take only visible
          indices (retain-visible indices visible?)

          xs (map (partial nth xs) indices)
          ys (map (partial nth ys) indices)
          letter-ids (map (partial nth letter-ids) indices)

          letters (apply str (map (partial nth letter-dict) letter-ids))

          ;; Render image using renderer from ...
          rendered-image (render xs ys letters false :mode OxCaptcha/ABSOLUTE)]

      ;; ABC-style observe
      (observe (abc-dist rendered-image abc-sigma) baseline-image)
      (observe (overlap-abc-dist avg-width 10000) xs)
      (predict :xs xs)
      (predict :ys ys)
      (predict :letters letters)
      (predict :rendered-image rendered-image))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/captcha</span>","value":"#'captcha/captcha"}
;; <=

;; **
;;; ## Generate some observes on which we want to do inference
;; **

;; @@
(def num-captchas 10)
(def tmp (generate-test-samples letter-dict num-captchas "tmp/captcha/"))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/tmp</span>","value":"#'captcha/tmp"}
;; <=

;; @@
(def num-captchas 10)
(def tmp (generate-test-samples letter-dict num-captchas "tmp/captcha/"))
(def letters (:letters tmp))
(def observes (:observes tmp))
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-ground.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAHHklEQVR42u2cT0gXQRTH99LVY0EkeLGTiZppkP+iJIvEkAgxI8WDiggSkRclMfAgGGIEIUlEHSQQDyFKRAcRieggIiLiIbp48ODFg7cXn4GRZdvf/n47v/3t+tN5MOwy83Z25u13vzPz5u06YsVKDsSxJrBigWXFAsuKBZYVKxZYp0WOjo7k4OBA9vb2ZHd3V6U/f/4cJ51HOXroW2BZSSuAZWdnR1ZXV2V+fl6lxcVFWVpaUkedRzl66FtgWUkrMNHKyorMzMzI0NCQSmNjYzI+Pq6OOo9y9NC3wLKSVhjmYCTAc+fOHZUePXokT548UUedRzl66FtgWUkr29vb8uHDB3n69KlcvnxZpevXr0tDQ4M66jzK0UPfAsuKBZZ9xGcLWN7VKPWSol6BWmCdEWBpQAGgnz9/qpXn3NycqpvEOXmUoZMtwCywciR+zOBmhY2NjVDAQt+UachHhzoWFhbk9evXalHQ19en6idxTh5l6KDLNabgcuIy5OHh4X8OwSioNy5qj4IZ3KyAv2pqakra2toCgUU5euibMI0GFeWfPn1S4KHOe/fuHa88dSKPMnTQ5RpTcDlxGXJ9fV1+//4t379/j8T5Fze1Z9qmIGZws8LExIQ8e/ZMPdCioiJfYJFPOXroh2UaN6jevXsn/f390tjYKNXV1XLjxg25e/eucm2QOCePMnQGBwcVuDRTxgqsMIakY2/fvpVXr14pQ2Xj/EuC2jNtUxAzPHjwQB4+fCjd3d3S0dEh9+/fl/Lycrlw4YIvsMinHD30uY7rqYf66uvrpba2Vm7evKnyh4eH5cuXL8dg0EMu7QFUNTU1cuXKFXWkvpcvX6pnQuKcPK0DuF68eKFsx4sZ1m5OXIYktbe3q3IMZ+r8S4raM21TEDPw4FpbW6W5uVnq6uqkpKRELl68KAUFBb7AIh/WqqyslKamJlUH/aEezm/fvi1Xr16V4uJipU+fAZcGA3uO3759U6ChPehVVFTI48ePZXJyUpaXl9VIQuKcPMrQKS0tVc+OF5N+hWUtJ0lDhl3xJEntQRKWGbq6uhTbABpsce7cOV9gkX/+/HlVFzYcGBhQWz5elqmqqpJLly6pazQYGAnW1tZkdnZWOjs7FVAopx7q+PHjx39DJnmAizoBKUdd1/7+fu6BFZUhwwIrSWoPEh6KCTPQLuzhOE5KYGEz6nz+/LnqNw85VV2Ai5eJfn7+/FkNi4CIl4z6rl27psBJvtcGnJNHndPT04rl6Q/1MDeOBVhRGTIssEzvGwW1B7Ho1tZWaGagDB2Ak4qxsBVg6O3tlY8fP8rm5ubxIkTXRT+xx61bt6SwsFDdH336+ebNGwUywEZ9AIz7Ak6//vtFXJgusJwkDRkGWKb3jYrag1iU+sIyA2XooJtqjsXkPQgM1MVLBEMz/OvVJXYdGRmR0dFRBTJtK3TQ5Ro/xvaLETN12ThJGjIMsEzvGxW1B7Eobo2wzODuS6pVIUBJBwY/Dz7nOuzGmx/XvqOTpCHDdNT0vlFReyoBvPjMwjKDH9OYbOmcGmBFacgwHTW9by68/Zk82HT9imqvMEpgJToURmnIMMDK9vo4N5NPIrAyGVYTnbxbYOUHsLyTd1aM6RYCibob8glYcQyFJ3WOBYBYEes5KT6uoMUONsENQ5vYJYndQZrUHMtrwLipPYlVYTbAIiIC3xfAwH+ny1K5Z9wrd/oR+5ZOUqtCL6DjpvYk/FjZAIstL0Jt2GngWbHzEeRQRo+vgwAe9uXZpurDqfJjeQEdN7Un4XnPBljk/fr1K+MtML2Zj225P+Xkm+5S5I3nXQMlKWpPYq8wW2DpUKFMN+2JliDRNuK/aKvpvmre7BVqsCRF7UlEN2QLLMrChBnx4vX09BzbMpu56KmNboia2pOIx4oCWO72pQuMxH7v379X7J9t1O2pjseKktrDtM008NHkS2i/L6r9rskklJtRKKpI29giSP1CcU0cnElRe5i2mYZqm/y7we8fEEHXnNjvCt0No0FEKfLQ8JnACmE+HjD1nCdB7WHtY/JxicnfZuLw1cUCLG9HtLGgUZKfIXl70OVthMUAlQ5IQ8fkhxdxU3s2L2Cmn8OZ/B8rjt2F2MJm3NQLC+GA/Pr1q3oLtVHchiSPN9Pt+9IhtAAim1/0nLTvCq0YAuvv37+KrpkHtLS0qEk58yfmMW5fkvuhE1LLUAlD5dqnZCVPgeXnAWfJ7/UlwRocYTcm2XwMwAou1z4lK3kKLPdWCUxVVlamHJVeXxLzHI4MfzAVqzacmbn2KVnJU2C5N3dZ6QES/f2e25fEyowjwyW+JL7W5YizNNc+JSt5CCz3yhBwwDwMa15fEk5R7xfJ6KEfh0/JSh4Cy7vUZ67k50vy+rLQi/MnHVbyDFiZ+pKS+uuLlTwHVjpfkvUnWWBZsWKBZcUCy8oZlX8wzDufkXfqCwAAAABJRU5ErkJggg==\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xd0941d2 \"BufferedImage@d0941d2: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x302c403a \"BufferedImage@302c403a: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x372f4b07 \"BufferedImage@372f4b07: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x355c2cac \"BufferedImage@355c2cac: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x721cfc04 \"BufferedImage@721cfc04: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAEc0lEQVR42u2cTSi0URSAZ2NrSYmyYYXyr/wl5CdKkoRCNiQbiY2yUBaKbJSUJBspWYkkC1lYWEiSZG9pY2F3vu85mWm+yff55n1n3nfemXPqNrd779xz7rnP/Xn/bkhMTJIgIXOBiYFlYmCZGFgmJgaWSZqB9fn5Ke/v7/L29iavr68aiJNGXqqJU3uD1s7Ag4VjX15e5ObmRo6PjzUQJ428VBOn9gatnYEHi1F7fX0tOzs7sri4qIE4aeSlmji1N2jtDDxYLAmMXhzd0dGhgThp5KWaOLU3aO0MPFjPz8+yt7cnY2NjUlxcrIE4aeSlmji1N2jtNLAMLAPLwDKwDCwDK33Benh4+OPeD2UJybwHFHu/KVYfNiUaLD/amZFgDQ0Nyebmppydncnt7a2cnp7K4eGhliMQJ408nJ8Ix4eBor5YndH6sAnbsNEtWH60M2PBKiws1Evxubk5WVtbk42NDb0sn56e1jIE4qSRd3JyoiOeke3U6fyP/1MP9cXqjNaHTdiGjdjqFCw/2pnRYOXm5kp5ebn09PTI6OioTE5OysDAgPT19WlHNDc3S2Njo7S0tGj60tKSHB0dRZYTp1AxKxwcHGhHMpN0d3dH7jehG13Ygk3Yho3Y6hQsr9uZ8WBlZ2fraK6qqpLOzk4ZHBzUjsb5xNva2qSyslKKioq0PJ2A0xnRLBfxjOZoqLa3t2VmZkba29ultrZW6uvrpaurS3Wiu7+/X3p7e6WpqUlKSkokLy9PbXUKlpftNLB+OzArK0tycnKktLRUO3J2dlZWV1dla2tLw/Lysjq/pqZG8vPz9T+MaJYLAIlnNIc348xUQNXQ0KB6+UUHuqL1TkxM6EwCEICBrU7B8rKdBtaXw+k4Zo75+XntdJ6n3d/fazg/P5f19XUZGRnRTsHpzC4LCwu60Y3nuRtlLy4utBPRx+xQUVGhdaMDXX/TC1ihUMgVWF6108D6WiKqq6tlampK9vf35fHxMXI1FF66cDowtLa2SkFBgZSVlWl5rqL+97kbdT09Pcnu7q6Mj49rHehn9mDmuLq6imyUw3pJI48yQOFmxvKqnQZW1KaWvQ0dyAiOnfJxOiOa5YK9SDxXZrHLIPWjB33UQUezJLFJjt3HECeNPMpQ1s0ey6t2GlhRl+E4Eofi2O82qYm4i82MwJLC0sISQx3/6ujvYHRzVehVOw2sOJyXCIcz+7CksLSEl8GfOtrNLOJXOw0sjx3u9TM/A8vAMrAMLAPLwLI9loGVCWD5fVVoYKUpWH7fxzKw0hQsv++8G1hpClZ4OfTrWaGBlcZg+fl2g4GVxmD5+T6WgZVksJx8IZzIr4rjfYOUMDw8rPl1dXVJ/RLavp52AZaTMw0SfQ5CPO+8M7OxNK6srOj76sk8u8HOe3ABlpNTWJJxcsv/fqXDRv7u7k4uLy+TftqMnVDjAiwn50Yl86ypn74r/Pj48Ox8LDtTywVYJiYGlomBZWJgmZgYWCYGlomBZWJiYJkYWCYGlomJgWXil/wCBFimokY/bowAAAAASUVORK5CYII=\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x20634fed \"BufferedImage@20634fed: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x6dd94f95 \"BufferedImage@6dd94f95: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x393427ce \"BufferedImage@393427ce: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAFhElEQVR42u2cQSh9TxTH38bWVomyYYVQoogUiYgkCYUskJQkViLKQpGUkkhiISULiSQLSZKFJEkWsrGwsLGwO78+U1evi+fd97/6P3PPqeleM8Pz5n3OmTPfmftCoqb2CxbSIVBTsNQULDUFS01NwVJTsNQULP/s/f1dXl9f5fn5WR4eHkzhnjra1BSsmAyA7u/v5fT0VLa3t03hnjra1BSsmIzodHJyIktLSzIyMmIK99TRpqZgxWRMfUQpgKqsrDSFe+poU1OwYrK7uztZXV2V9vZ2ycjIMIV76mhTU7AULDUFS03BUlOwPptbpwKWcK3q+vpawVKLHiwHKCA6Pz+Xvb092dzcNMBw5Wfq9/f3ZW5uTpqbm/8cWD85zdvbm4q/foLFoDGARKOdnR2ZnZ01EkJvb6+Bhis/Uz89PS2Dg4NGZkhLS/sTYEXrNFdXV3J5eSlHR0dRib9fgeqG1VYgQ9FCxcCur68bgIhG1dXVHzpVfX29NDY2SldXl7S1tUlNTY3k5uZKUlJS3IPlxWkWFxdlYWFBJicnjfN8J/5GAtUNK31sBCwULVQMal9fn1RUVEhBQYEUFRVJVVWVNDU1GZgaGhqktrZWSkpKJDMzU5KTkyUxMTGuwfLqNJSWlhbTXlhY+KX4+xOobljpQ19+xya4IoLlJOMMOlAVFxdLVlaWuQLT2NiY8WAK952dnVJaWmqmQKBKSEiIW7B+w2miAdUp1NFGH/ryOzbBFREs3ujh4aGBhkFPT0+XvLw8aW1tlZmZGTk4ODB5B4V76mgDPgY+FArFLVh+O40TdX4ClcI9dbTRZ2BgwPwf/A1bNupDkTz69vZWVlZWpKOjQ7Kzs81A4rlTU1NyfHz84WGOp1JHG334AOI5YvntNBcXF55Apc7pw+sPDw+baZEp1YaoFYrk0SSlgIKHMYj5+fnS398vW1tbnwaAe+poow994zXH+g2noT0WUOnD65PHkXMR8WyIWqFIHs3KBU8ibDOIAMbgAtxXb94NY7yuCv12GqY+EnivoAIXkYuciytg8X+9vLzYCxYDybK4p6fnY6DIDwjleN1X4Zo62uhD33jVsfx2GgTh5eVlz6ASuebn500CT6Tb2NgwOpnVYMW65/cX9gr9dhqAIvp4BdXmk7aBBMvv9zY6Oirj4+OeQbV5e0jB8uG9OUewdeM94DmWgqWrwj+RY8UCVmCnQpt1LL+dxg0W0P0EamCTd5uVd7+dxp28p6amRgQ10HKD49k27hX67TRuuSElJSUiqICGwk5E48REoARSZwBsPd3gp9MgkK6trRkw2Jpx8qzvQA2PfoAYqC0dx7NtPY/lp9MwNhzJZhOZqEW/SKDSb2JiwowXUDGW30U3K8Fyw+XXYbh4mQ79dBovpxucMeR1GCPaqbclWkUFVviH4Ofx3XiD678eu/Z6Hqu8vNwUIhrPCRDRbIlWUYMVrrv4/cDB/wWUoyExPZ+dnZkPljyJDznWB0W8nCAF1u7u7o9p0rZv4/H9ucK/8IiUW0NyHINknvKV0xBx6UsyThQDKvIjJAb6eD3zztTIiQgSexsfqAjkN/q5v2qJKISetLu7ayLu4+PjJ6ehjigcrn0xxZGsA57Xp3QA2MaHKAIN1tPTk1nFsTKrq6szSTn5E9NSuDQQDsnNzY2ZKolQ0UoE+lxhACNW+JYOgiYrOLc0AARciW7kTENDQyYht1UiULB8yLEc5ZtIlZOTY3QntzTAtMWV6Y9IRRKONmWrRKBg+bAqdPbqWOkBifM4Vrg0QKLNlekSaaCsrMxcEUttlAgULB9XhsBB5GFac0sDiKLuB0zpR38bJQIFy0cty1m5kSt9JQ24tSz62fydCwrWLwHmlgaC8iUeCpaagqUWXPsHVBC2aOxsJccAAAAASUVORK5CYII=\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x2c5d0030 \"BufferedImage@2c5d0030: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x167b2e71 \"BufferedImage@167b2e71: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}],"value":"(#gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xd0941d2 \"BufferedImage@d0941d2: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x302c403a \"BufferedImage@302c403a: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x372f4b07 \"BufferedImage@372f4b07: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x355c2cac \"BufferedImage@355c2cac: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x721cfc04 \"BufferedImage@721cfc04: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x20634fed \"BufferedImage@20634fed: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x6dd94f95 \"BufferedImage@6dd94f95: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x393427ce \"BufferedImage@393427ce: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x2c5d0030 \"BufferedImage@2c5d0030: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x167b2e71 \"BufferedImage@167b2e71: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@22cda5d2 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50})"}
;; <=

;; **
;;; ## Inference
;; **

;; **
;;; Sequential Monte Carlo:
;;; 
;;; We put SMC here only for illustrative purposes and to get you to think about the differences between an SMC and an MCMC (below) scheme. An MCMC scheme is much better suited to this probabilistic program.
;; **

;; @@
;; Don't run with too many particles (up to 1000) as it doesn't work even with 10000 particles and can cause memory issues.
(def num-particles 100)
(def predicted-captchas-smc 
  (doall (map extract-from-state
              (map #(smc-captcha-MAP-state captcha num-particles [% letter-dict abc-sigma])
                   observes)
              (map #(str "tmp/captcha/captcha-" % "-smc.png") (range 1 (inc (count observes)))))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/predicted-captchas-smc</span>","value":"#'captcha/predicted-captchas-smc"}
;; <=

;; **
;;; Random-walk Metropolis Hastings (a Markov Chain Monte Carlo scheme):
;; **

;; @@
;; Start with small values to see what it does but later use 10000 for good performance (can take around 10 minutes...)
(def num-iters 100)
(def predicted-captchas-rmh 
  (doall (map extract-from-state
              (map #(rmh-captcha-posterior-state captcha num-iters [% letter-dict abc-sigma])
                   observes)
              (map #(str "tmp/captcha/captcha-" % "-rmh.png") (range 1 (inc (count observes)))))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/predicted-captchas-rmh</span>","value":"#'captcha/predicted-captchas-rmh"}
;; <=

;; **
;;; Letter identities and recognition rates:
;; **

;; @@
(def smc-letters (map :letters predicted-captchas-smc))
(def rmh-letters (map :letters predicted-captchas-rmh))
(def smc-rate (* 100.0 (/ (count (filter identity (map = letters smc-letters))) (count letters))))
(def rmh-rate (* 100.0 (/ (count (filter identity (map = letters rmh-letters))) (count letters))))

"--- Ground truth ---"
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-ground.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
letters

"-------- SMC -------"
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-smc.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
smc-letters
(str "SMC: recognition rate: " smc-rate "%")


"-------- RMH -------"
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-rmh.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
rmh-letters
(str "RMH: recognition rate: " rmh-rate "%")
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-string'>&quot;RMH: recognition rate: 0.0%&quot;</span>","value":"\"RMH: recognition rate: 0.0%\""}
;; <=

;; **
;;; Which algorithm works better? Why?
;; **
