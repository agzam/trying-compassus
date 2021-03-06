(ns sample-routing.core
  (:require [bidi.bidi :as bidi]
            [cognitect.transit :as transit]
            [compassus.core :as c]
            [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [pushy.core :as pushy]
            [sample-routing.colors :as colors]
            [sample-routing.menu :refer [Menu]]
            [sample-routing.numbers :refer [Numbers]]
            [sample-routing.parser :as parser]
            [taoensso.timbre :as log]))

(defonce app-state
  (atom {:menu-items [{:id 0 :title "colors" :route :route.colors/list :url "/colors"}
                      {:id 1 :title "numbers" :route :route.numbers :url "/numbers"}]}))

(declare app)

(defn send [{:keys [remote] :as env} cb]
  (let [xhr          (new js/XMLHttpRequest)
        request-body (transit/write (transit/writer :json) remote)]
    (.open xhr "POST" "/data")
    (.setRequestHeader xhr "Content-Type" "application/transit+json")
    (.setRequestHeader xhr "Accept" "application/transit+json")
    (.addEventListener
      xhr "load"
      (fn [evt]
        (let [status (.. evt -currentTarget -status)]
          (case status
            200 (let [response (transit/read (transit/reader :json)
                                 (.. evt -currentTarget -responseText))]
                  (cb response))
            (js/alert (str "Error: Unexpected status code: " status
                        ". Please screenshot and contact an engineer."))))))
    (.send xhr request-body)))

(defn merge-fn
  "https://github.com/omcljs/om/wiki/Documentation-(om.next)#reconciler-1"
  [reconciler state novelty query]
  {:next (merge state novelty)})

(defonce  bidi-routes
  ["/" {"colors"        :route.colors/list
        "numbers"       :route.numbers 
        ["color/" :color-id] :route.colors/color}])

(def history
  (pushy/pushy (fn [{:keys [handler route-params] :as r}]
                 (c/set-route! app handler
                   (when route-params
                     {:params {:route-params route-params}})))
    (partial bidi/match-route bidi-routes)))

(defonce app
  (c/application {:routes      {:route.colors/list  colors/Colors
                                :route.numbers      Numbers
                                :route.colors/color colors/ColorDetails}
                  :index-route :route.colors/list
                  :reconciler  (om/reconciler
                                 {:state   app-state
                                  :parser  (c/parser {:read parser/readf :mutate parser/mutate})
                                  :send    send
                                  :merge   merge-fn
                                  :remotes [:remote]
                                  :shared  {:history history}})
                  :mixins      [(c/wrap-render Menu)
                                (c/did-mount (fn [_] (pushy/start! history)))
                                (c/will-unmount (fn [_] (pushy/stop! history)))]}))

(defonce mounted? (atom false))

(defn init []
  (enable-console-print!)
  (if-not @mounted?
    (do
      (c/mount! app (js/document.getElementById "app"))
      (swap! mounted? not))
    (let [route->component (-> app :config :route->component)
          c                (om/class->any (c/get-reconciler app) (get route->component (c/current-route app)))]
      (when c (.forceUpdate c)))))
