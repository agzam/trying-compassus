(set-env!
 :source-paths    #{"src/clj" "src/cljc" "src/cljs"}
 :resource-paths  #{"resources"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript     "1.9.229"]
                 [org.omcljs/om                 "1.0.0-alpha46"]
                 [compassus                     "0.3.0-SNAPSHOT"]
                 [kibu/pushy                    "0.3.6"]
                 [sablono                       "0.7.5"]
                 [com.taoensso/timbre           "4.7.4"]
                 [org.martinklepsch/boot-garden "1.3.2-0"]

                 [pandeiro/boot-http            "0.7.3"]
                 [org.clojure/core.async        "0.2.385"]
                 [com.cognitect/transit-clj     "0.8.288"]
                 [com.cognitect/transit-cljs    "0.8.239"]
                 [ring                          "1.5.0"]
                 [ring/ring-defaults            "0.3.0-beta1"]
                 [ring-middleware-format        "0.7.0"]
                 [metosin/ring-http-response    "0.8.0"]
                 [bidi                          "2.0.10"]
                 [prone                         "1.1.2"]
                 [com.cemerick/url              "0.1.2-SNAPSHOT"]
                 [hiccup                        "1.0.5"          :exclusions [cljsjs/react]]
                 
                 [org.slf4j/slf4j-nop           "1.7.13"         :scope      "test"]
                 [com.cognitect/transit-clj     "0.8.288"        :scope      "test"]
                 [com.cemerick/piggieback       "0.2.1"          :scope      "test"]
                 [adzerk/boot-cljs              "1.7.228-1"      :scope      "test"]
                 [adzerk/boot-cljs-repl         "0.3.3"          :scope      "test"]
                 [adzerk/boot-reload            "0.4.12"         :scope      "test"]
                 [crisptrutski/boot-cljs-test   "0.2.2-SNAPSHOT" :scope      "test"]
                 [org.slf4j/slf4j-nop           "1.7.21"         :scope      "test"]
                 [org.clojure/tools.nrepl       "0.2.12"         :scope      "test"]
                 [pandeiro/boot-http            "0.7.3"          :scope      "test"]
                 [weasel                        "0.7.0"          :scope      "test"]
                 [binaryage/devtools            "0.8.2"           :scope      "test"]])

(require
 '[adzerk.boot-cljs              :refer [cljs]]
 '[adzerk.boot-cljs-repl         :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload            :refer [reload]]
 '[crisptrutski.boot-cljs-test   :refer [test-cljs]]
 '[pandeiro.boot-http            :refer [serve]]
 '[org.martinklepsch.boot-garden :refer [garden]])

(task-options!
  garden {:styles-var   'sample-routing.styles/combined
          :output-to    "css/styles.css"
          :pretty-print true}
  cljs {:ids              #{"js/dev"}
        :compiler-options {:asset-path     "js/dev.out"
                           :verbose        true
                           :parallel-build true}}
  serve {:handler 'sample-routing.server/app})

(deftask dev []
  (comp
    (serve)
    (watch)
    (cljs-repl)
    (reload :on-jsload 'sample-routing.core/init!)
    (speak)
    (cljs)
    (garden)
    (sift :move {#"dev.js" "main.js"})
    (target)))

(deftask release []
  (comp
    (cljs :optimizations :advanced
      :ids #{"js/dev"})
    (sift :move {#"dev.js" "main.js"})
    (target)))

(deftask testing []
  (set-env! :source-paths #(conj % "src/test"))
  identity)

(ns-unmap 'boot.user 'test)

(deftask test
  [e exit?     bool  "Exit after running the tests."]
  (let [exit? (cond-> exit?
                (nil? exit?) not)]
    (comp
      (testing)
      (test-cljs
        :js-env :node
        :namespaces #{'sample-routing.tests}
        :cljs-opts {:parallel-build true}
        :exit? exit?))))

(deftask auto-test []
  (comp
    (watch)
    (speak)
    (test :exit? false)))
