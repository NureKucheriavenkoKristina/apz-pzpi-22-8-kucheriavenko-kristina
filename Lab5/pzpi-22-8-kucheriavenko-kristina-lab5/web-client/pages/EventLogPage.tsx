import { useState, useEffect } from 'react';
import { AlertCircle, Edit, Trash2, X, Plus, Clock, User, FileText } from 'lucide-react';
import {useTranslation} from "react-i18next";
import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";
import moment from "moment";

export default function EventLogManagementPage() {
    const [eventLogs, setEventLogs] = useState([]);
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { t } = useTranslation();
    const [UseEuFormat, setUseEuFormat] = useState(true);
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
    const [isFormVisible, setIsFormVisible] = useState(false);
    const [editingEventLog, setEditingEventLog] = useState(null);
    const [formData, setFormData] = useState({
        actionDetails: '',
        actionTime: new Date().toISOString().split('T')[0],
        creatorID: ''
    });

    const API_BASE_URL = 'http://localhost:8080/api/event-logs';
    const USERS_API_URL = 'http://localhost:8080/api/user';

    useEffect(() => {
        fetchEventLogs();
        fetchUsers();
    }, []);

    const userRole = localStorage.getItem('role');

    const fetchEventLogs = async () => {
        try {
            setLoading(true);
            const userId = localStorage.getItem("userID");
            const response = await fetch(`${API_BASE_URL}/admin/${userId}`);
            if (response.status === 403) {
                throw new Error('forbidden_access');
            }
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setEventLogs(data);
            setError(null);
        } catch (err) {
            setError(`${err.message}`);
            console.error("Error fetching event logs:", err);
        } finally {
            setLoading(false);
        }
    };

    const fetchUsers = async () => {
        try {
            const response = await fetch(USERS_API_URL);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setUsers(data);
        } catch (err) {
            console.error("Error fetching users:", err);
        }
    };

    const fetchEventLogById = async (id) => {
        try {
            const userId = localStorage.getItem("userID");
            const response = await fetch(`${API_BASE_URL}/admin/${userId}/${id}`);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (err) {
            setError(`Failed to fetch event log: ${err.message}`);
            console.error(`Error fetching event log ${id}:`, err);
            return null;
        }
    };

    const createEventLog = async () => {
        try {
            const userId = localStorage.getItem("userID");
            const eventLogData = {
                ...formData,
                creatorID: { userID: parseInt(formData.creatorID) }
            };
            console.log(eventLogData);
            const response = await fetch(`${API_BASE_URL}/admin/${userId}/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(eventLogData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchEventLogs();
            resetForm();
        } catch (err) {
            setError(`Failed to create event log: ${err.message}`);
            console.error("Error creating event log:", err);
        }
    };

    const updateEventLog = async (id) => {
        try {
            const userId = localStorage.getItem("userID");
            const eventLogData = {
                ...formData,
                eventLogID: id,
                creatorID: { userID: parseInt(formData.creatorID) }
            };

            const response = await fetch(`${API_BASE_URL}/admin/${userId}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(eventLogData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchEventLogs();
            resetForm();
        } catch (err) {
            setError(`Failed to update event log: ${err.message}`);
            console.error(`Error updating event log ${id}:`, err);
        }
    };

    const deleteEventLog = async (id) => {
        if (!window.confirm(t('delete_event_log_confirm'))) return;

        try {
            const userId = localStorage.getItem("userID");

            const response = await fetch(`${API_BASE_URL}/admin/${userId}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            await fetchEventLogs();
        } catch (err) {
            setError(`Failed to delete event log: ${err.message}`);
            console.error(`Error deleting event log ${id}:`, err);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingEventLog) {
            updateEventLog(editingEventLog.eventLogID);
        } else {
            createEventLog();
        }
    };

    const startEdit = async (id) => {
        resetFormData();

        const eventLog = await fetchEventLogById(id);
        if (eventLog) {
            setEditingEventLog(eventLog);

            const actionTime = eventLog.actionTime ? moment(eventLog.actionTime).toISOString() : '';

            setFormData({
                actionDetails: eventLog.actionDetails || '',
                actionTime: actionTime,
                creatorID: eventLog.creatorID?.userID || ''
            });

            setIsFormVisible(true);
        }
    };

    const resetFormData = () => {
        setFormData({
            actionDetails: '',
            actionTime: moment().toISOString(),
            creatorID: ''
        });
        setEditingEventLog(null);
    };

    const resetForm = () => {
        resetFormData();
        setIsFormVisible(false);
    };

    const showAddEventLogForm = () => {
        resetFormData();
        setIsFormVisible(true);
    };

    const ErrorAlert = ({ message }) => (
        <div className="bg-red-100 border border-red-400 text-red-800 rounded-md p-4 mb-4 flex items-center">
            <AlertCircle className="w-5 h-5 mr-2" />
            <span>{message}</span>
            <button
                onClick={() => setError(null)}
                className="ml-auto text-red-600 hover:text-red-800"
            >
                <X className="w-5 h-5" />
            </button>
        </div>
    );1

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

    const getUserName = (userId) => {
        if (!userId) return userId;
        const user = users.find(u => u.userID === userId);
        return user ? `${user.first_name} ${user.last_name}` : 'Unknown';
    };

    const sortedEventLogs = [...eventLogs].sort((a, b) => {
        if (!sortConfig.key) return 0;

        let aValue, bValue;

        switch (sortConfig.key) {
            case 'actionDetails':
                aValue = a.actionDetails.toLowerCase();
                bValue = b.actionDetails.toLowerCase();
                break;
            case 'actionTime':
                aValue = new Date(a.actionTime);
                bValue = new Date(b.actionTime);
                break;
            case 'creator':
                aValue = getUserName(a.creatorID?.userID).toLowerCase();
                bValue = getUserName(b.creatorID?.userID).toLowerCase();
                break;
            default:
                return 0;
        }

        if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
        return 0;
    });

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };
    return (
        <div className="max-w-7xl mx-auto p-6 bg-gradient-to-br from-red-50 to-pink-50 min-h-screen rounded-lg shadow-md">
            <h3 className="text-3xl font-bold mb-8 text-red-800">{t("event_log_management")}</h3>

            {/* Error display */}
            {error && <ErrorAlert message={t(error)} />}

            {/* Add button */}
            {userRole === 'FULL' && (
            <div className="mb-6">
                <button
                    onClick={isFormVisible ? resetForm : showAddEventLogForm}
                    className="bg-gradient-to-r from-red-500 to-pink-500 hover:from-red-600 hover:to-pink-600 text-white px-5 py-2 rounded-lg shadow transition flex items-center"
                >
                    {isFormVisible ? (
                        <X className="w-5 h-5 mr-2" />
                    ) : (
                        <Plus className="w-5 h-5 mr-2" />
                    )}
                    {isFormVisible ? t("hide_form") : t("add_event_log")}
                </button>
                <button
                    type="button"
                    onClick={() => setUseEuFormat(prev => !prev)}
                    className="mb-4 bg-red-100 text-red-800 px-4 py-2 rounded shadow hover:bg-red-200 transition"
                >
                    {UseEuFormat ? t('usa_format') : t('eu_format')}
                </button>
            </div>)}

            {/* Form */}
            {isFormVisible && (
                <div className="bg-white p-6 rounded-xl shadow border border-red-200 mb-8">
                    <h2 className="text-xl font-semibold mb-4 text-red-700">
                        {editingEventLog ? t("edit_event_log") : t("add_new_event_log")}
                    </h2>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-red-700 mb-1">{t("action_details")}</label>
                            <textarea
                                name="actionDetails"
                                value={formData.actionDetails}
                                onChange={handleChange}
                                className="w-full px-4 py-2 border border-red-300 rounded-md focus:ring-2 focus:ring-red-400"
                                required
                                minLength={2}
                                maxLength={1000}
                                rows={4}
                            />
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-red-700 mb-1">{t("action_time")}</label>
                                <Datetime
                                    className="w-full px-4 py-2 border border-red-300 rounded-md focus:ring-2 focus:ring-red-400"
                                    value={formData.actionTime ? moment(formData.actionTime) : null}
                                    onChange={(date) =>
                                        setFormData((prev) => ({
                                            ...prev,
                                            actionTime: moment(date).format("DD-MM-YYYYTHH:mm:ss"),
                                        }))
                                    }
                                    isValidDate={(currentDate) => currentDate.isSameOrBefore(moment(), 'day')}
                                    dateFormat={UseEuFormat ? "DD-MM-YYYY": "MM-DD-YYYY"}
                                    timeFormat={UseEuFormat ? "HH:mm:ss" : "hh:mm:ss A"}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-red-700 mb-1">{t("creator")}</label>
                                <select
                                    name="creatorID"
                                    value={formData.creatorID}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-red-300 rounded-md focus:ring-2 focus:ring-red-400"
                                    required
                                >
                                    <option value="">{t("select_user")}</option>
                                    {users.map(user => (
                                        <option key={user.userID} value={user.userID}>
                                            {user.first_name} {user.last_name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div className="flex space-x-3">
                            <button
                                type="submit"
                                className="bg-gradient-to-r from-red-500 to-pink-500 hover:from-red-600 hover:to-pink-600 text-white px-5 py-2 rounded-lg shadow transition"
                            >
                                {editingEventLog ? t('update') : t('create')}
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
                <table className="min-w-full divide-y divide-red-200 border border-red-300">
                    <thead className="bg-red-100">
                    <tr>
                        <th onClick={() => handleSort('actionDetails')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-red-700 uppercase border border-red-200">
                            {t("action_details")} {sortConfig.key === 'actionDetails' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('actionTime')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-red-700 uppercase border border-red-200">
                            {t("action_time")} {sortConfig.key === 'actionTime' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('creator')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-red-700 uppercase border border-red-200">
                            {t("creator")} {sortConfig.key === 'creator' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th className="px-4 py-3 text-center text-xs font-bold text-red-700 uppercase border border-red-200">{t('actions')}</th>
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-red-100">
                    {loading ? (
                        <tr>
                            <td colSpan="5" className="text-center py-4 text-red-500">{t("loading")}</td>
                        </tr>
                    ) : eventLogs.length === 0 ? (
                        <tr>
                            <td colSpan="5" className="text-center py-4 text-gray-500">No event logs found</td>
                        </tr>
                    ) : (
                        sortedEventLogs.map((eventLog) => (
                            <tr key={eventLog.eventLogID} className="hover:bg-red-50 transition">
                                <td className="px-4 py-3">
                                    <div className="flex items-center">
                                        <FileText className="w-4 h-4 mr-2 text-red-600" />
                                        <span className="whitespace-pre-line">{eventLog.actionDetails}</span>
                                    </div>
                                </td>
                                <td className="px-4 py-3">
                                    <div className="flex items-center">
                                        <Clock className="w-4 h-4 mr-2 text-red-600" />
                                        <span className="whitespace-nowrap font-medium text-red-700">{formatDateTime(eventLog.actionTime)}</span>
                                    </div>
                                </td>
                                <td className="px-4 py-3">
                                    <div className="flex items-center">
                                        <User className="w-4 h-4 mr-2 text-red-600" />
                                        <span>{getUserName(eventLog.creatorID?.userID)}</span>
                                    </div>
                                </td>

                                <td className="px-4 py-3 text-center space-x-2">
                                    <button
                                        onClick={() => startEdit(eventLog.eventLogID)}
                                        className="text-red-600 hover:text-red-800 bg-red-50 hover:bg-red-100 rounded px-2 py-1"
                                    >
                                        <Edit className="w-5 h-5 inline" />
                                    </button>
                                    <button
                                        onClick={() => deleteEventLog(eventLog.eventLogID)}
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