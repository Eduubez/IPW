import { PrimaryBadge } from "../PrimaryBadge/PrimaryBadge";
import { Color } from "../../../StyleGuide/colors";
import { useTranslation } from "react-i18next";

const NOT_ASSIGNED_STYLE = {
  background: Color.Gray,
};
const ASSIGNED_STYLE = {
  background: Color.GreenPrimary,
};
const ON_GOING_STYLE = {
  background: Color.LightBlue,
};
const WAITING_APPROVAL_SUPERVISOR_STYLE = {
  background: Color.YellowPrimary,
};
const APPROVED_BY_SUPERVISOR_STYLE = {
  background: Color.YellowPrimary,
};
const REJECTED_BY_SUPERVISOR_STYLE = {
  background: Color.DarkRed,
};
const WAITING_APPROVAL_MANAGER_STYLE = {
  background: Color.YellowPrimary,
};
const APPROVED_BY_MANAGER_STYLE = {
  background: Color.GreenPrimary,
};
const REJECTED_BY_MANAGER_STYLE = {
  background: Color.DarkRed,
};
const CANCELED_STYLE = {
  background: Color.Gray,
};

export type StateType =
  | "NOT_ASSIGNED"
  | "ASSIGNED"
  | "ON_GOING"
  | "WAITING_APPROVAL_SUPERVISOR"
  | "APPROVED_BY_SUPERVISOR"
  | "REJECTED_BY_SUPERVISOR"
  | "WAITING_APPROVAL_MANAGER"
  | "APPROVED_BY_MANAGER"
  | "REJECTED_BY_MANAGER"
  | "CANCELED";

export function StateBadge({ state }: { state: StateType }) {
  const { t } = useTranslation();
  const normalizedState = state.toUpperCase();
  const getStyle = () => {
    switch (normalizedState) {
      case "NOT_ASSIGNED":
        return NOT_ASSIGNED_STYLE;
      case "ASSIGNED":
        return ASSIGNED_STYLE;
      case "ON_GOING":
        return ON_GOING_STYLE;
      case "WAITING_APPROVAL_SUPERVISOR":
        return WAITING_APPROVAL_SUPERVISOR_STYLE;
      case "APPROVED_BY_SUPERVISOR":
        return APPROVED_BY_SUPERVISOR_STYLE;
      case "REJECTED_BY_SUPERVISOR":
        return REJECTED_BY_SUPERVISOR_STYLE;
      case "WAITING_APPROVAL_MANAGER":
        return WAITING_APPROVAL_MANAGER_STYLE;
      case "APPROVED_BY_MANAGER":
        return APPROVED_BY_MANAGER_STYLE;
      case "REJECTED_BY_MANAGER":
        return REJECTED_BY_MANAGER_STYLE;
      case "CANCELED":
        return CANCELED_STYLE;
      default:
        return {};
    }
  };

  return <PrimaryBadge text={t(`State.${normalizedState}`)} style={getStyle()} />;
}
