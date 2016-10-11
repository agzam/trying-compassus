(ns sample-routing.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [compassus.core :as c]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [sample-routing.colors :refer [Colors]]
            [sample-routing.numbers :refer [Numbers]]
            [sample-routing.menu :refer [Menu]]
            [sample-routing.parser :as parser]))

(defonce app-state
  (atom {:menu-items [{:id 0 :title "colors"}
                      {:id 1 :title "numbers"}]
         :colors/title "here will be colors"
         :numbers/title "numbers are here!"}))

(defonce bidi-routes
  ["/" {""        :colors
        "numbers" :numbers}])

(declare app)

(defonce history
  (pushy/pushy #(c/set-route! app (:handler %))
    (partial bidi/match-route bidi-routes)))

(defonce app
  (c/application {:routes {:colors (c/index-route Colors)
                           :numbers Numbers}
                  :reconciler-opts {:state app-state
                                    :parser (om/parser {:read parser/read})}
                  :mixins [(c/wrap-render Menu)]
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
