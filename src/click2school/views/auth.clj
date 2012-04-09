(ns click2school.views.auth
  (:require [click2school.views.common :as common]
            (click2school.models [user :as u]
                                 [person :as p])
            [noir.response :as resp]
            [noir.session :as sess]
            [noir.validation :as vali])
  (:use [noir.core :only [defpage url-for render]]
        [clojure.data.json :only (read-json json-str)]))



(defn login-form []
  [:form {:enctype "application/x-www-form-urlencoded",
          :action "/login"
          :method "POST",
          :class "form-horizontal"}
   [:fieldset {}
    [:legend {} "Login into Click2Interact System"]
    [:div {:class "control-group"}
     [:label {:for "username", :class "control-label"} "Username/Email"]
     [:div {:class "controls"}
      [:input {:type "text", :class "input-xlarge focused", :id "username", :name "username", :value ""}]]]
    [:div {:class "control-group"}
     [:label {:for "password", :class "control-label"} "Password"]
     [:div {:class "controls"}
      [:input {:type "password", :class "input-xlarge focused", :id "password", :name "password", :value ""}]]]

    [:div {:class "form-actions"}
     [:button {:type "submit", :class "btn btn-primary"} "Login"] "&nbsp;"
     [:button {:type "submit", :class "btn"} "Cancel"]]]] )


(defn login! [{:keys [username password] :as user}]
  (let [usr (u/find-record {:name username})
        {stored-pass :password} usr]
    (if (= stored-pass password)
      (do
        (sess/put! :user-id  (:id usr)))
      (vali/set-error :username "Invalid username or password"))))

;;; when we get the login form
(defpage [:get "/login"] []
  (common/default-layout
    (login-form)))


;;; we submit login
(defpage [:post "/login"] {:as user}
  (if (login! user)
    (resp/redirect "/messages")
    (render "/login" user)))

(defpage logout "/logout" []
  (common/default-layout
    [:h1 "Thank you for using Click2Interact"]))

(defpage [:post "/finduser"] {:as q}
  (json-str (map p/fullname  (p/fuzzy-find (:q q)))))

(defpage [:get "/finduser"] {:as q}
  (json-str (map p/fullname  (p/fuzzy-find (:q q)))))
