(ns cljs-blog.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [cljs-blog.channel]
   [cljs-blog.events :as events]
   [cljs-blog.views :as views]
   [cljs-blog.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(def functional-compiler (r/create-compiler {:function-components true}))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render (r/as-element [views/main-panel] functional-compiler) root-el)))

(defn listen-port1 []
  (let [port1 (.-port1 js/window)]
    (set! (.-onmessage port1) (clj->js (fn [e] (js/console.log "from cljs " e))))))

(comment
  (listen-port1)
  ;; end
  )

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
