(ns ametrine.core
  (:require [ametrine.utils :as utils]
            [ametrine.dict :as dict])
  (:gen-class))

(defn -main
  [& args]
  (let [config (utils/load-resource "config-1")
        output-file ((config :settings) :output-file)
        dict (merge (dict/generate
                      (config :starters)
                      (config :vowels)
                      (config :enders))
                    (config :overrides))
        output-dict (utils/transform-json dict)]
    (spit output-file output-dict)
    (println "Finished.")))
