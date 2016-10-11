(ns sample-routing.menu
  (:require [om.next              :as om  :refer-macros [defui]]
            [sablono.core                 :refer-macros [html]]
            [sample-routing.utils         :refer [change-route]]
            [taoensso.timbre      :as log]
            [compassus.core       :as c]))

(defui MenuItem
  static om/Ident
  (ident [_ {:keys [id]}]
    [:menu-item/by-id id])

  static om/IQuery
  (query [_]
    '[:id :title])

  Object
  (componentWillReceiveProps [this next-props]
    (om/set-state! this {:active (-> this c/current-route name)}))

  (render [this]
    (let [{:keys [title id]} (om/props this)
          active?            (= (or (-> this om/get-state :active)
                                    (-> this c/current-route name)) title)]
      (log/info (om/get-state this))
      (html [:li {:key      id
                  :class    (if active? "active" "")
                  :on-click #(c/set-route! this (keyword title)) }
             [:div title]]))))

(def item (om/factory MenuItem))

(defui Menu
  static om/IQuery
  (query [this]
    [{:menu-items (om/get-query MenuItem)}])

  Object
  (render [this]
    (let [
          {:keys [menu-items]} (om/props this)
          {:keys [owner factory props]} (om/get-computed this)]
      (html [:div
             [:ul.menu (map item menu-items)]
             (factory props)]))))

(def menu-wrapper (om/factory Menu))

