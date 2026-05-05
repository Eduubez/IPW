import { PrimaryBadge } from "../PrimaryBadge/PrimaryBadge";
import { Color } from "../../../StyleGuide/colors";
import { useTranslation } from "react-i18next";

const UrgentStyle = {
    background: Color.DarkRed,
}
const MediumStyle = {
    background: Color.Orange,
}
const LowStyle = {
    background: Color.GreenPrimary,
}

export type PriorityType = "NORMAL" | "WITH_PRIORITY" | "URGENT";

export function PriorityBadge( { priority}: { priority: PriorityType} ) {
    const { t } = useTranslation();
    
    const getStyle = () => {
        switch (priority) {
            case "URGENT":
                return UrgentStyle;
            case "WITH_PRIORITY":
                return MediumStyle;
            case "NORMAL":
                return LowStyle;
            default:
                return {};
        }
    }


    return (
        <PrimaryBadge
            text={t(`Priority.${priority}`)}
            style={getStyle()}
        />
    )
}