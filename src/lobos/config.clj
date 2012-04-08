(ns lobos.config
  (:use [lobos.connectivity]
        [click2school.config.db]))

;;; reloading config
(when (@global-connections :default-connection)
  (close-global))
(open-global db)
