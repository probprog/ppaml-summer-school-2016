;; gorilla-repl.fileformat = 1

;; **
;;; # Captcha with solutions
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
;;; {"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x2542f41f \"BufferedImage@2542f41f: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}
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
(def letters (:letters tmp))
(def observes (:observes tmp))
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-ground.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x2b53ea77 \"BufferedImage@2b53ea77: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x124640da \"BufferedImage@124640da: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x424bb195 \"BufferedImage@424bb195: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x7777cf33 \"BufferedImage@7777cf33: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x5ac45a8b \"BufferedImage@5ac45a8b: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x11148a3b \"BufferedImage@11148a3b: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x5b7f303f \"BufferedImage@5b7f303f: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x7b38a706 \"BufferedImage@7b38a706: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x445dba1d \"BufferedImage@445dba1d: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAGjElEQVR42u2cTyhmXxjHbayUlShZKI00IZQ/DdEUifxr0hBTaGYxkpImNkpRFmomkZKSZCMli0k0yUKSZDFJ0mQxzcbSZprsnl+fU0f3d+e+3Pf13nfem+ep03udc273z/mc73nOc86VImpqAViKvgI1BUtNwVJTsNTUFCw1BUtNwUpuu7u7k9vbW7m5uZHr62uTOCaPMjUFKyYDoB8/fsjR0ZFsbW2ZxDF5lKkpWDEZ6nR4eCjLy8syPj5uEsfkUaamYMVkDH2oFEA1NjaaxDF5lKkpWDHZ1dWVrK6uSl9fn+Tn55vEMXmUqSlYCpaCpWCpKVhqClZ8wTo/P/9fjIu6GudSsGIGq7u7W+bm5mR3d1dOTk5kZ2dHNjY2TD1++Zt8IPuXgLmDuzzLc4A/lGDl5uaakMPo6KjMzs7Kly9fTPhhcHDQ1OGXv8nf3t42qkYjJrIBLVAA5AY/KPi9IHYC/Pv374StYIQSrKysLCktLZWWlhZ59+6dvH//Xjo7O6Wjo+M+ztXc3GxUDcDW19dNAyYKLq7BtQAasN3gxxv+hyB2Avz9+3c5OzuT/f39wFcwQglWenq6ZGdnS2FhodTW1kpra6u8efPGQPb27VtpamqS6upqqayslIaGBhkaGpKlpaWEwGWh4loADTwADugW+njC/xjEToB5B4uLizI9PW3UPsgVjFCClZqaauBiSKyrq5OBgQGZnJw0L43EMZDV1NRIUVGR+QUuGs86+0FDRSOOjIxIW1ubvH79Wurr66W9vd2AT+KYPMqoQ91o4fcDMSqOmqPqpJ6eHlNeVVUV6ApGKMFKSUkxYAFNb2+vfP78Wfb29ozUkzgmj7KysjJ58eKFUS6A+/btW2DriwALuDQyIHPNV69emYb8+PGjzMzM3MPPMXmUUccqq1/43RDb66HSqDWqDcB0MNQcVUfdUXnUnvcXZNgmtIqFWvGyaKCDg4P7Xm5fOHmUUYdziouLpb+/X1ZWVuTy8jKQ4ZDrAi4A4//RyIDz6dMnAwxDjoWfY/Ioo44dtsfGxsyQhoI8dI9uiJ3qDExuBUfVUXfeG1DxDhUsDx+rvLxchoeHZXNz869G4Jg8yqhDXc6jFwMbjRrv4ZBrAizgArBVDlRpbW1NLi4u7mdf1tkmjzLqUBf4Gbbwh1Cih+7RCTFAosqo82MKDny8P1RfwfKYFT4GCXmUUYe6nEfjoQjMkuI9HLqv9/LlS3M9wAae09PT+xiWTeRRRh3qoiZ+7tENMUDyfH4VnOuoYkWIY+E/IPP0SK8hgzzKqENdOxyiDkzB4+2s0njAABQWEhQEZxpVwQ+yMSybyKOMOtSlw/i5R69OE62Cq48lsa8VJnKNkYYDBqAAjoyMDMnLyzOQMTOz8St3oow61PXb2G6I/QzzbhiBWMEKAVjua6WlpUlOTo6UlJSYkAJDkFdiyo9TjYowY/MTAnBDzPWiVXAUVcEKIViojx/Fcic/QcswvA/1sQKcYAAVQUmGH7d/FSn5WWZRsJ7RrNA9PNmF8omJCQMLMSf3rNAr+VkYVrCeURwrFofa3mu0Ow7Ux3pGkfdoQwDOZRnUjGAnysb+MnYg/Pr1S/78+aOzwkSDlZmZKRUVFfLhwwdZWFj452uF0QQtnVDZBWTuDced+/769at55khgaRwrwKGQhVQajzU51sqSYXeD32UWnsd2ADoF98ZzsOOBtUOGuYfA0sh7gM57QUGBAYYG6erqSor9WH4XhnkWrw4QzSK0rhXGacbl/BKagCMA4bwi7TRiMuwg9buVhcb06gDsyfKrqrq7IU5DjPt/N9ADeSEs4vLykmXPu5/Ndzbi/pQOoPux4jTEuP/bDPu2mT0h9cn2lY6fPe/x6ADR7iAFMlwHvhfQWeEjsR73lyfJ8mnVY1/pPKUDON8Hz3t8fGz8KNSbr5YiqTdl7HUHOI1jhdwifVf4lA7gVnALKM48yUu9cSWoy8wQFQt6iUvBCqG5fU5UaH5+3sS/cA1+/vz5l3qTh/uQqCUuBSuERlSeCP3U1JSJfeGU4z8xofEKxD51G7SC9YwUy7mkw74vry+WbCDW+eEGM0egstumIy03KVjP0FAXVIYwC0rFZkICpJECsc5PzQii8k0h9SgLQq0UrBBPCFAZlImZHpDYQKtXINb5cSy/BEs5j/ODUCsFK+SqxcwQOFAehjU/gVjqUZ/zgvyv0wpWyMMYNk6Gr+QnEEu9RASOFSw1BUstPPYf8DBLvk5MwYoAAAAASUVORK5CYII=\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x6565e8fb \"BufferedImage@6565e8fb: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}],"value":"(#gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x2b53ea77 \"BufferedImage@2b53ea77: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x124640da \"BufferedImage@124640da: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x424bb195 \"BufferedImage@424bb195: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x7777cf33 \"BufferedImage@7777cf33: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x5ac45a8b \"BufferedImage@5ac45a8b: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x11148a3b \"BufferedImage@11148a3b: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x5b7f303f \"BufferedImage@5b7f303f: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x7b38a706 \"BufferedImage@7b38a706: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x445dba1d \"BufferedImage@445dba1d: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x6565e8fb \"BufferedImage@6565e8fb: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@5ead5af8 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50})"}
;; <=

;; **
;;; ## Inference
;; **

;; **
;;; Sequential Monte Carlo:
;; **

;; @@
(def num-particles 10000)
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
(def num-iters 10000)
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
