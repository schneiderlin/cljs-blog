(ns cljs-blog.components.kraken-room
  (:require
   [cljs-blog.events :as events]
   [cljs-blog.subs :as subs]
   [cljs-blog.components.pill-button :refer [pill-button]]
   [re-frame.core :as rf]))

(defn card [img description selected
            on-click]
  [:div {:class (concat [:max-w-sm
                         :bg-white
                         :border :border-gray-200
                         :rounded-lg
                         :shadow-md]
                        (if (not selected)
                          []
                          [:outline-none
                           :ring-2
                           :ring-offset-2
                           :ring-indigo-400]))}
   [:a {:href "#"}
    [:img {:class [:rounded-t-lg]
           :src img}]]
   [:div {:class [:p-5]}
    [:a {:on-click on-click}
     [:h5 {:class [:mb-2 :text-2xl :font-bold
                   :traking-tight
                   :text-gray-900]}
      description]]]])

(def random-image-url "data:image/jpeg;base64,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")

(defn kraken-room []
  (let [select-role @(rf/subscribe [::subs/select-role])]
    [:div {:class :space-y-8}
     [:div {:class [:flex :space-x-4]}
      [card random-image-url "海盗" (= select-role :prirate) #(rf/dispatch [::events/select-role :prirate])]
      [card random-image-url "海怪" (= select-role :kraken) #(rf/dispatch [::events/select-role :kraken])]]
     [:div
      [pill-button "开始游戏"]
      [pill-button "离开房间" #(rf/dispatch [::events/leave-room])]]]))