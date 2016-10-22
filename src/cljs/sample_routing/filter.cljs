(ns sample-routing.filter
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [taoensso.timbre :as log]
            [compassus.core :as c]
            [pushy.core :as pushy]))

(defui Filter
  Object
  (initLocalState [this]
    {:input ""})

  (render [this]
    (let [{:keys [param-def] :as props} (om/props this)]
      (log/spy props)
      (html [:div
             [:input {:type      "text"
                      :on-change #(om/set-state! this {:input (.. % -target -value)})
                      :value     (om/get-state this :input)}]
             [:button {:on-click (fn [e]
                                   (.preventDefault e)
                                   (om/transact! this
                                     `[(~'set-filter!
                                        {:key       ~param-def
                                         :value     ~(-> this om/get-state :input)
                                         :component ~this})]))}
              "Apply"]]))))

(def filter-ui (om/factory Filter))

(defn make-filter [param-def]
  (filter-ui {:param-def param-def}))
