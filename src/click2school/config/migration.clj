(ns click2school.config.migration
  (:require [lobos.config]
            [lobos.core])
  )

(defn -main []
  (print "Migrating database...")
  (lobos.core/migrate)
  (println " done"))
