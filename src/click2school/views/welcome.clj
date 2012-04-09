(ns click2school.views.welcome
  (:require [click2school.views.common :as common]
            [click2school.views.message :as messages]
            [click2school.views.auth :as auth]
            [noir.response :as resp])
  (:use [noir.core :only [defpage url-for]]
        [hiccup.core :only [html]]))

(defpage "/" []
  (resp/redirect (url-for home)))

(defpage home "/home" []
  (common/default-layout
    [:div {:class "hero-unit"}
     [:h1 " Welcome To Click2Interact"]

     [:p
      [:b  "Click2interact"] " is a web application that allows teachers, students and their parents communicate in the easy and most efficient manner."]

     [:p "Every day, teachers make countless real-time decisions and facilitate dozens of interactions between themselves and their students. Although they share this commonality, educators all over the country often talk about these decisions and interactions in different ways."]
     [:p {:class "signup"}
      [:a {:title "Register for Click2Interact System" :href "/signup" :class "btn btn-success btn-huge"}
              [:strong "Sign Up"]]
     ]]
    ))

(defpage about "/about" []
  (common/default-layout
    [:div {:class "hero-unit"}
     [:h1 "About Us"]
     [:p "We all love reading about new things. We also like to remember what we read and use information to build our knowledge."]
      [:p "However the workflow of finding, reading, storing, searching and sharing information on the web seems broken to us. It's very random and scattered to many places."]
      [:p "Kippt grew from that frustration. We want to make your information workflow and archiving effortless."]]))
