(ns cljs-blog.views
  (:require
   [cljs-blog.components.pill-button :refer [pill-button]]
   [cljs-blog.components.kraken-room :refer [kraken-room]]
   [cljs-blog.components.create-room-popup :refer [create-room-popup]]
   [cljs-blog.components.join-room-popup :refer [join-room-popup]]
   [cljs-blog.subs :as subs]
   [cljs-blog.events :as events]
   [re-frame.core :as rf]))

(defn lobby []
  (let [open-create @(rf/subscribe [::subs/open-create-room])
        open-join @(rf/subscribe [::subs/open-join-room])]
    [:div
     [pill-button "创建房间" #(rf/dispatch [::events/craete-room-open])]
     [pill-button "加入房间"]
     (when open-create
       [create-room-popup {:open true
                           :on-close #(rf/dispatch [::events/craete-room-close])
                           :on-confirm #(rf/dispatch [::events/craete-room %])}])]))

(defn main-panel []
  (let [current-room @(rf/subscribe [::subs/current-room])]
    (if (nil? current-room)
      [lobby]
      [kraken-room])))

(defn has-atom [atoms pos]
  (some #{pos} atoms))

(defn next-position [[x y] direction]
  (cond
    (= direction :up) [x (inc y)]
    (= direction :down) [x (dec y)]
    (= direction :left) [(dec x) y]
    (= direction :right) [(inc x) y]))

(defn next-direction [atoms [x y] current-direction]
  (cond
    (= current-direction :up) (cond
                                (and (has-atom atoms [(dec x) (dec y)])
                                     (has-atom atoms [(inc x) (dec y)]))
                                :down
                                (has-atom atoms [(dec x) (dec y)])
                                :right
                                (has-atom atoms [(inc x) (dec y)])
                                :left
                                :else
                                :up)
    (= current-direction :down) (cond
                                  (and (has-atom atoms [(dec x) (inc y)])
                                       (has-atom atoms [(inc x) (inc y)]))
                                  :up
                                  (has-atom atoms [(dec x) (inc y)])
                                  :right
                                  (has-atom atoms [(inc x) (inc y)])
                                  :left
                                  :else
                                  :down)
    (= current-direction :left) (cond
                                  (and (has-atom atoms [(dec x) (inc y)])
                                       (has-atom atoms [(dec x) (dec y)]))
                                  :right
                                  (has-atom atoms [(dec x) (inc y)])
                                  :up
                                  (has-atom atoms [(dec x) (dec y)])
                                  :down
                                  :else
                                  :left)
    (= current-direction :right) (cond
                                   (and (has-atom atoms [(inc x) (inc y)])
                                        (has-atom atoms [(inc x) (dec y)]))
                                   :left
                                   (has-atom atoms [(inc x) (inc y)])
                                   :up
                                   (has-atom atoms [(inc x) (dec y)])
                                   :down
                                   :else
                                   :right)))

(defn edge-reflect [[x y] direction atoms]
  (cond
    (= direction :up) (if (or (has-atom atoms [(dec x) y])
                              (has-atom atoms [(inc x) y]))
                        [:reflect :down x]
                        [[x y] direction])
    (= direction :down) (if (or (has-atom atoms [(dec x) y])
                                (has-atom atoms [(inc x) y]))
                          [:reflect :up x]
                          [[x y] direction])
    (= direction :left) (if (or (has-atom atoms [x (dec y)])
                                (has-atom atoms [x (inc y)]))
                          [:reflect :right y]
                          [[x y] direction])
    (= direction :right) (if (or (has-atom atoms [x (dec y)])
                                 (has-atom atoms [x (inc y)]))
                           [:reflect :left y]
                           [[x y] direction])))

(defn get-init-state [[edge-side index] size]
  (let [init-state
        (cond
          (= edge-side :up) [[index 1] :down]
          (= edge-side :down) [[index size] :up]
          (= edge-side :left) [[0 index] :right]
          (= edge-side :right) [[size index] :left])]
    (apply edge-reflect init-state)))

(defn- out-of-bound [[x y] size]
  (or (= x 0)
      (= y 0)
      (> x size)
      (> y size)))

(defn- end-state? [state]
  (or (= :abosrt state)
      (= :edge (first state))
      (= :reflect (first state))))

(defn- get-edge [[x y] direction]
  (cond
    (= direction :up) [:edge :up x]
    (= direction :down) [:edge :down x]
    (= direction :left) [:edge :left y]
    (= direction :right) [:edge :right y]))

(defn next-state [state atoms size]
  (if (end-state? state) state
      (let [[[x y] direction] state]
        (cond
          (out-of-bound [x y] size) (get-edge [x y] direction)
          (has-atom atoms [x y]) :abosrt
          :else (let [new-pos (next-position [x y] direction)
                     new-dir (next-direction atoms new-pos direction)]
                 [new-pos new-dir])))))

(defn run [edge atoms size]
  (let [init-state (get-init-state edge size)]
    (loop [state init-state
           result [init-state]]
      (let [new-state (next-state state atoms size)
            new-result (conj result new-state)]
        (if (end-state? new-state)
          new-result
          (recur new-state new-result))))))

(comment
  (def atoms [[2 3] [7 3] [4 6] [7 8]])
  (def size 8)

  (def edge1 [:up 3])
  (def init-state1 (get-init-state edge1 size))
  (def init-state-1 (next-state init-state1 atoms size))

  (run edge1 atoms size)

  (def edge2 [:down 6])
  (run edge2 atoms size)

  (def edge3 [:right 6])
  (run edge3 atoms size)

;; end
  )
