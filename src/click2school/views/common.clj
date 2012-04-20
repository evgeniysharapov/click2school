(ns click2school.views.common
  (:require [click2school.utils :as utils]
            [click2school.views.sidebar :as sidebar]
            [click2school.models.user :as u]
            [click2school.models.message :as m]
            [noir.session :as sess]
            [clojure.string :as s])
  (:use [noir.core :only [defpartial defpage]]
        [hiccup.page-helpers]
        [click2school.views.icon]))

(defn- default-layout-header []
  [:head {}
     [:meta {:charset "utf-8"}] [:title {} "Click 2 Interact"]
     [:meta {:name "viewport", :content "width=device-width, initial-scale=1.0"}]
     [:meta {:name "description", :content ""}]
     [:meta {:name "author", :content "Evgeniy N. Sharapov"}]
     [:link {:href "/bootstrap/css/bootstrap.css", :rel "stylesheet"}]
     [:style {} "body { padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */}"]
     [:link {:href "/bootstrap/css/bootstrap-responsive.css", :rel "stylesheet"}]
   [:link {:href "/css/site.css", :rel "stylesheet"}]
   (comment
     [:link {:rel "shortcut icon", :href "images/favicon.ico"}]
     [:link {:rel "apple-touch-icon", :href "images/apple-touch-icon.png"}]
     [:link {:rel "apple-touch-icon", :sizes "72x72", :href "images/apple-touch-icon-72x72.png"}]
     [:link {:rel "apple-touch-icon", :sizes "114x114", :href "images/apple-touch-icon-114x114.png"}])
   (include-js "/bootstrap/js/jquery.js")])

(defn- default-layout-footer []
  [:footer {}
   [:p {} "© Click2Interact Company 2012"]])

(defn- bootstrap-javascript []
  (let [scripts (map #(str "/bootstrap/js/bootstrap-" %) ["transition.js" "alert.js" "modal.js" "dropdown.js" "scrollspy.js" "tab.js" "tooltip.js" "popover.js" "button.js" "collapse.js" "carousel.js" "typeahead.js"])]
    (for [js scripts]
      (include-js js))))

(defn me []
  "Returns user from the session"
  (u/get-record
   (sess/get :user-id)))

(defpartial navbar-item [url title & rest ]
  (let [params (filter #(keyword?  %) rest)
        params1 (map #(name %) params)
        classes (s/join " " params1)]
    [:li {:class classes}
     [:a {:shape "rect", :href url} title ]]))

(defpartial navbar-login-item [url]
  [:ul.nav.pull-right
   [:li
    [:a {:shape "rect", :href url} "Login"]]])

(defpartial navbar-logout-item [url username]
  [:ul.nav.pull-right
   [:li
    [:a {:shape "rect", :href url} (str "Logged in as " username ". ") "Logout?"]]])

(defn navbar [log-in-out  & contents]
  [:div.navbar.navbar-fixed-top
      [:div.navbar-inner
       [:div.container
        [:a.brand {:shape "rect", :href "/"} "Click2Interact"]
        [:div.nav-collapse
         [:ul.nav
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
   (default-layout-header)
    [:body {}

     navbar

     [:div {:class "container"}

      content

      [:hr {}]
      (default-layout-footer)]
     (bootstrap-javascript)
     ]
   ))

(defn default-layout  [& content]
  (layout-with-navbar
    (default-navbar)
    content))

(defn layout-with-navbar-and-sidebar [navbar sidebar & content]
  (layout-with-navbar
    navbar
    (sidebar-content sidebar content)))

(defmacro defview
  "Helper macro that calls a defpage macro with some predefined stuff. Last two arguments are key of the active sidebar and a main area content.
This is a macro you want to use to define pages."
  [& args ]
  (let [[main-content k-sb] (take 2 (reverse args))]
    `(defpage ~@(drop-last 2 args)
       (layout-with-navbar-and-sidebar
         (default-navbar (sess/get :username ))
         (sidebar/sidebar
          (sidebar/activate-item sidebar/*default-sidebar* ~k-sb))
         ~main-content))))

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
