(ns jira-clone.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [jira-clone.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
