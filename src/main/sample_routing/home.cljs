(ns sample-routing.home
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [sample-routing.utils :refer [change-route]]))

(defui ^:once Home
  static om/IQuery
  (query [this]
    [:app/title])
  Object
  (render [this]
    (let [{:keys [app/title]} (om/props this)]
      (html [:div
             [:h3 title]
             [:p "Home page"]
             [:a {:href "#" :on-click #(change-route this :about %)}
              "Go to about page"]]))))
