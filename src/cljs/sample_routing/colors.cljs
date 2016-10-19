(ns sample-routing.colors
  (:require [om.next               :as           om :refer-macros [defui]]
            [sablono.core          :refer-macros [html]]
            [sample-routing.filter :refer        [filter-ui]]
            [taoensso.timbre       :as           log]
            [compassus.core :as c]))

(defui ColorDetailsItem
  static om/Ident
  (ident [_ {:keys [id]}]
    [:color-details-item/by-id id])

  static om/IQuery
  (query [_]
    [:id :description :title])

  Object
  (render [this]
    (let [{:keys [id description title]} (om/props this)]
      (html [:tr {:style {:cursor "pointer"}}
              [:td id]
              [:td description]
              [:td title]]))))

(defui ColorDetails
  static om/Ident
  (ident [_ {:keys [color-id]}]
    [:color-item/by-id color-id])

  ;; static om/IQueryParams
  ;; (params [this]
  ;;   {:color-details-title ""
  ;;    :color-id nil})

  static om/IQuery
  (query [this]
    `[{:color/header [:color-id :name]}]
    ;; `[({:colors/header [:color-id :name]} {:color-id ~'?color-id})
    ;;   ;; #_({:colors/details ~(om/get-query ColorDetailsItem)}
    ;;   ;;  {:color-details-title ~'?color-details-title})
    ;;   ]
    )

  Object
  (render [this]
    (let [props (om/props this)]
      (log/spy props)
      (html [:div 
             [:label 
              [:h1 (-> props :color/header :name)]]]))))

(defui ColorItem
  static om/Ident
  (ident [_ {:keys [id]}]
    [:color-item/by-id id])

  static om/IQuery
  (query [_]
    [:color-id :name])

  Object
  (render [this]
    (let [{:keys [color-id name]} (om/props this)
          current-route (c/current-route this)]
      (html [:tr
             {:style {:cursor "pointer"}
              :on-click #(c/set-route! this :route.colors/color {:params {:route-params {:color-id (str color-id)}}})}
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
      (html [:div
             [:h3 title]
             [:p "page of colors"]
             [:br]
             [:table
              [:tbody
               (map #(color-item %) list)]]]))))
