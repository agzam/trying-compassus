(ns sample-routing.filter
  (:require [om.next :as om       :refer-macros [defui]]
            [sablono.core         :refer-macros [html]]
            [taoensso.timbre      :as log]
            [compassus.core       :as c ]
            [pushy.core           :as pushy]))

(defui Filter
  Object
  (initLocalState [this]
    {:input ""})
  (render [this]
    (html [:div
           [:input {:type "text"
                    :on-change #(om/set-state! this {:input (.. % -target -value)})
                    :value (om/get-state this :input)}]
           [:button {:on-click (fn [e]
                                 (let [{:keys [history]} (om/shared this)]
                                   (.preventDefault e)
                                   (pushy/set-token! history (str "/colors/" (om/get-state this :input)))))}
            "Apply"]])))

(def filter-ui (om/factory Filter))
