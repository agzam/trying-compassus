(ns sample-routing.styles
  (:require [garden.def        :refer [defrule defstyles]]
            [garden.stylesheet :refer [rule]]))

(defstyles base
  (let [body (rule :body)]
    (body
      {:font-size        "18px"
       :font-family      "Helvetica"
       :background-color "wheat"}
      [:ul {:list-style-type :none
            :padding-left    0}])))

(defstyles menu
  [:.menu
   [:li {:display      :inline-block
         :margin-right "10px"
         :cursor       :pointer}]
   [:li.active {:color         :red
                :border-bottom ["1px solid black"]}]])

(defstyles combined
  base
  menu)
