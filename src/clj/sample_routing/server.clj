(ns sample-routing.server
  (:require [clojure.data.json :as json]
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
            [sample-routing.pages :as pages])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]))

(def transit #{"application/transit+msgpack"
              "application/transit+json;q=0.9"})

(defn run-query-impl
  [env k params]
  (log/info "here should be the query"))

(def om-parser (om/parser {:read run-query-impl}))

(defn log-request [handler]
  (fn [request]
      (log/info request)
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
