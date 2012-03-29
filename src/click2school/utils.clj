(ns click2school.utils
  (:require [pl.danieljanus.tagsoup :as tagsoup]
            [compojure.route :as route]
            [noir.session :as session]))

(defn substring? [s search]
  "Returns true if SEARCH is a substring of S"
  (> (.indexOf  (.toUpperCase  (str s))  (.toUpperCase  (str search))) -1))

(defn file-to-soup [f]
  (route/resources "welcome_statement.html")
  (tagsoup/parse (slurp f)))

