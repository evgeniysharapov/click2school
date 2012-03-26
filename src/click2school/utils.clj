(ns click2school.utils
  (:require [pl.danieljanus.tagsoup :as tagsoup]
            [compojure.route :as route]
            [click2school.models.user :as user]
            [noir.session :as session]))

(defn file-to-soup [f]
  (route/resources "welcome_statement.html")
  (tagsoup/parse (slurp f)))

(defn me []
  (user/fetch
   (session/get :user-id)))


