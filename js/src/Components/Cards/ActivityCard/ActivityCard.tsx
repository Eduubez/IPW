import React from "react";


import styles from "./activitycard.module.css";
type ActivityResponse = {
    id: number;
    processId: number;
    userId: number;
    action: string;
    description: string;
    createdAt: string;
}

export function ActivityCard({ activity }: { activity: ActivityResponse }) {
    return(
        <div className={styles["activity-card"]}>
            <span className={styles["activity-description"]}>{activity.description}</span>
            <span className={styles["activity-date"]}>{new Date(activity.createdAt).toLocaleString()}</span>
        </div>
    )
}
