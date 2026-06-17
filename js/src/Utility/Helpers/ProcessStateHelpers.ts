
export const normalizeState = (state: string | undefined | null): string => {
    return state?.toUpperCase() ?? "";
}
