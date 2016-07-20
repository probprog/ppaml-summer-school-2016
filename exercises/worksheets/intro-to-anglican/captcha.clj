;; gorilla-repl.fileformat = 1

;; **
;;; # Captcha
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
        [utils captcha])
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
;;; {"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x629a3da7 \"BufferedImage@629a3da7: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@39b864de transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}
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
(def xs-1 ...complete-me...)
(def ys-1 ...complete-me...)
(def letters-1 ...complete-me...)
(def salt-and-pepper false)
(def test-captcha-1 (render xs-1 ys-1 letters-1 salt-and-pepper :mode render-mode))
(def filename-1 "tmp/captcha/test-1.png")
(render-to-file xs-1 ys-1 letters-1 salt-and-pepper filename-1 :mode render-mode)

;; Second captcha
(def xs-2 ...complete-me...)
(def ys-2 ...complete-me...)
(def letters-2 ...complete-me...)
(def salt-and-pepper false)
(def test-captcha-2 (render xs-2 ys-2 letters-2 salt-and-pepper :mode render-mode))
(def filename-2 "tmp/captcha/test-2.png")
(render-to-file xs-2 ys-2 letters-2 salt-and-pepper filename-2 :mode render-mode)

;; View the two captchas
[(image/image-view (ImageIO/read (File. filename-1)) :type "png" :alt "captcha-1")
 (image/image-view (ImageIO/read (File. filename-2)) :type "png" :alt "captcha-2")]

;; Inspect the log-likelihood value of the ABC likelihood described above for different abc-sigma's
(def abc-sigma ...complete-me...) ; Standard deviation calculated from each pixel (pixels range from 0 to 255)
(observe (abc-dist test-captcha-1 abc-sigma) test-captcha-2)
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
(def abc-sigma ...complete-me...) ; Standard deviation calculated from each pixel (pixels range from 0 to 255)
(def letter-dict "abcdeghk") ; Captcha letter dictionary (keep it reasonably small for good inference)

(with-primitive-procedures [render abc-dist overlap-abc-dist index-of-sorted retain-visible]
  (defquery captcha [baseline-image letter-dict abc-sigma]
    (let [;; prior for number of letters
          num-letters (sample (uniform-discrete 3 6))

          ;; prior for the letter positions and identities
          [xs ys letter-ids visible?] (loop [xs [] ys [] letter-ids [] visible? []]
                                        (if (= (count xs) num-letters)
                                          [xs ys letter-ids visible?]
                                          (let [x (round (sample (uniform-continuous 0 ...complete-me...)))
                                                y (round (sample (uniform-continuous ...complete-me... ...complete-me...)))
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
(def letters (:letters tmp))
(def observes (:observes tmp))
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-ground.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x1229be52 \"BufferedImage@1229be52: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}],"value":"(#gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x1229be52 \"BufferedImage@1229be52: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50})"}
;; <=

;; **
;;; ## Inference
;; **

;; **
;;; Sequential Monte Carlo:
;; **

;; @@
;; Start with small values to see what it does but later use 10000 for good performance (can take around 10 minutes...)
(def num-particles 4)
(def predicted-captchas-smc (doall (map extract-from-state
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
(def num-iters 4)
(def predicted-captchas-rmh (doall (map extract-from-state
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
