(defproject click2school "0.1.0-SNAPSHOT"
            :description "Click2Interact Web Application"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [clj-stacktrace "0.2.4"]
                           [noir "1.2.2"]
                           [clj-tagsoup "0.2.6"]
                           [clj-time "0.3.7"]
                           [org.clojure/data.json "0.1.2"]
                           [postgresql/postgresql "8.4-702.jdbc4"]
                           [org.clojure/java.jdbc "0.1.3"]
                           [lobos "1.0.0-SNAPSHOT"]]
            :main click2school.server)


