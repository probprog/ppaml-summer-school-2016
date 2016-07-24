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
;;; {"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x2b523fd5 \"BufferedImage@2b523fd5: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}
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
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xd4a6811 \"BufferedImage@d4a6811: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x607bd242 \"BufferedImage@607bd242: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x6717af99 \"BufferedImage@6717af99: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAEyElEQVR42u2cTSg1URjH78bWwkYppcQKRYnytRAiSpLkI0Qh2ViwUYpYECmJRBYspGQlkiRJkoUsJMnCxtJGsnve/k9dzXsbzNyZOcb7/v91uueeM5dxnp/nec7H3IhQVACKcAgogkURLIpgUdTvBev9/V1eXl7k+flZHh4etKCONvRRBCsuAaD7+3s5OzuTnZ0dLaijDX0Ek2DFJUBwenoqKysrMjIyogV1tKEvaDDpWf9RsGAgwACgqqqqtKCONvQFDaYpz0oZBuvu7k7W19elo6NDMjMztaCONvQFDaYpz0r9MrC8ft6UZ6UI1o/+HoJFsAjWT4IVO5uCEawzqpubG4JFsNwDBYguLi5kb29Ptra21BB4xXu07+/vy/z8vDQ3NwcO1neQf7VsQLBCABYMBGPBG+3u7src3Jwmuv39/WoMvOI92qenp2VoaEiT4bS0tMDAst7T4eGhwr25ualgX11dydPTk7y9vf04wATrG6jgjTY2NhQgeKOampqP2VR9fb00NjZKd3e3tLW1SW1treTm5kpycnIgYMXe09jYmAwODsrw8LAsLS0paI+Pj76A5RVggvUNVMvLyzIwMCAVFRVSUFAgRUVFUl1dLU1NTQpTQ0OD1NXVSWlpqWRlZUlKSookJib6DpbdPQHkyspKaW9vl5mZGd/A8gNggmWjaDKOQYUBi4uLJTs7W18BEwZ6cXFRC+pdXV1SVlamIRBQJSQk+AoW7sUKVU9Pz1/3hPdoRz+u85Jj+QUwwbIRBhaDB2jgqTIyMiQvL09aW1tldnZWDg4O5Pr6WgvqaEMfDA2wIpGIb2DBuCcnJ75AZRJggmUTBm9vb2VtbU06OzslJydHBx/hbmpqSo6Pjz8GNfrfjTb04Rp4LT88Fn4OcjjAjfCzsLDgi6FNAUywbMIg9s0ACnIpDHx+fr7mGNvb2zorsg4q6mhDH67BtX7kWJgAIJ/DhKG3t1dDsB+GNgUwwbIJg1ibQqIKw2LwARhAA3B2JwBiYfRjVgg409PTNQQDVhjZGpIBQDyGNgUwwYoRvA+m1n19fR9hEDNAJOrIqewGFm3owzW41o91LIRTwJWUlKQF9dTUVCkvL1fvgtwuHkObAphg+bQy7fdeISYAsQVLGcjjMDM7Pz+X19dX8fr3BQUwwQopWKY8VlAAE6yQgvVViMK62erqqi4PuD31SY/1n+dY1qQayx7YOgJUgAuLsZhcYP8ydpYaFoAJVkhnhdZlAGxyj4+Pa3iKekO7dTWvs0I/ASZYIV3Hsi5cYuMXxoWRYWwvHsUUwAQrpCvv1s9fXl4qPIAIMHnxKKYAJlifhMOw7BVa9/CsAMfrUUwBTLA+CYdhOt0QPXUAo3r1KKYAJlifhMOwnceyAu/Fo5gCmGA5gMvJCVKUlpYW7S8sLHT9vJ6bA3hePIopgAmWA7icnHmHZ0NonJiY0LPvbp8wdvogqdWjjI6OKlAlJSUKOGZ0WCr56hCeKYAJlgO4nDylg0QeZ8GPjo7i+k4EN4++Rz0KljgmJyddHRs2BTDBcgnYZ0+sYD/Ny7e4uPmyDi8POpgCmGCFRG6/XijeR7NMAUyw/jOZAphgURTBoggWRbAoimBRBIsiWBRFsCiCRREsinKuP97QtzQKN8peAAAAAElFTkSuQmCC\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x6fc23540 \"BufferedImage@6fc23540: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x3196d249 \"BufferedImage@3196d249: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAHuElEQVR42u2cXUgVTRjHvekq8EIiIRSCKEgq07CCPqRviiQJkSijoqIighApECMo6sIooogijBC9kEC8EDEiQkREIiIiIiIiuumii24iuntefg+MTMvuObvndc/OOc7AsOvMrGc+/vN/PubZrRCffEohVfgp8MkDyycPLJ88sHzyyQPLJw8snzywfPLJA8snDyyfPLB88skDyycPLJ8KSn///pVfv37Jjx8/5MuXL5q5p4w6DyyfCkoA6PPnzzI1NSXDw8OauaeMOg8snwpKsNPk5KQ8fvxYLl++rJl7yqibF8AK0vanT5/+oe7fv3+XNK1nkZgjWApA7dmzRzP3lFFX1sAygGKgMzMzMjY2JkNDQ/L06VO98jfl7969kzdv3sjLly+LSuthgLezywCnf8zjsWPHZMWKFZq5p4y6sgUWC8GivH//XkZGRuTOnTu6o86dO6cTwJW/KX/06JE8ePBArl+/Lp2dnanTei7A29kGP22TACxtlp6XwDKgYkEGBgYUKIcOHZJ9+/bN0nZra6u0tbXJyZMnNR8+fFjrN27cmCqt5wO8nW3w05ZneDbXwheLpecdsGxQwUTnz5+XXbt2yYYNG2TTpk2yd+9eaW9vl46ODjl48KC0tLTI1q1bZdWqVbJkyRKprKxMbZLiAJ7+cG1ubpYtW7bItm3bdAPQlmd4NgpcxWTpeQcsdhsTyyIAqs2bN8vq1av1CpiuXr2qE0rm/sSJE7qIS5cuVVAtWLAglUmKC3h+lytgo7y+vl4zbS9evKjjYnxBVik2SycBVj6xnIX+mBhYdPbFixcKGhZj+fLl0tjYKEeOHJHbt2/L8+fPVQyQuaeMOsAHsCoqKlIBVlzA85tcb968KWfPntUxrFy5UsfBPe0Yn80qWbB0XGDZLEq/EceDg4MyPj6uovj79+/y588ft4HFID5+/ChPnjyR48ePy5o1a3TATCQL9erVq1kxYgZMGXW0gbXSYqyfP3/OLvyZM2dk9+7dsn//fgXB/fv3ZwHP73FFJPX19Wk94KitrdXxADYWx2aVLFg6DrCCLMpvX7hwQS5duiQPHz5UoH39+tV9YDHBLAhAYZcy2KamJh3Ms2fPdDFs2uWeMupoQ9u0dCyAxQ5lt5oJ5hqmN2Gxffv2TUZHR6W7u1u2b9+uzBLVryxYOh+wwliUjcSGOnr0qNy6dat0gMVAsHrYEexyBgvAABqAC7N2gmCsrq5OTRTaRyCwDhML2CgHSEb/CGOsqH5lxdK5gGWsVwOqU6dO/cOi/E15LkPEKWCxMCwY4sJMMLoFIoDFirKkqKMNbZnotJR3o8Aa8Lx9+1amp6fV5LfdAkEda9myZZFMmhVLRwELwExMTDgNqsTAKtQELqbpHFRmYRpAYbsFAPiBAwdUwa6rq1O2WrhwYWi/smLp4JyxIbE6EcfoT+iNroKq7IAVpswivgCR7RYwGaDks9yyYungnAFO+ouLA+MEo8FVUJUVsKKUWVwC+JF27typADO+LNgLJZc2DQ0NkayS1ZiDzwN8RDZGA+IVQNlGBAzmCqjKSseKcgmsW7dOlWj0HUST8WUxDhYDrzjsFdUvV4CFAQC4qqqqNHOPi2THjh3KzFijroCqrKzCKJcAnvBr166pMk0/jC+LTYL1dvfuXRUvUYvvCrBwWQQzIpxNg2sBIwU3Skke6bjsxwqyqdnNKO2c6wWjF+IyqSs6Vlkzlsue9zArKgoApm8sBovC4kQxqStWYS4dC08/PrmwM86SOYR29awwLrPYSj46lt03l/1YtlXIpkbEM+/MP8dHAN8wc8kp7y5HN4QxixGFLLJ9TgjgjR9o/fr1snjx4sh+ueJ5t/1YhOWgN/L/jf4W1p+SApar8VgAnj4x6exmAGCUd8BlRzYQI0W/6SuRDYsWLcq5+C6dFTLnRC7ATmwiNq2LIrEoEaSALJ+/aC78WIgBJrynp0cBHQT76dOnNcNU9Is+E+gHAGCtKPHpWnTD69evtT+AiN9yUSQmVt7t8zhMXHZof3+/9Pb2RkZTUpfPXzSXvixEH+AKgt0wKeC4cuWKMldXV5eCDCaiDXXmoNdEBbgWj2UOoW2R65pITOxuCEYQoNcgJshh8d8ovrRlsLAYg4+Ke5rLc8J8ocOAjz7Tjp0P01CHGCOchoW1w02KzdJxwmYMQ7soEhM7SO2XKGGhe/fu6UIgOuzQFBMaSxnRBbZVxS5nMgDeXL+lE/dlB+qJ4aJ/5sCaDRCMvMyKpeM4WA1DuygSEwGLyWbisUhMdAA7k10epF6zIB8+fNBFgKEAFGzFrmbyWeC0dlTcOPB830jIiqWTRJC6KBL/15FOTU2NKrFB68gOpkNsoMegkzC5PBflA3IxZcXScd+EDjNaePvIHGXxe1lEkSbWsWAZrB+Yau3atUq9+V5YQA+BpokyoB11abLVXKasWDrJtxtso+XGjRtOxL0ntgrZHTATOgQTzMRFvWLFQhCugknPFT2A53i+FNgqS5ZO8rUZF9/UKcjzzuCYUJiHCYt6KZRMOfW0oz3PlcqneLJk6aTfx3Lt3cKCPO+21cUujHqNPWjeF/KNhKzTfGTpzDzv+cz6ufjohmusNZ9YOlNg5aLgUvhMkGdpx4Hlk08eWD4VLf0HcukHG8lTdPIAAAAASUVORK5CYII=\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xf41fffb \"BufferedImage@f41fffb: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x55f0f2ed \"BufferedImage@55f0f2ed: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x2be6b526 \"BufferedImage@2be6b526: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x2c653e89 \"BufferedImage@2c653e89: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAE4ElEQVR42u2cTyh1QRTAv42tJRvKhh3lv0IS8ifKQlIUZYGkJLFRysJCkZSSSGIhJSuRZCELyUKSJHsLC1u78/Wbuq/3ve+9h2vud+f6zqnJNDPvZM785szcM3PvL1FRCUB+qQlUFCwVBUtFwVJRUbBUFCyVNPL+/i5vb2/y8vIiz8/PJpGnjDoFS8WXANDT05NcXl7KwcGBSeQpo07BUvEleKeLiwtZX1+X6elpk8hTRp2CpeJLWPrwUgDV3NxsEnnKqFOwVHzJ4+OjbG1tSX9/vxQUFJhEnjLqFCwVBUvBUrAULAVLwfqqJMZwMCTJTyzHpi5XwLq7u4tkn0IDy4MAA11dXcnR0ZHs7e0ZY5LIU0YdbdIZ0KYuV8Dq6emR5eVlOT4+jmSfQgELAzDbmI2Hh4eytLRkHq9HRkaMcUnkKaOONrTlN4nGs6nLFbDy8vJMyGFiYkIWFhYi2ad/DpYHAjNtZ2fHGIfZ2dbWFovheIky6mhDW34TbzybulwCKzs7W4qLi6W9vV36+vpkcHBQurq6pLOz0/Slrq5Oamtrpb6+3pTPzMzI/v5+bNn878CKB2FtbU1GR0elqalJKisrpbq6WlpbW6W7u9sk8pRRR5vx8XEDhGc8m7pcAyszM9N4rbKyMmlpaTF9YFIAGfnGxkYpLS2V/Px80x7YgAvPxbLomtcKHCwGkcFkUAGhpqZGCgsLzV+MNjs7K6urqyaRp8xrAxBTU1Mx472+vlrTFfZAJIKVkZEhWVlZ5n/t6OiQsbExmZ+f/6s/FRUVkpOTY36D52JZZKK55rUCBwsPc3p6agzD4DLjSkpKpLe3VxYXF+Xk5ERub29NIk8ZdbQpKir6w3gMhi1dYQ9EMrDwWPRrcnLSTB7ODZP1B/iAC4/MZGFD79r5YqBg4RUeHh5kc3NTBgYGzOBiRGYks/H8/Dy25/GWOcowILMTd89fYDg7OzOGtqELPXg/15bC8vJyGR4elu3tbbm/v48t/15/gItJ1dDQILm5ucYGtOdp0bXzxUDBwjAMIgPPngcDYjzcPBvPxCWJPGUYcGVlxWy6MeTu7q4Bi2XMhq6bmxvnwGLzTr/oHzZL9Kj0B8/FssieC+/mclA1ULCYZbhp3DVuG0OkM54HY7J7SrQHChu6XLjzlCzcADCAA0DJ9oBRitYHChYeAzeNu/aWro+Ml+pmJe1ZImzociGw6AcSBSsAQ/y0szUFS8FSsH4CWKmWr+vraxMUtaFLl8IfBtZnNqipNtw8BHBAa0OXi5t3Besbm3diLx89UqcKERBSACIbulwMNyhY3wg3EC1OF3sCDqLiAMQhbHxQk/Y8FdrQ5WKAVMH6YoCUwWUwOU7xjJEqWg4cQAIswBN/DMNRDsmGLhePdBSsL4g3wETM8TRc/Uh3vke7ubk5AwsgAITnkbxblDZ0uXgIrWD58FqfvZHg3a0CgqqqKlNPuedhbOrScEPEwfrqHSruHZHwQtykxAt5HsamLgUr4mAlwvXRrU/2QUNDQ7GlLTE0YFNXmOLnTegovT3t3J13lrONjQ2zGU/1woBNXWGJn283ROl7D869pcOT32deErCpKwzxE7yN0hdq9L3CkMTPcVOUvqmlb0KrKFgqCpaKgqWiomCpKFgqCpaKioKlomCpKFgqKgqWivPyG1TEcDr59EgDAAAAAElFTkSuQmCC\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x2cdba2d9 \"BufferedImage@2cdba2d9: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}],"value":"(#gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xd4a6811 \"BufferedImage@d4a6811: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x607bd242 \"BufferedImage@607bd242: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x6717af99 \"BufferedImage@6717af99: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x6fc23540 \"BufferedImage@6fc23540: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x3196d249 \"BufferedImage@3196d249: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xf41fffb \"BufferedImage@f41fffb: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x55f0f2ed \"BufferedImage@55f0f2ed: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x2be6b526 \"BufferedImage@2be6b526: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x2c653e89 \"BufferedImage@2c653e89: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x2cdba2d9 \"BufferedImage@2cdba2d9: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50})"}
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
(def num-particles 1000)
(def predicted-captchas-smc 
  (time 
    (doall (map extract-from-state
                (map #(smc-captcha-MAP-state captcha num-particles [% letter-dict abc-sigma])
                     observes)
                (map #(str "tmp/captcha/captcha-" % "-smc.png") (range 1 (inc (count observes))))))))
;; @@
;; ->
;;; &quot;Elapsed time: 135692.922 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/predicted-captchas-smc</span>","value":"#'captcha/predicted-captchas-smc"}
;; <=

;; **
;;; Random-walk Metropolis Hastings (a Markov Chain Monte Carlo scheme):
;; **

;; @@
;; Start with small values to see what it does but later use 10000 for good performance (can take around 10 minutes...)
(def num-iters 1000)
(def predicted-captchas-rmh 
  (time 
    (doall (map extract-from-state
                (map #(rmh-captcha-posterior-state captcha num-iters [% letter-dict abc-sigma])
                     observes)
                (map #(str "tmp/captcha/captcha-" % "-rmh.png") (range 1 (inc (count observes))))))))
;; @@
;; ->
;;; &quot;Elapsed time: 129329.446 msecs&quot;
;;; 
;; <-
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
;;; {"type":"html","content":"<span class='clj-string'>&quot;RMH: recognition rate: 10.0%&quot;</span>","value":"\"RMH: recognition rate: 10.0%\""}
;; <=

;; **
;;; Which algorithm works better? Why?
;; **
