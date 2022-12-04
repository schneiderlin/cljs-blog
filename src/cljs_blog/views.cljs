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