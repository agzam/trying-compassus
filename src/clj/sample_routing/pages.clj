(ns sample-routing.pages
  (:require
   [hiccup.core :refer [html]]
   [cheshire.core :as json]))

(defn header []
  [:title "Sample routing using Compassus"]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:link {:href "css/styles.css" :rel "stylesheet"}]
   #_[:link {:href "/images/favicon.ico" :type "image/x-icon" :rel "icon"}]])

(defn index-page []
  (html
    [:html
     (header)
     [:body
      [:div {:id "app"}]
      [:script {:src "js/main.js"}]
      [:script "sample_routing.core.init()"]]]))

(defn dev-cards []
  (html
    [:html
     (header)
     [:body
      [:div {:id "dev-cards"}]
      [:script {:src "/js/devcards.js"}]]]))

(defn not-found []
  (html
    [:html
     (header)
     [:body
      [:h1 "Rats! Page not found"]]]))
