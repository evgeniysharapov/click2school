(ns click2school.views.welcome
  (:require [click2school.views.common :as common]
            [click2school.views.messages :as messages]
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

     [:p {}
      [:a {:shape "rect", :class "btn btn-primary btn-large"} "Learn more »"]]]))

