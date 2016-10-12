(ns sample-routing.filter
  (:require [om.next :as om       :refer-macros [defui]]
            [sablono.core         :refer-macros [html]]
            [sample-routing.utils :refer [change-route]]
            [taoensso.timbre      :as log]))

(defui Filter
  Object
  (render [this]
    (html [:div
           [:input {:type "text"}]
           [:button "Apply"]])))

(def filter-ui (om/factory Filter))
