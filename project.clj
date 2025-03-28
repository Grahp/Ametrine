(defproject ametrine "0.1.0-SNAPSHOT"
  :description "Finger spelling"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/data.json "2.5.0"]]
  :main ^:skip-aot dev.grahp.ametrine.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
