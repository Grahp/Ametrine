(ns ametrine.core
  (:require [ametrine.utils :as utils]
            [ametrine.dict :as dict])
  (:gen-class))

(defn -main
  [& args]
  (println "Started building...")
  (let [config (utils/load-resource "config")
        user-config (utils/load-resource "user")
        output-file (if-let [user-file ((user-config :settings) :output-file)]
                      user-file
                      ((config :settings) :output-file))
        _ (println "Config loaded.")
        dict (merge (dict/generate
                      (config :starters)
                      (config :vowels)
                      (config :enders))
                    (config :overrides))
        _ (println "Main dict built.")
        user-dict (merge dict
                         (dict/generate
                           (user-config :starters)
                           (user-config :vowels)
                           (user-config :enders))
                         (user-config :overrides))
        _ (println "User dict built.")
        output-dict (utils/transform-json user-dict)]
    (utils/spit-resource output-file output-dict)
    (println "Finished!")))
