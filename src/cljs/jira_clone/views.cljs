(ns jira-clone.views
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [jira-clone.events :refer [forward-issue]]))
 

(defn issues-table [title issues]
  [:div.container
   [:h4 title]
   [:table.table
    [:thead [:th "Id"] [:th "Description"] [:th "Assigned To"] [:th "Status"] [:th "Action"]]
    [:tbody
     (for [{:keys [id description assigned-to status]} issues]
       [:tr {:key (str status id)}
        [:td id]
        [:td description]
        [:td (or assigned-to "unassigned")]
        [:td [:span.label {:class (case status
                                    :todo "label-danger"
                                    :in-progress "label-warning"
                                    :done "label-success")}
              status]]
        [:td (when-not (= status :done)
               [:button {:on-click #(dispatch [:forward-issue id])}
                (name (forward-issue status))])]])]]])

(defn main-panel []
  (let [my-open-issues (subscribe [:my-open-issues])
        open-issues    (subscribe [:open-issues])
        closed-issues  (subscribe [:closed-issues])
        logged-user    (subscribe [:logged-user])
        in-progress?   (subscribe [:in-progress?])] 
    (fn []
      [:div.container
       [:h4.pull-right (str "Logged as :" @logged-user)]
       [:div.container
        [issues-table "My open issues" @my-open-issues]
        [issues-table "Open issues" @open-issues]
        [issues-table "Closed issues" @closed-issues]
        (if @in-progress?
          [rc/throbber :size :regular]
          [:button {:on-click #(dispatch [:add-issue-from-joke])}
           "Create issue from joke"])]])))
