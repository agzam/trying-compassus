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
         :numbers    {:numbers/title "numbers are here!"
                      :numbers/list  [{:number-id 0 :value "afb5f6da-3d8e-49ef-993d-95e55f186fd3"}
                                      {:number-id 1 :value "bc47140c-89ad-4832-a3d7-b22a6aafde6c"}
                                      {:number-id 2 :value "d5d88770-f477-4cec-9b8e-6c9ddf5ce2b7"}
                                      {:number-id 3 :value "478d9320-a1b2-459e-95f5-4bb963fdad1c"}
                                      {:number-id 4 :value "d8bf7561-b9b6-4be7-a5d6-a05f8c86973f"}
                                      {:number-id 5 :value "d8bf7561-b9b6-4be7-a5d6-a05f8c86973f"}]}
         :colors     {:colors/title "here will be colors"
                      :colors/list  [{:color-id 0 :name "red"}
                                     {:color-id 1 :name "orange"}
                                     {:color-id 2 :name "yellow"}
                                     {:color-id 3 :name "green"}
                                     {:color-id 4 :name "blue"}
                                     {:color-id 5 :name "indigo"}
                                     {:color-id 6 :name "violet"}]}}))

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
