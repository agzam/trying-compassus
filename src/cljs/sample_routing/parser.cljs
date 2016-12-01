(ns sample-routing.parser
  (:require
   [om.next :as om]
   [compassus.core :as c]))

(defmulti readf om/dispatch)
(defmulti mutate om/dispatch)

(defn set-ast-params [children params]
  "traverses given vector of `children' in an AST and sets `params`"
  (mapv
    (fn [c]
      (let [ks (clojure.set/intersection (-> params keys set) (-> c :params keys set))]
        (update-in c [:params] #(merge % (select-keys params (vec ks))))))
    children))

(defmethod readf :route.colors/list
  [{:keys [target state query ast] :as env} k params]
  (let [st @state]
    {:value  (get st k)
     :remote ast}))

(defmethod readf :route.colors/color
  [{:keys [parser target state query ast] :as env} k params]
  (let [{:keys [route-params] :as st} @state
        ast'                          (-> ast
                                        (update :children #(set-ast-params % route-params))
                                        om/ast->query
                                        om.next.impl.parser/expr->ast)]
    {:value  (get st k)
     :remote ast'}))

(defmethod readf :menu-items
  [{:keys [query state]} k _]
  (let [st @state]
    {:value (om/db->tree query (get st k) st)}))

(defmethod readf :route.numbers
  [{:keys [state]} k _]
  (let [st @state]
    {:value (get st k)
     :remote true}))

(defmethod mutate 'set-filter!
  [{:keys [state]} _ {:keys [key value component]}]
  (let [{:keys [route-params]} @state]
    {:action (fn []
               (c/set-route! component (c/current-route component)
                 {:queue? true
                  :params {:route-params (assoc route-params key value)}}))}))
