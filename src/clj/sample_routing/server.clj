(ns sample-routing.server
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.format :as fmt]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.http-response :refer [ok header resource-response] :as resp]
            [prone.middleware :refer [wrap-exceptions]]
            [taoensso.timbre :as log]
            [cognitect.transit :as transit]
            [bidi.ring :refer [make-handler]]
            [om.next.server :as om]
            [sample-routing.shared-data :as shared-data]
            [prone.debug :refer [debug]]
            [sample-routing.pages :as pages])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]))

(def transit #{"application/transit+msgpack"
              "application/transit+json;q=0.9"})

(defmulti readf om/dispatch)

(def om-parser (om/parser {:read readf}))

(def colors-data {:colors/title   "here will be colors"
                  :colors/list    [{:color-id 0 :name "red"}
                                   {:color-id 1 :name "orange"}
                                   {:color-id 2 :name "yellow"}
                                   {:color-id 3 :name "green"}
                                   {:color-id 4 :name "blue"}
                                   {:color-id 5 :name "indigo"}
                                   {:color-id 6 :name "violet"}]
                  :colors/details [{:id 0 :details [{ :id 0 :description "Red. Cras placerat accumsan nulla" :title "Red. 0"}
                                                    { :id 1 :description "Red. Donec vitae dolor" :title "Red. 1"}
                                                    { :id 2 :description "Red. Nullam rutrum" :title "Red. 2"}]}
                                   {:id 1 :details [{ :id 0 :description "Orange. Cras placerat accumsan nulla" :title "Orange. 0"}
                                                    { :id 1 :description "Orange. Donec vitae dolor" :title "Orange. 1"}
                                                    { :id 2 :description "Orange. Nullam rutrum" :title "Orange. 2"}]}
                                   {:id 2 :details [{ :id 0 :description "Yellow. Cras placerat accumsan nulla" :title "Yellow. 1"}
                                                    { :id 1 :description "Yellow. Donec vitae dolor" :title "Yellow. 2"}
                                                    { :id 2 :description "Yellow. Nullam rutrum" :title "Yellow. 3"}]}
                                   {:id 3 :details [{ :id 0 :description "Green. Cras placerat accumsan nulla" :title "Green. 0"}
                                                    { :id 1 :description "Green. Donec vitae dolor" :title "Green. 1"}
                                                    { :id 2 :description "Green. Nullam rutrum" :title "Green. 2"}]}
                                   {:id 4 :details [{ :id 0 :description "Blue. Cras placerat accumsan nulla" :title "Blue. 0"}
                                                    { :id 1 :description "Blue. Donec vitae dolor" :title "Blue. 1"}
                                                    { :id 2 :description "Blue. Nullam rutrum" :title "Blue. 2"}]}
                                   {:id 5 :details [{ :id 0 :description "Indigo. Cras placerat accumsan nulla" :title "Indigo. 0"}
                                                    { :id 1 :description "Indigo. Donec vitae dolor" :title "Indigo. 1"}
                                                    { :id 2 :description "Indigo. Nullam rutrum" :title "Indigo. 2"}]}
                                   {:id 6 :details [{ :id 0 :description "Violet. Cras placerat accumsan nulla" :title "Violet.. 0"}
                                                    { :id 1 :description "Violet. Donec vitae dolor" :title "Violet.. 1"}
                                                    { :id 2 :description "Violet. Nullam rutrum" :title "Violet.. 2"}]}]})

(defmethod readf :route.colors/list
  [{:keys [query ast]} k params]
  {:value 
   {:colors/title (:colors/title colors-data)
    :colors/list  (:colors/list colors-data)}})

(defmethod readf :route.colors/color
  [{:keys [query parser] :as env} _ params]
  {:value (parser env query)})

(defmethod readf :color/info
  [{:keys [query parser] :as env} _ {:keys [color-id color-desc]}]
  (let [id (clojure.edn/read-string color-id)]
    {:value
     {:color/header  (->> colors-data
                       :colors/list
                       (filter #(= (:color-id %) id))
                       first)
      :color/details (->> colors-data
                       :colors/details
                       (filter #(= (:id %) id))
                       first
                       :details
                       (filter #(if (empty? color-desc)
                                  true
                                  (str/includes?
                                    (-> % :description str/lower-case)
                                    (-> color-desc str str/lower-case)))))}}))

(defmethod readf :route.numbers
  [_ _ _]
  {:value
   {:numbers/title "numbers are here!"
    :numbers/list
    [{:number-id 0 :value "afb5f6da-3d8e-49ef-993d-95e55f186fd3"}
     {:number-id 1 :value "bc47140c-89ad-4832-a3d7-b22a6aafde6c"}
     {:number-id 2 :value "d5d88770-f477-4cec-9b8e-6c9ddf5ce2b7"}
     {:number-id 3 :value "478d9320-a1b2-459e-95f5-4bb963fdad1c"}
     {:number-id 4 :value "d8bf7561-b9b6-4be7-a5d6-a05f8c86973f"}
     {:number-id 5 :value "d8bf7561-b9b6-4be7-a5d6-a05f8c86973f"}]}})

(defn log-request [handler]
  (fn [request]
    (handler request)))

(defn content-type [cnt ctype]
  (let [c (get {:html    "text/html; charset=UTF-8"
                :css     "text/css; charset=UTF-8"
                :js      "application/javascript; charset=UTF-8"
                :json    "application/json; charset=UTF-8"
                :transit "application/transit+json; charset=UTF-8"
                :font    "font/opentype"
                :svg     "image/svg+xml"} ctype
            (name ctype))]
    (resp/content-type cnt c)))

(defn serve-index [_]
  (-> (pages/index-page) (ok) (content-type :html)))

(defn transit-write [clj-obj]
  (let [out-stream (ByteArrayOutputStream.)]
    (transit/write (transit/writer out-stream :json) clj-obj)
    (.toString out-stream)))

(defn om-query-resource [req]
  (let [query (transit/read (transit/reader (:body req) :json))
        result (om-parser nil query)]
    (-> result transit-write ok (content-type :transit))));

(defn not-found [req]
  (-> (pages/not-found) (resp/not-found) (content-type :html)))

(def routes
  "This data structure represents bidi-style routes.
   Handlers are either a ring handler function, or a keyword."
  ["/"
   [[:get shared-data/routes]
    ["" :index]
    #_["devcards" {:get serve-devcards}]
    ["data" {:post om-query-resource}]
    [true not-found]]])

(defn spa-handler
  "The argument handler comes from the routes above.
   This will either be a keyword indicating which page to display,
   or some handler function. The output is a ring handler.
   Since this is a single page app, all pages are served via serve-index."
  [handler]
  (if (keyword? handler) serve-index handler))

(def app
  (let [handler (-> routes
                  (make-handler spa-handler)
                  (wrap-resource "")
                  (wrap-content-type)
                  (wrap-not-modified)
                  (wrap-defaults api-defaults)
                  log-request
                  )]
    (-> handler
      wrap-exceptions
      wrap-reload
      )))
