(ns sample-routing.url-routing
  (:require
   [bidi.bidi :as bidi]
   [compassus.core :as c]
   [pushy.core :as pushy]
   [sample-routing.url-routing :as url]))

(defonce  bidi-routes
  ["/" {""              :route.colors/list 
        "numbers"       :route.numbers 
        ["colors/" :id] :route.colors/color}])

(defn history [app]
  (pushy/pushy (fn [e]
                 (c/set-route! app (:handler e)))
    (partial bidi/match-route bidi-routes)))

#_(defn history [app]
  (pushy/pushy (fn [{:keys [handler route-params]}]
                 (c/set-route! app handler {:params {:route-params route-params}}))
    (partial bidi/match-route bidi-routes)))

(defn start! [app]
  (pushy/start! (history app)))

(defn stop! [app]
  (pushy/stop! (history app)))

