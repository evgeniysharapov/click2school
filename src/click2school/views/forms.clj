(ns click2school.views.forms
  (:require (click2school.views [sidebar :as sidebar]
                                [common :as common])
            (click2school.models [question :as question]
                                 [answer_option :as answer]
                                 [form :as form]
                                 [form_question :as formq]
                                 [user :as user]
                                 [person :as person])
            [noir.session :as sess])
  (:use [noir.core :only (defpage defpartial)]
        [hiccup.core :only (escape-html)]))


(defpartial  list-of-forms []
  [:section#list-of-forms
   [:div.page-header
    [:h1 " Forms "
     [:small "sent, pending and templates"]]]

   [:h2 "Forms sent"]
   [:table.table.table-bordered.table-striped {:id "forms-list"}
    [:thead
     [:tr
      [:th {:width "10%"} "Form #"]
      [:th "Title"]
      [:th "Description"]
      [:th "Author"]]]
    [:tbody
     (for [ {:keys [id title description composer_user_id]} (form/find-records ["1=1"])]
          [:tr {:id (str "sent-forms-form-" id)}
           [:td id [:a {:href (str  "forms/" id)}]]
           [:td title]
           [:td description]
           [:td (person/fullname (user/find-person (user/get-record composer_user_id)))]])

     ]
    [:script "$('#forms-list tr').click(function () {
        location.href = $(this).find('td a').attr('href');
    });"]
    ]
   ])

;;; Renders form question
(defpartial render-form-question [{:keys [id title question qtype] :as q}]
  (let [question-id (str "question-" id)]
    [:div.row
     [:h3 title]
     [:p (escape-html question)]
     [:p
      (case qtype
        "CHOICE"  (for [ans (answer/find-records {:question_id id})]
                    (let [{:keys [id correct t_answer]} ans
                          answer-id (str "answer-" id)]
                      [:label.checkbox {:for answer-id}
                       [:input {:type "checkbox" :id answer-id :name answer-id}]
                       t_answer]))
        "BOOLEAN" [:input {:type "checkbox" }]
        "TEXT" [:textarea.input-xlarge {:name question-id}]
        "MULTIPLE" (for [ans (answer/find-records {:question_id id})]
                     (let [{:keys [id correct t_answer]} ans
                           answer-id (str "answer-" id)]
                       [:label.radio {:for answer-id}
                        [:input {:type "radio" :id answer-id :name question-id :value answer-id}]
                        t_answer]))
        )]]))

(defpartial render-form [{:keys [id title description composer_user_id]}]
  [:h2 title ]
  [:p description]
  ;;; for questions in the form
  (for [fq (formq/find-records {:form_id id})]
    (render-form-question (question/get-record (:question_id fq)))))

(defpage forms-route "/forms" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar/sidebar
     (sidebar/activate-item sidebar/*default-sidebar* :forms))
    (list-of-forms)))

(defpage forms-view [:get ["/forms/:id" :id #"\d+"]] {:keys [id]}
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar/sidebar
     (sidebar/activate-item sidebar/*default-sidebar* :forms))
    (render-form (form/get-record (Integer/parseInt id)))))

(defpage forms-create [:post "/forms/create"] {}
  (str "You tried to login as  with the password "))

(defpage forms-send [:post "/forms/:id/send" :id #"\d+"] {:keys [id]})
