  import { Icon } from "../Components/Icons/Icons";
  import { Color } from "../StyleGuide/colors";

  export const mockRoles = [
    {
      icon: {name: Icon.Crown, style: { background: Color.DarkBlue } },
      title: "Admin",
      subtitle: "Full access to all features",
      permissions: ["Manage Users", "View Reports", "Configure Settings"],
      style: { border: `2px solid ${Color.DarkBlue}` },
    },
    {
      icon: {name: Icon.Search, style: { background: Color.DarkRed } },
      title: "Editor",
      subtitle: "Can edit content but has limited access to settings",
      permissions: ["Edit Content", "View Reports"],
      style: { border: `2px solid ${Color.DarkRed}` },
    },
    {
      icon: {name: Icon.Visibility, style: { background: Color.GreenPrimary } },
      title: "Viewer",
      subtitle: "Can only view content and reports",
      permissions: ["View Content", "View Reports"],
      style: { border: `2px solid ${Color.GreenPrimary}` },
    },
    {
      icon: {name: Icon.Visibility, style: { background: Color.LightGreen } },
      title: "Viewer",
      subtitle: "Can only view content and reports",
      permissions: ["View Content", "View Reports"],
      style: { border: `2px solid ${Color.LightGreen}` },
    },
    {
      icon: {name: Icon.Visibility, style: { background: Color.DarkBlue } },
      title: "Viewer",
      subtitle: "Can only view content and reports",
      permissions: ["View Content", "View Reports"],
      style: { border: `2px solid ${Color.DarkBlue}` },
    },
  ];