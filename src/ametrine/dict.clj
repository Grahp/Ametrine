(ns ametrine.dict
  (:require [clojure.string :as str]
            [ametrine.utils :as utils]))


(defn capitalize-entries
  "Capitalizes entries that have a #"
  [entries]
  (into {}
        (map (fn [[k v]]
               (if (= \# (first k))
                 (cond
                   (re-find #"\{>\}\{\^\}" v) {k (str/replace v #"\{>\}\{\^\}" "{^}{-|}")}
                   (re-find #"\{>\}" v) {k (str/replace v #"\{>\}" "{-|}")}
                   (re-find #"\{\^\}" v) {k (str/replace v #"\{\^\}" "{^}{-|}")}
                   :else {k (str "{-|}" v)})
                   {k v}))
             entries)))

(defn do-starters
  [starters vowels]
  (into {}
        (map (fn [[starter-stroke starter-val]]
               (reduce (fn [acc [vowel-stroke vowel-val]]
                         (conj acc
                               (letfn [(apply-main-thing [vowel-stroke vowel-val starter-stroke starter-val append prepend]
                                         (merge
                                           {(str prepend "WHR" vowel-stroke starter-stroke)    (str "{>}" starter-val vowel-val append)}
                                           {(str prepend "TWHR" vowel-stroke starter-stroke)   (str "{>}" starter-val vowel-val append "e")}
                                           {(str prepend "SWHR" vowel-stroke starter-stroke)   (str "{>}" "{^}" starter-val vowel-val append)}
                                           {(str prepend "STWHR" vowel-stroke starter-stroke)  (str "{>}" "{^}" starter-val vowel-val append "e")}))]

                                 (merge
                                   (if (re-find #"S" starter-stroke)
                                     (merge
                                       (apply-main-thing vowel-stroke vowel-val (str/replace starter-stroke #"S" "TS") starter-val "t" "")
                                       (apply-main-thing vowel-stroke vowel-val (str/replace starter-stroke #"S" "TSDZ") starter-val "ted" "")
                                       (apply-main-thing vowel-stroke vowel-val (str/replace starter-stroke #"S" "TS") starter-val "rt" "+")
                                       (apply-main-thing vowel-stroke vowel-val (str/replace starter-stroke #"S" "TSDZ") starter-val "rted" "+"))
                                     (merge
                                       (apply-main-thing vowel-stroke vowel-val (str starter-stroke "T") starter-val "t" "")
                                       (apply-main-thing vowel-stroke vowel-val (str starter-stroke "T") starter-val "rt" "+")
                                       (apply-main-thing vowel-stroke vowel-val (str starter-stroke "TD") starter-val "rted" "+")
                                       (apply-main-thing vowel-stroke vowel-val (str starter-stroke "TD") starter-val "ted" "")))
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "Z") starter-val "s" "")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "*Z") starter-val "n" "")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "*Z") starter-val "m" "^")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "*Z") starter-val "rn" "+")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "D") starter-val "rd" "+")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "*D") starter-val "ry" "+")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "*Z") starter-val "rm" "^+")
                                   (apply-main-thing vowel-stroke vowel-val starter-stroke starter-val "r" "+")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "Z") starter-val "rs" "+")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "D") starter-val "d" "") ;; TODO ^d?
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "*D") starter-val "y" "") ;; ^y?
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "*DZ") starter-val "ys" "")
                                   (apply-main-thing vowel-stroke vowel-val (str starter-stroke "*DZ") starter-val "nd" "^")
                                   (apply-main-thing vowel-stroke vowel-val starter-stroke starter-val "a" "^+")
                                   (apply-main-thing vowel-stroke vowel-val starter-stroke starter-val "" "")))))
                       {}
                       vowels))
             starters)))

(defn do-enders
  [enders vowels]
  (into {}
        (map (fn [[ender-stroke ender-val]]
               (reduce (fn [acc [vowel-stroke vowel-val]]
                         (conj acc
                               (letfn [(main-guy [vowel-stroke vowel-val ender-stroke ender-val append prepend]
                                         (merge
                                           {(str prepend "KWHR" vowel-stroke ender-stroke) (str "{>}" vowel-val ender-val append)}
                                           {(str prepend "TKWHR" vowel-stroke ender-stroke) (str "{>}" vowel-val ender-val append "e")}
                                           {(str prepend "SKWHR" vowel-stroke ender-stroke) (str "{^}" "{>}" vowel-val ender-val append)}
                                           {(str prepend "STKWHR" vowel-stroke ender-stroke) (str "{^}" "{>}" vowel-val ender-val append "e")}))]
                                 (merge
                                   (main-guy vowel-stroke vowel-val ender-stroke ender-val "" "")
                                   (main-guy vowel-stroke vowel-val ender-stroke ender-val "r" "+")
                                   (main-guy vowel-stroke vowel-val ender-stroke ender-val "a" "^+")))))
                       {}
                       vowels))
               enders)))

(defn generate
  "Generates the dict"
  [starters vowels enders]
  (-> (merge
        (do-starters starters vowels)
        (do-enders enders vowels))
      utils/fix-stars
      utils/fix-carrot
      utils/add-capitalized-entries
      capitalize-entries))
