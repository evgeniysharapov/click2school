(ns click2school.views.questions
  (:require (click2school.models [question :as question]))
  (:use [noir.core :only (defpage defpartial)]
        [click2school.views.common :only [defview]]))


(defn q-id->html
  "Turns DB id of the question into its HTML counterpart and a form input name. Returns string."
  [a]
  (str "q-" a))

(defn q->html
  "Converts question into HTML representation"
  [q]
  [:tr   [:td  [:input {:type "checkbox" :name (q-id->html (:id q))} ]]
   [:td  [:a {:href "#"} (:id q)]]
   [:td  (:title q)]
   [:td  (:question q)]
   [:td [:a {:href "#" :title "Find where else this question has been used"} "Usage"]]
   ])

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
        (q->html q))
      ]
     ]]
   [:button.btn.btn-success {:onclick "location.href='/questions/create'"} "Add More"]
   ])


(defview questions-page "/questions" []
  :questions
  (list-of-questions-and-answers))

(defpartial on-form-control
  [type name label & [value placeholder]]
  (let [ctrl-id (gensym name)
        type-name (clojure.core/name type)]
    [:div.control-group
     [:label.control-label {:for ctrl-id} label]
     [:div.controls
      [(keyword (str (if (= type-name "textarea") type-name "input") ".input-xlarge"))
       (merge  {:id ctrl-id :type type-name :name name}
               (when value {:value value})
               (when placeholder {:placeholder placeholder}))]
      ]]))

(defpartial text
  [name label]
  (on-form-control "text" name label))

(defpartial text-area
  [name label]
  (on-form-control "textarea" name label))

(defpartial checkbox
  [name label]
  (on-form-control "checkbox" name label))

(defpartial checkbox-group
  [name label]
  (on-form-control "checkbox" name label))

(defpartial radio
  [name label]
  (on-form-control "radio" name label))

(defpartial radio-group
  [name & val-label-map]
  (for [[val lable] (apply hash-map val-label-map)]
    (on-form-control "radio" name label val)))

(defpartial question-edit-form [new-or-edit-title]
  [:section#list-of-questions-and-answers
   [:div.page-header
    [:h1 new-or-edit-title]]
   [:p "Type in the question, choose it's type and depending on its type you can choose answers. There are different types of questions. Depending on its type there are different options for adding answers."
    [:dl.dl-horizontal
     [:dt " Choice"]
     [:dd "This is a quesion with mupltiple answer options. Only one option is correct. Good fit for quizzes. Also good for use in forms and surveys. Example, \" how old are you ?\" with options for different answers, only one of which would be correct."]
     [:dt " Text"]
     [:dd "This question assumes a freeform typing in response. Typical question could be \"How did you spend your summer\"."]
     [:dt " Checkbox"]
     [:dd "This question makes people to check the box in response. Example, \"I agree with terms and conditions\"."]
     [:dt "Multiple Choice"]
     [:dd "This questions are akin to " [:i "Choice"] " questions, but they let respondent to choose several answers."]]
    [:form
     [:fieldset
      ]]
    ]])

(defview questions-create "/questions/create" []
  :questions
  (question-edit-form "Create New Question"))
