import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";

import translationUA from "./locales/ua-translation.json";
import translationEN from "./locales/en-translation.json";

const resources = {
    ua: { translation: translationUA },
    en: { translation: translationEN },
};

i18n
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        resources,
        fallbackLng: "ua",
        interpolation: {
            escapeValue: false,
        },
    });

export default i18n;
