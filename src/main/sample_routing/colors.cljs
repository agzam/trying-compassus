(ns sample-routing.colors
  (:require [om.next               :as           om :refer-macros [defui]]
            [sablono.core          :refer-macros [html]]
            [sample-routing.utils  :refer        [change-route]]
            [sample-routing.filter :refer        [filter-ui]]
            [taoensso.timbre       :as           log]))

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
  static om/IQueryParams
  (params [this]
    {:color-id 0})

  static om/IQuery
  (query [this]
    `[:colors/title
      ({:colors/list ~(om/get-query ColorItem)} {:color-id ?color-id})])

  Object
  (render [this]
    (let [{:keys [colors/title
                  colors/list]} (om/props this)]
      (html [:div
             [:h3 title]
             [:p "page of colors"]
             (filter-ui)
             [:br]
             [:table
              [:tbody
               (map #(color-item %) list)
               ]
              ]]))))
