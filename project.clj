(defproject click2school "0.1.0-SNAPSHOT"
            :description "Click2Interact Web Application"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [clj-stacktrace "0.2.4"]
                           [noir "1.2.2"]
                           [clj-tagsoup "0.2.6"]
                           [clj-time "0.3.7"]
                           [org.clojure/data.json "0.1.2"]
                           [postgresql/postgresql "8.4-702.jdbc4"]
                           [mysql/mysql-connector-java "5.1.6"]
                           [org.clojure/java.jdbc "0.1.3"]
                           [lobos "1.0.0-SNAPSHOT"]
                           [korma "0.3.0-beta7"]
                           [clojureql "1.0.3"]
                           [clj-yaml "0.3.1"]
                           [clj-record "1.1.1"]]
            :main click2school.server)


