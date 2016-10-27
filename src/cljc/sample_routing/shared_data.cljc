(ns sample-routing.shared-data)

(def routes
  {"accounts/"
   {"system"
    [[""                                  :accounts.system/index]
     ["/"                                 :accounts.system/index]
     [["/" [#".*$" :system-account-name]] :accounts.system/details]]
    "customer"
    [[""                                  :accounts.customer/index]
     ["/"                                 :accounts.customer/index]
     [["/" [#".*$" :customer-account-id]] :accounts.customer/details]]}})
