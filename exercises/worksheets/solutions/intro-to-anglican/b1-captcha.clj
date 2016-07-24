;; gorilla-repl.fileformat = 1

;; **
;;; # Bonus Exercise 1: Captcha
;; **

;; @@
(ns captcha
  (:require [gorilla-plot.core :as plot]
            [anglican.rmh :as rmh]
            [anglican.smc :as smc]
            [anglican.ipmcmc :as ipmcmc]
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
;;; {"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAM7klEQVR42u3chbNVVRQG8PdHyIwzjjWiYqGMBdiFhd0YWBhgK3Z3d2EntiC2InYnYHcrdhfocn5rZr95Pt9TQe57D9h7ZnPPu+ecfc696zvf+ta396UpaqutAa0p/2lqmvwT2znnxx9/jFdffTVGjRoVJ510UhxxxBFx9tlnx3333RfvvfdeTJw4sTEfZAo+Q2eM2SVB0IjvbmoP+P3338cLL7wQV1xxRey999753qGHHhojRoyI119/PX777bf6OM8ojDU12zfffBOPPvponHLKKbHBBhvESiutFNtss01cdNFFMW7cuPj1118rW8wYLNg01YH10EMPxXHHHRerr7569OnTJzbddNM477zzksn+K7Bqq4xVgVXblAHrjz/+iN9//z2FN43kVfeefZMLrDJey956nMm5r0mTJjXfk+0pHa+2BgJLQATn559/jm+//TYmTJiQ1Rzh/dJLL6VO0l9++eV4++2349NPP43vvvsuASOg7QFLZfjEE0/EZ599Fh9//HG8+eabWT2+9tprOc4nn3yS5/7yyy//Cibg/uGHH3Ksd999N8d58cUXs2ves88xjq0g62RgAQZAff755/HGG2/Ek08+Gffcc0/cdNNNcdVVV8XFF18cw4YNy37JJZfE9ddfn/ufe+65eP/99xNgX3zxxd+AteGGG8YJJ5wQt9xySzz22GNxxx13xDXXXBOXX355Vo/XXXdd3HXXXfHMM88kKACsNSBsYyRV50cffZTgZmHceOONOc6FF16Y3bb7HTNmTIwfPz5BDGAelmo3dAKwgEoAPvjggwSUgJ1xxhlpFey1114xePDg2H777WPrrbeOrbbaKrbddtvYZZddcr+Ajh49OlkNu91///3NwFp88cVj1VVXjd122y3BZcxDDjkkz91hhx2y77rrrvneueeeG7fffnuCBuNgL4AqLPXVV18lO7kWkB955JGx++67x6BBg/K+VJ/G23PPPePoo4+Oyy67LB544IF466238jNOCbhmdD/qfwPrp59+StbBNhdccEHsu+++MXDgwNh4441jo402ynRme/31148111wzVl555bQSbG+33XZx8sknJ+tgr7vvvjuOPfbYBFbPnj1j0UUXjbXWWivH23HHHWPzzTfPMTHZuuuum1+IV8A4/PDD49prr81xgEuKxVRAJdWNHDkyjj/++BxHc/3+/fvntjHWXnvtvJaxhwwZEqeddlrej9SL7TxAtXUgsASRBpLmsFMBjyBtscUWsdNOO2WgvAKIAC611FLRq1evTHdY7Mwzz0yQ3HzzzXHMMccksOaZZ56YY445Elz9+vVLcAIWxsMuxgIIIF1hhRVivfXWi/333z/To1T29ddfZ4qlxYAK8DbZZJNYccUVY/nll8/7HDBgQLIp5jI2oNnn/jHZqaeemqnxww8/nCar0kYzUevxp8b1mkeQYgRzjz32iFVWWSV69+6dAZKmBIZ2wSTDhw9Ps1OqEbRlllkm5p9//lh22WXzXCnKsdIUYM0222wx00wzxXzzzRerrbZa7LzzznkuADvOq2ONBVxACmgARIthmnfeeSf1FKbCdI5xXSAFQoUBrabbxrbMWcBfbrnlEnDu+fnnn8+CpDPE/NQGR1fXf81350unlQQBs2CkffbZJ4NPcAMerSLQUtK9996baWbLLbdMNtKxxYknnpjBpb0Aa5ZZZolu3brFEksskaxy1lln5bljx47NMb36+/TTT89UuPTSSydwsCTN9eCDD8YjjzySxYKUCyxLLrlkshZWVBAQ/RhNf/bZZ1Mfur60jbmAESjpLcxc02EHAovHdPXVV8eBBx6YqfDggw9OdvK+Ko3OKb6RdKIywygHHXRQpiUpcZ111smAApdxAEsanHvuuVPz0GE0HGuBMCemvfqb4Bd8YCiC3xiArSI1mS3tAbBx3d9tt92WbFaqPt02gEnHzgFGKVzRAKTskQqsxjNzM7AwER0inUgbJo0xAR+rGI8AZfUCIY29sIXgS2FEuoDvt99+ySRe/d2jR49kIJUlGwAQiklabATgYm9ItapFrKQDhbRpPCkUmwGdmy9zj4DUMrUBjfuzz/2dc8452V0bK7NDpiVgNSrlNVy3lQ1fOHCpxp566qlkKk8+IPCWbAuWfVKKoJlolr6kORoKywwdOjSOOuqo1DmAhcnWWGONvAa2IsYLEMqHE2jeGRuBtpKKjUnIY09go6e8R8tJ0YQ8a6QtCwFQPRCvvPJKPP7445lKfR7HT8+VYZe0G/hEqi9piR+FraQnvtINN9yQTEZo01UYhGhWCdIw3bt3j7nmmisBIegFWADVt2/f1EP0EsZoz1nHPKpShQLxDkSY0LmbbbZZgnaxxRbLa0hxxPyXX37ZphAHHNcphi3Q+tJZKiWlV0B1ELCkJ4HCTAB15ZVXpiaioZibrAHsBEw+iGADjtQ0++yzx5xzzvk3YNFEqjKWgmYaqD2TEhAAT9rCTgS6dKgyZXlgKsAyJqMVC6nwppXAzWgu/l9SoVRHWwmcFAQ80hFRXlrxoQCM0Fb2Yyy9LWDxpgCSCAfa9tgCY0pXJquZsYBFmwGvXvQVkFmZqlKV1mowu+Znbb4bekQlddhhh2X6aems+xs4gA1wpCKVn8Zu4HktsMACzcDiS00usFoylusZE2iBWpdypUcM5toWE0p17TUpz5hSLAB6LZVonZTuwKqwGJBsAQGkjTAS0xND8LhMHBPtjlW6syNUhYzPRRZZ5C/AIuIxDXAAnyqO/9XemneBN0dJw2FJwDIedtQVAu7LtVSKCgjVX1sNcMpktfQLsFx84l36bNS6+8pwbTAWr8j8G0Cp5AhoE8MsAOwgMKpG0yLSplUDZbIZiyy88MLp2FvnXoBVwMAF5yM9/fTTKaDbAoIxNWxIqAOW81SE3P+iuzDXAQcckEWFe2irwgMc+8pkOrZUgPgb2DpzWmeG+YFGyw+MrQCEthFQhqkn3lNe1luV5SsCxyAVZAJ93nnnzfTJrwIOzAVYCy20UKZUKdYSGzZASwFvPGPzxQRfoYDlaCqzAMXHMkfpffdm24MgtZYVEK3Zil7EsB4Oqx1Mit966635cPzbmq+qo/7/dZvPkO4I4wUXXDArsMIK7IfCCgVUvCh+l1UQKj7gURkCkCDyojAXYHHdgZXZaVpGwAG1aB2gMs3y8MMPpy+GmbBVcdfZHCpUjr7UigGl6DJFA6gtV6UCjaU7XHnnSKs+l5SOfVkpjWKsun6rDWDRNgLGQRdY0yCCCgiCx4oAAIYpk1SwgQeYZp111ph55plTqGMcLIG5gAPg7JcmpUe6jEdm1SjNI9BAdf7556elIdVhK1WnCpGWY6zy0ExUsyAAH1AJffuNIUUbz/yjZTIEvqrW8VKrYoI+bORvG2trA1iXXnppLmXBCMBlEhprAZdAme6RymgWATVlo3pzPOCYbJYSpSn76CIMw98COnN8JoONySy1+pS1Id3yy+g7AAAELCOd3nnnnZkigdA9sDGA3zGEvVQJQMbwA1nAAXgNMMvSGlWpa3ogCP5GVoVd0qzshHtqvmIJXFl4h7WAC1AwkH1SHNCwHYCQ3yTAxD6NhWlUgECFfTCWqR6uPAACmrVT2AazYTVzgABqHCBwfeADPGvspU22Aq1Hg6lCgQs7lvVdxqDpdPdLK9J7ulUS0qaHAltZel0rucaP3Tyaqk8wOe2YRerSeUiCAwwYQgcamgXIaCqMABCABjC0kSkfy2SKwVrGAEhAcg3defyusqDQuUQ3i0DqLb8KMi3jPWvGFAK0nXFVpK5RVroaz1jeK4xGbxH6AFpXNnQwYwki59sqAM47gAAMBvIqVakUi09F/wiyTsSr3LxPq9FD9isIVHUYwxNhhSl7wjp1AJOudACgv5xrctkardZr3m17j+ZT3RkLeDEU7eU+dYDDYB4Qus1aLxqsLP2prWPYraml881LIn5VWwADHCaFaSAL8QSKnmEz8KQETLe4TgAF3Lp3/hZNJr2Wv00w68xVY9A8vC0L/8oyHdM07ABVZ1u/0vEegNBcxqKr/GDCwkL3qRuPb6WiVSSUXw9Nj5PP0wRjFSuBA64K1DCYAFp6UpbSWDfFwxJgesX6LBWjqszyGjpGdabbFliWBdA6rqyekNaAU/A58s51DAP1n35NU36iRoS7hqkowGZ+ulfbHg734x6L/1Ztg04CVuvgAZkAlrk2APq/c23Fa8I8xgYiXfAnd9zWY7lP3Xjeq7+G7oLAqq22Cqzp0AOqwKqtAnt6AVZX/i8hK7tVxqqsVoFVW2Ws2mr7J2BNS1phetQ1nbIaoVGT0FWnNFUxXoFVW2cx3X+9RlMtuae/9NwVYlBRUFsFVi0QKrC6ftXSQWCZUUE5WXZDfXJr6xKM9Z8riArYf/0epuZ31CHVY9UvtTWi/QnC8DN+X7DjUAAAAABJRU5ErkJggg==\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x74b06cc7 \"BufferedImage@74b06cc7: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}
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
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x6180c273 \"BufferedImage@6180c273: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x58585ffa \"BufferedImage@58585ffa: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAEt0lEQVR42u2cTyh8URTHZ2NrSYmyIQsU5U8RKRJRklAUopCULFgqCwtFIiWRxEJKViLJQhYWFrKQZG9hYWNhd359br3Jb36DZ5p75735nVOveXP/NDPvfr7nnvvueRMRNTULFtFLoKZgqSlYagqWmpqCpaZgqSlY4bCPjw95e3uTl5cXeX5+NgfnlFGnpmAlZAD09PQk19fXcnR0ZA7OKaNORaBgJWQMzNXVlWxubsrs7Kw5OKeMOhWBgpWQoXoGCKCam5vNwTll1KkIFKyE7PHxUXZ2dmRgYEAKCwvNwTll1KkIFKy0BytM31XBUrAULAVLwUoLsO7v7/9a3tM2lUt8BSvkYPX29srKyoqcnp7Kzc2NnJycyMHBgWnHK+8pBzKXgIVJBApWzGDl5+eb1db09LQsLi7K8vKyWXmNj4+bNrzynvLj42MzoAyei4ELgghib9LynVxBHGqwsrOzpaysTNra2qS/v1+Gh4elq6tLOjo6okv81tZWM6AAtre3ZwbOBVypFIEHFADFAuzKk4carMzMTMnJyZHi4mKpq6uT9vZ26ezsNJB1d3dLS0uL1NTUSFVVlTQ1NcnExIRsbGw4gStVIqAtfQATQGMBduXJQw1WRkaGgQtvUF9fL0NDQzI3Nyfr6+vm4JxBrK2tlZKSEvMKXAyaF+ekkwg8qOjDbwQeQAVYD15XnjzUYEUiETNgQNPX1ydLS0tydnYmd3d35uCcMurKy8uloKDADBrAnZ+fW91acS2Cz1ABJH35rYAKsIALwPEgnpqaSrrYQu+xGCjUv7CwIJeXl1HVeReaMupoQ5/S0lIZHByU7e1teXh4sDYduhYBQAAGgADVZ0AB9juI+ZyZmRkzLRJzJeOahD7GqqiokMnJSTk8PPznonBOGXW0oS39UCywsSFsazp0LQL6AyDQAApgAqgfiPkc4j1iLjxeMq5J6FeFP0FCGXW0oS39mAZQKCsjW9OhSxHQF/AAEBABhb4/QQxceC5iLl4Bi895fX3V+1jEDLh3lBhP0ZRRRxvaep5gbGzMLLttZRq4FEG8fn4gxnOtrq6aAB5Pt7+/L7e3twqW322SVGyvuBQBwAEeAAKiH09nOxFRwXIEls3vCnCAB4DeNOgHYpup0wpWGoAVxA1vjbEcxVgKlq4KQyeCRMDSqTBN7mPZFEEiEGvwniZ33m2KIDZ4z8vL+7af3m7wAVZWVpZUVlbKyMiIrK2tBXqv0JYIYm835ObmfgsxoHGHHY9GloXeIP0iY4ABIB2FPbKgZjfYFIEHCmCwNePFWV9B/NkzAqJu6cSJW4qKigwwgNXT0xPYfCybIvBgYRMZr0X2xHd7hbSbn5834PF9uEZfebf/AqzYh0AbGhoMQMQWXBgGIqgZpLZF8JvsBi9Pi8+rrq429ZQny1uFDqx4j62jRgZwd3c3etGCkPPuWgS/zcdqbGw0Bx6NVGk8WrK8VejAirdEvri4MCsZXHyQntJJhQh+k0EK1KOjo9FpMtl/VpI2/4/1/v4eqEeqUiUCvznvTI1bW1smsLchOv1HvzQUgZ+ndFhp2gwNFKz/CG59rlAt9KZgqSlYagqWmoKlpqZgqSlYagqWmlqS7Q8XxDHgqGbTzQAAAABJRU5ErkJggg==\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x688ac2d3 \"BufferedImage@688ac2d3: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xf99d074 \"BufferedImage@f99d074: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x68ed18ae \"BufferedImage@68ed18ae: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x72502dc6 \"BufferedImage@72502dc6: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAFuElEQVR42u2cTUhVXxDA27gKWomBGAhikGih5gekiZ+pKLqQEFQUUkokkBCDUIQEF0EiiBCSiOQiAhERUSRaiESIiIgLkRbhxoULNxHt5s9v4Mrt8tR61Lsf/xk43Mv58OGZ35xzZs68d0VMTP6BXLEpMDGwTAwsEwPLxMTAMjGwTAwsE5MQgPXz5085PT2V4+Nj+fr1qxbeqaPNxMCKSwDo8PBQNjc3ZWFhQQvv1NFmYmDFJaxOGxsbMj09Lc+fP9fCO3W0mRhYcQlbH6sUQD148EAL79TRZmJgxSUHBwcyOzsrnZ2dcvPmTS28U0ebiYFlYJkYWCYGlomB9XfB2tvb+yXGRV+KxboMrLjAam1tlYmJCVldXZUvX77IysqKvH//XvtReKeONiC7DDBvENYB1ED9H4GVnp6uIYdnz57Jq1evZHx8XMMPvb292ofCO3W0LS4u6soGGF4oHKAAJxag8YLqt0TNUBIC1vXr1yU3N1caGhqkvb1dHj16JC0tLdLc3KzAlZWVSWlpqZSXl2v90NCQfPjw4WzbdE8+k0s98MUC9E9BDQpQUTOUhIB17do1XbXu3r0rtbW18vDhQ90agYz3qqoqyc/Pl8zMTO0PbMAFEEwiE+hAxcS+e/dOoeFv1NfXnwVhGxsbY4JKX8YwNkhwRdFQEgpWUlKSpKSkSE5Ojir/6dOnMjY2JlNTU1pGRkYUssLCQklLS9MxAMEkAsPJyckZVG/evJG+vj6prq6WoqIiuXfvntTV1SmgfB5PYKP+zp07Wujb39+vcHlXQb+hipKh+AIWKxYKHhgY0Enh3nB3d1fL2tqavH79Wtra2hQ+4AKMwcFBXf75ewDBOKAqKSnRfjwBEjABlM/kCbRPnjzRz7t165auhLzTb3193ff7SjdUUTEU37bCgoICVfbc3Jzs7++fnRGcSQYuFF9ZWSk3btyQ27dva3/OFtvb22dKePz4sdTU1Oh5DYVMTk7qWADlc3kC7du3b7UdhXj/nt/3lfzvUTIUXw/vWCEThNK9FgZcAMEkYqGsbu7Y1+fPnxWu+fl5nUy2Up6xtoPv37/Lt2/fZHl5WV68eCEVFRWSmpoaqCAtW3uUDMXXcAPAAA4TFetMcFFQdWtr65f8LiYTSwU26gHJcdFjKQKwgwZWlAwlEJH38ybhd6P1Djw7Ozu6kn38+PEXF927dWRkZOhWHCRFeBMhw24ooQXLGeN2z1HEzMyMQuR20VkZm5qa5P79+5KVlaVKuHr1aqAU4Q6GRsFQQg2W1z1n6+jq6lKI3C66U7Ds7Oxs3TaCqoioGEqot0Kve85hF/e8uLhYA6xMvuOio5SOjg7tQ8Q/iFtHFA0ldGBxeI/lnhOt9wZcnesPvCvuJlGK18sMwlYYNUMJJVifPn3SrQKr5pxBHCcvL0+j0C9fvtR7RSfg6lzYMoZsCqLZQcsJOy+OFVZDCS1YpNowuRxgid8QxyGQylnEuU90X8peFhfzWxGsVlEylNCCtbS09NtxsViR/KBtHYASJUOJzIpFWyyw3GcXtg7n7jFoh90/CSCHwVBCfcYitsOlNN4RbY6Fs224rz9QAFB1d3drtgRZFVyCB3nFCruhhNorZHJJo+EcgjKcMwlwuS9sR0dH9UDM3SQXtsnJyYEDC1iiZCgJAyueb0JfNMY5wHL+IAEQz8mbYtLT06MFBZChyiGX/CUsHGWctyr45RVGyVASBlY8v91w2RjHRceigcubFAc0pJyghOHhYVUIuV9AhudFH9qcgOuPHz98jWNFyVASBlY8vzZz2ZjL0njdqbvAhztPPy5vOfTSRjIhWQJYuJ9guWNZUTCUhIEVz+9j/c6Yi7544P2yAakpZAk493CAindJBsHR0ZHvioiaoSQErEQoxftVqYtADOoPwEXJUCIBVtQkKoZiYJkYWCYGlomJgWViYJkYWCYmBpbJP5T/AAJQG7Q3Gu70AAAAAElFTkSuQmCC\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x4c72f505 \"BufferedImage@4c72f505: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAFZUlEQVR42u2cTyh9TRjHz8bWkpKFDVkglD9FpEhEJCEUYkGykVgqykKRSElJspGShUSShSQLC0mSLGRjaWNh9/z6TB2d7nvvfc89HJ177vPUdM6dmVPz/c7zzDzz3JmxREXFB7GUAhVVLBVVLBVVLBUVVSyVJFOsr68v+fj4kPf3d3l5eTGJd/IoU1HxpFgo0PPzs1xdXcnBwYFJvJNHmYqKJ8VidLq8vJTNzU2ZmZkxiXfyKFNR8aRYTH2MUihUY2OjSbyTR5mKiifFenp6ku3tbRkYGJC8vDyTeCePMhUVVSwVVSwVVSyVP5bIEBD94AwDfX5+BjZE5Eqx7u/v4wL0G0QyE/wTvOC4ubmR4+Nj2dvbM33Bk9/k393dye3trZyfnwcuRBRXsXp6emRlZUVOTk7iAoQAPzoxDAR7wYxRYMyHh4eyvLxsVudjY2Omb3jym/yNjQ1ZX1+X+fl5mZycDFSIKKZi5eTkmJADDV5cXIwLEAIgAjC/pVxhIdgLZoxld3fX4MC4m5ubv0NA7e3t0tnZKcPDwyb19vaa8srKykCFiGIqVmZmppSUlEhLS4v09/cbEAACmA0SQAAHDERAyG8oV5gI9oIZQxkfH5eGhgapqKiQqqoqaWpqkq6uLtMXHR0d0traKjU1NVJQUCBZWVmSnp4eKL84pmLRUBpMwwEAEAABDIAABTDAIQAiIOSnyhU2gt0KUzajM4YE5urqaiksLDRPsM7OzppRmcT70NCQ1NbWmpkFzGlpacmhWDSUBtNwAAAkEhyAnQRACMTYzr4S7F4wprOzM4MJQ8rNzZXS0lLp6+uTpaUlOT09Nb4kiXfyKIMbcFuWlRyKRUNpMA3/P3AQABEQAjEQ5NW3CRvBbkfpx8dH2draksHBQSkqKjIYGI0XFhbk4uLiexawR3TyKKMORpVUIxYNdguObyAEYiAIohKdDsNIsNtRmoUGOJjqaX9ZWZlMTEzI/v6+8ROdXPJOHmXUoW5S+ViJguM7iIEgiEp0OgwjwW5HaUIn09PTxo90w2MkVyy2kkKxaGii4PgOYiAIohKdDsNIsBvBOIjLjY6Ofo/SLFDwI5nyo4385FFGHeoyWieFYtHQRMHZ0yEEQVSiS/0wEuxGvP6dFuS/4awggQsjwapYqliqWKpY4VIs9bHUx9JVoa4KNY6lcawUj2Np5F0j775OhynxXyEdlJGRIeXl5TIyMiJra2v6X6HP02FK7G6wt80warAnC7C6u8Hf6TAl9mPhBOfn55sORbG6u7t1P9YfKpebDY5wQN+wITOwq8LIk9B1dXWmA1n64xjTwUHdQRpkgt3itQ+D0N7r62szze/s7Jht4bG2ZFPGVmz4CGwcK9rdDfgvNBKATD1+73kPI8Fup3/nhSz2QRF8TVK0QyT0FXVZuGBkP40h+qZY0W6b4dQLp19wlP/ilE4YCXa7YHEaNUayuroqR0dHhvvX19f/HHsjj/75rRiib4oV736syHN7fp0rDCPBbuTt7c0csZubm5O2tjbjMzK9M2M4QyzOfnp4eDAjOQYEXowJ14ARHGMPzLlCJTg4f+lkZ2dH3RKOQfHE+PA9p6amzMIGzHwXK5Cc8ooVRoLdugAYAX4shlRcXGzid5EhFlwAnozOGBKLGWJ8HHmjHmVBMSZLCQ7GqhAjwHBYiIDBDuU4QywsSHgymtfX15uVO09ieXzH90ExJksJDtbKkLZjGIy6kSEWYnaRoR7qUZ/vgnSlgKUEByuWZd9TwVQeea2AnZyhFur5eX9GaBQrbAT/FL8zxGKnv7iQJXSKFTaCfyNYbIdY7JQM1zRZSrBKSiqWiiqWiooqloq/8g+evTj5yVujYAAAAABJRU5ErkJggg==\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xda9968f \"BufferedImage@da9968f: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAGiElEQVR42u2cO0gcXRSAbWwtUiiIQkAUiyga8AG+CGpEUSIiGhJFRcGI2ISgjSAYYqEokiCKRIJoEYSQIoQECSIiImIhIiISgqSxtAnB7vx8F0bmn8y6O+tjZ8dz4DLjPHbXe757XnPvJIiKyg1IgnaBioKlomCpKFgqKgqWioKlomCpqChYKgqW/+T8/FzOzs7k9PRUfv78aRr7HOOcioIVlQDQ8fGxbG5uyqdPn0xjn2OcU5gVrKgEhW5sbMj8/LwMDQ2Zxj7HOBcUmBWsWxasBYoFqJqaGtPY5xjnggKzgnXLcnR0JB8+fJCOjg7JysoyjX2OcS4oMCtYdwCsWHyngqVgKVh+KA+gCHtW9efPH08Zl4J1x8GygAKU7e1t+fr1q3z8+NEogy1/c3xvb092d3flx48fEWVclyl5f3//UoijLQ0oWD6CCmWi6M+fP8vU1JQJdvv6+oxC2PI3x+fm5mRmZkZev34tL1++DJtxuSn56dOnMj09Ld++fbsUYiCLBjAvYIWz0PFS90rwK1QocmlpyYCC4uvq6i4yqsbGRmlubpbu7m7Tnj17Zs4XFxeHzbicSr5//765ByjHx8cvhRjIgZ3f50XBkYJlH1Crq6sG6uXlZQM8Vvn379/y9+9fBesqUGGJ+vv7pbq6WoqKiqSkpERqa2ulpaVF2trapKmpSRoaGqS8vFwePHggqampkpSUFNbNOJWckpIi+fn5Ul9fbz4XUIEWeC2QgRa4AQzY+X1e4IoELOeAGhkZkYGBARkcHJTZ2VkD2q9fvxSsaARTz2ilY4GqtLRUcnJyzBal09m4PRr7XV1dUlFRYawOUCUmJnoGi/uAEjiBFFiBlu8DYmAGauAGcn4X0HuBKxxYbgMK0B8/fizt7e0yMTGhYF1F6Fw6EGhQYmZmpjx8+FCeP38uk5OT8v37dxOs09jnGOeAD0ASEhI8gwWM3AucQAqsToCBzA45igd+K9i/CliWa7Wg6unp+d938bdXkBUshxs8PDyUhYUF6ezslNzcXKMALMjY2Jisra1ddKw1wjnGOa4BjGgsFjACFooMBzCQAzvQAxyDIJJHMqHAApj19fXAQeUrsBj5ZHKAgvuh8wsKCkycsbKyYgJxe8eyzzHOcQ3XRhNjASNQRgow9wA98DMIGAzhFO6WMBDDASfx07t37wIFla/AovNI6wlWiWlQAIChUIBzczlOGAnEo4mxvAIcyW8LlzDwP5IQ9Pb2/uNq4x0qX4GF8kivX7x4ceEGCZ6Jc3BJbp3LMc5xDddiCaLJCr0CzH2AwSBgMIRzh24wZ2RkGNcKqABljyexYPEMla/AirY67fU+N7fkFWDLHTIIGAzhZiiEShju3btnGvvp6elSWVlp3CNxXTxDpWDd4PeESxicjZIHMRylha2tLfMMVB/pKFhqsTTGij1Yl8VY1NHev38fcY1MwfJpVhiLGMueFVK24BGSVSOjSEsf8FzSmaEqWHFUx4pFVmivY/Fwe3R09KJGFqoorGDFWeU9FnUse+WdmQtYJyDFWgXFJeqzwhhU3u2/cWdnx8ADRMAUFJd452c3cE9ycrIUFhaaijfFydt6Vmh/CO2EN95d4p2fj2VNm8EKMVWF77zN2Q3WtBmsUpBcYtzPIEXpAMFkvWiyQu7Jzs42wPA5ra2ttz4fy26tg+ISfRW8W3O96Wyqz7ihxcVFM2U41HRhzjGtGOAiqWM5F48+evTIAETpgOAccGI5gzQoLtFX5Qb7+w2sRQzEMTS3BQ5kZFxLpwNCJPUlt+XuxFB8LhDj/q57znukK6HtLnF4eNgAVVZWZkCnJMH/HC+zSH1VILUrHCv09u1b+fLliwmiT05O/lm1wjGWfXmpL7m9oIPPYLEC33MTq3S8vLvBcomUN968eRO38959AxYrUKjpMDKfPHlignLiHKyJ0wVYbvPg4MBYGSwUQGGtGN1YF0BwC3Yve6WQc/HrdS298vK2maCs1PHtI520tDTX6cIomy2jnXjn1atXJqgGKu4LVej0S/wYyWrtIKwt9FWMhZUhxsFS5eXlmazIWcPCPbHF/WGpCKzJoFhTyHWcC2WtVO5oVsioxDKR6QGJlebba1gE1Gxxl1VVVSarY0uKzn3cH88PbxWsG8wMgQPLg1tz1rDIlJxlAK7jeu4L8lvyFKwrxiLWi0CIlZxL3q1mLwNw3VWyNpWAgxUKMHv6b7XrKAOo3DGwVBQsFRVX+Q/AkvzU6GrIWwAAAABJRU5ErkJggg==\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xd01c7fc \"BufferedImage@d01c7fc: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x7e4ce587 \"BufferedImage@7e4ce587: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}],"value":"(#gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x6180c273 \"BufferedImage@6180c273: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x58585ffa \"BufferedImage@58585ffa: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x688ac2d3 \"BufferedImage@688ac2d3: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xf99d074 \"BufferedImage@f99d074: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x68ed18ae \"BufferedImage@68ed18ae: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x72502dc6 \"BufferedImage@72502dc6: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x4c72f505 \"BufferedImage@4c72f505: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xda9968f \"BufferedImage@da9968f: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xd01c7fc \"BufferedImage@d01c7fc: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x7e4ce587 \"BufferedImage@7e4ce587: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@b9fbe77 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50})"}
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
(defn- smc-captcha-posterior-states-l [query num-particles value]
  (take num-particles (infer :ipmcmc
                             query
                             value :number-of-particles 500 :number-of-nodes 8)))

;(defn- smc-captcha-posterior-states-l [query num-particles value]
;  (take num-particles (infer :smc
;                             query
;                             value :number-of-particles num-particles)))

(defn smc-captcha-MAP-state-l [query num-particles value]
  (let [states (smc-captcha-posterior-states-l query num-particles value)
        log-weights (map :log-weight states)]
    (nth states (max-index log-weights))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/smc-captcha-MAP-state-l</span>","value":"#'captcha/smc-captcha-MAP-state-l"}
;; <=

;; @@
;; Don't run with too many particles (up to 1000) as it doesn't work even with 10000 particles and can cause memory issues.
(def num-particles 5000)
(def predicted-captchas-smc 
  (doall (map extract-from-state
              (map #(smc-captcha-MAP-state-l captcha num-particles [% letter-dict abc-sigma])
                   observes)
              (map #(str "tmp/captcha/captcha-" % "-smc.png") (range 1 (inc (count observes)))))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/num-particles</span>","value":"#'captcha/num-particles"}
;; <=

;; **
;;; Random-walk Metropolis Hastings (a Markov Chain Monte Carlo scheme):
;; **

;; @@
;; Start with small values to see what it does but later use 10000 for good performance (can take around 10 minutes...)
(def num-iters 10000)
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
;;; {"type":"html","content":"<span class='clj-string'>&quot;RMH: recognition rate: 70.0%&quot;</span>","value":"\"RMH: recognition rate: 70.0%\""}
;; <=

;; **
;;; Which algorithm works better? Why?
;; **
