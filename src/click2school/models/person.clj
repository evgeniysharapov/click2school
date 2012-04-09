(ns click2school.models.person
  (:require [clj-record.boot]
            [clj-record.query :as query])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (has-many user :model user)))

(defn fuzzy-find
  "Fuzzily searches for a person looking for a subtring Q in last name and first name"
  [q]
  (let [like-q (.toUpperCase (str "%" q "%"))]
    (find-by-sql ["select * from persons where upper(first_name) like ? or upper(last_name) like ? ", like-q, like-q])))

(defn fullname
  "Returns full name of the person P."
  [p]
  (str (:first_name p) " " (:last_name p)))

(defn find-person-by-fullname
  [fullname]
  (let [names (clojure.string/split fullname #" +")
        fname (first names)
        lname (last names)]
    (find-record {:first_name fname, :last_name lname})))

(find-person-by-fullname "Evgeniy N. Sharapov")


