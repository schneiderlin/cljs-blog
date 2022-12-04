(ns cljs-blog.components.pill-button)

(defn pill-button [text on-click]
  [:button {:class [:bg-blue-500 "hover:bg-blue-700"
                    :text-white
                    :font-bold
                    :py-2 :px-4
                    :rounded-full]
            :on-click on-click} 
   text])