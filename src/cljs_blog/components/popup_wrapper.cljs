(ns cljs-blog.components.popup-wrapper
  (:require 
   [headlessui-reagent.core :as ui]
   ["react" :as react]
   [reagent.core :as r]))

(def !open (r/atom true))

(defn on-key-down [event]
  (.log js/console event)
  (when (and (= "k" (.-key event))
             (or (.-altKey event)
                 (.-metaKey event)
                 (.-ctrlKey event)))
    (swap! !open not)))

(defn popup-wrapper [content]
  (let [open @!open]
    (react/useEffect
     (fn []
       (.. js/window
           (addEventListener "keydown" on-key-down))
       (fn []
         (.. js/window
             (removeEventListner "keydown" on-key-down))))
     (array))
    [ui/transition {:show open}
     [ui/dialog {:on-close #(reset! !open false)
                 :class [:fixed :inset-0 :p-4 "pt-[25vh]"]}
      [ui/transition-child {:enter "duration-300 ease-out"
                            :enter-from "opacity-0"
                            :enter-to "opacity-100"
                            :leave "ease-in duration-200"
                            :leave-from "opacity-100"
                            :leave-to "opacity-0"}
       [ui/dialog-overlay {:class [:fixed :inset-0 "bg-gray-500/75"]}]]
      [ui/transition-child {:enter "duration-300 ease-out"
                            :enter-from "opacity-0 scale-95"
                            :enter-to "opacity-100 scale-100"
                            :leave "ease-in duration-200"
                            :leave-from "opacity-100 scale-100"
                            :leave-to "opacity-0 scale-95"}
       [ui/combobox {:as "div"
                     :on-change (fn [_]
                                  (reset! !open false))
                     :class [:relative
                             :max-w-xl
                             :mx-auto
                             :bg-white
                             :rounded-xl
                             :shadow-2xl
                             :ring-1 "ring-black/5"
                             :divide-y
                             :divide-gray-100
                             :overflow-hidden]}
        content]]]]))