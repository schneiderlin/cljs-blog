(ns cljs-blog.components.popup-wrapper
  (:require
   [headlessui-reagent.core :as ui]
   ["react" :as react]))

#_(defn on-key-down [event]
    (when (and (= "k" (.-key event))
               (or (.-altKey event)
                   (.-metaKey event)
                   (.-ctrlKey event)))
      (swap! !open not)))

(defn default-on-key-down [_event])

(defn popup-wrapper [content & {:keys [on-key-down
                                       open
                                       on-close]}]
  (react/useEffect
   (fn []
     (.. js/window
         (addEventListener "keydown" on-key-down))
     (fn []
       ;; removeEventListner is not a function, 有这个 warning, 好像不影响业务, 暂时去掉
       #_(.. js/window
           (removeEventListner "keydown" on-key-down))))
   (array))
  [ui/transition {:show open}
   [ui/dialog {:on-close on-close
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
                   :on-change on-close
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
      content]]]])