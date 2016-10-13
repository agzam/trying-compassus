(ns sample-routing.colors
  (:require [om.next               :as           om :refer-macros [defui]]
            [sablono.core          :refer-macros [html]]
            [sample-routing.filter :refer        [filter-ui]]
            [taoensso.timbre       :as           log]
            [compassus.core :as c]))

(defui ColorDetails
  static om/Ident
  (ident [_ {:keys [id]}]
    [:color-item/by-id id])

  static om/IQueryParams
  (params [this]
    {:color-id 0})

  static om/IQuery
  (query [this]
    `[({:colors [:colors/list [:name :id]]}
       {:color-id ~'?color-id})])

  Object
  (render [this]
    (let [{:keys [details name color-id]} (om/props this)]
      (html [:div 
             [:h1 name]
             (filter-ui)
             [:table [:tbody
                      (map (fn [{:keys [description id]}]
                             [:tr {:key id}
                              [:td id]
                              [:td description]]) details)]]]))))

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
      (html [:tr
             {:style {:cursor "pointer"}
              :on-click #(c/set-route! this :color/by-id {:params {:route-params {:id (str color-id)}}})}
             [:td color-id]
             [:td name] ]))))

(def color-item (om/factory ColorItem))

(defui Colors
  static om/IQuery
  (query [this]
    `[:colors/title
      {:colors/list ~(om/get-query ColorItem)}])

  Object
  (render [this]
    (let [{:keys [colors/title colors/list] :as props} (om/props this)]
      (log/spy props)
      (html [:div
             [:h3 title]
             [:p "page of colors"]
             [:br]
             [:table
              [:tbody
               (map #(color-item %) list)]]]))))
