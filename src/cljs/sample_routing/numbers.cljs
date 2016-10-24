(ns sample-routing.numbers
  (:require [compassus.core :as c]
            [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]))

(defui NumberItem
  static om/Ident
  (ident [_ {:keys [number-id]}]
    [:number-item/by-id number-id])

  static om/IQuery
  (query [_]
    '[:number-id :value])

  Object
  (render [this]
    (let [{:keys [number-id value]} (om/props this)]
      (html [:tr
             [:td number-id]
             [:td value]]))))

(def number-item (om/factory NumberItem))

(defui Numbers
  static om/IQuery
  (query [this]
    [:numbers/title
     {:numbers/list (om/get-query NumberItem)}])

  Object
  (render [this]
    (let [{:keys [numbers/title numbers/list]} (om/props this)]
      (html [:div
             [:h3 title]
             [:p "numbers page"]
             [:table
              [:tbody
               (map #(number-item %) list)]]
             [:div {:on-click (fn [e] (c/set-route! this :route.colors/list))}
              "Go to colors page"]]))))
