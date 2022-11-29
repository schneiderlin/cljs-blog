(ns cljs-blog.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [cljs-blog.events :as events]
   [cljs-blog.views :as views]
   [cljs-blog.config :as config]
   [com.wsscode.pathom3.connect.indexes :as pci]
   [com.wsscode.pathom3.connect.operation :as pco]
   [com.wsscode.pathom3.interface.smart-map :as psm]
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

;; pantom3
(comment

  ;; resolver 就像是普通的 function
  ;; 不过 input 和 output 都必须是 map
  ;; 还要描述一下 output 的形状
  (pco/defresolver ip->lat-long
    [{:keys [ip]}]
    {::pco/output [:latitude :longitude]}
    {:longitude "-88.0569", :latitude "41.5119"})

  (pco/defresolver latlong->woeid
    [{:keys [latitude longitude]}]
    {:woeid 2379574})

  (pco/defresolver woeid->temperature
    [{:keys [woeid]}]
    {:temperature 4.529999999999999})

  ;; 可以当成普通的函数调用
  (-> {:ip "198.29.213.3"}
      ip->lat-long
      latlong->woeid
      woeid->temperature)


  ;; 把这些 resolver 都注册到 index 里面
  (def env
    (pci/register [ip->lat-long
                   latlong->woeid
                   woeid->temperature]))
  
  ;; 现在就可以跳着查
  (-> (psm/smart-map env {:ip "198.29.213.3"})
      :temperature)

  ;; end
  )

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
