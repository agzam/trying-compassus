(ns sample-routing.numbers
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [sample-routing.utils :refer [change-route]]))

(defui ^:once Numbers
  static om/IQuery
  (query [this]
    [:numbers/title])
  Object
  (render [this]
    (let [{:keys [numbers/title]} (om/props this)]
      (html [:div
             [:h3 title]
             [:p "numbers page"]
             [:a {:href "#" :on-click #(change-route this :colors %)}
              "Go to colors page"]]))))
