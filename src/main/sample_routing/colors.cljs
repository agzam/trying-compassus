(ns sample-routing.colors
  (:require [om.next               :as           om :refer-macros [defui]]
            [sablono.core          :refer-macros [html]]
            [sample-routing.filter :refer        [filter-ui]]
            [taoensso.timbre       :as           log]
            [compassus.core :as c]))

(defui ColorItem
  static om/Ident
  (ident [_ {:keys [id]}]
    [:color-item/by-id id])

  static om/IQuery
  (query [_]
    '[:color-id :name])

  Object
  (render [this]
    (let [{:keys [color-id name]} (om/props this)
          current-route (c/current-route this)]
      (log/spy current-route)
      (condp = current-route
        :colors
        (html [:tr
               {:on-click #(c/set-route! this :color/by-id {:params {:route-params {:id (str color-id)}}})}
               [:td color-id]
               [:td name] ])

        :color/by-id
        (html [:div
               [:span "Color ID: " color-id "; "]
               [:span "Name: " name]])))))

(def color-item (om/factory ColorItem))

(defui Colors
  static om/IQueryParams
  (params [this]
    {:color-id 0})

  static om/IQuery
  (query [this]
    `[:colors/title
      ({:colors/list ~(om/get-query ColorItem)} {:color-id ~'?color-id})])

  Object
  (render [this]
    (let [{:keys [colors/title colors/list] :as props} (om/props this)]
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
