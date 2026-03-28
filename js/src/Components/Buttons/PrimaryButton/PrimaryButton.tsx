import styles from './primaryButton.module.css';
export default function PrimaryButton({ text, onClick, enabled }: { text: string; onClick: () => void; enabled: boolean }) {

    return(
        <>
        <div className={enabled  ? styles["primary-button"] : styles["primary-button-disabled"]} onClick={onClick}>
            <span className={styles["primary-button-text"]}>{text}</span>
        </div>
        </>
    )
}