(ns sample-routing.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [compassus.core :as c]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [sample-routing.colors :as colors]
            [sample-routing.numbers :refer [Numbers]]
            [sample-routing.menu :refer [Menu]]
            [sample-routing.parser :as parser]
            [cognitect.transit :as transit]
            [taoensso.timbre :as log]))

(defonce app-state
  (atom {:menu-items [{:id 0 :title "colors" :url "/"}
                      {:id 1 :title "numbers" :url "/numbers"}]
         ;; :numbers    nil #_{:numbers/title "numbers are here!"
         ;;              :numbers/list  [{:number-id 0 :value "afb5f6da-3d8e-49ef-993d-95e55f186fd3"}
         ;;                              {:number-id 1 :value "bc47140c-89ad-4832-a3d7-b22a6aafde6c"}
         ;;                              {:number-id 2 :value "d5d88770-f477-4cec-9b8e-6c9ddf5ce2b7"}
         ;;                              {:number-id 3 :value "478d9320-a1b2-459e-95f5-4bb963fdad1c"}
         ;;                              {:number-id 4 :value "d8bf7561-b9b6-4be7-a5d6-a05f8c86973f"}
         ;;                              {:number-id 5 :value "d8bf7561-b9b6-4be7-a5d6-a05f8c86973f"}]}
         ;; :colors     nil
         }))

(defonce bidi-routes
  ["/" {""        :colors/list
        "numbers" :numbers
        ["colors/" :id] :colors/color}])

(declare app)

(defonce history
  (pushy/pushy (fn [{:keys [handler route-params]}]
                 (c/set-route! app handler {:params {:route-params route-params}}))
    (partial bidi/match-route bidi-routes)))

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

(defonce app
  (c/application {:routes          {:route.colors/list  (c/index-route colors/Colors)
                                    :route.numbers            Numbers
                                    :route.colors/color colors/ColorDetails}
                  :reconciler-opts {:state   app-state
                                    :parser  (om/parser {:read parser/readf})
                                    :send    send
                                    :merge   merge-fn
                                    :remotes [:remote]
                                    :shared  {:history history}}
                  :mixins          [(c/wrap-render Menu)]
                  ;; :history         {:setup    #(pushy/start! history)
                  ;;                   :teardown #(pushy/stop! history)}
                  }))

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


