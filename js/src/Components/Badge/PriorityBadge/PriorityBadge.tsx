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

type PriorityType = "Urgent" | "Medium" | "Low";

export default function PriorityBadge( { priority}: { priority: PriorityType} ) {
    const { t } = useTranslation();
    
    const getStyle = () => {
        switch (priority) {
            case "Urgent":
                return UrgentStyle;
            case "Medium":
                return MediumStyle;
            case "Low":
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