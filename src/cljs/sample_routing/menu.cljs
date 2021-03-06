(ns sample-routing.menu
  (:require [compassus.core :as c]
            [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [taoensso.timbre :as log]))

(defui MenuItem
  static om/Ident
  (ident [_ {:keys [id]}]
    [:menu-item/by-id id])

  static om/IQuery
  (query [_]
    '[:id :title :route :url])

  Object
  (componentWillReceiveProps [this next-props]
    (om/set-state! this {:active (-> this c/current-route name)}))

  (render [this]
    (let [{:keys [title id route url]} (om/props this)
          active?            (= (or (-> this om/get-state :active)
                                    (-> this c/current-route name)) title)]
      (html [:li {:key      id
                  :class    (if active? "active" "")}
             [:a {:href url} title]]))))

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
             (factory props)
             [:h1 "Footer"]
             ]))))

(def menu-wrapper (om/factory Menu))
