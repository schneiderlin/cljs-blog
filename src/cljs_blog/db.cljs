(ns cljs-blog.db)

(def default-db
  {:open-create-room true
   :open-join-room false
   :room-name ""
   :current-room nil
   :select-role :kraken})
