(ns cljs-blog.views
  (:require
   [re-frame.core :as re-frame]
   [cljs-blog.subs :as subs]
   [cljs-blog.components.kraken-room :refer [kraken-room]]
   ))


(defn main-panel []
  [kraken-room])
