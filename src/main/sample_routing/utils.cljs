(ns sample-routing.utils
  (:require [compassus.core :as c]))

(defn change-route [c route e]
  (.preventDefault e)
  (c/set-route! c route))
