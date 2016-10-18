(ns sample-routing.parser
  (:require [om.next :as om]
            [taoensso.timbre :as log]))

(defmulti readf om/dispatch)

(defmethod readf :colors/list
  [{:keys [target state query ast] :as env} k params]
  (let [st @state]
    (log/spy env)
    {:value  (get st k)
     :remote ast}))

(defn set-ast-params [children params]
  "traverses given vector of `children' in an AST and sets `params`"
  (mapv
    (fn [c]
      (let [ks (clojure.set/intersection (-> params keys set) (-> c :params keys set))]
        (update-in c [:params] #(merge % (select-keys params (vec ks))))))
    children))

(defmethod readf :colors/color
  [{:keys [target state query ast parser] :as env} k params]
  (let [{:keys [route-params] :as st} @state]
    {:remote ast})) 

(defmethod readf :menu-items
  [{:keys [query state]} k _]
  (let [st @state]
    {:value (om/db->tree query (get st k) st)}))
