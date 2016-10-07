(ns sample-routing.about
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [sample-routing.utils :refer [change-route]]))

(defui ^:once About
  static om/IQuery
  (query [this]
    [:about/title])
  Object
  (render [this]
    (let [{:keys [about/title]} (om/props this)]
      (html [:div
             [:h3 title]
             [:p "About page"]
             [:a {:href "#" :on-click #(change-route this :home %)}
              "Go to home page"]]))))
