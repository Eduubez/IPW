import React from "react";
import { useState } from "react";
import styles from "./slideshow.module.css";

export function SlideShow({ content ,offContentArrow}: { content: React.ReactNode[], offContentArrow?: boolean }) {
  const [currentIndex, setCurrentIndex] = useState(0);

  const hasNext = currentIndex < content.length - 1;
  const handleNext = () => {
    if (hasNext) {
      setCurrentIndex(currentIndex + 1);
    } else {
      setCurrentIndex(0);
    }
  };

  return (

      <div className={`${styles["slide-show"]} ${offContentArrow ? styles["slide-show-outter-arrow"] : ""}`}>
        {content[currentIndex]}
        <button onClick={handleNext} className={styles["next-button"]}>
          <span className={`material-symbols-outlined ${styles["icon"]}`}>arrow_forward</span>
        </button>
      </div>

  );
}
