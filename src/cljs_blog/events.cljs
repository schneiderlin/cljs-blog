(ns cljs-blog.events
  (:require
   [re-frame.core :as re-frame]
   [cljs-blog.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
