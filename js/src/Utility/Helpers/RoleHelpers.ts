import { ROLES } from "../../Config/RolesConfig";
import { Color } from "../../StyleGuide/colors";
import type { CSSProperties } from "react";

export function getRoleStyle(role: string): CSSProperties {
  const match = ROLES.find((r) => r.key === role.toLowerCase());
  return { background: match ? match.icon.style.background : Color.Gray };
}
