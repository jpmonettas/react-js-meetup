(ns jira-clone.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame :refer [subscribe]]))


(re-frame/reg-sub
 :logged-user
 (fn [db _]
   (:logged-user db)))

(re-frame/reg-sub
 :all-issues
 (fn [db _]
   (map (fn [[id issue]]
          (assoc issue :id id))
        (:issues db)))) 

(re-frame/reg-sub
 :closed-issues
 (fn [_ _]
   (subscribe [:all-issues]))
 (fn [issues _]
   (->> issues
        (filter #(= (:status %) :done)))))

(re-frame/reg-sub
 :open-issues
 (fn [_ _]
   (subscribe [:all-issues]))
 (fn [issues]
   (->> issues
        (remove #(= (:status %) :done)))))

(re-frame/reg-sub
 :my-open-issues
 (fn [_ _]
   [(subscribe [:logged-user])
    (subscribe [:open-issues])])
 (fn [[logged-user open-issues] _]
   (filter #(= (:assigned-to %) logged-user) open-issues)))

(re-frame/reg-sub
 :in-progress?
 (fn [db _]
   (:in-progress? db)))
