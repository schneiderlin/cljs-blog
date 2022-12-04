(ns cljs-blog.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [cljs-blog.events :as events]
   [cljs-blog.views :as views]
   [cljs-blog.config :as config]
   ["phoenix" :as phoenix]))

;; phoenix channel
(comment
  ;; connect socket
  (def socket (phoenix/Socket. "ws://localhost:4000/socket", {}))
  (.connect socket)

  ;; let channel = socket.channel("room:123", {token: roomToken})
  (def channel (.channel socket "room:lobby" {}))
  ;; channel.on ("new_msg", msg => console.log ("Got message", msg))
  (.on channel "new_msg" (fn [msg] (.log js/console "Got message" msg)))
  (.join channel)
  ;; channel.push("new_msg", {body: e.target.val}, 10000)
  (.push channel "new_msg" {:body "body"} 10000)
  ;; end
  )

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(def functional-compiler (r/create-compiler {:function-components true}))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render (r/as-element [views/main-panel] functional-compiler) root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
