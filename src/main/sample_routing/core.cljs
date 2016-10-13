(ns sample-routing.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [compassus.core :as c]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [sample-routing.colors :as colors]
            [sample-routing.numbers :refer [Numbers]]
            [sample-routing.menu :refer [Menu]]
            [sample-routing.parser :as parser]))

(defonce app-state
  (atom {:menu-items [{:id 0 :title "colors" :url "/"}
                      {:id 1 :title "numbers" :url "/numbers"}]
         :numbers    {:numbers/title "numbers are here!"
                      :numbers/list  [{:number-id 0 :value "afb5f6da-3d8e-49ef-993d-95e55f186fd3"}
                                      {:number-id 1 :value "bc47140c-89ad-4832-a3d7-b22a6aafde6c"}
                                      {:number-id 2 :value "d5d88770-f477-4cec-9b8e-6c9ddf5ce2b7"}
                                      {:number-id 3 :value "478d9320-a1b2-459e-95f5-4bb963fdad1c"}
                                      {:number-id 4 :value "d8bf7561-b9b6-4be7-a5d6-a05f8c86973f"}
                                      {:number-id 5 :value "d8bf7561-b9b6-4be7-a5d6-a05f8c86973f"}]}
         :colors     {:colors/title "here will be colors"
                      :colors/list  [{:color-id 0 :name "red"
                                      :details  [{:id 0 :description "Red. Cras placerat accumsan nulla"}
                                                 {:id 1 :description "Red. Donec vitae dolor"}
                                                 {:id 2 :description "Red. Nullam rutrum"}]}
                                     {:color-id 1 :name "orange"
                                      :details  [{:id 0 :description "Orange. Cras placerat accumsan nulla"}
                                                 {:id 1 :description "Orange. Donec vitae dolor"}
                                                 {:id 2 :description "Orange. Nullam rutrum"}]}
                                     {:color-id 2 :name "yellow"
                                      :details  [{:id 0 :description "Yellow. Cras placerat accumsan nulla"}
                                                 {:id 1 :description "Yellow. Donec vitae dolor"}
                                                 {:id 2 :description "Yellow. Nullam rutrum"}]}
                                     {:color-id 3 :name "green"
                                      :details  [{:id 0 :description "Green. Cras placerat accumsan nulla"}
                                                 {:id 1 :description "Green. Donec vitae dolor"}
                                                 {:id 2 :description "Green. Nullam rutrum"}]}
                                     {:color-id 4 :name "blue"
                                      :details  [{:id 0 :description "Blue. Cras placerat accumsan nulla"}
                                                 {:id 1 :description "Blue. Donec vitae dolor"}
                                                 {:id 2 :description "Blue. Nullam rutrum"}]}
                                     {:color-id 5 :name "indigo"
                                      :details  [{:id 0 :description "Indigo. Cras placerat accumsan nulla"}
                                                 {:id 1 :description "Indigo. Donec vitae dolor"}
                                                 {:id 2 :description "Indigo. Nullam rutrum"}]}
                                     {:color-id 6 :name "violet"
                                      :details  [{:id 0 :description "Violet. Cras placerat accumsan nulla"}
                                                 {:id 1 :description "Violet. Donec vitae dolor"}
                                                 {:id 2 :description "Violet. Nullam rutrum"}]}]}}))

(defonce bidi-routes
  ["/" {""        :colors
        "numbers" :numbers
        ["colors/" :id] :color/by-id}])

(declare app)

(defonce history
  (pushy/pushy (fn [{:keys [handler route-params]}]
                 (c/set-route! app handler {:params {:route-params route-params}}))
    (partial bidi/match-route bidi-routes)))

(defonce app
  (c/application {:routes {:colors (c/index-route colors/Colors)
                           :numbers Numbers
                           :color/by-id colors/ColorDetails}
                  :reconciler-opts {:state app-state
                                    :parser (om/parser {:read parser/read})
                                    :shared {:history history}}
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
