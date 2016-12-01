(ns sample-routing.menu
  (:require
   [compassus.core :as c]
   [om.next :as om]
   [sample-routing.utils :refer [defcomponent]]))

(defcomponent MenuItem []
  (ident [_ {:keys [id]}]
    [:menu-item/by-id id])
  (query [_]
    '[:id :title :route :url])
  (componentWillReceiveProps
    [this next-props]
    (om/set-state! this {:active (-> this c/current-route name)}))
  (render
    [this]
    (let [{:keys [title id route url]} (om/props this)
          active?            (= (or (-> this om/get-state :active)
                                    (-> this c/current-route name)) title)]
      [:li {:key   id
            :class (if active? "active" "")}
       [:a {:href url} title]])))

(defcomponent Menu []
  (query [_]
    [{:menu-items (om/get-query MenuItem)}])
  (render
    [this]
    (let [
          {:keys [menu-items]} (om/props this)
          {:keys [owner factory props]} (om/get-computed this)]
      [:div
       [:ul.menu (map ui-menu-item menu-items)]
       (factory props)
       [:h1 "Footer"]])))
