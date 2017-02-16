(ns jira-clone.db
   (:require [clojure.spec :as s]
             [clojure.test.check.generators :as gen]))

(s/def ::user (s/and string? not-empty))
(s/def :issue/id (s/and integer? pos?))
(s/def :issue/description (s/and string? #(> (count %) 5)))
(s/def :issue/status #{:todo :in-progress :done})
(s/def :issue/assigned-to ::user)
(s/def :db/issue (s/keys :req-un [:issue/description :issue/status]
                         :opt-un [:issue/assigned-to]))

(s/def :db/issues (s/map-of :issue/id :db/issue))

(s/def :db/logged-user ::user)
(s/def :db/in-progress? boolean?)

(s/def ::db (s/keys :req-un [:db/issues :db/logged-user :db/in-progress?]))

(def default-db
  {:issues {1 {:description "First issue" :status :todo :assigned-to "root"}
            2 {:description "This doesn't work either" :status :in-progress}
            3 {:description "Improve that" :status :done}}
   :logged-user "root"
   :in-progress? false})

(comment
  (re-frame.core/dispatch [:initialize-db  (-> (s/gen ::db)
                                               (gen/sample 1)
                                               first)])
  
  )
