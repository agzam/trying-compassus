(ns sample-routing.parser
  (:require [om.next :as om]
            [taoensso.timbre :as log]))

(defmulti readf om/dispatch)

(defmethod readf :route.colors/list
  [{:keys [target parser state query ast] :as env} k params]
  (let [st @state]
    (log/spy k)
    (log/spy target)
    {:value  (get st k)
     :remote ast}))

(defn set-ast-params [children params]
  "traverses given vector of `children' in an AST and sets `params`"
  (mapv
    (fn [c]
      (let [ks (clojure.set/intersection (-> params keys set) (-> c :params keys set))]
        (update-in c [:params] #(merge % (select-keys params (vec ks))))))
    children))

(defmethod readf :route.colors/color
  [{:keys [target state query ast parser] :as env} k params]
  (log/spy k)
  (log/spy target)
  (let [{:keys [route-params] :as st} @state]
    {:value (get k st) ;; TODO: parameterize ast with route-params
     :remote ast}))

(defmethod readf :menu-items
  [{:keys [query state]} k _]
  (let [st @state]
    {:value (om/db->tree query (get st k) st)}))
