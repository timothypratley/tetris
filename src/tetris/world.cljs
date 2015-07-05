(ns tetris.world
  (:require [reagent.core :as reagent]))

(defonce app-state
  (reagent/atom {}))

(defn make-block-pile [x y]
  (vec (repeat x (vec (repeat y -1)))))

(def pieces
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

(defn rand-piece []
  (transpose (rand-nth pieces)))

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

(defn with-new-piece [world]
  (let [piece (rand-piece)]
    (assoc world
           :x (- 5 (quot (count piece) 2))
           :y 0
           :piece piece
           :color (rand-int (count colors)))))

(defn new-world []
  (with-new-piece
    {:score 0
     :block-pile (make-block-pile 10 20)}))

(defn valid-world? [{:keys [x y piece block-pile done]}]
  (every? #{-1}
          (for [i (range (count piece))
                j (range (count (first piece)))
                :when (pos? (get-in piece [i j]))
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

(defn collect-piece [block-pile [x y color]]
  (assoc-in block-pile [x y] color))

(defn push-piece [{:as world :keys [piece color x y block-pile]}]
  (let [piece-width (count piece)
        piece-height (count (first piece))]
    (assoc world :block-pile
           (reduce collect-piece block-pile
                   (for [i (range piece-width)
                         j (range piece-height)
                         :when (pos? (get-in piece [i j]))]
                     [(+ x i) (+ y j) color])))))

(defn maybe-done [world]
  (if (valid-world? world)
    world
    (assoc world :done true)))

(defn landed [world]
  (-> world
      push-piece
      with-completed-rows
      with-new-piece
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
  (update-in world [:piece] (comp transpose flip)))

(defn drop-to-ground [world]
  (landed (last (take-while valid-world? (iterate move-down world)))))
