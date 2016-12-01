(ns sample-routing.filter
  (:require
   [sample-routing.utils :refer [defcomponent]]
   [om.next :as om]
   [sablono.core :refer-macros [html]]
   [compassus.core :as c]
   [pushy.core :as pushy]))

(defcomponent Filter []
  (initLocalState
    [_]
    {:input ""})
  (render
    [this]
    [:div
     [:input {:type      "text"
              :on-change #(om/set-state! this {:input (.. % -target -value)})
              :value     (om/get-state this :input)}]
     [:button {:on-click (fn [e]
                           (.preventDefault e)
                           (om/transact! this
                                         `[(~'set-filter!
                                            {:key       :color-desc
                                             :value     ~(-> this om/get-state :input)
                                             :component ~this})]))}
      "Apply"]]))
