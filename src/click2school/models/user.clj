(ns  click2school.models.user
  (:require [click2school.utils :as utils]
            [noir.util.crypt :as crypt]
            [noir.validation :as validate]))

(def *data*
  (ref
   [{:id 1,
     :first-name "Evgeniy",
     :middle-name "N."
     :last-name "Sharapov",
     :username "test",
     :password "test",
     :gender "Male",
     :email "evgeniy.sharapove@gmail.com",
     :org "My Organization",
     :roles [:admin]}

    {:id 2,
     :first-name "Chittu",
     :last-name "Desai",
     :gender "Male",
     :username "test2",
     :password "test2",
     :email "chittu_d@yahoo.com",
     :org "Siemens",
     :roles [:parent]}
    
    {:id 3,
     :first-name "Darla",
     :last-name "Sparrow",
     :gender "Female",
     :username "test3",
     :password "test3",
     :email "test@gmail.com",
     :org "John Hopkins Elementary School",
     :roles [:teacher]}
    
    {:id 4,
     :first-name "Roger",
     :last-name "Ellis",
     :gender "Male",
     :username "test4",
     :password "test4",
     :email "roger.ellis@emai.com",
     :org "Siemens",
     :roles [:parent]}]))

(defn get-list []
"Returns list of all users"
  @*data*)

(defn find-by-id [id]
  (first
   (filter #(= id (:id %)) (get-list))))

(defn find-by-first-last-name [{ first-name :first-name, last-name :last-name}]
  (first
   (filter #(and (= first-name (:first-name %)) (= last-name (:last-name %))) (get-list))))

(defn find-by-username [{username :username}]
  (first
   (filter #(= username (:username %)) (get-list))))

(defn fuzzy-find [q]
  (filter #(or (utils/substring? (:first-name %) q) (utils/substring? (:last-name %) q)) (get-list)))

(defn fullname [u]
  (str (:first-name u) " " (:last-name u)))

(defn create [])

(defn delete [])

