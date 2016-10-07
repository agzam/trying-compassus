(ns sample-routing.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [compassus.core :as c]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [sample-routing.home :refer [Home]]
            [sample-routing.about :refer [About]]
            [sample-routing.parser :as parser]))

(defonce app-state
  (atom {:app/title "Das home"
         :about/title "Yo! About what?"}))

(defonce bidi-routes
  ["/" {""      :home
        "about" :about}])

(declare app)

(defonce history
  (pushy/pushy #(c/set-route! app (:handler %))
    (partial bidi/match-route bidi-routes)))

(defonce app
  (c/application {:routes {:home (c/index-route Home)
                           :about About}
                  :reconciler-opts {:state app-state
                                    :parser (om/parser {:read parser/read})}
                  :history {:setup    #(pushy/start! history)
                            :teardown #(pushy/stop! history)}}))

(defonce mounted? (atom false))

(defn init! []
  (enable-console-print!)
  (if-not @mounted?
    (do
      (c/mount! app (js/document.getElementById "app"))
      (swap! mounted? not))
    (let [route->component (-> app :config :route->component)
          c (om/class->any (c/get-reconciler app) (get route->component (c/current-route app)))]
      (.forceUpdate c))))
