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
  static om/IQueryParams
  (params [this]
    {:color-desc nil
     :color-id nil})

  static om/IQuery
  (query [this]
    `[({:color/info
        {:color/header [:color-id :name]
         :color/details [:id :description :title]}} {:color-id ~'?color-id
                                                     :color-desc ~'?color-desc})])

  Object
  (render [this]
    (let [{{:keys [color/header color/details]} :color/info} (om/props this)]
      (html [:div 
             [:h1 (:name header)]
             [:label "description filter:"
              (filter-ui)]
             [:table
              [:tbody
               [:tr [:th "id"][:th "description"][:th "title"]]
               (map
                 (fn [{:keys [id description title]}]
                   [:tr {:key id}
                    [:td id] [:td description] [:td title]])
                 details)]]]))))

(defui ColorItem
  static om/Ident
  (ident [_ {:keys [color-id]}]
    [:color-item/by-id color-id])

  static om/IQuery
  (query [_]
    [:color-id :name])

  Object
  (render [this]
    (let [{:keys [color-id name]} (om/props this)
          current-route (c/current-route this)
          {:keys [selected?]} (om/get-state this)]
      (html [:tr
             {:style {:cursor "pointer" :background (if selected? "orange" "wheat")}
              ;; This is a trivial example that of course should be done purely in CSS
              ;; I just needed a basic case to demonstrate that local state works 
              :on-mouse-enter #(om/set-state! this {:selected? true})
              :on-mouse-leave #(om/set-state! this {:selected? false})
              :on-click #(c/set-route! this :route.colors/color {:queue? true
                                                                 :params {:route-params {:color-id (str color-id)}}})}
             [:td color-id]
             [:td name]]))))

(def color-item-ui (om/factory ColorItem))

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
               (map #(color-item-ui %) list)]]]))))
