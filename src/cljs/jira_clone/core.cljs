(ns jira-clone.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-frisk.core :refer [enable-re-frisk!]]
            [jira-clone.events]
            [jira-clone.subs]
            [day8.re-frame.http-fx]
            [jira-clone.views :as views]
            [jira-clone.config :as config]
            [jira-clone.db :as db]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")
    (set! (.-onkeypress js/document)
          (fn [e]
            (when (and (.-ctrlKey e)
                       (= (.-key e) "z"))
              (re-frame/dispatch [:undo]))))))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db db/default-db])
  (dev-setup)
  (mount-root))
