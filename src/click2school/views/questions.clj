(ns click2school.views.questions
  (:require (click2school.views [sidebar :as sidebar]
                                [common :as common])
            (click2school.models [question :as question]
                                 )
            [noir.session :as sess])
  (:use [noir.core :only (defpage defpartial)]))


(defpartial list-of-questions-and-answers []
  [:section#list-of-questions-and-answers
   [:div.page-header
    [:h1 " Q & A "
     [:small "questions and answers that could be reused in forms and quizzes"]]]
   [:h2 "Assortment of Questions"]
   [:p "You can check the questions you like and then press the button to create a quizz or form out of them. Do not worry about the order you can arrange question later."]
   [:form.form-horizontal {:action "/questions" :method "GET"}
    [:label "Create from selection"]
    [:button.btn {:type "submit"} "Form" ] "&nbsp;"
    [:button.btn {:type "submit"} "Quiz" ] "&nbsp;"
    [:input.span3.pull-right {:type "text" :placeholder "Filter questions"}]
    [:table.table.table-striped
     [:thead
      [:tr
       [:th]
       [:th {:width "10%"} "#"]
       [:th "Title"]
       [:th "Question"]
       [:th "Used In"]]]
     [:tbody
      (for [q  (question/find-records ["1=1"])]
        [:tr {}
         [:td  [:input {:type "checkbox" :name (str "question-" (:id q))} ]]
         [:td  [:a {:href "#"} (:id q)]]
         [:td  (:title q)]
         [:td  (:question q)]
         [:td [:a {:href "#" :title "Find where else this question has been used"} "Usage"]]
         ])
      ]
     ]]
   ])

(defpage questions-page "/questions" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar/sidebar
     (sidebar/activate-item sidebar/*default-sidebar* :forms))
    (list-of-questions-and-answers)))
