(ns ametrine.generate)

;; InsStreams
(defn generate-dict []
  (map #(when (= % 842624) true) (range 0 (bit-shift-left 1 24))))

(def nums
  (->> (bit-shift-left 1 25)
       (range 0)))

(time (doseq [n nums]
        (when (= n 842624)
          (pr "Igual"))))

(Integer/toBinaryString 8)

(def letter-string "#^+STKPWHRAO*EUFRPBLGTSDZ")
(bit-and 68608 (bit-shift-left 1 16))

(defn stroke->letters-seq [n]
  (let [letters (seq letter-string)
        reverse-letters (reverse letter-string)
        c (count letters)]
    (loop [i 0
           result '()]
      (if (< i c)
        (if-not (zero? (bit-and n (bit-shift-left 1 i)))
          (recur (inc i) (conj result (nth reverse-letters i)))
          (recur (inc i) result))  ; Otherwise, continue
        result))))

(defn stroke->str [stroke]
  (apply str (stroke->letters-seq stroke)))

(bit-and 68608 (bit-shift-left 1 10))

(apply str (stroke->letters-seq 140))
(time (count (mapv stroke->str (range 1 (bit-shift-left 1 21)))))
