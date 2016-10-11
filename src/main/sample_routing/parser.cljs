(ns sample-routing.parser
  (:require [om.next              :as om]
            [taoensso.timbre      :as log]))

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [state query]} _ _]
  {:value (select-keys @state query)})

(defmethod read :menu-items
  [{:keys [query state]} k _]
  (let [st @state]
    {:value (om/db->tree query (get st k) st)}))
