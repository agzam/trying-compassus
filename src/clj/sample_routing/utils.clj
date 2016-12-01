(ns sample-routing.utils
  (:require
   [clojure.string :as str]
   [inflections.core :refer [hyphenate]]
   [om.next :as om]
   [sablono.core :refer [html]]))

(defn- compile-render-body
  [[_ args & render-body :as render-form]]
  `(~'render ~args
    ~@(butlast render-body)
    (html ~(last render-body))))

(defn- frm->protocol [form-name]
  (case form-name
    ident                 {:id `[~'static om.next/Ident]}
    params                {:id `[~'static om.next/IQueryParams]}
    query                 {:id `[~'static om.next/IQuery]}
    -set-state!           {:id `[om.next/ILocalState]}
    -get-state            {:id `[om.next/ILocalState]}
    -get-rendered-state   {:id `[om.next/ILocalState]}
    -merge-pending-state! {:id `[om.next/ILocalState]}
    render                {:id `[~'Object] ` :body-fn compile-render-body}
    {:id `[~'Object]} ))

(defn- build-body
  "Builds the main body for `defui*` and `ui*` macro.
  You can add a specific protocol to the resulting body of `defui` form only once: e.g. you can have only on `om.next/Ident` or `om.next/Object` etc.
  Probably the easiest way - first to 'distribute' each form (of all forms passed into the macro) by putting a form into its own 'bucket' of a protocol"
  [protocol-forms]
  ;; Essentially here, a hash-map is being used with protocols for keys and for values - body of each form (e.g. body of `render` function)
  (let [frms-sorted (reduce
                      (fn [a [fname :as form]]
                        (let [prot (frm->protocol fname)
                              body (if-some [bfn (:body-fn prot)]
                                     (bfn form)
                                     form)]
                          (update a (:id prot)
                                  #(conj % body))))
                      {} protocol-forms)]
    ;; then, once the forms are sorted - each in its own 'protocol-bucket', they need to be 'unwrapped' into simpler structure
    (mapcat (fn [[k v]] (concat k v)) frms-sorted)))

(defmacro defcomponent
  "Wraps the body of a render function, in an Om.next component,
  in a sablono compiler/interpreter call. Adds protocol names for various
  om.next protocol implementations. Also defines a snake-cased factory function
  for the component."
  [& [class-symbol factory-args & protocol-forms]]
  `(do (om.next/defui ~class-symbol
         ~@(build-body protocol-forms))
       (def ~(hyphenate (->> class-symbol (str "ui-") symbol))
         (om.next/factory ~class-symbol ~factory-args))))

(defmacro ui*
  "Wraps the body of a render function, into anonymous Om.next component,
  in a sablono compiler/interpreter call. Adds protocol names for various
  om.next protocol implementations"
  [& [& protocol-forms]]
  `(do
     (om.next/factory
       (om.next/ui ~@(build-body protocol-forms)))))
