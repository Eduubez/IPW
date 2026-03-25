import { AppBar, Button, Paper } from "@mui/material";
import React from "react";
import { useTranslation } from "react-i18next";

export default function Login() {
  const {t} = useTranslation();

  return (
    <div>

        <AppBar position="static" color="primary">
         
          <Button color="inherit">{t("login")}</Button>
          <Button color="inherit">{t("register")}</Button>
        </AppBar>
         <span>{t("text.title")}</span>


    </div>
  );
}