import { Icon } from "../Components/Icons/Icons";
import { Color } from "../StyleGuide/colors";

export const ROLES = [
  {
    key: "admin",
    icon: { name: Icon.Crown, style: { background: Color.Purple } },
    title: "admin",
    permissions: ["ManageUsers", "CreateUsers"],
    style: { border: `2px solid ${Color.Purple}` },
  },
  {
    key: "triator",
    icon: { name: Icon.Star, style: { background: Color.DarkRed } },
    title: "triator",
    permissions: ["AddReport", "MyHistory"],
    style: { border: `2px solid ${Color.DarkRed}` },
  },
  {
    key: "investigator",
    icon: { name: Icon.Visibility, style: { background: Color.DarkBlue } },
    title: "investigator",
    permissions: ["ViewOwnProcess", "SubmitProcess", "MyHistory"],
    style: { border: `2px solid ${Color.DarkBlue}` },
  },
  {
    key: "supervisor",
    icon: { name: Icon.Visibility, style: { background: Color.YellowDark } },
    title: "supervisor",
    permissions: ["ViewAreaProcess", "ValidateAreaProcess", "AreaHistory"],
    style: { border: `2px solid ${Color.YellowDark}` },
  },
  {
    key: "manager",
    icon: { name: Icon.Group, style: { background: Color.GreenPrimary } },
    title: "manager",
    permissions: ["UpdateEveryProcess", "ValidateAllProcess", "AllHistory"],
    style: { border: `2px solid ${Color.GreenPrimary}` },
  },
];
