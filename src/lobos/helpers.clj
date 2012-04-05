(ns lobos.helpers
  (:refer-clojure :exclude [bigint boolean char double float time])
  (:use (lobos schema)))

(defn surrogate-key [table]
  (integer table :id :auto-inc :primary-key))

(defn timestamps [table]
  (-> table
      (timestamp :updated_on)
      (timestamp :created_on (default (now)))))

(defn- refer-to-helper [cname table ptable]
  (println cname)
  (integer table cname [:refer ptable :id :on-delete :set-null]))

(defn refer-to
  ([table ptable]
     (let [cname (-> (->> ptable name butlast (apply str))
                  (str "_id")
                  keyword)]
       (refer-to-helper cname table ptable)))
  ([table pfx ptable]
     (let [cname (-> (str pfx "_"
                       (->> ptable name butlast (apply str)))
                  (str "_id")
                  keyword)]
       (refer-to-helper cname table ptable))))


(defmacro tbl [name & elements]
  `(-> (table ~name)
       (timestamps)
       ~@(reverse elements)
       (surrogate-key)))
