import { PrimaryBadge } from "../PrimaryBadge/PrimaryBadge";
import { Color } from "../../../StyleGuide/colors";
import { useTranslation } from "react-i18next";
import { STATES, type ProcessState } from "../../../MockData/MockStates";

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
  background: Color.Orange,
};
const APPROVED_BY_SUPERVISOR_STYLE = {
  background: Color.Orange,
};
const REJECTED_BY_SUPERVISOR_STYLE = {
  background: Color.DarkRed,
};
const WAITING_APPROVAL_MANAGER_STYLE = {
  background: Color.Orange,
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
const NOT_STARTED_STYLE = {
  background: Color.Gray,
};

export type StateType = ProcessState;

export function StateBadge({ state }: { state: StateType }) {
  const { t } = useTranslation();
  const normalizedState = state.toUpperCase();
  const getStyle = () => {
    switch (normalizedState) {
      case STATES.NOT_ASSIGNED:
        return NOT_ASSIGNED_STYLE;
      case STATES.ASSIGNED:
        return ASSIGNED_STYLE;
      case STATES.ON_GOING:
        return ON_GOING_STYLE;
      case STATES.WAITING_APPROVAL_SUPERVISOR:
        return WAITING_APPROVAL_SUPERVISOR_STYLE;
      case STATES.APPROVED_BY_SUPERVISOR:
        return APPROVED_BY_SUPERVISOR_STYLE;
      case STATES.REJECTED_BY_SUPERVISOR:
        return REJECTED_BY_SUPERVISOR_STYLE;
      case STATES.WAITING_APPROVAL_MANAGER:
        return WAITING_APPROVAL_MANAGER_STYLE;
      case STATES.APPROVED_BY_MANAGER:
        return APPROVED_BY_MANAGER_STYLE;
      case STATES.REJECTED_BY_MANAGER:
        return REJECTED_BY_MANAGER_STYLE;
      case STATES.CANCELED:
        return CANCELED_STYLE;
      case STATES.NOT_STARTED:
        return NOT_STARTED_STYLE;
      default:
        return {};
    }
  };

  return <PrimaryBadge text={t(`State.${normalizedState}`)} style={{ ...getStyle(), width: "100%" }} />;
}
