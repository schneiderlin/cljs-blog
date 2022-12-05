(ns cljs-blog.channel
  (:require
   ["phoenix" :as phoenix]))


(def socket (phoenix/Socket. "ws://localhost:4000/socket", {}))
(.connect socket)
(def channel (.channel socket "xiangqi:lobby" {}))
(.join channel)

(comment
  (.on channel "new_msg" (fn [msg] (.log js/console "Got message" msg)))
  (.push channel "save" (clj->js {:filename "fromCLJS.json"
                                  :payload "body"}))
  (.push channel "new_msg" {:body "body"})
  ;; end
  )