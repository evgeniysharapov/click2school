(ns click2school.views.admin
  (:require [click2school.views.common :as common])
  (:use [noir.core :only [defpartial defpage]]))
;;; 
;;; This is an admin view. One that can add/delete/modify users,
;;; assign classes, techers; upload forms and so on.
;;; 

