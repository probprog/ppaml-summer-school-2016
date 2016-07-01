(defproject examples "0.1.0-SNAPSHOT"
  :description "Source code examples for PPAML summer school lecture on functional programming"
  :url "https://bitbucket.org/probprog/ppaml-summer-school-2016/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [anglican "0.9.0"]
                 [lein-gorilla "0.3.6"]]
  :main ^:skip-aot examples.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
