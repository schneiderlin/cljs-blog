(ns cljs-blog.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [cljs-blog.channel]
   [cljs-blog.events :as events]
   [cljs-blog.views :as views]
   [cljs-blog.config :as config]
   [re-frame.core :as rf]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(def functional-compiler (r/create-compiler {:function-components true}))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render (r/as-element [views/main-panel] functional-compiler) root-el)))

(defn listen-port1
  "f 的参数是浏览器的 MessageEvent"
  [f]
  (let [port1 (.-port1 js/window)]
    (set! (.-onmessage port1) (clj->js f))))

(defn listen-port1-data
  "f 的参数是客户端发过来的内容, 视为 json 反序列化成 cljs 数据结构"
  [f]
  (listen-port1 (fn [e] (f (js->clj (.parse js/JSON (.-data e)))))))

(defn send-message [cljs-map]
  (let [port1 (.-port1 js/window)
        json (clj->js cljs-map)
        json-str (.stringify js/JSON json)]
    (.postMessage port1 json-str)))

(comment
  (listen-port1 (fn [e] (js/console.log "from cljs " e)))

  ;; 红跳马
  (send-message {:event :move
                 :payload {:type "move"
                           :actions [{:type "delete_piece"
                                      :sq 202
                                      :pc 11
                                      :capture false}
                                     {:type "add_piece"
                                      :sq 169
                                      :pc 11}]}})

  ;; 黑中炮
  (send-message {:event :move
                 :payload {:type "move"
                           :actions [{:type "delete_piece"
                                      :sq 90
                                      :pc 21
                                      :capture false}
                                     {:type "add_piece"
                                      :sq 87
                                      :pc 21}]}})

  ;; 退后一步
  (send-message {:event :undo
                 :payload {:type "move"
                           :actions [{:type "delete_piece"
                                      :sq 90
                                      :pc 21
                                      :capture false}
                                     {:type "add_piece"
                                      :sq 87
                                      :pc 21}]}})
  ;; end
  )

(defn handler
  [godot-payload]
  ;; 现在暂时全部都是 move event
  (if (= (godot-payload "event") "move")
    (let [event (godot-payload "payload")]
      (rf/dispatch [::events/move event]))
    (throw (js/Error. "未处理的 event type"))))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root)
  (listen-port1-data #'handler))
