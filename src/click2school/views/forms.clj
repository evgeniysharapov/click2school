(ns click2school.views.forms
  (:require (click2school.views [sidebar :as sidebar]
                                [common :as common])
            [noir.session :as sess])
  (:use [noir.core :only (defpage defpartial)]))


(defpartial  list-of-forms []
  [:section#list-of-forms
   [:div.page-header
    [:h1 " Forms "
     [:small "sent, pending and templates"]]]

   [:h2 "Forms sent"]
   [:table.table.table-bordered.table-striped
    [:thead
     [:tr
      [:th {:width "10%"} "Form #"]
      [:th "Name"]
      [:th "Description"]
      [:th "Answers"]]]
    [:tbody
     [:tr {:id (str "sent-forms-form-136")}
      [:td  "136"]
      [:td  "Super Form"]
      [:td  "This is a super form that has been sent to many people"]
      [:td  [:span.badge.badge-success "Done"]]
      ]]
    ]
   ])


(defpage forms-route "/forms" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar/sidebar
     (sidebar/activate-item sidebar/*default-sidebar* :forms))
    (list-of-forms)))

