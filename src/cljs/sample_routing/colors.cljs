(ns sample-routing.colors
  (:require
   [sample-routing.utils :refer [defcomponent]]
   [sample-routing.filter :refer [ui-filter]]
   [om.next :as om]
   [compassus.core :as c]))

(defcomponent ColorDetailsItem []
  (ident [_ {:keys [id]}]
    [:color-details-item/by-id id])
  (query [_]
    [:id :description :title])
  (render [this]
    (let [{:keys [id description title]} (om/props this)]
      [:tr {:style {:cursor "pointer"}}
       [:td id]
       [:td description]
       [:td title]])))

(defcomponent ColorDetails []
  (params [_]
    {:color-desc nil
     :color-id nil})
  (query [_]
    `[({:color/info
        {:color/header [:color-id :name]
         :color/details [:id :description :title]}} {:color-id ~'?color-id
                                                     :color-desc ~'?color-desc})])
  (render
    [this]
    (let [{{:keys [color/header color/details]} :color/info} (om/props this)]
      [:div 
       [:h1 (:name header)]
       [:label "description filter:"
        (ui-filter)]
       [:table
        [:tbody
         [:tr [:th "id"][:th "description"][:th "title"]]
         (map
           (fn [{:keys [id description title]}]
             [:tr {:key id}
              [:td id] [:td description] [:td title]])
           details)]]])))

(defcomponent ColorItem []
  (ident [_ {:keys [color-id]}]
    [:color-item/by-id color-id])
  (query [_]
    [:color-id :name])
  (render
    [this]
    (let [{:keys [color-id name]} (om/props this)
          current-route (c/current-route this)
          {:keys [selected?]} (om/get-state this)]
      [:tr
       {:style {:cursor "pointer" :background (if selected? "orange" "wheat")}
        ;; This is a trivial example that of course should be done purely in CSS
        ;; I just needed a basic case to demonstrate that local state works 
        :on-mouse-enter #(om/set-state! this {:selected? true})
        :on-mouse-leave #(om/set-state! this {:selected? false})}
       [:td color-id]
       [:td
        [:a {:href (str "/color/" color-id)} name]]])))

(defcomponent Colors []
  (query [_]
    `[:colors/title
      {:colors/list ~(om/get-query ColorItem)}])

  (render
    [this]
    (let [{:keys [colors/title colors/list] :as props} (om/props this)]
      [:div
       [:h3 title]
       [:p "page of colors"]
       [:br]
       [:table
        [:tbody
         (map #(ui-color-item %) list)]]])))
