(ns cljs-blog.events
  (:require
   [re-frame.core :as rf]
   [cljs-blog.db :as db]
   [cljs-blog.channel :refer [channel]]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 ::craete-room-close
 (fn [db _]
   (assoc db :open-create-room false)))

(rf/reg-event-db
 ::craete-room-open
 (fn [db _]
   (assoc db :open-create-room true)))

(rf/reg-event-db
 ::craete-room
 (fn [db _]
   (let [room-name (:room-name db)]
     (assoc db :current-room room-name))))

(rf/reg-event-db
 ::leave-room
 (fn [db _]
   (assoc db :current-room nil)))

(rf/reg-event-db
 ::create-room-input
 (fn [db [_ value]]
   (assoc db :room-name value)))

(rf/reg-event-db
 ::select-role
 (fn [db [_ role]]
   (js/console.log role)
   (assoc db :select-role role)))

(comment
  (rf/dispatch [::push-channel "test" {:key :value :key1 "value1"}])
  ;; end
  )

(rf/reg-event-fx
 ::push-channel
 (fn [_cofx [_ event-type payload]]
   {:push-channel [event-type payload]}))

(rf/reg-fx
 :push-channel
 (fn [event-type payload]
   (js/console.log payload)
   (.push channel event-type payload)))