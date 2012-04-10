(ns click2school.views.sidebar
  (:use
   [click2school.views.icon]
   [click2school.utils :only (kw->str)]
   [noir.core :only [defpartial]]
   [hiccup.page-helpers]
   ))
;;;
;;; This is an example of our sidebar structure
;;;

;; (def sb
;;  `("Communication"
;;    (:messages "/messages" "Messages" :inbox)
;;    (:emails "/emails" "Emails" :email)
;;    "Calendar"
;;    "Administration"
;;    (:classes "/classes"  "Classes" :folder-close)
;;    (:students "/students" "Students" :leaf)))

(def *default-sidebar*
  '("Communication"
    (:message "/messages" "Messages" :inbox :active)
    (:instants "/instants" "Instant Messages" :comment)
    "Calendar"
    (:meetings "/meetings" "Meetings" :user)
    (:papers "/papers" "Papers Due" :file)
    "Quizzes"
    (:quizz1 "/quizzes/1" "Quizz 1" :star)
    (:quizz2 "/quizzes/2" "Quizz 2" :empty)
    (:quizz3 "/quizzes/3" "Quizz 3" :heart)
    "Administration"
    (:students "/students" "Students" :folder-close)
    (:classes "/classes" "Classes" :leaf)
    (:groups "/groups" "Groups" :leaf)
    ))

(defn item
  "Creates item on the sidebar. REST contains keyword :active added to the sidebar if the sidebar item should be active and a keyword for the the icon"
  [url title & more ]
  (let [
        css-classes (->> (->>  more (filter #(= :active %))) (apply kw->str))
        icon (->  (->> more (filter #(not= :active %))) first)]
    [:li {:class "active"}
     [:a {:shape "rect", :href url}
      [:i {:class (when icon (icon BOOTSTRAP-ICONS))}] title]]))

(defn section [title]
  [:li.nav-header title])

(defn sidebar [ & items ]
  [:ul.nav.nav-list
   items
   ])

(defn build-sidebar
  [bar]
  (sidebar
   (for [v bar]
     (cond  (string? v) (section v)
            (seq? v) (item (second v) (nth v 2) (nth v 3))))))

(defn- find-sb-item-by-key
  "Returns a sidebar item by its key"
  [k sb]
  (first (filter #(and (list? %) (= :classes (first %))) sb)))
