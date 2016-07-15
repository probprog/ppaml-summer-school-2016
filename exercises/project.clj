(defproject exercises "0.1.0-SNAPSHOT"
  :description "Source code and exercise worksheets for the PPAML summer school on Anglican"
  :url "https://bitbucket.org/probprog/ppaml-summer-school-2016/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-gorilla "0.3.6"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [anglican "0.10.0-PRERELEASE"]
                 [net.mikera/core.matrix "0.52.2"]
                 [net.mikera/vectorz-clj "0.44.1"]
                 [net.polyc0l0r/clj-hdf5 "0.2.2-SNAPSHOT"]]
  :java-source-paths ["src/ox_captcha"]
  :main ^:skip-aot examples.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
