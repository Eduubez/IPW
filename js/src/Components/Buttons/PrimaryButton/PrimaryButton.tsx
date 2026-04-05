import styles from './primaryButton.module.css';
export default function PrimaryButton({ text, onClick, enabled , style}: { text: string; onClick: () => void; enabled: boolean, style?: React.CSSProperties }) {

    return(
        <>
        <div className={enabled  ? styles["primary-button"] : styles["primary-button-disabled"] } onClick={onClick} style={style}>
            <span className={styles["primary-button-text"]}>{text}</span>
        </div>
        </>
    )
}