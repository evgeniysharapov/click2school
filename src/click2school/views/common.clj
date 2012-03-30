(ns click2school.views.common
  (:require [click2school.utils :as utils]
            [noir.session :as session])
  (:use [noir.core :only [defpartial]]
        [click2school.models.user :as user]
        [hiccup.page-helpers]
        [clojure.string :only (join)]))

(defn- default-layout-header []
  [:head {}
     [:meta {:charset "utf-8"}] [:title {} "Click 2 Interact"]
     [:meta {:name "viewport", :content "width=device-width, initial-scale=1.0"}]
     [:meta {:name "description", :content ""}]
     [:meta {:name "author", :content "Evgeniy N. Sharapov"}]
     [:link {:href "/css/reset.css", :rel "stylesheet"}]
     [:link {:href "/bootstrap/css/bootstrap.css", :rel "stylesheet"}]
     [:style {} "body { padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */}"]
     [:link {:href "/bootstrap/css/bootstrap-responsive.css", :rel "stylesheet"}]
     [:link {:href "/css/site.css", :rel "stylesheet"}]
     [:link {:rel "shortcut icon", :href "images/favicon.ico"}]
     [:link {:rel "apple-touch-icon", :href "images/apple-touch-icon.png"}]
     [:link {:rel "apple-touch-icon", :sizes "72x72", :href "images/apple-touch-icon-72x72.png"}]
   [:link {:rel "apple-touch-icon", :sizes "114x114", :href "images/apple-touch-icon-114x114.png"}]
   (include-js "/bootstrap/js/jquery.js")])

(defn- default-layout-footer []
  [:footer {}
   [:p {} "© Click2Interact Company 2012"]])

(defn- bootstrap-javascript []
  (let [scripts (conj (map #(str "/bootstrap/js/bootstrap-" %) ["transition.js" "alert.js" "modal.js" "dropdown.js" "scrollspy.js" "tab.js" "tooltip.js" "popover.js" "button.js" "collapse.js" "carousel.js" "typeahead.js"]) "/bootstrap/js/jquery.js")]
    (for [js scripts]
      (include-js js))))

(def *bootstrap-icons*
  {:adjust "icon-adjust",
   :align-center "icon-align-center",
   :align-justify "icon-align-justify",
   :align-left "icon-align-left",
   :align-right "icon-align-right",
   :arrow-down "icon-arrow-down",
   :arrow-left "icon-arrow-left",
   :arrow-right "icon-arrow-right",
   :arrow-up "icon-arrow-up",
   :asterisk "icon-asterisk",
   :backward "icon-backward",
   :ban-circle "icon-ban-circle",
   :barcode "icon-barcode",
   :bold "icon-bold",
   :book "icon-book",
   :bookmark "icon-bookmark",
   :calendar "icon-calendar",
   :camera "icon-camera",
   :check "icon-check",
   :chevron-down "icon-chevron-down",
   :chevron-left "icon-chevron-left",
   :chevron-right "icon-chevron-right",
   :chevron-up "icon-chevron-up",
   :cog "icon-cog",
   :comment "icon-comment",
   :download "icon-download",
   :download-alt "icon-download-alt",
   :edit "icon-edit",
   :eject "icon-eject",
   :envelope "icon-envelope",
   :exclamation-sign "icon-exclamation-sign",
   :eye-close "icon-eye-close",
   :eye-open "icon-eye-open",
   :facetime-video "icon-facetime-video",
   :fast-backward "icon-fast-backward",
   :fast-forward "icon-fast-forward",
   :file "icon-file",
   :film "icon-film",
   :fire "icon-fire",
   :flag "icon-flag",
   :folder-close "icon-folder-close",
   :folder-open "icon-folder-open",
   :font "icon-font",
   :forward "icon-forward",
   :gift "icon-gift",
   :glass "icon-glass",
   :headphones "icon-headphones",
   :heart "icon-heart",
   :home "icon-home",
   :inbox "icon-inbox",
   :indent-left "icon-indent-left",
   :indent-right "icon-indent-right",
   :info-sign "icon-info-sign",
   :italic "icon-italic",
   :leaf "icon-leaf",
   :list "icon-list",
   :list-alt "icon-list-alt",
   :lock "icon-lock",
   :magnet "icon-magnet",
   :map-marker "icon-map-marker",
   :minus "icon-minus",
   :minus-sign "icon-minus-sign",
   :move "icon-move",
   :music "icon-music",
   :off "icon-off",
   :ok "icon-ok",
   :ok-circle "icon-ok-circle",
   :ok-sign "icon-ok-sign",
   :pause "icon-pause",
   :pencil "icon-pencil",
   :picture "icon-picture",
   :plane "icon-plane",
   :play "icon-play",
   :play-circle "icon-play-circle",
   :plus "icon-plus",
   :plus-sign "icon-plus-sign",
   :print "icon-print",
   :qrcode "icon-qrcode",
   :question-sign "icon-question-sign",
   :random "icon-random",
   :refresh "icon-refresh",
   :remove "icon-remove",
   :remove-circle "icon-remove-circle",
   :remove-sign "icon-remove-sign",
   :repeat "icon-repeat",
   :resize-full "icon-resize-full",
   :resize-horizontal "icon-resize-horizontal",
   :resize-small "icon-resize-small",
   :resize-vertical "icon-resize-vertical",
   :retweet "icon-retweet",
   :road "icon-road",
   :screenshot "icon-screenshot",
   :search "icon-search",
   :share "icon-share",
   :share-alt "icon-share-alt",
   :shopping-cart "icon-shopping-cart",
   :signal "icon-signal",
   :star "icon-star",
   :star-empty "icon-star-empty",
   :step-backward "icon-step-backward",
   :step-forward "icon-step-forward",
   :stop "icon-stop",
   :tag "icon-tag",
   :tags "icon-tags",
   :text-height "icon-text-height",
   :text-width "icon-text-width",
   :th "icon-th",
   :th-large "icon-th-large",
   :th-list "icon-th-list",
   :time "icon-time",
   :tint "icon-tint",
   :trash "icon-trash",
   :upload "icon-upload",
   :user "icon-user",
   :volume-down "icon-volume-down",
   :volume-off "icon-volume-off",
   :volume-up "icon-volume-up",
   :warning-sign "icon-warning-sign",
   :zoom-in "icon-zoom-in",
   :zoom-out "icon-zoom-out"})

(defn me []
"Returns user from the session"
  (user/find-by-id
   (session/get :user-id)))

(defn navbar-item [url title & rest ]
  (let [params (filter #(keyword?  %) rest)
        params1 (map #(name %) params)
        classes (join " " params1)
        ]
    [:li {:class classes}
     [:a {:shape "rect", :href url} title ]]))

(defn navbar-login-item [url]
  [:ul {:class "nav pull-right"}
   [:li {}
    [:a {:shape "rect", :href url} "Login"]]])

(defn navbar-logout-item [url username]
  [:ul {:class "nav pull-right"}
   [:li {}
    [:a {:shape "rect", :href url} (str "Logged in as " username ". ") "Logout?"]]])

(navbar-item "about" "About" :active)

(defn navbar [log-in-out  & contents]
  [:div {:class "navbar navbar-fixed-top"}
      [:div {:class "navbar-inner"}
       [:div {:class "container"}
        [:a {:shape "rect", :class "brand", :href "/"} "Click2Interact"]
        [:div {:class "nav-collapse"}
         [:ul {:class "nav"}
          contents
          ]
         log-in-out
         ]]]])

(defn default-navbar
  ([]
     (navbar
      (navbar-login-item "/login")
      (navbar-item "#home" "Home" :active)
      (navbar-item "#about" "About")
      (navbar-item "#contact" "Contact")))
  ([username]
     (navbar
      (navbar-logout-item "/logout" username)
      (navbar-item "#home" "Home" :active)
      (navbar-item "#about" "About")
      (navbar-item "#contact" "Contact"))))

(defn sidebar-item
  ([url title icon]
     [:li {}
       [:a {:shape "rect", :href url}
        [:i {:class (icon *bootstrap-icons*)}] title]])
  ([url title icon active]
      [:li {:class "active"}
       [:a {:shape "rect", :href url}
        [:i {:class (icon *bootstrap-icons*)}] title]]))

(defn sidebar-section-header [title]
  [:li {:class "nav-header"} title])

(defn sidebar [ & items ]
  [:ul {:class "nav nav-list"}
   items
   ])

(defn default-sidebar []
  (sidebar (sidebar-section-header "Communication")
           (sidebar-item "#" "Messages" :inbox :active)
           (sidebar-item "#" "Instant Messages" :comment)
           (sidebar-item "#" "Emails" :envelope)
           (sidebar-item "#" "Phone Calls" :inbox)
           (sidebar-section-header "Calendar")
           (sidebar-item "#" "Meetings" :user)
           (sidebar-item "#" "Papers Due" :file)
           (sidebar-section-header "Quizzes")
           (sidebar-item "#" "Quizz 1" :star)
           (sidebar-item "#" "Quizz 2" :empty)
           (sidebar-item "#" "Quizz 3" :heart)
           (sidebar-section-header "Administration")
           (sidebar-item "#" "Students" :folder-close)
           (sidebar-item "#" "Classes" :leaf)))

(defn alert [type & content]
  [:div {:class (str "alert " type " fade in")}
   [:a {:shape "rect", :class "close", :data-dismiss "alert"} "×"]
   content])

(defn alert-info [& content]
  (alert "alert-info" content))

(defn alert-error [& content]
  (alert "alert-error" content))

(comment
  (show-alert-info [:b {} "Today: "] " Call " [:u {} "407-278-4563"] " David Ashborn about Alicia's grades.")
  (show-alert-error "Papers are due "  [:b {} "Tomorrow"] ))

(defn sidebar-content-with-alerts [sidebar alerts & content]
  (let [user (me)
        user-title (if (= "Male" (:gender user)) "Mr." "Mrs.")
        user-fullname (str (:first-name user) " " (:last-name user))]
    [:div {:class "row-fluid"}
     [:div {:class "span12"}
      [:h1 {:id "user_name"} (str user-title " " user-fullname)]
      [:p {}]
      (for [alert alerts]
        alert)
      [:div {:class "row-fluid"}
       [:div {:class "span3"}
        [:div {:class "well sidebar-nav"}
         sidebar]]
       [:div {:class "span9"}
        content]]]]))

(defn sidebar-content [sidebar & content]
  (sidebar-content-with-alerts sidebar () content))

(defpartial layout-with-navbar [navbar & content]
  (html5 
   ;   [:html {:lang "en"}
   (bootstrap-javascript)
    [:body {}

     navbar

     [:div {:class "container"}

      content

      [:hr {}]
      (default-layout-footer)]
     (default-layout-header)
     ]
    ;    ]
   ))
;(layout-with-navbar "test" "test")

(defn default-layout  [& content]
  (layout-with-navbar
    (default-navbar)
    content))

(defn layout-with-navbar-and-sidebar [navbar sidebar & content]
  (layout-with-navbar
    navbar
    (sidebar-content sidebar content)))
