(ns ametrine.utils
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:import [java.nio.file Paths]))

(defn stroke->str
  [stroke]
  (let [groups ["#^+" "STKPWHR" "AO*-EU" "FRPBLGTSDZ"]]
    (apply str (mapcat
                 #(filter (get stroke %1) %2)
                 (range) groups))))

(defn str->stroke
  [str]
  (when-let [match (re-matches #"^([\^]?[+]?[#]?)(.*?)([AOEU\-*]+)(.*)$" str)]
    (mapv set (rest match))))

(defn add-capitalized-entries
  "returns the entries passed in, including entries with #"
  [entries]
  (into {}
        (map (fn [[k v]]
               {k v
                (str "#" k) v}))
        entries))

(defn starred
  "Returns the starred version of the given stroke"
  [stroke]
  (assoc stroke 2 (conj (stroke 2) \*)))

(defn absolute-path?
  "Returns whether the given path (string) is absolute"
  [path]
  ;; Some crazy java nonsense
  (-> (Paths/get path (into-array String []))
      .isAbsolute))

(defn spit-resource
  "Spits a file to the resource dir, or to an absolute path"
  [path contents]
  (try
    (if (absolute-path? path)
      (spit path contents)
      (spit (io/file "resources" path) contents))

    (catch Exception e
      (println (.getMessage e))
      (println "Failed to spit resource:" path)
      nil)))

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
