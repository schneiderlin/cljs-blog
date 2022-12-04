(ns cljs-blog.views
  (:require
   [cljs-blog.components.kraken-room :refer [kraken-room]]))


(defn main-panel [] 
  [kraken-room])