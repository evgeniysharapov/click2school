(ns click2school.views.user
  (:use [noir.core :only [defpage defpartial render]]
        [click2school.views.common :only [defview]])
  (:require (click2school.models [user :as u]
                                 [person :as p])
            [noir.response :as resp]))

(defpartial new-user [usr]
  [:section {:id "general-info"}
   [:div {:class "page-header"}
    [:h1 {} "Register For Click2Interact"]]
;   [:h2 {} "New User Registration"]
   [:div {:class "row"}
    [:div {:class "span8"}
     [:form {:enctype "application/x-www-form-urlencoded", :method "post", :class "form-horizontal"}
      [:fieldset {}
       [:legend {} "Sign up for a free account"]
       [:div {:class "control-group"}
        [:label {:for "fullname", :class "control-label"} "Your Full Name"]
        [:div {:class "controls"}
         [:input {:type "text", :class "input-xlarge", :id "fullname" :name "fullname" :value (p/fullname (u/find-person usr))}]
         [:p {:class "help-block"} "Required."]]]
       [:div {:class "control-group"}
        [:label {:for "email", :class "control-label"} "Your Email"]
        [:div {:class "controls"}
         [:input {:type "email", :class "input-xlarge", :id "email" :name "email" :value (:email (u/find-person  usr))}]
         [:p {:class "help-block"} "Required. Activation email will be sent to this address"]]]
       [:div {:class "control-group"}
        [:label {:for "passw", :class "control-label"} "Create Password"]
        [:div {:class "controls"}
         [:input {:type "password", :class "input-xlarge", :id "passw" :name "password" :value (:password usr)}]
         [:p {:class "help-block"} "Required."]]]
       [:div {:class "control-group"}
        [:label {:for "organization", :class "control-label"} "Your Organization"]
        [:div {:class "controls"}
         [:input {:type "text", :class "input-xlarge", :id "orgnaization" :name "org" :value (:org (u/find-person usr))}]
         [:p {:class "help-block"} "Purely optional, could be changed later"]]]
       [:div {:class "form-actions"}

        [:button {:type "submit", :class "btn btn-primary"} "Sign up"]
        "&nbsp;&nbsp;"
        ;[:button {:type "submit", :class "btn"} "Cancel"]
        ]]]]]])

(defmacro defview-user
  [name & content ]
  `(defpartial ~name []
     [:section {:id "user-info"}
   [:div {:class "page-header"}
    [:h1 {} "User Account Information"]]
   [:h2 {} "User Information"]
   [:div {:class "row"}
    [:div {:class "span8"}
     ~@content
     ]]]))

(defpartial change-account-info []
  [:section {:id "user-info"}
   [:div {:class "page-header"}
    [:h1 {} "User Account Information"]]
   [:h2 {} "User Information"]
   [:div {:class "row"}
    [:div {:class "span8"}
     [:form {:enctype "application/x-www-form-urlencoded", :method "get", :class "form-horizontal"}
      [:fieldset {}
       [:legend {} "Account Information"]
       [:div {:class "control-group"}
        [:label {:for "username", :class "control-label"} "Username"]
        [:div {:class "controls"}
         [:input {:type "text", :class "input-xlarge", :id "username"}]
         [:p {:class "help-block"} "Required. Activation email will be sent to this address\n                      "
          [:button {:type "", :class "btn btn-mini btn-success"} "Check"]]]]
       [:div {:class "control-group"}
        [:label {:for "email", :class "control-label"} "Your Email"]
        [:div {:class "controls"}
         [:input {:type "email", :class "input-xlarge", :id "email"}]
         [:p {:class "help-block"} "Required. Activation email will be sent to this address"]]]
       [:div {:class "control-group"}
        [:label {:for "passw", :class "control-label"} "New Password"]
        [:div {:class "controls"}
         [:input {:type "password", :class "input-xlarge", :id "passw"}]]]
       [:div {:class "control-group"}
        [:label {:for "passw", :class "control-label"} "Confirm Password"]
        [:div {:class "controls"}
         [:input {:type "password", :class "input-xlarge", :id "passw"}]
         [:p {:class "help-block"} "Should be the same as the password."]]]
       [:div {:class "form-actions"}
        [:button {:type "submit", :class "btn btn-primary"} "Save changes"]
        [:button {:type "submit", :class "btn"} "Cancel"]]]]
     [:form {:enctype "application/x-www-form-urlencoded", :method "get", :class "form-horizontal"}
      [:fieldset {}
       [:legend {} "Personal Information"]
       [:fieldset {}
        [:div {:class "control-group"}
         [:label {:for "firstname", :class "control-label"} "First Name"]
         [:div {:class "controls"}
          [:input {:type "email", :class "input-xlarge", :id "firstname"}]
          [:p {:class "help-block"} "Purely optional, could be changed later"]]]
        [:div {:class "control-group"}
         [:label {:for "middlename", :class "control-label"} "Middle Name"]
         [:div {:class "controls"}
          [:input {:type "email", :class "input-xlarge", :id "middlename"}]
          [:p {:class "help-block"} "Purely optional, could be changed later"]]]
        [:div {:class "control-group"}
         [:label {:for "lastname", :class "control-label"} "Last Name"]
         [:div {:class "controls"}
          [:input {:type "email", :class "input-xlarge", :id "lastname"}]
          [:p {:class "help-block"} "Purely optional, could be changed later"]]]
        [:div {:class "control-group"}
         [:label {:for "organization", :class "control-label"} "Your Organization"]
         [:div {:class "controls"}
          [:input {:type "email", :class "input-xlarge", :id "orgnaization"}]
          [:p {:class "help-block"} "Purely optional, could be changed later"]]]]]]]]])


;(defview )

(defpage "/signup" {:as usr}
  (common/default-layout
    (change-account-info)))

(defpage [:get "/checkusername"] {:as uname}
  (when-not (nil? (u/find-record {:name uname}))
    (json-str {:error "User with this username already exists!"})))

(defpage [:post "/signup"] {:as usr}
  (if true ;(u user)
    ;(do (users/login user))
    (resp/redirect "/messages")
    ;(render "/signup" user)
    ))
