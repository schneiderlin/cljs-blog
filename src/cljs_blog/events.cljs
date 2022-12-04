(ns cljs-blog.events
  (:require
   [re-frame.core :as re-frame]
   [cljs-blog.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::craete-room-close
 (fn [db _]
   (assoc db :open-create-room false)))

(re-frame/reg-event-db
 ::craete-room-open
 (fn [db _]
   (assoc db :open-create-room true)))

(re-frame/reg-event-db
 ::craete-room
 (fn [db _]
   (let [room-name (:room-name db)]
     (assoc db :current-room room-name))))

(re-frame/reg-event-db
 ::leave-room
 (fn [db _]
   (assoc db :current-room nil)))

(re-frame/reg-event-db
 ::create-room-input
 (fn [db [_ value]]
   (assoc db :room-name value)))