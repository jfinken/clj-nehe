(ns clj-nehe.tutorial4
  (:use [penumbra opengl]
        [penumbra.opengl.core :only [gl-import]])
  (:require [penumbra.app :as app]))

;; -----------------------------------------------------------------------------
;; Vars

(def app-width 640)
(def app-height 480)

(def tri [[1 0 0]     ; color, red
            [0 1 0]     ; vertex
            [0 1 0]     ; color, green
            [-1 -1 0]   ; vertex
            [0 0 1]     ; color, blue
            [1 -1 0]]   ; vertex
     )

(def quad [[-1 1 0]
             [1 1 0]
             [1 -1 0]
             [-1 -1 0]])

;; -----------------------------------------------------------------------------
;; Import

(gl-import glClearDepth clear-depth)

;; -----------------------------------------------------------------------------
;; Fns

(defn init [state]
  (app/title! "Nehe Tutorial 4")
  (app/vsync! false)
  (app/display-mode! app-width app-height)
  (shade-model :smooth)
  (clear-color 0 0 0 0.5)
  (clear-depth 1)
  (enable :depth-test)
  (depth-test :lequal)
  (hint :perspective-correction-hint :nicest)
  (assoc state
    :rtri 0
    :rquad 0))

(defn reshape [[x y width height] state]
  (viewport 0 0 app-width app-height)
  (frustum-view 45 (/ (double app-width) app-height) 0.1 100)
  (load-identity)
  state)

(defn update [[delta time] state]
   (-> state
       (update-in [:rtri] #(+ % 0.2))
       (update-in [:rquad] #(+ % 0.15))))

(defn key-press [key state]
  (condp = key
    :f1 (let [state (update-in state [:fullscreen] #(not %))]
          (app/fullscreen! (:fullscreen state))
          state)
    state))

(defn display [[delta time] state]
  (translate -1.5 0 -6)
  (rotate (:rtri state) 0 1 0)
  (draw-triangles
   (doseq [[c v] (partition 2 tri)]
     (apply color c)
     (apply vertex v)))
  (load-identity)
  (translate -1.5 0 -6)
  (translate 3 0 0)
  (rotate (:rquad state) 1 0 0)
  (color 0.5 0.5 1)
  (draw-quads
   (dorun (map #(apply vertex %) quad)))
  (app/repaint!))

(defn display-proxy [& args]
  (apply display args))

(def options {:reshape reshape
              :update update
              :key-press key-press
              :display display-proxy
              :init init})

(defn start []
  (app/start options {}))