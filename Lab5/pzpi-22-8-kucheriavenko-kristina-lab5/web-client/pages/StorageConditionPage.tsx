import { useState, useEffect } from 'react';
import { AlertCircle, Edit, Trash2, X, Plus, Thermometer, Droplets, Wind } from 'lucide-react';
import {useTranslation} from "react-i18next";
import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";
import moment from "moment";

export default function StorageConditionsManagementPage() {
    const [conditions, setConditions] = useState([]);
    const [materials, setMaterials] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { t } = useTranslation();
    const [UseEuFormat, setUseEuFormat] = useState(true);
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
    const [isFormVisible, setIsFormVisible] = useState(false);
    const [editingCondition, setEditingCondition] = useState(null);
    const [formData, setFormData] = useState({
        temperature: '',
        oxygenLevel: '',
        humidity: '',
        measurementTime:'',
        materialID: ''
    });

    const API_BASE_URL = 'http://localhost:8080/api/storage-conditions';
    const MATERIALS_API_URL = 'http://localhost:8080/api/biological-materials';

    useEffect(() => {
        fetchConditions();
        fetchMaterials();
    }, []);

    const fetchConditions = async () => {
        try {
            setLoading(true);
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setConditions(data);
            setError(null);
            console.log(data);
        } catch (err) {
            setError(`Failed to fetch storage conditions: ${err.message}`);
            console.error("Error fetching storage conditions:", err);
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
            console.error("Error fetching biological materials:", err);
        }
    };

    const fetchConditionById = async (id) => {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (err) {
            setError(`Failed to fetch condition: ${err.message}`);
            console.error(`Error fetching condition ${id}:`, err);
            return null;
        }
    };

    const createCondition = async () => {
        try {
            const userId = localStorage.getItem("userID");
            const conditionData = {
                ...formData,
                materialID: { materialID: parseInt(formData.materialID) }
            };

            const response = await fetch(`${API_BASE_URL}/admin/${userId}/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(conditionData)
            });
            console.log(conditionData);
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }
            await fetchConditions();
            resetForm();
        } catch (err) {
            setError(`Failed to create storage condition: ${err.message}`);
            console.error("Error creating storage condition:", err);
        }
    };

    const updateCondition = async (id) => {
        try {
            const userId = localStorage.getItem("userID");
            const conditionData = {
                ...formData,
                materialID: { materialID: parseInt(formData.materialID) }
            };

            const response = await fetch(`${API_BASE_URL}/admin/${userId}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(conditionData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchConditions();
            resetForm();
        } catch (err) {
            setError(`Failed to update storage condition: ${err.message}`);
            console.error(`Error updating storage condition ${id}:`, err);
        }
    };

    const deleteCondition = async (id) => {
        if (!window.confirm(t('delete_storage_condition_confirm'))) return;

        try {
            const userId = localStorage.getItem("userID");

            const response = await fetch(`${API_BASE_URL}/admin/${userId}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            await fetchConditions();
        } catch (err) {
            setError(`Failed to delete storage condition: ${err.message}`);
            console.error(`Error deleting storage condition ${id}:`, err);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingCondition) {
            updateCondition(editingCondition.recordID);
        } else {
            createCondition();
        }
    };

    const startEdit = async (id) => {
        resetFormData();

        const condition = await fetchConditionById(id);
        if (condition) {
            setEditingCondition(condition);

            const measurementTime = condition.measurementTime ? moment(condition.measurementTime).toISOString() : '';

            setFormData({
                temperature: condition.temperature || '',
                oxygenLevel: condition.oxygenLevel || '',
                humidity: condition.humidity || '',
                measurementTime: measurementTime,
                materialID: condition.materialID?.materialID || ''
            });

            setIsFormVisible(true);
        }
    };

    const resetFormData = () => {
        setFormData({
            temperature: '',
            oxygenLevel: '',
            humidity: '',
            measurementTime: moment().toISOString(),
            materialID: ''
        });
        setEditingCondition(null);
    };

    const resetForm = () => {
        resetFormData();
        setIsFormVisible(false);
    };

    const showAddConditionForm = () => {
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

    const getStorageZoneClass = (storage_zone) => {
        switch(storage_zone) {
            case 'RED': return 'bg-red-100 text-red-800';
            case 'YELLOW': return 'bg-yellow-100 text-yellow-800';
            case 'GREEN': return 'bg-green-100 text-green-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    const getMaterialName = (materialId) => {
        if (!materialId) return 'Unknown';
        const material = materials.find(m => m.materialID === materialId);
        return material ? material.materialName : 'Unknown';
    };

    const sortedConditions = [...conditions].sort((a, b) => {
        if (!sortConfig.key) return 0;

        let aValue = a[sortConfig.key];
        let bValue = b[sortConfig.key];

        if (sortConfig.key === 'materialName') {
            aValue = getMaterialName(a.materialID?.materialID);
            bValue = getMaterialName(b.materialID?.materialID);
        }

        if (sortConfig.key === 'measurementTime') {
            aValue = new Date(aValue);
            bValue = new Date(bValue);
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
        <div className="max-w-7xl mx-auto p-6 bg-gradient-to-br from-purple-50 to-violet-50 min-h-screen rounded-lg shadow-md">

            <h3 className="text-3xl font-bold mb-8 text-purple-800">{t("storage_conditions_management")}</h3>

            {/* Error display */}
            {error && <ErrorAlert message={error} />}

            {/* Add button */}
            <div className="mb-6">
                <button
                    onClick={isFormVisible ? resetForm : showAddConditionForm}
                    className="bg-gradient-to-r from-purple-500 to-violet-500 hover:from-purple-600 hover:to-violet-600 text-white px-5 py-2 rounded-lg shadow transition flex items-center"
                >
                    {isFormVisible ? (
                        <X className="w-5 h-5 mr-2" />
                    ) : (
                        <Plus className="w-5 h-5 mr-2" />
                    )}
                    {isFormVisible ? t("hide_form") : t("add_storage_condition")}
                </button>
                <button
                    type="button"
                    onClick={() => setUseEuFormat(prev => !prev)}
                    className="mb-4 bg-purple-100 text-purple-800 px-4 py-2 rounded shadow hover:bg-purple-200 transition"
                >
                    {UseEuFormat ? t('usa_format') : t('eu_format')}
                </button>
            </div>

            {/* Form */}
            {isFormVisible && (
                <div className="bg-white p-6 rounded-xl shadow border border-purple-200 mb-8">
                    <h2 className="text-xl font-semibold mb-4 text-purple-700">
                        {editingCondition ? t("edit_storage_condition") : t("add_new_storage_condition")}
                    </h2>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-purple-700 mb-1">{t("biological_material")}</label>
                                <select
                                    name="materialID"
                                    value={formData.materialID}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-purple-300 rounded-md focus:ring-2 focus:ring-purple-400"
                                    required
                                >
                                    <option value="">{t("select_material")}</option>
                                    {materials.map(material => (
                                        <option key={material.materialID} value={material.materialID}>
                                            {material.materialName}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                        <label className="block text-sm font-medium text-purple-700 mb-1">{t("measurement_time")} </label>
                        <Datetime
                            value={formData.measurementTime ? moment(formData.measurementTime) : null}
                            onChange={(date) =>
                                setFormData((prev) => ({
                                    ...prev,
                                    measurementTime: date?.toISOString() ?? ''
                                }))
                            }
                            isValidDate={(currentDate) => currentDate.isSameOrBefore(moment(), 'day')}
                            dateFormat={UseEuFormat ? "DD-MM-YYYY": "MM-DD-YYYY"}
                            timeFormat={UseEuFormat ? "HH:mm:ss" : "hh:mm:ss A"}
                            inputProps={{
                                name: "measurementTime",
                                required: true,
                                className: "w-full px-4 py-2 border border-purple-300 rounded-md focus:ring-2 focus:ring-purple-400"
                            }}
                        />


                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-purple-700 mb-1">{t("temperature")} </label>
                                <input
                                    type="number"
                                    name="temperature"
                                    value={formData.temperature}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-purple-300 rounded-md focus:ring-2 focus:ring-purple-400"
                                    required
                                    min="-100"
                                    max="100"
                                    step="0.1"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-purple-700 mb-1">{t("oxygen_level")}</label>
                                <input
                                    type="number"
                                    name="oxygenLevel"
                                    value={formData.oxygenLevel}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-purple-300 rounded-md focus:ring-2 focus:ring-purple-400"
                                    required
                                    min="0"
                                    max="100"
                                    step="0.1"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-purple-700 mb-1">{t("humidity")}</label>
                                <input
                                    type="number"
                                    name="humidity"
                                    value={formData.humidity}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-purple-300 rounded-md focus:ring-2 focus:ring-purple-400"
                                    required
                                    min="0"
                                    max="100"
                                    step="0.1"
                                />
                            </div>
                        </div>

                        <div className="flex space-x-3">
                            <button
                                type="submit"
                                className="bg-gradient-to-r from-purple-500 to-violet-500 hover:from-purple-600 hover:to-violet-600 text-white px-5 py-2 rounded-lg shadow transition"
                            >
                                {editingCondition ? t("update") : t("create")}
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
                <table className="min-w-full divide-y divide-purple-200 border border-purple-300">
                    <thead className="bg-purple-100">
                    <tr>
                        <th onClick={() => handleSort('materialName')} className="px-4 py-3 text-center text-xs font-bold text-purple-700 uppercase border border-purple-200">
                            {t('material_name')} {sortConfig.key === 'materialName' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('storage_zone')} className="px-4 py-3 text-center text-xs font-bold text-purple-700 uppercase border border-purple-200">
                            {t("storage_zone")} {sortConfig.key === 'storage_zone' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('measurementTime')} className="px-4 py-3 text-center text-xs font-bold text-purple-700 uppercase border border-purple-200">
                            {t("measurement_time")} {sortConfig.key === 'measurementTime' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('temperature')} className="px-4 py-3 text-center text-xs font-bold text-purple-700 uppercase border border-purple-200">
                            {t("temperature")} / (°F) {sortConfig.key === 'temperature' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('oxygenLevel')} className="px-4 py-3 text-center text-xs font-bold text-purple-700 uppercase border border-purple-200">
                            {t("oxygen_level")} {sortConfig.key === 'oxygenLevel' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('humidity')} className="px-4 py-3 text-center text-xs font-bold text-purple-700 uppercase border border-purple-200">
                            {t('humidity')} {sortConfig.key === 'humidity' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th className="px-4 py-3 text-center text-xs font-bold text-purple-700 uppercase border border-purple-200">
                            {t('actions')}
                        </th>
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-purple-100">
                    {loading ? (
                        <tr>
                            <td colSpan="7" className="text-center py-4 text-purple-500">{t("loading")}</td>
                        </tr>
                    ) : conditions.length === 0 ? (
                        <tr>
                            <td colSpan="7" className="text-center py-4 text-gray-500">{t("no_storage_conditions_found")}</td>
                        </tr>
                    ) : (
                        sortedConditions.map((condition) => (
                            <tr key={condition.recordID} className="hover:bg-purple-50 transition">
                                <td className="px-4 py-3 font-medium">{getMaterialName(condition.materialID?.materialID)}</td>
                                <td className="px-4 py-3">
                                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium> ${getStorageZoneClass(condition.storage_zone)}`}>
                                         {t(`actual_zone.${condition.storage_zone}`)}
                                    </span>
                                </td>
                                <td className="px-4 py-3 font-medium text-purple-700">{formatDateTime(condition.measurementTime)}</td>
                                <td className="px-4 py-3">
                                    <div className="flex items-center flex gap-2">
                                        <Thermometer className="w-4 h-4 mr-1 text-purple-600" />
                                        <span>{condition.temperature}°C</span>
                                        <span>{(condition.temperature * 9/5 + 32).toFixed(1)}°F</span>
                                    </div>
                                </td>
                                <td className="px-4 py-3">
                                    <div className="flex items-center">
                                        <Droplets className="w-4 h-4 mr-1 text-purple-600" />
                                        <span>{condition.oxygenLevel}%</span>
                                    </div>
                                </td>
                                <td className="px-4 py-3">
                                    <div className="flex items-center">
                                        <Wind className="w-4 h-4 mr-1 text-purple-600" />
                                        <span>{condition.humidity}%</span>
                                    </div>
                                </td>
                                <td className="px-4 py-3 text-center space-x-2">
                                    <button
                                        onClick={() => startEdit(condition.recordID)}
                                        className="text-purple-600 hover:text-purple-800 bg-purple-50 hover:bg-purple-100 rounded px-2 py-1"
                                    >
                                        <Edit className="w-5 h-5 inline" />
                                    </button>
                                    <button
                                        onClick={() => deleteCondition(condition.recordID)}
                                        className="text-purple-500 hover:text-purple-700 bg-purple-50 hover:bg-purple-100 rounded px-2 py-1"
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