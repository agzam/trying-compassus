(ns sample-routing.menu
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [sample-routing.utils :refer [change-route]]))

(defui ^:once MenuItem
  )

(defui ^:once Menu
  static om/IQuery
  (query [this]
    [{:app/menu-items (om/get-query MenuItem)}])

  Object
  (render [this]
    (let [{:keys [app/title]} (om/props this)]
      (html #_[:div
             [:h3 title]
             [:p "Home page"]
             [:a {:href "#" :on-click #(change-route this :about %)}
              "Go to about page"]]))))
