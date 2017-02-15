(ns jira-clone.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [jira-clone.core-test]))

(doo-tests 'jira-clone.core-test)
