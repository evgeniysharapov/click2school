(ns click2school.views.questions
  (:require (click2school.models [question :as question]
                                 [answer_option :as answer])
            [noir.response :as resp]
            [noir.request :as req])
  (:use [noir.core :only (defpage defpartial url-for)]
        [hiccup.page-helpers :only [javascript-tag]]
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
       (merge  {:id ctrl-id :type type-name :name name :style "width: 400px"}
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
  [name label & val-label-map]
  (on-form-control "checkbox" name label))

(defpartial radio
  [name label]
  (on-form-control "radio" name label))

(defpartial radio-group
  [name & val-label-map]
  (for [[val label] (apply hash-map val-label-map)]
    (on-form-control "radio" name label val)))

;;; "Produces a group of radio buttons in one line."
(defpartial radio-group-inline
  [name & val-label-map]
  [:div.control-group
   [:div.controls
    (for [[val label] (apply hash-map val-label-map)]
      (let [ctrl-id (gensym name)]
        [:label.radio.inline {:for ctrl-id} label
         [:input {:id ctrl-id :type "radio" :name name :value val}]]))
    ]])

(defpartial question-answer-option
  [name type]
  (let [ctrl-id (gensym name)
        ctrl-id-correct (str ctrl-id "-correct")]
    [:div.control-group.answer-option
     [:div.controls
      [:div.input-append
       [:input {:id ctrl-id :type "text" :name name :placeholder "Type an answer"}]
       [:label {:id (str ctrl-id-correct "-label")  :for ctrl-id-correct :class (str  type " inline")}  "Correct"
        [:input {:id ctrl-id-correct :type type :name "correct" :value name} ]]
       [:a.btn.remove-q [:i {:class "icon-minus-sign"}] "Remove" ]
       ]]]))


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
     [:dd "This questions are akin to " [:i "Choice"] " questions, but they let respondent to choose several answers."]]]

   [:form {:method "POST" :action "/questions/save"}
    (text "title" "Question Title")
    (text-area "question" "Question")
    (radio-group-inline "type" "CHOICE" "Choice" "BOOLEAN" "Checkbox" "TEXT" "Free form typing" "MULTIPLE" "Multiple choice")
    (javascript-tag
     "
     function showCheckedQTypePanel () {
        $('input[name=type]:not(:checked)').each(function(){$('#'+$(this).val()).hide();});
        $('#'+$('input[name=type]:checked').val()).show();
     }
     $('input[name=type]').click(showCheckedQTypePanel);
     $(document).ready(function(){$('.q-answers').each(function(){$(this).hide();});$('input[name=type]:first').click();});"
     )

    ;; add elements that are going to be varying on the form.

    [:div#CHOICE {:class "q-answers"}
     (question-answer-option "answer-1", "radio")
     [:div.controls
      [:div.control-group
       [:a.add-answer.btn  [:i {:class "icon-plus-sign"}] "Add"]]

      ]]
    [:div#BOOLEAN  {:class "q-answers"}
     [:p "Respondent would be presented with a check box when he or she will be answering this question."]
     ]
    [:div#TEXT  {:class "q-answers"}
     [:p "Respondent would be presented with a text typing area when he or she will be answering this question."]
     ]
    [:div#MULTIPLE  {:class "q-answers"}
     (question-answer-option "answer-1" "checkbox")
     [:div.controls
      [:div.control-group
       [:a.add-answer.btn  [:i {:class "icon-plus-sign"}] "Add"]]
      ]]
      (javascript-tag
       "$('a.add-answer').click(function(){
          var e =  $('div.q-answers:visible div.answer-option:last').clone(true);
          /*change input field name*/
          var inp = e.find('input[name!=correct]');
          var ix = inp.attr('name').match(/.+-(\\d+)/);
          var newName = 'answer-'+(parseInt(ix[1])+1);
          inp.attr('name', newName);
          inp.attr('id', 'answer-'+(parseInt(inp.attr('id').match(/[^\\d]+(\\d+)/)[1])+1));
          /* change names of the correct box and label's for attr */
          var corr = e.find('input[name=correct]');
          var m = corr.attr('id').match(/([^\\d]+)(\\d+)([^\\d]+)/);
          var newId = m[1]+(parseInt(m[2])+1)+m[3];
          corr.attr('id',newId);
          corr.attr('value',newName);
          corr.parent('label').attr('for',newId);
          /* add new control to the end */
          $('div.q-answers:visible div.answer-option:last').after(e);
})")
    (javascript-tag
     "$('a.remove-q').each(function(){$(this).live('click', function(){
         if($('div.q-answers:visible div.answer-option').size() > 1 ) {
             $(this).parents('div.answer-option').remove();
         } 
});});")
    [:div.form-actions
     [:button.btn.btn-primary {:id "submit_question"  :type "submit"} "Save"]
     [:button.btn {:type "submit" :name "cancel"} "Cancel"]
     (javascript-tag
      "$('#submit_question').click(function(){
          $('form  input:hidden').attr('disabled',true);
          $('form').submit();
});")
     ]
    ]])

(defview questions-create [:get  "/questions/create"] []
  :questions
  (question-edit-form "Create New Question"))

;;; TODO: add bunch of validations
(defpage questions-save [:post "/questions/save"] { :keys [title type question cancel] :as q}
  (if (nil? cancel)
    (let [ring-req (req/ring-request)
          correct  (case type
                     "MULTIPLE" (set (:correct (:form-params ring-req)))
                     "CHOICE"  #{(keyword  (:correct q))})]
      ;; first we are creating or updating question
      (let [id  (:id  (question/create {:title title :qtype type :question question}))]
        (when (not-empty correct)
          ;; now we save answers for this question
          (doseq [ans (filter #(re-matches #"answer-\d+" (name  %)) (keys q))]
            (answer/create {:question_id id :correct (contains? correct ans) :t_answer (ans q)}))
          ))
      (resp/redirect (url-for questions-page)))))
