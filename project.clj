(defproject ametrine "1"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/data.json "2.5.0"]]
  :main ^:skip-aot dev.grahp.ametrine.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
