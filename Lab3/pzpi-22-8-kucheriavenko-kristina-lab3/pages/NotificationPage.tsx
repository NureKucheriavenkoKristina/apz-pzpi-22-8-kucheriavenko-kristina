import { useState, useEffect } from 'react';
import {AlertCircle, Edit, Trash2, X, Bell, Clock} from 'lucide-react';
import {useTranslation} from "react-i18next";
import moment from "moment/moment";
import Datetime from "react-datetime";

export default function NotificationsManagementPage() {
    const [notifications, setNotifications] = useState([]);
    const [materials, setMaterials] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { t } = useTranslation();
    const [UseEuFormat, setUseEuFormat] = useState(true);
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
    const [isFormVisible, setIsFormVisible] = useState(false);
    const [editingNotification, setEditingNotification] = useState(null);
    const [formData, setFormData] = useState({
        eventType: '',
        details: '',
        notificationTime: '',
        materialID: ''
    });

    const API_BASE_URL = 'http://localhost:8080/api/notifications';
    const MATERIALS_API_URL = 'http://localhost:8080/api/biological-materials';

    useEffect(() => {
        fetchNotifications();
        fetchMaterials();
    }, []);

    const fetchNotifications = async () => {
        try {
            setLoading(true);
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setNotifications(data);
            setError(null);
        } catch (err) {
            setError(`Failed to fetch notifications: ${err.message}`);
            console.error("Error fetching notifications:", err);
        } finally {
            setLoading(false);
        }
    };

    const fetchMaterials = async () => {
        try {
            const response = await fetch(MATERIALS_API_URL);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setMaterials(data);
        } catch (err) {
            console.error("Error fetching materials:", err);
            setError(`Failed to fetch materials: ${err.message}`);
        }
    };

    const fetchNotificationById = async (id) => {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (err) {
            setError(`Failed to fetch notification: ${err.message}`);
            console.error(`Error fetching notification ${id}:`, err);
            return null;
        }
    };

    const createNotification = async () => {
        try {
            const adminUserId = localStorage.getItem("userID");
            const notificationData = {
                ...formData,
                materialID: { materialID: parseInt(formData.materialID) }
            };
            const response = await fetch(`${API_BASE_URL}/admin/${adminUserId}/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(notificationData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchNotifications();
            resetForm();
        } catch (err) {
            setError(`Failed to create notification: ${err.message}`);
            console.error("Error creating notification:", err);
        }
    };

    const updateNotification = async (id) => {
        try {
            const notificationData = {
                ...formData,
                materialID: { materialID: parseInt(formData.materialID) }
            };
            const adminUserId = localStorage.getItem("userID");
            console.log(formData);
            const response = await fetch(`${API_BASE_URL}/admin/${adminUserId}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(notificationData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchNotifications();
            resetForm();
        } catch (err) {
            setError(`Failed to update notification: ${err.message}`);
            console.error(`Error updating notification ${id}:`, err);
        }
    };

    const deleteNotification = async (id) => {
        if (!window.confirm(t('delete_notification_confirm'))) return;

        try {
            const adminUserId = localStorage.getItem("userID");

            const response = await fetch(`${API_BASE_URL}/admin/${adminUserId}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            await fetchNotifications();
        } catch (err) {
            setError(`Failed to delete notification: ${err.message}`);
            console.error(`Error deleting notification ${id}:`, err);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingNotification) {
            updateNotification(editingNotification.notificationID);
        } else {
            createNotification();
        }
    };

    const startEdit = async (id) => {
        resetFormData();

        const notification = await fetchNotificationById(id);
        if (notification) {
            setEditingNotification(notification);

            const notificationTime = notification.notificationTime ? moment(notification.notificationTime).toISOString() : '';

            setFormData({
                eventType: notification.eventType || '',
                details: notification.details || '',
                notificationTime: notificationTime,
                materialID: notification.materialID?.materialID || ''
            });

            setIsFormVisible(true);
        }
    };

    const resetFormData = () => {
        const now = new Date();
        const isoWithSeconds = now.toISOString().slice(0, 19);
        setFormData({
            eventType: '',
            details: '',
            notificationTime: isoWithSeconds,
            materialID: ''
        });
        setEditingNotification(null);
    };

    const resetForm = () => {
        resetFormData();
        setIsFormVisible(false);
    };

    const showAddNotificationForm = () => {
        resetFormData();
        setIsFormVisible(true);
    };

    const ErrorAlert = ({ message }) => (
        <div className="bg-red-50 border border-red-200 text-red-800 rounded-md p-4 mb-4 flex items-center">
            <AlertCircle className="w-5 h-5 mr-2" />
            <span>{message}</span>
            <button
                onClick={() => setError(null)}
                className="ml-auto text-red-600 hover:text-red-800"
            >
                <X className="w-5 h-5" />
            </button>
        </div>
    );

    const formatDateTime = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);

        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();

        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');

        const datePart = UseEuFormat
            ? `${day}-${month}-${year}`
            : `${month}-${day}-${year}`;

        const timePart = UseEuFormat
            ? `${hours}:${minutes}:${seconds}`
            : `${((+hours % 12) || 12)}:${minutes}:${seconds} ${+hours < 12 ? 'AM' : 'PM'}`;

        const dayUTC = String(date.getUTCDate()).padStart(2, '0');
        const monthUTC = String(date.getUTCMonth() + 1).padStart(2, '0');
        const yearUTC = date.getUTCFullYear();

        const hoursUTC = String(date.getUTCHours()).padStart(2, '0');
        const minutesUTC = String(date.getUTCMinutes()).padStart(2, '0');
        const secondsUTC = String(date.getUTCSeconds()).padStart(2, '0');

        const datePartUTC = UseEuFormat
            ? `${dayUTC}-${monthUTC}-${yearUTC}`
            : `${monthUTC}-${dayUTC}-${yearUTC}`;

        const timePartUTC = UseEuFormat
            ? `${hoursUTC}:${minutesUTC}:${secondsUTC}`
            : `${((+hoursUTC % 12) || 12)}:${minutesUTC}:${secondsUTC} ${+hoursUTC < 12 ? 'AM' : 'PM'}`;

        const localTime = `${datePart} ${timePart}`;
        const utcTime = `${datePartUTC} ${timePartUTC}`;

        return <>
            <span>{t("locale")}: {localTime}</span><br/>
            <span>UTC: {utcTime}</span>
        </>;
    };

    const getMaterialName = (materialObj) => {
        if (!materialObj) return 'N/A';
        const material = materials.find(m => m.materialID === materialObj.materialID);
        return material ? material.materialName + ` (ID: ${material.materialID})`: materialObj.materialName || 'Unknown';
    };

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    const sortedNotifications = [...notifications].sort((a, b) => {
        if (!sortConfig.key) return 0;

        let aValue, bValue;

        switch (sortConfig.key) {
            case 'eventType':
                aValue = a.eventType.toLowerCase();
                bValue = b.eventType.toLowerCase();
                break;
            case 'notificationTime':
                aValue = new Date(a.notificationTime);
                bValue = new Date(b.notificationTime);
                break;
            case 'material':
                aValue = getMaterialName(a.materialID).toLowerCase();
                bValue = getMaterialName(b.materialID).toLowerCase();
                break;
            case 'details':
                aValue = a.details?.toLowerCase() || '';
                bValue = b.details?.toLowerCase() || '';
                break;
            default:
                return 0;
        }

        if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
        return 0;
    });

    return (
        <div className="max-w-7xl mx-auto p-6 bg-gradient-to-br from-yellow-50 to-amber-50 min-h-screen rounded-lg shadow-md">
            <h3 className="text-3xl font-bold mb-8 text-yellow-800">{t("notification_management")}</h3>

            {/* Error display */}
            {error && <ErrorAlert message={error} />}

            {/* Add button */}
            <div className="mb-6">
                <button
                    onClick={isFormVisible ? resetForm : showAddNotificationForm}
                    className="bg-gradient-to-r from-yellow-500 to-amber-500 hover:from-yellow-600 hover:to-amber-600 text-white px-5 py-2 rounded-lg shadow transition flex items-center"
                >
                    {isFormVisible ? (
                        <X className="w-5 h-5 mr-2" />
                    ) : (
                        <Bell className="w-5 h-5 mr-2" />
                    )}
                    {isFormVisible ? t("hide_form") : t("add_notification")}
                </button>
                <button
                    type="button"
                    onClick={() => setUseEuFormat(prev => !prev)}
                    className="mb-4 bg-yellow-100 text-yellow-800 px-4 py-2 rounded shadow hover:bg-yellow-200 transition"
                >
                    {UseEuFormat ? t('usa_format') : t('eu_format')}
                </button>
            </div>

            {/* Form */}
            {isFormVisible && (
                <div className="bg-white p-6 rounded-xl shadow border border-yellow-200 mb-8">
                    <h2 className="text-xl font-semibold mb-4 text-yellow-700">
                        {editingNotification ? t("edit_notification") : t("add_new_notification")}
                    </h2>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-yellow-700 mb-1">{t("event_type")}</label>
                                <input
                                    type="text"
                                    name="eventType"
                                    value={formData.eventType}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-yellow-300 rounded-md focus:ring-2 focus:ring-yellow-400"
                                    required
                                    minLength={2}
                                    maxLength={500}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-yellow-700 mb-1">{t("notification_time")}</label>
                                <Datetime
                                    className="w-full px-4 py-2 border border-yellow-300 rounded-md focus:ring-2 focus:ring-yellow-400"
                                    value={formData.notificationTime ? moment(formData.notificationTime) : null}
                                    onChange={(date) =>
                                        setFormData((prev) => ({
                                            ...prev,
                                            notificationTime: moment(date).toISOString(),
                                        }))
                                    }
                                    isValidDate={(currentDate) => currentDate.isSameOrBefore(moment(), 'day')}
                                    dateFormat={UseEuFormat ? "DD-MM-YYYY": "MM-DD-YYYY"}
                                    timeFormat={UseEuFormat ? "HH:mm:ss" : "hh:mm:ss A"}
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-yellow-700 mb-1">{t("biological_material")}</label>
                            <select
                                name="materialID"
                                value={formData.materialID}
                                onChange={handleChange}
                                className="w-full px-4 py-2 border border-yellow-300 rounded-md focus:ring-2 focus:ring-yellow-400"
                                required
                            >
                                <option value="">{t("select_material")}</option>
                                {materials.map((material) => (
                                    <option key={material.materialID} value={material.materialID}>
                                        {material.materialName}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-yellow-700 mb-1">{t("details")}</label>
                            <textarea
                                name="details"
                                value={formData.details}
                                onChange={handleChange}
                                className="w-full px-4 py-2 border border-yellow-300 rounded-md focus:ring-2 focus:ring-yellow-400 min-h-24"
                                required
                                minLength={5}
                                maxLength={500}
                            ></textarea>
                            <p className="text-xs text-gray-500 mt-1">{formData.details.length}/{t("500_characters")}</p>
                        </div>

                        <div className="flex space-x-3">
                            <button
                                type="submit"
                                className="bg-gradient-to-r from-yellow-500 to-amber-500 hover:from-yellow-600 hover:to-amber-600 text-white px-5 py-2 rounded-lg shadow transition"
                            >
                                {editingNotification ? t("update") : t("create")}
                            </button>
                            <button
                                type="button"
                                onClick={resetForm}
                                className="bg-gradient-to-r from-gray-500 to-gray-600 hover:from-gray-600 hover:to-gray-700 text-white px-5 py-2 rounded-lg shadow transition"
                            >
                                {t("cancel")}
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Table */}
            <div className="bg-white rounded-xl shadow overflow-hidden">
                <table className="min-w-full divide-y divide-yellow-200 border border-yellow-300">
                    <thead className="bg-yellow-100">
                    <tr>
                        <th onClick={() => handleSort('eventType')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-yellow-700 uppercase border border-yellow-200">
                            {t("event_type")} {sortConfig.key === 'eventType' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('notificationTime')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-yellow-700 uppercase border border-yellow-200">
                            {t("notification_time")} {sortConfig.key === 'notificationTime' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('material')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-yellow-700 uppercase border border-yellow-200">
                            {t("material")} {sortConfig.key === 'material' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('details')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-yellow-700 uppercase border border-yellow-200">
                            {t("details")} {sortConfig.key === 'details' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th className="px-4 py-3 text-center text-xs font-bold text-yellow-700 uppercase border border-yellow-200">{t('actions')}</th>
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-yellow-100">
                    {loading ? (
                        <tr>
                            <td colSpan="5" className="text-center py-4 text-yellow-500">{t("loading")}</td>
                        </tr>
                    ) : notifications.length === 0 ? (
                        <tr>
                            <td colSpan="5" className="text-center py-4 text-gray-500">{t("no_notifications_found")}</td>
                        </tr>
                    ) : (
                        sortedNotifications.map((notification) => (
                            <tr key={notification.notificationID} className="hover:bg-yellow-50 transition">
                                <td className="px-4 py-3">{notification.eventType}</td>
                                <td className="px-4 py-3">
                                    <span className="flex items-center font-medium text-yellow-700">
                                        <Clock className="w-4 h-4 mr-1"/>
                                        <span className="px-4 py-3">{formatDateTime(notification.notificationTime)}</span>
                                    </span>
                                </td>
                                <td className="px-4 py-3">{getMaterialName(notification.materialID)}</td>
                                <td className="px-4 py-3">
                                    <div className="max-w-xs truncate">{notification.details}</div>
                                </td>
                                <td className="px-4 py-3 text-center space-x-2">
                                    <button
                                        onClick={() => startEdit(notification.notificationID)}
                                        className="text-yellow-600 hover:text-yellow-800 bg-yellow-50 hover:bg-yellow-100 rounded px-2 py-1"
                                    >
                                        <Edit className="w-5 h-5 inline" />
                                    </button>
                                    <button
                                        onClick={() => deleteNotification(notification.notificationID)}
                                        className="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 rounded px-2 py-1"
                                    >
                                        <Trash2 className="w-5 h-5 inline" />
                                    </button>
                                </td>
                            </tr>
                        ))
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}