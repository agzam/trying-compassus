(ns sample-routing.numbers
  (:require
   [sample-routing.utils :refer [defcomponent]]
   [compassus.core :as c]
   [om.next :as om]))

(defcomponent NumberItem []
  (ident [_ {:keys [number-id]}]
    [:number-item/by-id number-id])
  (query [_]
    '[:number-id :value])
  (render [this]
    (let [{:keys [number-id value]} (om/props this)]
      [:tr
       [:td number-id]
       [:td value]])))

(defcomponent Numbers []
  (query [this]
    [:numbers/title
     {:numbers/list (om/get-query NumberItem)}])

  (render [this]
    (let [{:keys [numbers/title numbers/list]} (om/props this)]
      [:div
       [:h3 title]
       [:p "numbers page"]
       [:table
        [:tbody
         (map #(ui-number-item %) list)]]
       [:div {:on-click (fn [e] (c/set-route! this :route.colors/list))}
        "Go to colors page"]])))
