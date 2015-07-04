(ns tetris.world
  (:require [reagent.core :as reagent]))

(defonce app-state
  (reagent/atom {}))

(defn make-block-pile [x y]
  (vec (repeat x (vec (repeat y -1)))))

(def peices
  [[[0 0 0 0]
    [0 0 0 0]
    [1 1 1 1]
    [0 0 0 0]
    [0 0 0 0]]
   [[1 1]
    [1 1]]
   [[1 0]
    [1 1]
    [0 1]]
   [[1 0]
    [1 0]
    [1 1]]
   [[1 1 1]
    [0 1 0]]])

(defn transpose [matrix]
  (apply mapv vector matrix))

(defn flip [matrix]
  (vec (reverse matrix)))

(defn rand-peice []
  (transpose (rand-nth peices)))

(def colors
  ["#181818"
   "#585858"
   "#D8D8D8"
   "#AB4642"
   "#DC9656"
   "#F7CA88"
   "#A1B56C"
   "#86C1B9"
   "#7CAFC2"
   "#BA8BAF"
   "#A16946"])

(defn with-new-peice [world]
  (let [peice (rand-peice)]
    (assoc world
           :x (- 5 (quot (count peice) 2))
           :y 0
           :peice peice
           :color (rand-int (count colors)))))

(defn new-world []
  (with-new-peice
    {:score 0
     :block-pile (make-block-pile 10 20)}))

(defn valid-world? [{:keys [x y peice block-pile done]}]
  (every? #{-1}
          (for [i (range (count peice))
                j (range (count (first peice)))
                :when (pos? (get-in peice [i j]))
                :let [matrix-x (+ x i)
                      matrix-y (+ y j)]]
            (get-in block-pile [matrix-x matrix-y]))))

(defn complete? [row]
  (not-any? #{-1} row))

(defn with-completed-rows [{:as world :keys [block-pile]}]
  (let [remaining-rows (remove complete? (transpose block-pile))
        cc (- 20 (count remaining-rows))
        new-rows (repeat cc (vec (repeat 10 -1)))]
    (-> world
        (update-in [:score] inc)
        (update-in [:score] + (* 10 cc cc))
        (assoc :block-pile (transpose (concat new-rows remaining-rows))))))

(defn collect-peice [block-pile [x y color]]
  (assoc-in block-pile [x y] color))

(defn push-peice [{:as world :keys [peice color x y block-pile]}]
  (let [peice-width (count peice)
        peice-height (count (first peice))]
    (assoc world :block-pile
           (reduce collect-peice block-pile
                   (for [i (range peice-width)
                         j (range peice-height)
                         :when (pos? (get-in peice [i j]))]
                     [(+ x i) (+ y j) color])))))

(defn maybe-done [world]
  (if (valid-world? world)
    world
    (assoc world :done true)))

(defn landed [world]
  (-> world
      push-peice
      with-completed-rows
      with-new-peice
      maybe-done))

(defn move-down [world]
  (update-in world [:y] inc))

(defn gravity [world]
  (let [new-world (move-down world)]
    (if (valid-world? new-world)
      new-world
      (landed world))))

(defn tick! []
  (when-not (:done @app-state)
    (swap! app-state gravity)))

(defn move-left [world]
  (update-in world [:x] dec))

(defn move-right [world]
  (update-in world [:x] inc))

(defn rotate [world]
  (update-in world [:peice] (comp transpose flip)))

(defn drop-to-ground [world]
  (landed (last (take-while valid-world? (iterate move-down world)))))
