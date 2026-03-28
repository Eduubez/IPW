import styles from './withbackground.module.css';
import React from 'react';


export function WithBackground({children}:{children:React.ReactNode}) {
  return (
    <div className={styles["with-background-container"]}>
      {children}
    </div>
  );
}