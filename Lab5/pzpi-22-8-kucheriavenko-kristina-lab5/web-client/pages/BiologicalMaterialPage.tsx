import { useState, useEffect } from 'react';
import { AlertCircle, Edit, Trash2, X, Plus, Thermometer, Droplets, Wind } from 'lucide-react';
import {useTranslation} from "react-i18next";
import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";
import moment from "moment/moment";


export default function BiologicalMaterialsManagementPage() {
    const [materials, setMaterials] = useState([]);
    const [donors, setDonors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { t } = useTranslation();
    const [UseEuFormat, setUseEuFormat] = useState(true);
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
    const [isFormVisible, setIsFormVisible] = useState(false);
    const [editingMaterial, setEditingMaterial] = useState(null);
    const [formData, setFormData] = useState({
        materialName: '',
        expirationDate: '',
        status: 'AVAILABLE',
        transferDate: '',
        idealTemperature: '',
        idealOxygenLevel: '',
        idealHumidity: '',
        donorID: ''
    });


    const API_BASE_URL = 'http://localhost:8080/api/biological-materials';
    const DONORS_API_URL = 'http://localhost:8080/api/donors';

    useEffect(() => {
        fetchMaterials();
        fetchDonors();
    }, []);

    const fetchMaterials = async () => {
        try {
            setLoading(true);
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setMaterials(data);
            setError(null);
        } catch (err) {
            setError(`Failed to fetch biological materials: ${err.message}`);
            console.error("Error fetching biological materials:", err);
        } finally {
            setLoading(false);
        }
    };

    const fetchDonors = async () => {
        try {
            const response = await fetch(DONORS_API_URL);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setDonors(data);
        } catch (err) {
            console.error("Error fetching donors:", err);
        }
    };

    const fetchMaterialById = async (id) => {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (err) {
            setError(`Failed to fetch material: ${err.message}`);
            console.error(`Error fetching material ${id}:`, err);
            return null;
        }
    };

    const createMaterial = async () => {
        try {
            const userId = localStorage.getItem("userID");
            const materialData = {
                ...formData,
                donorID: { donorID: parseInt(formData.donorID) }
            };

            const response = await fetch(`${API_BASE_URL}/${userId}/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(materialData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchMaterials();
            resetForm();
        } catch (err) {
            setError(`Failed to create biological material: ${err.message}`);
            console.error("Error creating biological material:", err);
        }
    };

    const updateMaterial = async (id) => {
        try {
            const userId = localStorage.getItem("userID");
            const materialData = {
                ...formData,
                donorID: { donorID: parseInt(formData.donorID) }
            };

            const response = await fetch(`${API_BASE_URL}/${userId}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(materialData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchMaterials();
            resetForm();
        } catch (err) {
            setError(`Failed to update biological material: ${err.message}`);
            console.error(`Error updating biological material ${id}:`, err);
        }
    };
    const deleteMaterial = async (id) => {
        if (!window.confirm(t('delete_material'))) return;

        try {
            const userId = localStorage.getItem("userID");

            const response = await fetch(`${API_BASE_URL}/admin/${userId}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            await fetchMaterials();
        } catch (err) {
            setError(`Failed to delete biological material: ${err.message}`);
            console.error(`Error deleting biological material ${id}:`, err);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingMaterial) {
            updateMaterial(editingMaterial.materialID);
        } else {
            createMaterial();
        }
    };

    const startEdit = async (id) => {
        resetFormData();

        const material = await fetchMaterialById(id);
        if (material) {
            setEditingMaterial(material);

            const expirationDate = material.expirationDate ? new Date(material.expirationDate).toISOString().split('T')[0] : '';
            const transferDate = material.transferDate ? new Date(material.transferDate).toISOString().split('T')[0] : '';

            setFormData({
                materialName: material.materialName || '',
                expirationDate: expirationDate,
                status: material.status || 'AVAILABLE',
                transferDate: transferDate,
                idealTemperature: material.idealTemperature || '',
                idealOxygenLevel: material.idealOxygenLevel || '',
                idealHumidity: material.idealHumidity || '',
                donorID: material.donorID?.donorID || ''
            });

            setIsFormVisible(true);
        }
    };

    const resetFormData = () => {
        setFormData({
            materialName: '',
            expirationDate: '',
            status: 'AVAILABLE',
            transferDate: '',
            idealTemperature: '',
            idealOxygenLevel: '',
            idealHumidity: '',
            donorID: ''
        });
        setEditingMaterial(null);
    };

    const resetForm = () => {
        resetFormData();
        setIsFormVisible(false);
    };

    const showAddMaterialForm = () => {
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

    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);

        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();

        const datePart = UseEuFormat
            ? `${day}-${month}-${year}`
            : `${month}-${day}-${year}`;
        return `${datePart}`;
    };

    const getStatusClass = (status) => {
        switch(status) {
            case 'AVAILABLE': return 'bg-green-100 text-green-800';
            case 'DONATED': return 'bg-yellow-100 text-yellow-800';
            case 'DISPOSED': return 'bg-red-100 text-red-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    const getDonorName = (donorId) => {
        if (!donorId) return 'Unknown';
        const donor = donors.find(d => d.donorID === donorId);
        return donor ? `${donor.firstName} ${donor.lastName}` : 'Unknown';
    };

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    const sortedMaterials = [...materials].sort((a, b) => {
        if (!sortConfig.key) return 0;

        let aValue, bValue;

        switch (sortConfig.key) {
            case 'materialName':
                aValue = a.materialName.toLowerCase();
                bValue = b.materialName.toLowerCase();
                break;
            case 'donor':
                aValue = getDonorName(a.donorID?.donorID).toLowerCase();
                bValue = getDonorName(b.donorID?.donorID).toLowerCase();
                break;
            case 'expirationDate':
                aValue = new Date(a.expirationDate);
                bValue = new Date(b.expirationDate);
                break;
            case 'transferDate':
                aValue = new Date(a.transferDate);
                bValue = new Date(b.transferDate);
                break;
            case 'status':
                aValue = a.status.toLowerCase();
                bValue = b.status.toLowerCase();
                break;
            default:
                return 0;
        }

        if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
        return 0;
    });

    return (
        <div className="max-w-7xl mx-auto p-6 bg-gradient-to-br from-blue-50 to-indigo-50 min-h-screen rounded-lg shadow-md">
            <h3 className="text-3xl font-bold mb-8 text-blue-800">{t('biological_materials_management')}</h3>

            {error && <ErrorAlert message={error} />}

            <div className="mb-6">
                <button
                    onClick={isFormVisible ? resetForm : showAddMaterialForm}
                    className="bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white px-5 py-2 rounded-lg shadow transition flex items-center"
                >
                    {isFormVisible ? (
                        <X className="w-5 h-5 mr-2" />
                    ) : (
                        <Plus className="w-5 h-5 mr-2" />
                    )}
                    {isFormVisible ? t('hide_form') : t('add_biological_material')}
                </button>
                <button
                    type="button"
                    onClick={() => setUseEuFormat(prev => !prev)}
                    className="mb-4 bg-blue-100 text-blue-800 px-4 py-2 rounded shadow hover:bg-blue-200 transition"
                >
                    {UseEuFormat ? t('usa_format') : t('eu_format')}
                </button>
            </div>

            {isFormVisible && (
                <div className="bg-white p-6 rounded-xl shadow border border-blue-200 mb-8">
                    <h2 className="text-xl font-semibold mb-4 text-blue-700">
                        {editingMaterial ? t('edit_biological_material') : t('add_biological_material')}
                    </h2>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('material_name')}</label>
                                <input
                                    type="text"
                                    name="materialName"
                                    value={formData.materialName}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-400"
                                    required
                                    minLength={2}
                                    maxLength={100}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('donor')}</label>
                                <select
                                    name="donorID"
                                    value={formData.donorID}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-400"
                                    required
                                >
                                    <option value="">{t('select_donor')}</option>
                                    {donors.map(donor => (
                                        <option key={donor.donorID} value={donor.donorID}>
                                            {donor.firstName} {donor.lastName}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('transfer_date')}</label>
                                <Datetime
                                    value={formData.transferDate ? moment(formData.transferDate) : null}
                                    onChange={(date) =>
                                        setFormData((prev) => ({
                                            ...prev,
                                            transferDate: date?.toISOString() ?? ''
                                        }))
                                    }
                                    isValidDate={(currentDate) => currentDate.isSameOrBefore(moment(), 'day')}
                                    timeFormat={false}
                                    dateFormat={UseEuFormat ? "DD-MM-YYYY" : "MM-DD-YYYY"}
                                    inputProps={{
                                        className: "w-full px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-400",
                                        required: true
                                    }}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('expiration_date')}</label>
                                <Datetime
                                    value={formData.expirationDate ? moment(formData.expirationDate) : null}
                                    onChange={(date) =>
                                        setFormData((prev) => ({
                                            ...prev,
                                            expirationDate: date?.toISOString() ?? ''
                                        }))
                                    }
                                    timeFormat={false}
                                    isValidDate={(currentDate) => currentDate.isSameOrAfter(moment(), 'day')}
                                    dateFormat={UseEuFormat ? "DD-MM-YYYY": "MM-DD-YYYY"}
                                    inputProps={{
                                        className: "w-full px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-400",
                                        required: true
                                    }}
                                />
                            </div>



                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('status')}</label>
                                <select
                                    name="status"
                                    value={formData.status}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-400"
                                    required
                                >
                                    <option value="AVAILABLE">{t('available')}</option>
                                    <option value="DONATED">{t('donated')}</option>
                                    <option value="DISPOSED">{t('disposed')}</option>
                                </select>
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('ideal_temperature')}</label>
                                <input
                                    type="number"
                                    name="idealTemperature"
                                    value={formData.idealTemperature}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-400"
                                    required
                                    min="-100"
                                    max="100"
                                    step="0.1"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('ideal_oxygen_level')}</label>
                                <input
                                    type="number"
                                    name="idealOxygenLevel"
                                    value={formData.idealOxygenLevel}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-400"
                                    required
                                    min="0"
                                    max="100"
                                    step="0.1"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('ideal_humidity')}</label>
                                <input
                                    type="number"
                                    name="idealHumidity"
                                    value={formData.idealHumidity}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-400"
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
                                className="bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white px-5 py-2 rounded-lg shadow transition"
                            >
                                {editingMaterial ? t('update') : t('create')}
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

            <div className="bg-white rounded-xl shadow overflow-hidden">
                <table className="min-w-full divide-y divide-blue-200 border border-blue-300">
                    <thead className="bg-blue-100">
                    <tr>
                        <th onClick={() => handleSort('materialName')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-blue-700 uppercase border border-blue-200">
                            {t('material_name')} {sortConfig.key === 'materialName' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('donor')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-blue-700 uppercase border border-blue-200">
                            {t('donor')} {sortConfig.key === 'donor' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('expirationDate')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-blue-700 uppercase border border-blue-200">
                            {t('expiration_date')} {sortConfig.key === 'expirationDate' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('transferDate')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-blue-700 uppercase border border-blue-200">
                            {t('transfer_date')} {sortConfig.key === 'transferDate' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('status')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-blue-700 uppercase border border-blue-200">
                            {t('status')} {sortConfig.key === 'status' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th className="px-4 py-3 text-left text-xs font-bold text-blue-700 uppercase border border-blue-200">{t('storage_conditions')}</th>
                        <th className="px-4 py-3 text-center text-xs font-bold text-blue-700 uppercase border border-blue-200">{t('actions')}</th>
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-blue-100">
                    {loading ? (
                        <tr>
                            <td colSpan="7" className="text-center py-4 text-blue-500">{t('loading')}</td>
                        </tr>
                    ) : materials.length === 0 ? (
                        <tr>
                            <td colSpan="7" className="text-center py-4 text-gray-500">{t('no_materials_found')}</td>
                        </tr>
                    ) : (
                        sortedMaterials.map((material) => (
                            <tr key={material.materialID} className="hover:bg-blue-50 transition">
                                <td className="px-4 py-3 font-medium">{material.materialName}</td>
                                <td className="px-4 py-3">{getDonorName(material.donorID?.donorID)}</td>
                                <td className="px-4 py-3">{formatDate(material.expirationDate)}</td>
                                <td className="px-4 py-3">{formatDate(material.transferDate)}</td>
                                <td className="px-4 py-3">
                                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusClass(material.status)}`}>
                                        {t(material.status.toLowerCase())}
                                    </span>
                                </td>
                                <td className="px-4 py-3">
                                    <div className="flex flex-col space-y-1 text-xs">
                                        <div className="flex items-center">
                                            <Thermometer className="w-3 h-3 mr-1 text-blue-600" />
                                            <span>{material.idealTemperature}°C</span>
                                            <span className="px-1"></span>
                                            <span>{(material.idealTemperature * 9/5 + 32).toFixed(1)}°F</span>
                                        </div>
                                        <div className="flex items-center">
                                            <Droplets className="w-3 h-3 mr-1 text-blue-600" />
                                            <span>{material.idealOxygenLevel}% O₂</span>
                                        </div>
                                        <div className="flex items-center">
                                            <Wind className="w-3 h-3 mr-1 text-blue-600" />
                                            <span>{material.idealHumidity}%</span>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-4 py-3 text-center space-x-2">
                                    <button
                                        onClick={() => startEdit(material.materialID)}
                                        className="text-blue-600 hover:text-blue-800 bg-blue-50 hover:bg-blue-100 rounded px-2 py-1"
                                    >
                                        <Edit className="w-5 h-5 inline" />
                                    </button>
                                    <button
                                        onClick={() => deleteMaterial(material.materialID)}
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