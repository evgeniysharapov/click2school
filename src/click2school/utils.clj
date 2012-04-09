(ns click2school.utils
  (:require [pl.danieljanus.tagsoup :as tagsoup]
            [compojure.route :as route]
            [noir.session :as session]
            [clj-time.core :as ctime]
            [clj-time.coerce :as tcoerce]
            [clj-time.format :as tform]))

(defn substring? [s search]
  "Returns true if SEARCH is a substring of S"
  (> (.indexOf  (.toUpperCase  (str s))  (.toUpperCase  (str search))) -1))

(defn file-to-soup [f]
  (route/resources "welcome_statement.html")
  (tagsoup/parse (slurp f)))

(defn describe-time-elapsed
  "Describe the amount of time that has passed (in minutes) in a conversational way"
  [minutes]
  (let [[hours days months years]
        (for [amt [60.0 1440.0 43829.0639 525948.766]]
          (Math/round (/ minutes amt)))]
    (cond
     (= minutes 0) "just now"
     (< minutes 2) "a minute ago"
     (< minutes 60) (str minutes " minutes ago")
     (= hours 1) "an hour ago"
     (< hours 24) (str hours " hours ago")
     (= days 1) "yesterday"
     (< days 7) (str days " days ago")
     (= 1 (Math/round (/ days 7.0))) "1 week ago"
     (< days 31) (str (Math/round (/ days 7.0)) " weeks ago")
     (= months 1) "a month ago"
     (< months 12) (str months " months ago")
     (= years 1) "a year ago"
     :else (str years " years ago"))))

(defn- human-date-helper
  [t]
  (let [tm t
        minutes-elapsed (ctime/in-minutes
                         (ctime/interval
                          tm (ctime/now)))
        datetime (tform/unparse (tform/formatters :rfc822)
                                tm)]
    (describe-time-elapsed minutes-elapsed)))

(defmulti human-date 
  "Convert a timestamp to a human readable date date, like '3 minutes ago' or '2 weeks ago'."
  class)

(defmethod human-date java.sql.Timestamp [t]
  (human-date-helper (tcoerce/from-long (.getTime t))))

(defmethod human-date java.util.Date [t]
  (human-date-helper (tcoerce/from-long (.getTime t))))

(defmethod human-date org.joda.time.DateTime [t]
  (human-date-helper t))

