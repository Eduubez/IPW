import React from "react";
import { useTranslation } from "react-i18next";
import { formatDate } from "../../../Utility/Helpers/DateHelpers";
import styles from "./activitycard.module.css";

type ActivityCardProps = {
    label: string;
    date: Date | string;
};

export function ActivityCard({ activity }: { activity: ActivityCardProps }) {
    const { t } = useTranslation();
    const formattedDate = formatDate(
        activity.date instanceof Date ? activity.date : new Date(activity.date),
    );

    return (
        <div className={styles["activity-card"]}>
            <span className={styles["activity-description"]}>{t(`ActivityItem.${activity.label}`)}</span>
            <span className={styles["activity-date"]}>{formattedDate}</span>
        </div>
    );
}
