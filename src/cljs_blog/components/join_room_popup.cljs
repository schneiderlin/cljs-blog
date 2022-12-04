(ns cljs-blog.components.join-room-popup
  (:require
   [cljs-blog.components.pill-button :refer [pill-button]]
   [cljs-blog.components.floating-label :refer [floating-label]]
   [cljs-blog.components.popup-wrapper :refer [popup-wrapper]]))

(defn content []
  [:div {:class [:p-4 :space-y-4]}
   [floating-label "id" "text" "name" "房间名"]
   [pill-button "加入房间"]])

(defn join-room-popup []
  [popup-wrapper [content]])
