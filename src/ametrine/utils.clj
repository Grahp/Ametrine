(ns ametrine.utils
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.data.json :as json]
            [clojure.java.io :as io]))

(defn starred
  "Takes a stroke, and returns it's starred version
  If it cannot be starred, returns the unmodified stroke"
  [stroke]
  (if (some #(= \* %) stroke)
    stroke
    (or
      (some (fn [[regex replacement]]
              (when (re-find regex stroke)
                (str/replace stroke regex replacement)))
            [[#"AOEU" "AO*EU"]
             [#"AOU" "AO*U"]
             [#"AEU" "A*EU"]
             [#"AOE" "AO*E"]
             [#"OEU" "O*EU"]
             [#"AO" "AO*"]
             [#"EU" "*EU"]
             [#"AE" "A*E"]
             [#"AU" "A*U"]
             [#"OE" "O*E"]
             [#"OU" "O*U"]
             [#"A" "A*"]
             [#"O" "O*"]
             [#"E" "*E"]
             [#"U" "*U"]
             [#"-" "*"]])
      stroke)))

(defn fix-carrot
  "fixes the ^ thing (it's called a carrot right?) on all entries"
  [entries]
  (into {}
        (map (fn [[k v]]
               {(if (some #(= \^  %) k)
                  (->> (str/replace k #"\^" "")
                       (str "^"))
                  k)
                v})
             entries)))

(defn fix-stars
  "Fixes the stars and shit on entries."
  [entries]
  (into {}
        (map (fn [[k v]]
               {(if (some #(= \* %) k)
                  (-> (str/replace k #"\*\*" "-")
                      (str/replace #"\*" "")
                      starred)
                  k)
                v})
             entries)))

(comment
  (fix-stars {"STWHR-FR" "{^}Lle"})
  )

(defn load-resource
  "Loads a file from the resource dir.
  Returns nil if the file is not present."
  [file]
  (try
    (edn/read-string
      (slurp (io/resource (str file ".edn"))))

    (catch Exception e
      (println (.getMessage e))
      (println "Failed to load resource:" file)
      nil)))

(defn transform-json
  "Transforms an edn dict into json"
  [dict]
  (-> dict
      (json/write-str :escape-slash false)
      (str/replace #"," ",\n")))

(comment
  (transform-json {"TEFT" "test"})
  )
