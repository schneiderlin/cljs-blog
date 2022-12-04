(ns cljs-blog.subs
  (:require
   [re-frame.core :as rf]))

 (rf/reg-sub
  ::open-create-room
  (fn [db _query-vector]
    (:open-create-room db)))
 
 (rf/reg-sub
  ::open-join-room
  (fn [db _query-vector]
    (:open-join-room db)))