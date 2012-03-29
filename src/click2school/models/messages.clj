(ns click2school.models.messages
  (:require [clj-time.core :as time]
            [clj-time.format :as time-fmt]
            [click2school.models.user :as user]))

(def *data*
  (ref
   [{:id 1 :subject "Organizing meeting 1" :from "Jacob Thornton" :to "Evgeniy Sharapov" :sent "01/01/2012" :text "Dear Mr. Sharapov"}
    {:id 2 :subject "Organizing meeting 2" :from "Jacob Thornton" :to "Evgeniy Sharapov" :sent "01/01/2012" :text "Dear Mr. Sharapov"}
    {:id 3 :subject "Organizing meeting 3" :from "Jacob Thornton" :to "Evgeniy Sharapov" :sent "01/01/2012" :text "Dear Mr. Sharapov"}
    {:id 4 :subject "Organizing meeting 3" :from "Jacob Thornton" :to "Chittu Desai" :sent "01/01/2012" :text "Dear Mr. Sharapov"}]))


(defn get-list []
  @*data*)

(defn find-by-id [id]
  (first
   (filter #(= id (:id %)) (get-list))))

(defn find-by-username [username]
  (filter #(= (user/fullname (user/find-by-username {:username  username})) (:to %)) (get-list)))

(defn- mk-message-id []
  (rand-int 1000000))

(defn create [sender recipient subject text]
  (dosync
   (alter *data* conj {:id (mk-message-id),
                       :to recipient,
                       :from sender,
                       :sent (time/now),
                       :subject subject,
                       :text text})))

;(create "Evgeniy Sharapov" "Evgeniy Sharapove" "This is test" "Ok, this is a test message")

(defn delete [id]
  (dosync (ref-set *data* (filter #(not (= (:id %) id)) @*data*))))
