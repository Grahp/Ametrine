(ns ametrine.dict
  (:require [clojure.string :as str]
            [ametrine.utils :as utils]))

(defn apply-dict-fixes
  "Oops I broke some stuff"
  [entries]
  (merge
   (into {}
         (map (fn [[k v]]
                {(if (some #(= \^  %) k)
                   (->> (str/replace k #"\^" "")
                        (str "^"))
                   k)
                 v})
              entries))
   (into {}
         (map (fn [[k v]]
                {(if (some #(= \* %) k)
                   (-> (str/replace k #"\*\*" "-")
                       (str/replace #"\*" "")
                       utils/starred)
                   k)
                 v})
              entries))))

(defn capitalize-entries
  "Capitalizes entries that have a #"
  [entries]
  (into {}
        (map (fn [[k v]]
               (if (= \# (first k))
                 (cond
                   (re-find #"\{>\}\{\^\}" v) {k (str/replace v #"\{>\}\{\^\}" "{^}{-|}")}
                   (re-find #"\{>\}" v) {k (str/replace v #"\{>\}" "{-|}")}
                   (re-find #"\{\^\}" v) {k (str/replace v #"\{\^\}." "{^}{-|}")}
                   :else {k (str "{-|}" v)})
                 {k v}))
             (utils/add-capitalized-entries entries))))

(defn- generate-starter-entries
  [opts]
  (let [prepend (or (:prepend opts) "")
        append (or (:append opts) "")
        vowel-stroke (:vowel-stroke opts)
        vowel (:vowel opts)
        starter-stroke (:starter-stroke opts)
        starter (:starter opts)]
    (merge
     {(str prepend "WHR" vowel-stroke starter-stroke)    (str "{>}" starter vowel append)}
     {(str prepend "TWHR" vowel-stroke starter-stroke)   (str "{>}" starter vowel append "e")}
     {(str prepend "SWHR" vowel-stroke starter-stroke)   (str "{>}" "{^}" starter vowel append)}
     {(str prepend "STWHR" vowel-stroke starter-stroke)  (str "{>}" "{^}" starter vowel append "e")})))

(defn generate-starters
  [starters vowels]
  (into {}
        (map (fn [[starter-stroke starter]]
               (reduce (fn [acc [vowel-stroke vowel]]
                         (conj acc
                               (merge
                                (if (re-find #"S" starter-stroke)
                                  (merge
                                     ;; TODO make this all one map and just one function call so it's less code.
                                   (generate-starter-entries {:append "t" :starter-stroke (str/replace starter-stroke #"S" "TS")
                                                              :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                   (generate-starter-entries {:append "ted" :starter-stroke (str/replace starter-stroke #"S" "TSDZ")
                                                              :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                   (generate-starter-entries {:append "rt" :starter-stroke (str/replace starter-stroke #"S" "TS")
                                                              :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                   (generate-starter-entries {:prepend "+" :append "rted" :starter-stroke (str/replace starter-stroke #"S" "TSDZ")
                                                              :starter starter :vowel-stroke vowel-stroke :vowel vowel}))
                                  (merge
                                   (generate-starter-entries {:append "t" :starter-stroke (str starter-stroke "T")
                                                              :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                   (generate-starter-entries {:prepend "+" :append "rt" :starter-stroke (str starter-stroke "T")
                                                              :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                   (generate-starter-entries {:prepend "+" :append "rted" :starter-stroke (str starter-stroke "TD")
                                                              :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                   (generate-starter-entries {:append "ted" :starter-stroke (str starter-stroke "TD")
                                                              :starter starter :vowel-stroke vowel-stroke :vowel vowel})))
                                (generate-starter-entries {:append "s" :starter-stroke (str starter-stroke "Z")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "+" :append "rs" :starter-stroke (str starter-stroke "Z")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "+" :append "r"
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:append "n" :starter-stroke (str starter-stroke "*Z")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "+" :append "rn" :starter-stroke (str starter-stroke "*Z")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "^" :append "m" :starter-stroke (str starter-stroke "*Z")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "^+" :append "rm" :starter-stroke (str starter-stroke "*Z")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:append "y" :starter-stroke (str starter-stroke "*D")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "+" :append "ry" :starter-stroke (str starter-stroke "*D")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:append "d" :starter-stroke (str starter-stroke "D")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "+" :append "rd" :starter-stroke (str starter-stroke "D")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:append "ys" :starter-stroke (str starter-stroke "*DZ")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "^" :append "nd" :starter-stroke (str starter-stroke "*DZ")
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:prepend "^" :append "p"
                                                           :starter starter :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-starter-entries {:starter starter :vowel-stroke vowel-stroke :vowel vowel}))))
                       {}
                       vowels))
             starters)))

(defn- generate-ender-outlines
  [opts]
  (let [prepend (or (:prepend opts) "")
        append (or (:append opts) "")
        vowel-stroke (:vowel-stroke opts)
        vowel (:vowel opts)
        ender-stroke (:ender-stroke opts)
        ender (:ender opts)]
    (merge
     {(str prepend "KWHR" vowel-stroke ender-stroke) (str "{>}" vowel ender append)}
     {(str prepend "TKWHR" vowel-stroke ender-stroke) (str "{>}" vowel ender append "e")}
     {(str prepend "SKWHR" vowel-stroke ender-stroke) (str "{^}" "{>}" vowel ender append)}
     {(str prepend "STKWHR" vowel-stroke ender-stroke) (str "{^}" "{>}" vowel ender append "e")})))

(defn generate-enders
  [enders vowels]
  (into {}
        (map (fn [[ender-stroke ender]]
               (reduce (fn [acc [vowel-stroke vowel]]
                         (conj acc
                               (merge
                                (generate-ender-outlines {:prepend "+" :append "r"
                                                          :ender-stroke ender-stroke :ender ender :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-ender-outlines {:prepend "^" :append "p"
                                                          :ender-stroke ender-stroke :ender ender :vowel-stroke vowel-stroke :vowel vowel})
                                (generate-ender-outlines {:ender-stroke ender-stroke :ender ender :vowel-stroke vowel-stroke :vowel vowel}))))
                       {}
                       vowels))
             enders)))

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

(defn generate-starters2
  "Generates starters"
  [starters vowels]
  (into {}
        (map (fn [[starter-stroke starter]]
               (reduce (fn [acc [vowel-stroke vowel]]
                         (letfn [(generate-starter-outlines [defaults]
                                   (let [vowel-stroke (:vowel-stroke defaults)
                                         vowel (:vowel defaults)
                                         starter-stroke (:starter-stroke defaults)
                                         starter (:starter defaults)
                                         special (:special defaults)
                                         append (:append defaults)]
                                     {[(set special) (set "WHR") vowel-stroke starter-stroke] (str "{>}" starter vowel append)
                                      [(set special) (set "TKWHR") vowel-stroke starter-stroke] (str "{>}" starter vowel append "e")
                                      [(set special) (set "SWHR") vowel-stroke starter-stroke] (str "{>}{^}" starter vowel append)
                                      [(set special) (set "STWHR") vowel-stroke starter-stroke] (str "{>}{^}" starter vowel append "e")}))]
                           (conj acc
                                 (merge
                                  (generate-starter-outlines {:vowel-stroke vowel-stroke
                                                              :vowel vowel
                                                              :starter-stroke starter-stroke
                                                              :starter starter})
                                  (prn "starter-stroke:" starter-stroke)
                                  (generate-starter-outlines {:vowel-stroke (utils/starred vowel-stroke)
                                                              :vowel vowel
                                                              :starter-stroke (conj starter-stroke \Z)
                                                              :starter starter
                                                              :append "s"})))))
                       {}
                       vowels))
             starters)))

(comment
  (into {}
        (map
         (fn [[k v]]
           {(utils/stroke->str k) v})
         (generate-starters2 {#{\S} "s"} {#{\A} "a"})))
  (do-starters {"S" "s"} {"A" "a"}))

(defn generate
  "Generates the dict"
  [starters vowels enders]
  (-> (merge
       (generate-starters starters vowels)
       (generate-enders enders vowels))
      apply-dict-fixes
      capitalize-entries))

(comment
  (def coll {"Bob" {:health 10}
             "John" {:health 20}
             "Jim" {:a 5}})
  (get-in coll [:health])
  (keep :health coll)
  (doseq [[_ v] coll]
    (prn (get v :health))))
