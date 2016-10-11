(ns sample-routing.colors
  (:require [om.next :as om       :refer-macros [defui]]
            [sablono.core         :refer-macros [html]]
            [sample-routing.utils :refer [change-route]]
            [taoensso.timbre      :as log]))

(defui ColorItem
  static om/Ident
  (ident [_ {:keys [id]}]
    [:color-item/by-id id])

  static om/IQuery
  (query [_]
    '[:color-id :name])

  Object
  (render [this]
    (let [{:keys [color-id name]} (om/props this)]
      (html [:tr
             [:td color-id]
             [:td name]]))))

(def color-item (om/factory ColorItem))

(defui Colors
  static om/IQuery
  (query [this]
    [:colors/title
     {:colors/list (om/get-query ColorItem)}])

  Object
  (render [this]
    (let [{:keys [colors/title
                  colors/list]} (om/props this)]
      (log/spy list)
      (html [:div
             [:h3 title]
             [:p "page of colors"]
             [:table
              [:tbody
               (map #(color-item %) list)
               ]
              ]]))))
