(ns sample-routing.numbers
  (:require [om.next              :as           om :refer-macros [defui]]
            [sablono.core         :refer-macros [html]]
            [sample-routing.utils :refer        [change-route]]))

(defui NumberItem
  static om/Ident
  (ident [_ {:keys [id]}]
    [:number-item/by-id id])

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
             [:a {:href "/"}
              "Go to colors page"]]))))
