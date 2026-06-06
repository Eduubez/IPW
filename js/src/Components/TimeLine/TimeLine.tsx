import Timeline from "@mui/lab/Timeline";
import TimelineItem, { timelineItemClasses } from "@mui/lab/TimelineItem";
import TimelineSeparator from "@mui/lab/TimelineSeparator";
import TimelineConnector from "@mui/lab/TimelineConnector";
import TimelineContent from "@mui/lab/TimelineContent";
import TimelineDot from "@mui/lab/TimelineDot";
import { Icon } from "../Icons/Icons";
import styles from "./timeline.module.css";
import { TimeLineActivityItem } from "./TimeLineActivityItem/TimeLineActivityItem";
import LoadingComponent from "../LoadingComponent/LoadingComponent";

export function TimeLine({ loading ,items}: { loading?: boolean ,items: { done: boolean; label: string; date: Date; userName: string }[] }) {
  return loading ? (
    <LoadingComponent />
  ) : (
    <Timeline
      position="right"
      sx={{
        [`& .${timelineItemClasses.root}:before`]: {
          flex: 0,
          padding: 0,
        },
      }}>
      {items.map((item, index) => (
        <TimelineItem key={index}>
          <TimelineSeparator
            sx={
              item.done
                ? {
                    "& .MuiTimelineConnector-root": {
                      backgroundColor: "var(--color-light-green)",
                    },
                    "& .MuiTimelineDot-root": {
                      borderColor: "var(--color-light-green)",
                      color: "var(--color-light-green)",
                    },
                  }
                : {}
            }>
            <TimelineDot
              className={`${styles["dot-style"]} ${item.done && styles["done"]}`}
              variant="outlined">
              <span
                className={`material-symbols-outlined ${item.done && styles["icon-done"]}`}>
                {item.done ? Icon.Check : ""}
              </span>
            </TimelineDot>
            {index < items.length - 1 && <TimelineConnector />}
          </TimelineSeparator>
          <TimelineContent>
            <TimeLineActivityItem
              title={item.label}
              time={item.date.toLocaleTimeString()}
              user={item.userName}
            />
          </TimelineContent>
        </TimelineItem>
      ))}
    </Timeline>
  );
}
