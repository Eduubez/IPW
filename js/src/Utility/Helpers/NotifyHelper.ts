import { enqueueSnackbar } from "notistack";
import i18next from 'i18next'


export const notifyUser = (errorCode:String) => {
        enqueueSnackbar(i18next.t(`Error.${errorCode}`) || i18next.t("Error.INTERNAL_ERROR"), { variant: "error" });
}