(ns click2school.views.questions
  (:require (click2school.models [question :as question]))
  (:use [noir.core :only (defpage defpartial)]
        [click2school.views.common :only [defview]]))


(defpartial list-of-questions-and-answers []
  [:section#list-of-questions-and-answers
   [:div.page-header
    [:h1 " Q & A "
     [:small "questions and answers that could be reused in forms and quizzes"]]]
   [:h2 "Assortment of Questions"]
   [:p "You can check the questions you like and then press the button to create a quizz or form out of them. Do not worry about the order you can arrange question later."]
   [:form.form-horizontal {:action "/form/create" :method "POST"}
    [:label [:h4 "Create from selection"]]
    [:button.btn {:type "submit"} "Form" ]
    [:button.btn {:type "submit"} "Quiz" ]
    [:input.pull-right {:type "text" :placeholder "Search questions"}]
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
     ]
    [:button.btn.btn-success "Add More"]]
   ])


(defview questions-page "/questions" []
  :questions
  (list-of-questions-and-answers))

