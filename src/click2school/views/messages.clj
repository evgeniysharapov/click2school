(ns click2school.views.messages
  (:require [click2school.models.messages :as messages]
            [click2school.views.common :as common]
            [click2school.utils :as utils]
            [clojure.string :as s]
            [noir.session :as session]
            [noir.response :as resp])
  (:use noir.core))

(def default-button-list [{:url "/message/new" :text "New"}
                          {:url "/message/delete" :text "Delete"}])

(defn-  show-control-panel [button-list]
  [:form {:action "/message/new",
          :method "GET"}
   (for [button button-list]
     [:input {:type "submit" :class "btn" :value (:text button)}])])

(defn- message-inbox-message [msg]
  [:tr {:id (str "message-in-inbox-" (:id msg))}
     [:td {:colspan "1", :rowspan "1"}
      [:input {:type "checkbox", :name "emailsSelection", :value "all"}]]
     [:td {:colspan "1", :rowspan "1"} (:from msg)]
     [:td {:colspan "1", :rowspan "1"} (:sent msg)]
     [:td {:colspan "1", :rowspan "1"} (:subject msg)]
     [:td {:colspan "1", :rowspan "1"} (:text msg)]])

(defn show-messages-inbox [list-of-messages]
  [:div
   (show-control-panel default-button-list)
   [:table {:class "table table-striped"}
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
     ]]])

(defn show-message [msg]
  [:div {:class "hero-unit"}
   [:h1 (:title msg)]
   [:h3 "From: " (:from msg)]
   [:p (:text msg)]
   [:p
    [:button {:href (url-for messages) :class ["btn" "btn-primary"] } "Back"]]])

;(def message-inbox "/messages")

(defpage message-inbox "/messages" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
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
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    (show-messages-inbox (messages/fetch-messages-for (utils/me)))))

(defpage view-message [:get ["/message/:id" :id #"\d+"]] {id :id}
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar  (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox :active)
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
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    (show-message (messages/fetch (Integer/parseInt id)))))

(defn message-create-form []
  [:form {:enctype "application/x-www-form-urlencoded",
          :action (url-for message-create)
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
      [:input {:type "text", :class "input-xlarge focused", :id "message-subject", :name "subject", :value "Subject of the Message"}]]]
    [:div {:class "control-group"}
     [:label {:for "message-body", :class "control-label"} ""]
     [:div {:class "controls"}
      [:textarea { :class "input-xlarge focused" :rows "10" :id "message-body" :name "body"}]
      ]]
    [:div {:class "form-actions"}
     [:button {:type "submit", :class "btn btn-primary"} "Send"] "&nbsp;"
     [:button {:type "submit", :class "btn"} "Cancel"]]]
 
   [:script {:type "text/javascript"}
"    var autocomplete = $('#message-to').typeahead()
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
        });"]
   ])

(defpage message-new "/message/new" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar  (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox :active)
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
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    (message-create-form)))

(defpage message-create [:post "/message/create"] {:keys [to subject body]}
  (messages/create (str (:first-name (utils/me)) " "(:last-name (utils/me))) to subject body)
  (resp/redirect (url-for message-inbox))
  )


(defpage instant-messages "/instant" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
                    (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment :active)
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
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Instant Messages"]))

(defpage emails "/emails" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
                    (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment)
                    (common/sidebar-item (url-for emails) "Emails" :envelope :active)
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
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
   [:h1 "Emails"]))

(defpage phone-calls "/phonecalls" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
                    (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment)
                    (common/sidebar-item (url-for emails) "Emails" :envelope)
                    (common/sidebar-item (url-for phone-calls) "Phone Calls" :inbox :active)
                    (common/sidebar-section-header "Calendar")
                    (common/sidebar-item (url-for meetings) "Meetings" :user)
                    (common/sidebar-item (url-for papers-due) "Papers Due" :file)
                    (common/sidebar-section-header "Quizzes")
                    (common/sidebar-item (url-for quizz1) "Quizz 1" :star)
                    (common/sidebar-item (url-for quizz2) "Quizz 2" :empty)
                    (common/sidebar-item (url-for quizz3) "Quizz 3" :heart)
                    (common/sidebar-section-header "Administration")
                    (common/sidebar-item (url-for students) "Students" :folder-close)
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Phone Calls"]))

(defpage meetings "/meetings" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
                    (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment)
                    (common/sidebar-item (url-for emails) "Emails" :envelope)
                    (common/sidebar-item (url-for phone-calls) "Phone Calls" :inbox)
                    (common/sidebar-section-header "Calendar")
                    (common/sidebar-item (url-for meetings) "Meetings" :user :active)
                    (common/sidebar-item (url-for papers-due) "Papers Due" :file)
                    (common/sidebar-section-header "Quizzes")
                    (common/sidebar-item (url-for quizz1) "Quizz 1" :star)
                    (common/sidebar-item (url-for quizz2) "Quizz 2" :empty)
                    (common/sidebar-item (url-for quizz3) "Quizz 3" :heart)
                    (common/sidebar-section-header "Administration")
                    (common/sidebar-item (url-for students) "Students" :folder-close)
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Meetings"]))

(defpage papers-due "/papers" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
                    (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment)
                    (common/sidebar-item (url-for emails) "Emails" :envelope)
                    (common/sidebar-item (url-for phone-calls) "Phone Calls" :inbox)
                    (common/sidebar-section-header "Calendar")
                    (common/sidebar-item (url-for meetings) "Meetings" :user)
                    (common/sidebar-item (url-for papers-due) "Papers Due" :file :active)
                    (common/sidebar-section-header "Quizzes")
                    (common/sidebar-item (url-for quizz1) "Quizz 1" :star)
                    (common/sidebar-item (url-for quizz2) "Quizz 2" :empty)
                    (common/sidebar-item (url-for quizz3) "Quizz 3" :heart)
                    (common/sidebar-section-header "Administration")
                    (common/sidebar-item (url-for students) "Students" :folder-close)
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Papers Due"]))

(defpage quizzes "/quizzes" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
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
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Quizzes"]))

(defpage quizz1 "/quizz/1" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
                    (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment )
                    (common/sidebar-item (url-for emails) "Emails" :envelope)
                    (common/sidebar-item (url-for phone-calls) "Phone Calls" :inbox)
                    (common/sidebar-section-header "Calendar")
                    (common/sidebar-item (url-for meetings) "Meetings" :user)
                    (common/sidebar-item (url-for papers-due) "Papers Due" :file)
                    (common/sidebar-section-header "Quizzes")
                    (common/sidebar-item (url-for quizz1) "Quizz 1" :star :active)
                    (common/sidebar-item (url-for quizz2) "Quizz 2" :empty)
                    (common/sidebar-item (url-for quizz3) "Quizz 3" :heart)
                    (common/sidebar-section-header "Administration")
                    (common/sidebar-item (url-for students) "Students" :folder-close)
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Quizz 1"]))

(defpage quizz2 "/quizz/2" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
                    (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment)
                    (common/sidebar-item (url-for emails) "Emails" :envelope)
                    (common/sidebar-item (url-for phone-calls) "Phone Calls" :inbox)
                    (common/sidebar-section-header "Calendar")
                    (common/sidebar-item (url-for meetings) "Meetings" :user)
                    (common/sidebar-item (url-for papers-due) "Papers Due" :file)
                    (common/sidebar-section-header "Quizzes")
                    (common/sidebar-item (url-for quizz1) "Quizz 1" :star)
                    (common/sidebar-item (url-for quizz2) "Quizz 2" :empty :active)
                    (common/sidebar-item (url-for quizz3) "Quizz 3" :heart)
                    (common/sidebar-section-header "Administration")
                    (common/sidebar-item (url-for students) "Students" :folder-close)
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Quizz 2"]))

(defpage quizz3 "/quizz/3" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
                    (common/sidebar-item (url-for instant-messages) "Instant Messages" :comment)
                    (common/sidebar-item (url-for emails) "Emails" :envelope)
                    (common/sidebar-item (url-for phone-calls) "Phone Calls" :inbox)
                    (common/sidebar-section-header "Calendar")
                    (common/sidebar-item (url-for meetings) "Meetings" :user)
                    (common/sidebar-item (url-for papers-due) "Papers Due" :file)
                    (common/sidebar-section-header "Quizzes")
                    (common/sidebar-item (url-for quizz1) "Quizz 1" :star)
                    (common/sidebar-item (url-for quizz2) "Quizz 2" :empty)
                    (common/sidebar-item (url-for quizz3) "Quizz 3" :heart :active)
                    (common/sidebar-section-header "Administration")
                    (common/sidebar-item (url-for students) "Students" :folder-close)
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Quizz 3"]))

(defpage students "/students" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
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
                    (common/sidebar-item (url-for students) "Students" :folder-close :active)
                    (common/sidebar-item (url-for classes) "Classes" :leaf))
    [:h1 "Students"]))

(defpage classes "/classes" []
  (common/layout-with-navbar-and-sidebar
    (common/default-navbar (session/get :username ))
    (common/sidebar (common/sidebar-section-header "Communication")
                    (common/sidebar-item (url-for message-inbox) "Messages" :inbox)
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
                    (common/sidebar-item (url-for classes) "Classes" :leaf :active))
    [:h1 "Classes"]))
