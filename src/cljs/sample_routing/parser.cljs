(ns sample-routing.parser
  (:require [om.next              :as om]
            [taoensso.timbre      :as log]))

(defmulti readf om/dispatch)

(defmethod readf :default
  [{:keys [state query ast]} k params]
  (let [st @state]
    {:value (get st k)
     :remote ast}))

(defmethod readf :color/by-id
  [{:keys [state query]} k params]
  (let [{:keys [route-params] :as st} @state]
    (log/spy query)
    {:value (->> (get-in st [:colors :colors/list])
              (filter #(= (str (:color-id %)) (:id route-params)))
              first)}))

(defmethod readf :menu-items
  [{:keys [query state]} k _]
  (let [st @state]
    {:value (om/db->tree query (get st k) st)}))
