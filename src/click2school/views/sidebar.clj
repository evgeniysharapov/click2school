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
(def *default-sidebar*
  [["Communication"
    [:messages "/messages" "Messages" :inbox :active]
    [:instants "/instants" "Instant Messages" :comment]]
   ["Calendar"
    [:meetings  "/meetings"  "Meetings"  :user ]
    [:papers  "/papers"  "Papers Due"  :file ]]
   ["Quizzes"
    [:quizz1  "/quizz/1"  "Quizz 1"  :star ]
    [:quizz2  "/quizz/2"  "Quizz 2"  :empty ]
    [:quizz3  "/quizz/3"  "Quizz 3"  :heart ]]
   ["Administration"
    [:students  "/students"  "Students"  :folder-close ]
    [:classes  "/classes"  "Classes"  :leaf ]
    [:groups  "/groups"  "Groups"  :leaf ]]]
  )

(defn- item
  "Creates item on the sidebar. REST contains keyword :active added to the sidebar if the sidebar item should be active and a keyword for the the icon"
  [url title & more ]
  (let [css-classes (->> (->>  more (filter #(= :active %))) (apply kw->str))
        icon (->  (->> more (filter #(not= :active %))) first)]
    [:li {:class css-classes}
     [:a {:shape "rect", :href url}
      [:i {:class (when icon (icon BOOTSTRAP-ICONS))}] title]]))

(defn- section [title]
  [:li.nav-header title])

(defn- sidebar-main [ & items ]
  [:ul.nav.nav-list
   items
   ])

(defn sidebar
  "Creates hiccup sidebar out of the sidebar stucture."
  [bar]
  (sidebar-main
   (for [sec bar]
       (do
        (section (first sec))
        (map #(apply item (rest %)) (rest  sec))))))

(defn- find-section-by-item-key
  "Returns a sidebar item by its key"
  [sb k]
  (letfn [(in-section [sec]
            (first  (filter #(= (first %) k) (rest sec)))) ]
    (filter in-section  sb)))
;(find-section-by-item-key *default-sidebar* :papers)

(defn alter-item
  "Modifies item in the bar using function fn and a key k pointing to the item (do not forget that item is a vector)"
  [bar k fn]
  (vec (for [sec bar]
         (let [[title & items] sec]
           (vec
            (cons title
                  (for [[kk & _ :as i] items]
                    (if (= kk k)
                      (apply fn (list i))
                      i))))))))

(defn alter-all-items
  "Modifies all items in the bar using function fn"
  [bar fn]
  (vec (for [sec bar]
         (let [[title & items] sec]
           (vec
            (cons title
                  (for [i items]
                    (apply fn (list i))
                    )))))))

(defn activate-item
  "Activates item k in sidebar bar"
  [bar k]
  (apply #'alter-item (list bar k #(if (nil? (some #{:active} %)) (conj % :active) %))))
;; (activate-item *default-sidebar* :message)
;; (activate-item *default-sidebar* :instants)

(defn deactivate-item
  "Deactivates item k in sidebar bar"
  [bar k]
  (letfn [(is-not-active? [x] (not= x :active))]
    (apply #'alter-item (list bar k #(vec (filter is-not-active? %))))
    ))
;; (deactivate-item *default-sidebar* :message)

(defn deactivate-sidebar
  "Deactivates all items in the sidebar"
  [bar]
  (letfn [(is-not-active? [x] (not= x :active))]
    (apply #'alter-all-items (list bar #(vec (filter is-not-active? %))))
    ))
