import { Icon } from "./Icons";
import { Color } from "../StyleGuide/colors";
import i18next from 'i18next'

export const ROLE_KEYS = {
  ADMIN: "admin",
  TRIATOR: "triator",
  INVESTIGATOR: "investigator",
  SUPERVISOR: "supervisor",
  MANAGER: "manager",
} as const;

export type RoleKey = (typeof ROLE_KEYS)[keyof typeof ROLE_KEYS];

export const ROLES = [
  {
    key: "admin",
    icon: { name: Icon.Crown, style: { background: Color.Purple } },
    title: "admin",
    permissions: ["ManageUsers", "CreateUsers"],
    style: { border: `2px solid ${Color.Purple}` },
    navigationItems : [
      { name: i18next.t("NavigationItems.Dashboard"), path: "/dashboard", icon: "dashboard" },
    ]
  },
  {
    key: "triator",
    icon: { name: Icon.Star, style: { background: Color.DarkRed } },
    title: "triator",
    permissions: ["AddReport", "MyHistory"],
    style: { border: `2px solid ${Color.DarkRed}` },
    navigationItems : [
      { name: i18next.t("NavigationItems.Dashboard"), path: "/dashboard", icon: "dashboard" },
      { name: i18next.t("NavigationItems.NewProcess"), path: "/processes/new", icon: "add" },
      { name: i18next.t("NavigationItems.History"), path: "/user/history", icon: "history" },
    ]
  },
  {
    key: "investigator",
    icon: { name: Icon.Visibility, style: { background: Color.DarkBlue } },
    title: "investigator",
    permissions: ["ViewOwnProcess", "SubmitProcess", "MyHistory"],
    style: { border: `2px solid ${Color.DarkBlue}` },
    navigationItems : [
      { name: i18next.t("NavigationItems.Dashboard"), path: "/dashboard", icon: "dashboard" },
      { name: i18next.t("NavigationItems.History"), path: "/user/history", icon: "history" },
    ]
  },
  {
    key: "supervisor",
    icon: { name: Icon.Visibility, style: { background: Color.YellowDark } },
    title: "supervisor",
    permissions: ["ViewAreaProcess", "ValidateAreaProcess", "AreaHistory"],
    style: { border: `2px solid ${Color.YellowDark}` },
    navigationItems : [
      { name: i18next.t("NavigationItems.Dashboard"), path: "/dashboard", icon: "dashboard" },
      { name: i18next.t("NavigationItems.History"), path: "/user/history", icon: "history" },
    ]
  },
  {
    key: "manager",
    icon: { name: Icon.Group, style: { background: Color.GreenPrimary } },
    title: "manager",
    permissions: ["UpdateEveryProcess", "ValidateAllProcess", "AllHistory"],
    style: { border: `2px solid ${Color.GreenPrimary}` },
    navigationItems : [
      { name: i18next.t("NavigationItems.Dashboard"), path: "/dashboard", icon: "dashboard" },
      { name: i18next.t("NavigationItems.History"), path: "/user/history", icon: "history" },
    ]
  },
];
