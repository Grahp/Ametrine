(ns dev.grahp.ametrine.core
  (:require [dev.grahp.ametrine.utils :as utils]
            [dev.grahp.ametrine.dict :as dict])
  (:gen-class))

(defn -main
  [& args]
  (println "Started building...")
  (let [config (utils/load-resource "config")
        user-config (utils/load-resource "user")
        output-file (if-let [user-file ((user-config :settings) :output-file)]
                      user-file
                      ((config :settings) :output-file))]

    ;; TODO paralellize
    (-> (merge (dict/generate (config :starters) (config :vowels) (config :enders))
               (config :overrides)
               (dict/generate (user-config :starters) (user-config :vowels) (user-config :enders))
               (user-config :overrides))
        utils/transform-json
        (->> (utils/spit-resource output-file)))
    (println "Finished!")))
