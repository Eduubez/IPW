export const Color = {
  TextPrimary: "var(--color-text-primary)",

  BackgroundSecondary: "var(--color-background-secondary)",
  BackgroundPrimary: "var(--color-background-primary)",
  BackgroundSurface:     "var(--color-background-surface)",

  DarkBlue: "var(--color-dark-blue)",
  DarkBlue100: "var(--color-dark-blue-100)",
  LightBlue: "var(--color-light-blue)",

  DarkRed: "var(--color-dark-red)",
  DarkRed100: "var(--color-dark-red-100)",

  Orange: "var(--color-orange)",

  Gray: "var(--color-gray)",

  GreenPrimary: "var(--color-green-primary)",
  LightGreen: "var(--color-light-green)",

  YellowDark: "var(--color-yellow-dark)",
  YellowPrimary: "var(--color-yellow-primary)",
  Purple:"var(--color-purple)",
}

export const getPriorityColor = (priority: string) => {
  const normalizedPriority = priority.toUpperCase();
  switch (normalizedPriority) {
    case "URGENT":
      return Color.DarkRed;
    case "WITH_PRIORITY":
      return Color.Orange;
    case "NORMAL":
      return Color.GreenPrimary;
    default:
      return Color.Gray;
  }
}
