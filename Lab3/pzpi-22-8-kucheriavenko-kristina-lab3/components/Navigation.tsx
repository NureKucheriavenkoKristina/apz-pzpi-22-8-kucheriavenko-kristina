import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const Navigation = () => {
    const location = useLocation();
    const isHomePage = location.pathname === "/";
    const { t, i18n } = useTranslation();

    const toggleLanguage = () => {
        const newLang = i18n.language === "ua" ? "en" : "ua";
        i18n.changeLanguage(newLang);
    };

    const logout = () => {
        localStorage.removeItem('isAuthenticated');
    }
    return (
        <nav className="main-nav">
            <ul>
                {isHomePage ? (
                    <h2 className="text-xl font-semibold mb-0 text-emerald-700">{t("homeTitle")}</h2>
                ) : (
                    <>
                        <li>  <button onClick={toggleLanguage} className="bg-emerald-100 text-emerald-800 px-4 py-1 rounded">
                            {i18n.language === "uk" ? "EN" : "UA"}
                        </button></li>
                        <li><Link to="/biological-materials">{t("biologicalMaterials")}</Link></li>
                        <li><Link to="/donors">{t("donors")}</Link></li>
                        <li><Link to="/event-logs">{t("eventLogs")}</Link></li>
                        <li><Link to="/notifications">{t("notifications")}</Link></li>
                        <li><Link to="/storage-conditions">{t("storageConditions")}</Link></li>
                        <li><Link to="/users">{t("users")}</Link></li>
                        <li onClick={logout}><Link to="/">{t("logout")}</Link></li>
                    </>
                )}
            </ul>
        </nav>
    );
};

export default Navigation;
