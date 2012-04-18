(ns click2school.test.views.questions
  (:use [click2school.views.questions] :reload)
  (:use [clojure.test]
        [noir.util.test]))


(deftest test-controls
  (is (re-matches  #"<div class=\"control-group\"><label class=\"control-label\" for=\"name.+\">label</label><div class=\"controls\"><input class=\"input-xlarge\" id=\"name.+\" name=\"name\" type=\"text\".*/></div></div>" (on-form-control "text" "name" "label"))))
