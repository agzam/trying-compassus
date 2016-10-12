(ns sample-routing.parser
  (:require [om.next              :as om]
            [taoensso.timbre      :as log]))

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [state query]} k params]
  (let [st @state]
    {:value (get st k)}))

(defmethod read :menu-items
  [{:keys [query state]} k _]
  (let [st @state]
    {:value (om/db->tree query (get st k) st)}))
