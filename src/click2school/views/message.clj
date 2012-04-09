(ns click2school.views.message
  (:require (click2school.models [message :as msg]
                                 [user :as usr]
                                 [person :as prsn])
            [click2school.utils :as utils]
            [click2school.views.common :as common]
            [clojure.string :as s]
            [noir.session :as sess]
            [noir.response :as resp])
  (:use noir.core
        (clj-time [format :only (unparse formatters)]
                  [coerce :only (from-long)])))

(def default-button-list [{:url "/message/new" :text "New"}
                          {:url "/message/delete" :text "Delete"}])

(defn-  show-control-panel [button-list]
  [:form {:action "/message/new",
          :method "GET"}
   (for [button button-list]
     [:input {:type "submit" :class "btn" :value (:text button)}])])

(defn- add-from
  "This adds from person to the message"
  [msg]
  (assoc msg :from
    (-> msg  :from_user_id
        usr/get-record usr/find-person)))

(defn- add-sent
  "This adds better looking sent date"
  [msg]
  (assoc msg :sent
         (->>
          (-> msg :created_on .getTime from-long)
          (unparse (formatters :rfc822)))))

(add-sent (msg/get-record 1))

(defn- message-inbox-message [msg]
  [:tr {:id (str "message-in-inbox-" (:id msg)) }
   [:td {:colspan "1", :rowspan "1"}
    [:a {:href (str  "message/" (:id msg))}]
    [:input {:type "checkbox", :name "emailsSelection", :value "all"}]]
   [:td {:colspan "1", :rowspan "1"} (prsn/fullname (:from  (add-from msg)))]
   [:td {:colspan "1", :rowspan "1"} (utils/human-date (:created_on  msg))]
   [:td {:colspan "1", :rowspan "1"} (:subject msg)]
   [:td {:colspan "1", :rowspan "1"} (:content msg)]])

(defn show-messages-inbox [list-of-messages]
  [:div
   (show-control-panel default-button-list)
   [:table {:class "table table-striped" :id "message-inbox-table"}
    [:thead {}
     [:tr {}
      [:th {:colspan "1", :rowspan "1"}
       [:input {:type "checkbox", :name "emailsSelection", :value "all"}]]
      [:th {:colspan "1", :rowspan "1"} "From"]
      [:th {:colspan "1", :rowspan "1"} "When"]
      [:th {:colspan "1", :rowspan "1"} "Subject"]
      [:th {:colspan "1", :rowspan "1"} "Content"]]]
    [:tbody {}
     (for [msg list-of-messages]
       (message-inbox-message msg))
     ]]
   [:script "$('#message-inbox-table tr').click(function () {
        location.href = $(this).find('td a').attr('href');
    });"]])

(defn show-message [msg]
  [:div {:class "hero-unit"}
   [:h2 (:subject msg)]
   [:h3 "From: " (prsn/fullname (:from  (add-from msg)))]
   [:h3 "Sent: " (:sent (add-sent msg))]
   [:p (:content msg)]
   [:p
    [:a {:href "/messages" :class "btn" } "Back"]]])

(defmacro sidebar-for-page []
  `(common/sidebar (common/sidebar-section-header "Communication")
                  (common/sidebar-item "#" ;(url-for message-inbox)
                                       "Messages" :inbox :active
                                       )
                  (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment)
                  (common/sidebar-item (url-for emails) "Emails" :envelope)
                  (common/sidebar-item (url-for phone-calls) "Phone Calls" :inbox)
                  (common/sidebar-section-header "Calendar")
                  (common/sidebar-item (url-for meetings) "Meetings" :user)
                  (common/sidebar-item (url-for papers-due) "Papers Due" :file)
                  (common/sidebar-section-header "Quizzes")
                  (common/sidebar-item (url-for quizz1) "Quizz 1" :star)
                  (common/sidebar-item (url-for quizz2) "Quizz 2" :empty)
                  (common/sidebar-item (url-for quizz3) "Quizz 3" :heart)
                  (common/sidebar-section-header "Administration")
                  (common/sidebar-item (url-for students) "Students" :folder-close)
                  (common/sidebar-item (url-for classes) "Classes" :leaf)))

(defpage message-inbx "/messages" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page )
    (show-messages-inbox (usr/find-messages-to (common/me)))))

(defpage view-message [:get ["/message/:id" :id #"\d+"]] {id :id}
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar  (sess/get :username ))
    (sidebar-for-page)
    (show-message (msg/get-record (Integer/parseInt id)))))

(defn message-create-form []
  [:form {:enctype "application/x-www-form-urlencoded",
          :action "/message/create"
          :method "POST",
          :class "form-horizontal"}
   [:fieldset {}
    [:legend {} "Create New Message"]
    [:div {:class "control-group"}
     [:label {:for "message-to", :class "control-label"} "To"]
     [:div {:class "controls"}
      [:input {:type "text", :class "input-xlarge focused", :data-provide "typeahead", :id "message-to", :name "to"}]]]
    [:div {:class "control-group"}
     [:label {:for "message-subject", :class "control-label"} "Subject"]
     [:div {:class "controls"}
      [:input {:type "text", :class "input-xlarge focused", :id "message-subject", :name "subject", :placeholder "Subject of the Message"}]]]
    [:div {:class "control-group"}
     [:label {:for "message-body", :class "control-label"} ""]
     [:div {:class "controls"}
      [:textarea { :class "input-xlarge focused" :rows "10" :id "message-body" :name "body"}]
      ]]
    [:div {:class "form-actions"}
     [:button {:type "submit", :class "btn btn-primary" :name "send"} "Send"] "&nbsp;"
     [:button {:type "submit", :class "btn", :name "cancel"} "Cancel"]]]
   [:script {:type "text/javascript"}
    "
$(document).ready(function(){
var autocomplete = $('#message-to').typeahead()
        .on('keyup', function(ev){

            ev.stopPropagation();
            ev.preventDefault();

            //filter out up/down, tab, enter, and escape keys
            if( $.inArray(ev.keyCode,[40,38,9,13,27]) === -1 ){

                var self = $(this);

                //set typeahead source to empty
                self.data('typeahead').source = [];

                //active used so we aren't triggering duplicate keyup events
                if( !self.data('active') && self.val().length > 0){

                    self.data('active', true);

                    //Do data request. Insert your own API logic here.
                    $.getJSON(\"/finduser\",{q: $(this).val()}, function(data) {

                        //set this to true when your callback executes
                        self.data('active',true);
                        console.log(data);
                        //Filter out your own parameters. Populate them into an array, since this is what typeahead's source requires
                        var arr = [], i=data.length;
                        while(i--){
                            arr[i] = data[i]
                        }
                        //set your results into the typehead's source
                        self.data('typeahead').source = arr;
                        console.log(self.data('typeahead'));

                        //trigger keyup on the typeahead to make it search
                        self.trigger('keyup');

                        //All done, set to false to prepare for the next remote query.
                        self.data('active', false);

                    });

                }
            }
        });});"]
   ])

(defpage message-new "/message/new" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar  (sess/get :username))
    (sidebar-for-page)
    (message-create-form)))

(defpage [:post "/message/create"] {:keys [to subject body cancel]}
  (if cancel
    (resp/redirect "/messages")
    (let [from-user-id (:id (common/me))
          to-user-id (-> to prsn/find-person-by-fullname prsn/find-user first :id)]
      (msg/create {:from_user_id from-user-id
                   :to_user_id to-user-id
                   :subject subject,
                   :content body})
      (resp/redirect "/messages"))))

(defpage instant-messages "/instant" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Instant Messages"]))

(defpage emails "/emails" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
   [:h1 "Emails"]))

(defpage phone-calls "/phonecalls" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Phone Calls"]))

(defpage meetings "/meetings" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Meetings"]))

(defpage papers-due "/papers" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Papers Due"]))

(defpage quizzes "/quizzes" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Quizzes"]))

(defpage quizz1 "/quizz/1" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Quizz 1"]))

(defpage quizz2 "/quizz/2" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Quizz 2"]))

(defpage quizz3 "/quizz/3" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Quizz 3"]))

(defpage students "/students" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Students"]))

(defpage classes "/classes" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (sess/get :username ))
    (sidebar-for-page)
    [:h1 "Classes"]))
