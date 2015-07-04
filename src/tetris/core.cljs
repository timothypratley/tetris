(ns ^:figwheel-always tetris.core
    (:require [tetris.world :as world]
              [tetris.view :as view]
              [reagent.core :as reagent]))

(enable-console-print!)

(defn maybe-step [world f]
  (let [new-world (f world)]
    (if (world/valid-world? new-world)
      (reset! world/app-state new-world)
      world)))

(def codename
  {37 "LEFT"
   38 "UP"
   39 "RIGHT"
   40 "DOWN"
   32 "SPACE"})

(def action
  {"LEFT" world/move-left
   "RIGHT" world/move-right
   "UP" world/rotate
   "SPACE" world/rotate
   "DOWN" world/drop-to-ground})

(defn handle-keydown [e]
  (when-not (:done @world/app-state)
    (when-let [f (action (codename (.-keyCode e)))]
      (.preventDefault e)
      (swap! world/app-state maybe-step f))))

(defn on-js-reload []
  (println "Reloaded...")
  (reset! world/app-state (world/new-world))
  (reagent/render-component [view/root-view] (. js/document (getElementById "app"))))

(defn init []
  (on-js-reload)
  (.addEventListener js/document "keydown" handle-keydown)
  (js/setInterval world/tick! 200))

(defonce start
  (init))
