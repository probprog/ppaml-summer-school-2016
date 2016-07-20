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
;;; {"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAM2UlEQVR42u3ch49VVRcFcP4ITUyMqFE0do2CHXsDK9grKvbeQAXsHbFXBLtiBXvvvYAV7DR7773tL7+dnMk43zBxgBlmnHOSk3fnvXfPPe/tdddee53zplvMYuvWrVvU1nlbW8Wv2+y+6E8//RRvvfVW3HHHHTF8+PA4/vjj44ILLoiHH344pk+fHn/88UeNZlcA7Owe8IcffohXXnklrr766jjssMNir732imOOOSbGjRsX77zzTvz+++/1W6/Aan379ttv4+mnn44RI0ZE//79Y5111oldd901Ro0aFa+99lpNJxVYMw+sJ554Ik499dTYaKONYuWVV45tt902Lr744mSy3377rX7rnRSYrZlnlwBWmwnURuNW1poJYP3999/x119/pfCmkTzqnvNaa4FVxmvcm47Tmnn9+eefDXNyPLPj1daGwBIQwfnll1/iu+++i88++yyrOcJ70qRJqZP0N954I6ZMmRKffvppfP/99wkYAZ0RsFSGzz33XHz++efx8ccfx3vvvZfV49tvv53jfPLJJ3nur7/+mtdvCUzA/eOPP+ZY06ZNy3EmTpyY3bHnvOY93tsUZLPKLpWdWgkswACoL774Iv9+/vnn44EHHohbb701rr322hg9enRceuml2S+//PK46aab8vWXXnop3n///QTYl19++X/A2nLLLeP000+P22+/PZ555pm45557YsyYMXHVVVdl9XjjjTfGfffdF+PHj29gvaaAcIyRVJ0fffRRgpuFccstt+Q4l112WXbH5vvII4/E66+/niAGsJbAWvVUGwILqATggw8+SEAJ2LnnnptWwaGHHhr77rtvDBw4MAYMGBA777xz7LbbbrH//vvn6wL60EMPJatht0cffbQBWD179owNNtggDjzwwASXMYcNG5bn7rnnntkPOOCAfO6iiy6Ku+++O0GDcbAXQBWW+vrrr5ORNCA/4YQT4qCDDoo99tgj56X6NN4hhxwSJ510Ulx55ZXx2GOPxeTJk5N9Owq4uhSwfv7552QdbDNy5MgYNGhQ7LLLLrH11lvHVlttlenMcb9+/aJv376x7rrrppXgePfdd48zzzwzWQd73X///XHKKacksJZeeulYfvnlY5NNNsnx+Fo77LBDjonJNt9889h4443z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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x4ba2df87 \"BufferedImage@4ba2df87: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}
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
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAJWElEQVR42u2cXagOQRjH3xtXypUoUUqEju985DPHV+QrCfkIISQliRIpyoUikZJI4kJKp5NEkgtJkiRJkgu5ceHCjeTu0e+pOc0Zu3tm5t3dc95jpqZ3z8zs7uzsf57n/3zsaUgqqVRQGmkJ/q/y6dMnuXnzpmzfvl3GjBkjjUZDK8e00ceYBKxUCsufP3/k+/fv8urVK7l9+7YcO3ZMNm3aJCtWrJBly5Z1q7TRxxjGcg7nco0ErFQyQXX16lU5dOiQrF69WhYuXCiLFy+WNWvWyIYNG7RyTBt9jGEs58SCKwGrH5efP3/K+/fvVfocOHBAlixZIrNnz1bJtG/fPjl79qxcuXJFK8e00ccYxnIO53INrtXvgMVu4cHYOV++fNHKMW0xYvp/KazRkydP5NSpU7Jy5UqZOXOmAufIkSMKmOfPn8u7d++0ckwbfYxhLOA6evSodHR06JqHrHWjVXbe58+f5cWLF3L//n2tHNMWupNaTZXZGwpSbW+qX79+5W44+j5+/Cg3btyQHTt2KFDmzJmjUunWrVvy4cOHro1p7kMbfYxh7MSJE2X9+vVy4cIFVYkha91olZ3Hjrp27ZoSSyrHtNHXXwEFUHihDx8+lLt376rFxi9/046kefPmjTx9+vSfDff161ddH1Tc8uXLZfz48QqWgwcPKnhev36tQLUrbfQxhrEjR47UX6QW9wxZ65YAFgvMogEoY8FwTBt9/ZFww2tQQUgLnnX//v3qDuCXv2mHXMOPzpw5I4cPH+624d6+fatgABQGJFOnTlWrD9XIuQDVrrTRxxjGDh06VKUWEgxAh6x1SwDL9b2U7XNpJdfA2rVrVT3t2rVL6+bNm7V/1qxZ3TYcUgwwAArAMXjwYBk1apSCjGuwflmVPsYwdtCgQdFrnYBVEvdx+U+oUeG6BowVZ7gR6gy3wNatW2XdunWyatUqmT9/vrS1tcmwYcP+AUFnZ2e3NRs4cKAMHz5cJk+erC4Fzs+qgHPBggUyffp0vXasdkjAKon7uPyHMSEAc10Dc+fOlQkTJugvYEJFGdcAxzt37lQAoOIA1YABAwqBxRgfieXWWD6bgFUS93H5D2MY6+tctF0DSKrRo0crz9myZYucP39eHj9+3OUa4Jg2+gAfoHHDMi6w4EuACvUJoXf5VV6NtcBbHljGeZdnkpfh56o6LEKf7RqAE/GMqCZA8OzZs65rmLnQRh9jkFquxHr06FE3jsUY5nfixAkFC+vmWoVZNXYtG6E8ogw+UQaweHkXL17UBSwyyUNVUiz3oXJMm3EuEhbx8VzTZ7sGeD44Dmb/vXv3/nFOckwbfYxhrMuxAJ5tFdLHtbkH98qbT1nO6EYojyiDTzQLLLP7MLHPnTtXaJKHqqRmuQ9tZoyv55q5hYLABSOqzgYWPqkQsLrqHrWMZGPj4iv79u2b/P79Ow5YPjyiDD7RLLBYxClTpmiYghcJb8AEh5SWHamP5T6M8fVc85JttcUzIgEBK9fNmjNt9DGGsWw2lyL4qtcsdc/zsnY8z4MHD/QdRAHLh0fY5ui8efPUbGXhykizCAEWYh8TG3MYk5t5YYIDsiyVhKSJidTHch9eBnNhrfgFWEiPHz9+lGqc9HSe76ZgrNkYly9f1vVi05LxQOwQ0EcDy4dHMGl+ARvtkyZN0hrCJ8oAFkQVcLFLATmmd5FK4jcmUh/LfXhBly5d0g3HXO7cuaPqpG5g+apxxjejyguBFTIBk2LBjceNG6c7gWPGsUPKjt9lZT0CLOboo5Ji5xfLfUID5lUBy9fwMAIj1vgoBJa5+d69e2Xp0qUqCpkIotEVmSzq9evXtZ+JjBgxIjqmFCuxkFa+5jjnMD/UGWoN9eaz+2K5T6hVVQXHMoAMoThlctMuYCGqEdnsakQ9v1kXJh2DyDmE7vjx49Le3q58p0qnZRbHCjXHfc3t3nDMVmEV2vPzNcrKNMi6gGWLb3YPKgOwmRQM48vKklhFD1WVVRi68JwXmgJSF7Cq8GO58/NxI5XpQmrYD8diG/CQdvHy5UuNktuTcDlWs1HwWD9WqKow6jBEXccAK0YVVuF5951fVU7vRpGDjAdl8rbYNIn3mPkkjyE9iJzXCayyyG0VgI7Ndi07Vtjb6US5fiwekN0DiLJiYqiVvJSN/gIsl1RjpBSp4Fh3QxXZDX0GWK5ZapLvSSCzPxUyJG/btm06Bg943RyrLmC5pJp8piLuAzhYQ14+0QBfB2kV+Vh9BlhZu2XatGn6ACyk+VTIkDzcEMTqWLw8U7fVOZYBCsAgwmDAmcd9bEId8zFCaAYpIKtrc0cDK0u/8wCnT5/WxTKfChmSx6KSXcCDV50j1VtWoQELpjfnoXqKuA/jWC+AB6jMxwt5Qd8sQs2zYjRxXT5sIMieF2Cnr67NHQ0sl0ssWrRIH8C4821Lwdc51+p+rFDuYyQMgIJC0E97T9LKJfzG7GezU7NSgngGxvI8SLEYiVwLsHxVjRHX7CgWFQDWzbHq8rzH5GPBR6lINCQK69RTnM39vA0pBPnHCc36235E4xKgDVdQMxK5domVF1KwFxmOZZu7dQNryJAhMmPGDNm9e3e3sFOZscIQ7mMq9IGwmFGTPim95DqR94QaNa4cpB3XyEpvKfvj0kqB5YYUjCpEnWSlVvBCebG84KpN3by0GRbTxDSryG7I8/EV5bxzb6ISAMLXe51lfWYF2O3Ih/kcng0TwudqB5Zt/TBRQ95ZPDuzgY8jeUmIXzIb+F6tbmCheseOHauAAVgbN26sJB8rj2QXfaWDRAyNsdluCp6BT7SQsj1llyAxeU8hfK52YBnrh2R7eInLIfbs2aMVSYWpiyog0Y+dhdTyicjHFvdLaO7LvVhcdinAqSqDNCQsEhsSsR2r8DJAYjZGVnqL/S+H+MVZ6svnageWsX4QpYDL5RDGOcdLPHnypEouRDEgQyowhj6TFhuSbejjqHT/dwMqgnvBM4xFVkXOe13FWIaAg+dhw/iktzCO8b58rtdCOj7/MwDwIfIZB59oNj/ad9Ht+BsWEWESpGOVX+nUVVw1yxr7pLcwri8+Z8OXQ7gvi/AEJm8ZX3SEqp6if+VT96dpVQOs6vSWWoBVxCGyXlb6h2jVA6zq9JZagZVKKglYqSRgpZKAlUoqCVipJGClkoCVSioJWKkkYKWSgJVKKglYqfSl8hcG/u7sk8EF2gAAAABJRU5ErkJggg==\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x49aa2aa8 \"BufferedImage@49aa2aa8: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAFi0lEQVR42u2cTSg1XxjA78bWkhJlwwr5pnwl5CMiSUIhG5KNxEYpykKRlJKSZCMlC4kkC0kWFpIkWcjG0sbC7nn7nRrd//y97n3nvXfuzLzPU6c7nTkz59x5fuc5z3nmnAmJikocJKSPQEXBUlGwVBQsFRUF61+Sz89PeX9/l7e3N3l+fjaJY/I4p2CpOBIAenp6ksvLS9nf3zeJY/I4p2Bpr3cktPPi4kI2NjZkenraJI7J45yCpb3ekdAJaC9ANTY2msQxeZxTsLTXO5LHx0fZ2tqSgYEByc7ONolj8jinYGmvV7CCJn5WjoKlytG2K1jBAOvu7u4/s13Kkrwy61Ww4hTWsCv54+Pjj8Me37W9p6dHVlZW5Pj4WK6vr+Xo6Eh2d3dNORLH5HGOOhIFmO/BiqRQenY8wbLqp067osOVfHt7Kzc3N3J2dhZ12MMOVmZmppl8TExMyOLioiwvL5uJyOjoqClD4pg8zh0cHJj/z7NwG66Q34GKpFB6Nj2cnh5rsGgDSkN5KNGu6HAlr6+vy9ramszPzxswogl72MFKTU2VgoICaW1tlf7+fhkeHpauri7p6OgwwNXU1EhVVZXU1taa/JmZGdnb2/saNhWsGCqUno0iefD0+FiBZbUBeHd2dkx9wNvS0vIV1kDhKBgASL29veZ8eXl5VGEPO1jJycnmPxQXF0tTU5N0d3ebOoGM4/r6eikqKpKsrCxTnjqAi2dEHW5arZBfoYpWoTx0ejg9nR4fC7DC24AlGhsbk4aGBikrK5OKigppbm42iqbuzs5OaWtrk+rqasnJyZG0tDQDSDTtsIOVlJQkKSkpkpuba+45Pj4uCwsLxhKSZmdnTZ2lpaWSnp5uruE50MFoq5tWK+RXqOKp0Ehi+W6ATRsqKyuNsvmlbhQcruyhoSEzTGFtaAOAOAWLe/CfJycnTf0MpfhvpJOTE1laWpK+vj7THuDiuUxNTRnXwM03Db4Cyy2FRhIUdHp6aupAyQw9hYWFRqEoFgX/Ttm0IxQKOQKLa0tKSmRkZES2t7fl/v7+a9ZndTrqo111dXWSkZEheXl5pjx+p5tvGnwFllsKjWQ1Hx4eZHNzUwYHB43iuCfWkWHp/Pz8axZmKZs8zlEGyJ1aLIZyrDL3wlLZhzbq47/TsbDcsfQpAwuWmwqNZDVRKvdFydwPK4K/wwzM7iRzTB7nKENZpz4W/wFgAAeAvnPGvRIQ9g1Ybio0ktXEX8FvwX/hfj9Zke/aHu0kwgkkCpaHFfqTACv+Cn6LZTUjWRGnQ5SC5YK4qdB4vCJyCxIFy8MKVbAULAVLwQoGWOpjqY8VFx9LZ4U6K4zLrDCRcSwFK8BxrERG3hWsAEfeLeuZiHeFClYch8NEvyu0rGciVjcoWHEcDr2wusGt5TtO9kR6ZR+lrseKQVviteDQyS5ur+z8DvwKUidLgiPVb23eAIirqysz7LI+imXQsVwi7eS7E175VkXg17w72cQQaTgOV5y1cQPfj/Tdpg7qoiwTCaAHqmgW4Dn5Uo5Xvq4T+F06TrZdRZpAhA81wLq6uiqHh4emrpeXl/9tQyOP+sNDJYlaMqxgORiaYrVR9Cd5fX01W8rm5uakvb3d+HD4T8xAw0Me4W1jCTFDJRYKoLBWidrkoGD55A0AGxaYmdpDHgDOL9YNX5DND0w0gIrrfhfYVbD+UcG6YGXw27BU+fn5Jp5mD3kwJPPL8IelYvJAzI0JBOU4F1RrpWA5HHqxMlgmZnpAggWyhzyYRPDLcMlGUnYn80tsjeu4PqjWSsH6C6uF8w8cWB6GNXvIgxiadUw+5ylHea7zw6cqFawEz0jxlb77QIc99EG5RH8FRsHyecjDa58VUrACEvLw2ofQFCwVBUtFRcFScVV+AaS37kzcgxIuAAAAAElFTkSuQmCC\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x280125ba \"BufferedImage@280125ba: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAEx0lEQVR42u2cTSh0URjHZ6PYWCklSoksEEqI2PiIKCQJhSyQlCzYKEVZKJJSEkkspGQlkiRZWFjIQpK9hYWNhd3z9jt1pzHvzHXH3Pe9d3ieOt3jnDPnnDy/+Z/POwFRU/sHFtB/gZqCpaZgqSlYamoKltrX9vHxIW9vb/Ly8iLPz88mECeNPAVL7VsGQE9PT3J9fS2Hh4cmECeNPAVL1eBbRn+urq5kY2NDpqenTSBOGnkKlqrBtwzY6RdANTY2mkCcNPIULB+aH9TgK3t8fJTt7W3p7++XvLw8E4iTRp6C5UPzgxooWD/Q/OA0BUvBUrDUEsdp4StT2g1dnd7f3ytYPxWsSM4PByDW7QmrTuq4ubmR4+Nj2d/fN23z5G/ST05OZGVlRbq7uxUsv1i8amDn/HAAKOMUMMrQB9o/OjqS5eVls2gYHR017fPkb9IXFxdlcnLSLCyys7MVLD8AFY8a4HQ754cDQBnrM3ZwWVDR/u7urvk87Tc3NwdXpm1tbdLZ2SlDQ0PS19cnLS0tUlJSIunp6QqWl1DFqwbr6+tyeXlp63wrkEYeZSjLZ6LBFQoVbYyNjUl9fb2Ul5dLVVWVNDU1SVdXl4Gpo6NDWltbpaamRgoKCiQjI0NSU1MVLC+hikcNKL+wsCA7Ozu2zicQJ408ykxMTJh2gTrSzr01/FKGequrq6WwsNA86cvs7Kysra2ZQHxwcFBqa2sN9ECVlJSkYHkJVTxqAHio2MzMjCPnk2aVob2pqSmjlAzD4apF/87OzsznKJubmyulpaXS29srS0tLcnp6Knd3dyYQJ4086qZ/gUBAwfrf5pYaNDQ0SE9Pj1GuWJxPmaKiIqOGDLMAHqpaQPbw8CBbW1syMDBgytIegKOQFxcXwSHU+pKQRh5l6KcqlgfmhhrgvLKyMqNklZWVjp1PXcDLUMsTsDhvfH19/QQ+adSDelIvbY2Pj8vBwcFfCkecNPIoQ1mdY3kwDLqhBsyzcnJyJD8/3zydOh9QV1dXzZwOsPf29uT29vYTWLTJapShkqGZugGMPgBctDlZKIy6KvRgGHRDDQhpaWnGgTydOt/J1RvaY6tjZGQkCD5zPoZmVDTaKpI8ylBW97E8GAbdUANUKzk5WVJSUszTqfOdXBb87vGRnhV6aG6pAfOs0OCmExWsBDS3nKZgKVi+AcvpUKhzLAUrGHDkV853OnnXVeEvnmOFT96zsrJsnR/LdoPuY/3iVWH4dkNmZqat86mDHXbg5OzRboNUd95/8T4WQLE5WlxcbM4QrWE1mvND6wBouyMdt04HFKwE3HnnyXFOe3u7OYzmLNHO+Rw2z83NmTpoE7iiwWx9AfR2QwIOh26oQSy3G6xrOQBVUVFh8kmPpFbWF0DvYyXgcOiGGsR6H6uurs4EIOXiINBGUqtIcDm5M0bgtgX5wKtvQnswHLqlBrHcIAWA4eHhoCo6eT0/lluu9IUvw/z8vFFS/e0Gj+GKVw0YZpzceUf9Njc3zZwt1hcqnNzLZ+hm6+L8/Fx/bcYPcLmhBk7e0mFe5+Qliq8Ai/Ym0fv7u/4+lp/gclMN/sV7hYlkClYCqoGCpaZgqakpWGoKlpqCpaamYKkpWGoKlpqagqWmYKn9NvsDJAQsc2KUe9EAAAAASUVORK5CYII=\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xc3dca09 \"BufferedImage@c3dca09: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x66a999d9 \"BufferedImage@66a999d9: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x7ece10ce \"BufferedImage@7ece10ce: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAE2klEQVR42u2cTyg+QRjH34urI+XkQg4oyp8icpCIkoRQiAPJxYGjg3JQpKSkJLlIyUkkOcjBwUEOktwdXRzcnl+fqVdv21rWb3ffGfs8tb1rZtbMPvN9Pju7M7sZUVOLwTLqAjUVlloo+/j4kLe3N3l9fZWXlxezsU8aeSostV8ZAnp+fpabmxs5Pj42G/ukkafCUjL8ymjD9fW17OzsyOLiotnYJ408FZblnZxPMgQZ505bEFRHR4fZ2CeNvLwLy9aItKWT80mGIHt6epK9vT0ZGxuT8vJys7FPGnl5F5atEWlLJ+eTDE4Ly9aItKWT89mBTgvL1oi0xZkqrF8Ky1bHpV1Y3rEvdeWOfx8eHlRYKqzwgkJEt7e3cnp6KoeHh6Y+fvmb9LOzM9nY2JChoSEV1l8TFtQIokrYu2rKcyz/9+TkRNbX182wZGZmxtTJL3+Tvrq6KvPz82boUlpaqsJyDf9+/oES0AJqBFGFtv9UYFlRcdzBwYEREPV0dXV9jn17e3ulv79fJicnZXR0VLq7u6WmpkaKi4vdElbUEeki/r3+gQ50MrSAGkFUgTr4EH8F+SpXVNvb2zI7Oyvt7e3S0NAgTU1N0tnZKQMDA0ZMfX190tPTIy0tLVJZWSklJSVSWFjohrDiiEhX8e/1D3SAEtCCjoYeUASaZMkCZfAh7YY++CpIXFkaUxZRNTc3S1VVlfmljqWlJdna2jIb+xMTE9La2mp8gKgKCgrsF1YcEeky/r3+oSOhBLSAGtADitA2qAJdoAy0gToIBQoFiYv0i4sLIxqOKSsrk9raWhkZGZG1tTU5Pz+X+/t7s7FPGnmIj/ZkMhn7hRVHRLqMf69/oAN1E4BQA3p4iUKbc6nDueGn7NDC64fHx0fZ3d2V8fFxqa6uNvVwvisrK3J1dfXp26zPSCOPMrTDCWLFEZEu49/rH+hA/bT1O6JAHeiDnzgHqOSdzcAPzHIgFHxLHXV1dTI3NydHR0dmuJHrU/ZJI48ylHVijBV1RAZNJbmAfz//4JufEoVjoBA0gkrQKVcolGfMurCwYAKW8giM4xGcnz+9YnTirjDqiPzqMugK/v2IHpYoQWKhLDdE09PTn37gykAQ42+/KwBp5FGGsk48x4o6Ir+6DLqCf78xaFiicBw0gkrQKTf4fvsc0bm5wqgj8qvLoCv497trDkuUbPBBJeiUO7mfGmFFHZF+5hL+4+741Agr6ojMR2e5JKzUjLGS6GQV1lP67gpVWMkSPTXPsZLoZJfHWFGPQVPz5D2JMZbLd4Vx3DWnZq4w7rtCl59jxfGcLxWrG5J4juXyk3fqLSoqkvr6epmampLNzc3/nplIxXqsJJ68uzxXmJ2k55xZAYIIophLDbuEiG14eNjkNzY22vcmdBIR+ZdWNzBUqKioMO1EWIODg5Gt/giz6JH/iW+Wl5fN2jnrvt2QVES6in/ve5dtbW2mXdyoMBSg3VGuV/vpMm2C/O7uTi4vL+382kySEeki/v3eFIfY+Gx/f98EWxwrbL97seT9/d3u72MlHZGu4d/v2xZQAlpAjbjfCbDRMjZHpCv4D/oaj5cacb3F5KSwbIpIm/GvFlJYGpFqsQhLTU2FpabCUlNhqampsNRUWGoqLDU1FZaaCktNhaWmpsJSy6f9A8g/T2wR4nflAAAAAElFTkSuQmCC\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x5fc3c658 \"BufferedImage@5fc3c658: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x3dcc222b \"BufferedImage@3dcc222b: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x1701131a \"BufferedImage@1701131a: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x19472954 \"BufferedImage@19472954: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x58f7cd22 \"BufferedImage@58f7cd22: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}],"value":"(#gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x49aa2aa8 \"BufferedImage@49aa2aa8: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x280125ba \"BufferedImage@280125ba: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xc3dca09 \"BufferedImage@c3dca09: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x66a999d9 \"BufferedImage@66a999d9: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x7ece10ce \"BufferedImage@7ece10ce: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x5fc3c658 \"BufferedImage@5fc3c658: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x3dcc222b \"BufferedImage@3dcc222b: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x1701131a \"BufferedImage@1701131a: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x19472954 \"BufferedImage@19472954: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x58f7cd22 \"BufferedImage@58f7cd22: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3856d05b transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50})"}
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
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/num-particles</span>","value":"#'captcha/num-particles"}
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

;; **
;;; Which algorithm works better? Why?
;; **
