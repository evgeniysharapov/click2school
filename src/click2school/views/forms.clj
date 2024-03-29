(ns click2school.views.forms
  (:require (click2school.views [sidebar :as sidebar]
                                [common :as common])
            (click2school.models [question :as question]
                                 [answer_option :as answer]
                                 [form :as form]
                                 [form_question :as formq]
                                 [user :as user]
                                 [person :as person])
            [noir.session :as sess]
            [noir.response :as resp])
  (:use [noir.core :only (defpage defpartial url-for)]
        [noir.request :only (ring-request)] 
        [hiccup.core :only (escape-html)]
        [click2school.views.common :only (defview text text-area hidden form-save-cancel)]
        [click2school.views.questions :only (render)]))


(defpartial  list-of-forms []
  [:section#list-of-forms
   [:div.page-header
    [:h1 " Forms "
     [:small "sent, pending and templates"]]]

   [:h2 "All Forms"]
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


(defpartial render-form [{:keys [id title description composer_user_id]}]
  [:h2 title ]
  [:p description]
  ;;; for questions in the form
  (for [fq (formq/find-records {:form_id id})]
    (render (question/get-record (:question_id fq)))))

(defview forms-route "/forms" []
  :forms
  (list-of-forms))

(defview forms-view [:get ["/forms/:id" :id #"\d+"]] {:keys [id]}
  :forms
  (render-form (form/get-record (Integer/parseInt id))))


(defpartial render-form-edit [{:keys [id title description composer_user_id]}]
  [:h2 "Edit Form" ]
  [:p "Enter description and title of the form. Add more question using plus button."]
  (form-save-cancel "/forms/update"
                    (hidden "id" id)
                    (hidden "composer_user_id" composer_user_id)
                    (text "title" "Form Title" title)
                    (text-area "description" "Form Description" description)
                    ;;; for questions in the form
                    (for [fq (formq/find-records {:form_id id})]
                      (render (question/get-record (:question_id fq))))))

(defview forms-edit [:get ["/forms/:id/edit" :id #"\d+"]] {:keys [id]}
  :forms
  (render-form-edit (form/get-record (Integer/parseInt id))))

(defpage forms-create [:post "/form/create"] {:as questions}
  (let [f (form/create {:title "" :description "" :composer_user_id (:id  (common/me))})
        question-ids (map #(Integer/parseInt %) (flatten (map #(re-seq #"\d+" %) (map name (keys questions)))))]
    ;; add questions to the form question
    (doseq [i question-ids]
      (formq/create {:form_id (:id f) :question_id i}))
    ;; post a form in a base
    (resp/redirect (url-for forms-edit {:id (:id f)}))
    ))

(defpage forms-update [:post "/forms/update"] {:keys [id title description composer_user_id cancel] :as frm}
  (when (nil? cancel)
    (form/update {:id (Integer/parseInt id)
                  :title title
                  :description description
                  :composer_user_id (Integer/parseInt composer_user_id)}))
  (if-not (nil? cancel)
    (resp/redirect (url-for forms-route))
    (resp/redirect (url-for forms-view {:id id}))))

(defpage forms-send [:post "/forms/:id/send" :id #"\d+"] {:keys [id]})

