(ns  click2school.models.user
  (:require [noir.session :as session]
            [noir.util.crypt :as crypt]
            [noir.validation :as validate]))

(def *data*
  (ref
   [{:id 1,
     :first-name "Evgeniy",
     :last-name "Sharapov",
     :username "test",
     :password "test",
     :gender "Male",
     :email "evgeniy.sharapove@gmail.com"}

    {:id 2, :first-name "Chittu", :last-name "Desai", :gender "Male", :username "test2", :password "test2", :email "chittu_d@yahoo.com"
     }
    {:id 3, :first-name "Darla", :last-name "Sparrow", :gender "Female", :username "test3", :password "test3", :email "test@gmail.com"}
    {:id 4, :first-name "Roger", :last-name "Ellis", :gender "Male", :username "test4", :password "test4", :email "roger.ellis@emai.com"}]))


(defn fetch-list []
  @*data*)


(defn fetch [id]
  (first
   (filter #(= id (:id %)) (fetch-list))))

(defn find-by-full-name [{ first-name :first-name, last-name :last-name}]
  (first
   (filter #(and (= first-name (:first-name %)) (= last-name (:last-name %))) (fetch-list))))


(defn find-by-username [{username :username}]
  (first
   (filter #(= username (:username %)) (fetch-list))))



(defn substring? [s search]
  (> (.indexOf  (.toUpperCase  (str s))  (.toUpperCase  (str search))) -1))


(defn fuzzy-find [q]
  (filter #(or (substring? (:first-name %) q) (substring? (:last-name %) q)) (fetch-list)))

(defn fullname [u]
  (str (:first-name u) " " (:last-name u)))

(defn create [])

(defn delete [])

