(ns cljs-blog.components.create-room-popup
  (:require
   [cljs-blog.events :as events]
   [cljs-blog.components.pill-button :refer [pill-button]]
   [cljs-blog.components.floating-label :refer [floating-label]]
   [cljs-blog.components.popup-wrapper :refer [popup-wrapper default-on-key-down]]
   [re-frame.core :as rf]))

(defn content [on-confirm]
  [:div {:class [:p-4 :space-y-4]}
   [floating-label {:id "id"
                    :type "text"
                    :name "name"
                    :text "房间名"
                    :on-change #(rf/dispatch [::events/create-room-input
                                              (.-value (.-target %))])}]
   [pill-button "创建房间" on-confirm]])

(defn create-room-popup [& {:keys [open on-close on-confirm]}]
  [popup-wrapper [content on-confirm] {:on-key-down default-on-key-down
                                       :open open
                                       :on-close on-close}])
