(ns tetris.view
  (:require [tetris.world :as world]
            [clojure.string :as string]))

(defn block [x y color]
  [:rect {:x x
          :y y
          :width 1
          :height 1
          :stroke "black"
          :stroke-width 0.01
          :rx 0.1
          :fill (world/colors color)}])

(defn board-view [{:keys [peice color x y block-pile done]}]
  (let [peice-width (count peice)
        peice-height (count (first peice))
        block-width (count block-pile)
        block-height (count (first block-pile))]
    [:svg {:style {:border "1px solid black"
                   :width 200
                   :height 400}
           :view-box (string/join " " [0 0 10 20])}
     (when-not done
       (into [:g {:name "current peice"}]
                          (for [i (range peice-width)
                                j (range peice-height)
                                :when (pos? (get-in peice [i j]))]
                            [block (+ x i) (+ y j) color])))
     (into [:g {:name "block pile"}]
           (for [i (range block-width)
                 j (range block-height)
                 :let [block-color (get-in block-pile [i j])]
                 :when (not (neg? block-color))]
             [block i j block-color]))]))

(defn tetris-view [{:as world :keys [done score]}]
  [:div {:style {:font-family "Courier New"
                 :text-align "center"}}
   [:h1 (if done "Game Over" "Tetris")]
   [board-view world]
   [:h2 [(if done :blink :span) "Score " score]]
   [:audio {:controls "true"
            :auto-play "true"
            :loop "true"
            :volume "0.2"}
    [:source {:src "https://archive.org/download/TetrisThemeMusic/Tetris.ogg"
              :type "audio/ogg"}]
    [:source {:src "https://archive.org/download/TetrisThemeMusic/Tetris.mp3"
              :type "audio/mpeg"}]
    "Your browser does not support the audio element."]
   [:br]
   (when done
     [:button {:on-click (fn restart-click [e]
                           (reset! world/app-state (world/new-world)))
               :style {:width 200
                       :padding "10px 20px 10px 20px"
                       :font-family "Courier New"
                       :font-size 16}}
      "Restart"])])

(defn root-view []
  [tetris-view @world/app-state])
