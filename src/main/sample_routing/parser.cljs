(ns sample-routing.parser
  (:require [om.next              :as om]
            [taoensso.timbre      :as log]))

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [state query]} k params]
  (let [st @state]
    {:value (get st k)}))

(defmethod read :color/by-id
  [{:keys [state query]} k params]
  (let [{:keys [route-params] :as st} @state]
    {:value (->> (get-in st [:colors :colors/list])
              (filter #(= (str (:color-id %)) (:id route-params)))
              first)}))

(defmethod read :menu-items
  [{:keys [query state]} k _]
  (let [st @state]
    {:value (om/db->tree query (get st k) st)}))
