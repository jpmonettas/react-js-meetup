(ns jira-clone.events
  (:require [re-frame.core :as re-frame :refer [debug path after]]
            [jira-clone.db :as db]
            [day8.re-frame.undo :as undo :refer [undoable]]
            [ajax.core :as ajax]
            [cljs.spec :as s]))


(defn check-and-throw
  "throw an exception if db doesn't match the spec"
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec (after (partial check-and-throw :jira-clone.db/db)))

(def forward-issue {:todo :in-progress
                    :in-progress :done})

(re-frame/reg-event-db
 :initialize-db
 [check-spec]
 (fn  [_ [_ db]]
   db))

(re-frame/reg-event-db
 :forward-issue
 [debug (undoable "Forward issue") check-spec]
 (fn  [db [_ issue-id]]
   (update-in db [:issues issue-id :status] forward-issue)))

(re-frame/reg-event-fx
 :add-issue-from-joke 
 [debug (undoable "Add issue from joke") check-spec]
 (fn [cofxs [_]]
   {:db (assoc (:db cofxs) :in-progress? true)
    :http-xhrio {:method          :get
                 :uri             "https://api.chucknorris.io/jokes/random"
                 :response-format (ajax/json-response-format {:keywords? true}) 
                 :on-success      [:got-a-joke]
                 :on-failure      [:error]}}))

(re-frame/reg-event-db
 :got-a-joke
 [(undoable "Got a joke") check-spec]
 (fn  [db [_ joke]]
   (let [new-id (->> (:issues db)
                     keys
                     (reduce max)
                     (inc))]
     (-> db
         (assoc :in-progress? false) 
         (assoc-in [:issues new-id] {:id new-id
                                     :description (:value joke)
                                     :status :todo})))))

