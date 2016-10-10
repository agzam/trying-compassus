(ns sample-routing.colors
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [sample-routing.utils :refer [change-route]]))

(defui ^:once Colors
  static om/IQuery
  (query [this]
    [:colors/title])
  Object
  (render [this]
    (let [{:keys [colors/title]} (om/props this)]
      (html [:div
             [:h3 title]
             [:p "page of colors"]
             [:a {:href "#" :on-click #(change-route this :numbers %)}
              "Go to numbers page"]]))))
