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
  (if (contains? u :fullname)
    (:fullname u)
    (str (:first-name u) " " (:last-name u))))

(defn- mk-user-id []
  (rand-int 1000000))

(defn create [user]
  (dosync
   (alter *data* conj
          (merge {:id (mk-user-id)}
                 (assoc user
                   :first-name (if (contains? user :first-name) (:first-name user) (first (clojure.string/split (fullname user) #" +")))
                   :last-name (if (contains? user :last-name) (:last-name user) (last (clojure.string/split (fullname user) #" +"))))))))

(defn update [user]
  (let [other-users (filter #(not= (:id %) (:id user)) @*data*)
        updated-user (merge (find-by-id (:id user)) user)]
    (dosync
     (ref-set *data* (conj other-users updated-user)))))

(defn delete [])

