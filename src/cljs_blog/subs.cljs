(ns cljs-blog.subs
  (:require
   [re-frame.core :as rf]
   [cljs-blog.db :as db]))

 (rf/reg-sub
  ::open-create-room
  (fn [db _query-vector]
    (:open-create-room db)))
 
 (rf/reg-sub
  ::open-join-room
  (fn [db _query-vector]
    (:open-join-room db)))
 
 (rf/reg-sub
  ::current-room
  (fn [db _]
    (:current-room db)))
 
 (rf/reg-sub
  ::select-role
  (fn [db _] 
    (:select-role db)))