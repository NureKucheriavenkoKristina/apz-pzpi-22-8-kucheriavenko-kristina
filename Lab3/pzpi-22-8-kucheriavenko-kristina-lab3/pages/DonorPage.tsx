import {useState, useEffect} from 'react';
import {AlertCircle, Edit, Trash2, X, UserPlus, HeartPulse} from 'lucide-react';
import {useTranslation} from "react-i18next";
import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";
import moment from "moment/moment";


export default function DonorsManagementPage() {
    const [donors, setDonors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const {t} = useTranslation();
    const [UseEuFormat, setUseEuFormat] = useState(true);
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
    const [isFormVisible, setIsFormVisible] = useState(false);
    const [editingDonor, setEditingDonor] = useState(null);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        birthDate: '',
        gender: 'MALE',
        idNumber: '',
        bloodType: 'A_POSITIVE',
        transplantRestrictions: ''
    });

    const API_BASE_URL = 'http://localhost:8080/api/donors';


    useEffect(() => {
        fetchDonors();
    }, []);

    const fetchDonors = async () => {
        try {
            setLoading(true);
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setDonors(data);
            setError(null);
        } catch (err) {
            setError(`Failed to fetch donors: ${err.message}`);
            console.error("Error fetching donors:", err);
        } finally {
            setLoading(false);
        }
    };

    const fetchDonorById = async (id) => {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (err) {
            setError(`Failed to fetch donor: ${err.message}`);
            console.error(`Error fetching donor ${id}:`, err);
            return null;
        }
    };

    const createDonor = async () => {
        try {
            const adminUserId = localStorage.getItem("userID");
            console.log(JSON.stringify(formData));
            const response = await fetch(`${API_BASE_URL}/admin/${adminUserId}/add`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchDonors();
            resetForm();
        } catch (err) {
            setError(`Failed to create donor: ${err.message}`);
            console.error("Error creating donor:", err);
        }
    };

    const updateDonor = async (id) => {
        try {
            const adminUserId = localStorage.getItem("userID");

            const response = await fetch(`${API_BASE_URL}/admin/${adminUserId}/${id}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchDonors();
            resetForm();
        } catch (err) {
            setError(`Failed to update donor: ${err.message}`);
            console.error(`Error updating donor ${id}:`, err);
        }
    };

    const deleteDonor = async (id) => {
        if (!window.confirm(t('delete_confirm'))) return;

        try {
            const adminUserId = localStorage.getItem("userID");

            const response = await fetch(`${API_BASE_URL}/admin/${adminUserId}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            await fetchDonors();
        } catch (err) {
            setError(`Failed to delete donor: ${err.message}`);
            console.error(`Error deleting donor ${id}:`, err);
        }
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData((prev) => ({...prev, [name]: value}));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingDonor) {
            updateDonor(editingDonor.donorID);
        } else {
            createDonor();
        }
    };

    const startEdit = async (id) => {
        resetFormData();

        const donor = await fetchDonorById(id);
        if (donor) {
            setEditingDonor(donor);

            const birthDate = donor.birthDate ? moment(donor.birthDate).toISOString() : '';

            setFormData({
                firstName: donor.firstName || '',
                lastName: donor.lastName || '',
                birthDate: birthDate,
                gender: donor.gender || 'MALE',
                idNumber: donor.idNumber || '',
                bloodType: donor.bloodType || 'A_POS',
                transplantRestrictions: donor.transplantRestrictions || ''
            });

            setIsFormVisible(true);
        }
    };

    const resetFormData = () => {
        setFormData({
            firstName: '',
            lastName: '',
            birthDate: moment().subtract(18, 'years').toISOString(),
            gender: 'MALE',
            idNumber: '',
            bloodType: 'A_POS',
            transplantRestrictions: ''
        });
        setEditingDonor(null);
    };

    const resetForm = () => {
        resetFormData();
        setIsFormVisible(false);
    };

    const showAddDonorForm = () => {
        resetFormData();
        setIsFormVisible(true);
    };

    const ErrorAlert = ({message}) => (
        <div className="bg-red-50 border border-red-200 text-red-800 rounded-md p-4 mb-4 flex items-center">
            <AlertCircle className="w-5 h-5 mr-2"/>
            <span>{message}</span>
            <button
                onClick={() => setError(null)}
                className="ml-auto text-red-600 hover:text-red-800"
            >
                <X className="w-5 h-5"/>
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

    const getBloodTypeDisplay = (bloodType: any) => {
        switch (bloodType) {
            case 'A_POS':
                return 'A+';
            case 'A_NEG':
                return 'A-';
            case 'B_POS':
                return 'B+';
            case 'B_NEG':
                return 'B-';
            case 'AB_POS':
                return 'AB+';
            case 'AB_NEG':
                return 'AB-';
            case 'O_POS':
                return 'O+';
            case 'O_NEG':
                return 'O-';
            default:
                return bloodType;
        }
    };
    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    const sortedDonors = [...donors].sort((a, b) => {
        if (!sortConfig.key) return 0;

        let aValue, bValue;

        switch (sortConfig.key) {
            case 'name':
                aValue = `${a.firstName} ${a.lastName}`.toLowerCase();
                bValue = `${b.firstName} ${b.lastName}`.toLowerCase();
                break;
            case 'birthDate':
                aValue = new Date(a.birthDate);
                bValue = new Date(b.birthDate);
                break;
            case 'gender':
                aValue = a.gender.toLowerCase();
                bValue = b.gender.toLowerCase();
                break;
            case 'idNumber':
                aValue = a.idNumber.toLowerCase();
                bValue = b.idNumber.toLowerCase();
                break;
            case 'bloodType':
                aValue = getBloodTypeDisplay(a.bloodType);
                bValue = getBloodTypeDisplay(b.bloodType);
                break;
            case 'transplantRestrictions':
                aValue = a.transplantRestrictions?.toLowerCase() || '';
                bValue = b.transplantRestrictions?.toLowerCase() || '';
                break;
            default:
                return 0;
        }

        if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
        return 0;
    });

    return (
        <div
            className="max-w-7xl mx-auto p-6 bg-gradient-to-br from-green-50 to-blue-50 min-h-screen rounded-lg shadow-md">
            <h3 className="text-3xl font-bold mb-8 text-green-800">{t("donor_management")}</h3>

            {/* Error display */}
            {error && <ErrorAlert message={error}/>}

            {/* Add button */}
            <div className="mb-6">
                <button
                    onClick={isFormVisible ? resetForm : showAddDonorForm}
                    className="bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white px-5 py-2 rounded-lg shadow transition flex items-center"
                >
                    {isFormVisible ? <X className="w-5 h-5 mr-2"/> : <UserPlus className="w-5 h-5 mr-2"/>}
                    {isFormVisible ? t("hide_form") : t("add_donor")}
                </button>
                <button
                    type="button"
                    onClick={() => setUseEuFormat(prev => !prev)}
                    className="mb-4 bg-green-100 text-green-800 px-4 py-2 rounded shadow hover:bg-green-200 transition"
                >
                    {UseEuFormat ? t('usa_format') : t('eu_format')}
                </button>
            </div>


            {/* Form */}
            {isFormVisible && (
                <div className="bg-white p-6 rounded-xl shadow border border-green-200 mb-8">
                    <h2 className="text-xl font-semibold mb-4 text-green-700">
                        {editingDonor ? t("edit_donor") : t("add_new_donor")}
                    </h2>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label
                                    className="block text-sm font-medium text-green-700 mb-1">{t("first_name")}</label>
                                <input
                                    type="text"
                                    name="firstName"
                                    value={formData.firstName}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-green-300 rounded-md focus:ring-2 focus:ring-green-400"
                                    required
                                    minLength={2}
                                    maxLength={100}
                                />
                            </div>
                            <div>
                                <label
                                    className="block text-sm font-medium text-green-700 mb-1">{t("last_name")}</label>
                                <input
                                    type="text"
                                    name="lastName"
                                    value={formData.lastName}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-green-300 rounded-md focus:ring-2 focus:ring-green-400"
                                    required
                                    minLength={2}
                                    maxLength={100}
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-green-700 mb-1">{t('birth_date')}</label>
                                <Datetime
                                    value={formData.birthDate ? moment(formData.birthDate) : moment().subtract(18, 'years')}

                                    onChange={(date) =>
                                        setFormData((prev) => ({
                                            ...prev,
                                            birthDate: date?.toISOString() ?? ''
                                        }))
                                    }
                                    isValidDate={(currentDate) => {
                                        const todayMinus18 = moment().subtract(18, 'years');
                                        return currentDate.isSameOrBefore(todayMinus18, 'day');
                                    }}
                                    timeFormat={false}
                                    dateFormat={UseEuFormat ? "DD-MM-YYYY": "MM-DD-YYYY"}
                                    inputProps={{
                                        className: "w-full px-4 py-2 border border-green-300 rounded-md focus:ring-2 focus:ring-green-400",
                                        required: true
                                    }}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-green-700 mb-1">{t("gender")}</label>
                                <select
                                    name="gender"
                                    value={formData.gender}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-green-300 rounded-md focus:ring-2 focus:ring-green-400"
                                    required
                                >
                                    <option value="MALE">{t("male")}</option>
                                    <option value="FEMALE">{t("female")}</option>
                                </select>
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label
                                    className="block text-sm font-medium text-green-700 mb-1">{t("id_number")}</label>
                                <input
                                    type="text"
                                    name="idNumber"
                                    value={formData.idNumber}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-green-300 rounded-md focus:ring-2 focus:ring-green-400"
                                    required
                                    pattern="^[A-Za-z0-9]{10}$"
                                    title={t("alphanumeric_characters")}
                                />
                                <p className="text-xs text-gray-500 mt-1">{t("alphanumeric_characters")}</p>
                            </div>
                            <div>
                                <label
                                    className="block text-sm font-medium text-green-700 mb-1">{t("blood_type")}</label>
                                <select
                                    name="bloodType"
                                    value={formData.bloodType}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-green-300 rounded-md focus:ring-2 focus:ring-green-400"
                                    required
                                >
                                    <option value="A_POS">A+</option>
                                    <option value="A_NEG">A-</option>
                                    <option value="B_POS">B+</option>
                                    <option value="B_NEG">B-</option>
                                    <option value="AB_POS">AB+</option>
                                    <option value="AB_NEG">AB-</option>
                                    <option value="O_POS">O+</option>
                                    <option value="O_NEG">O-</option>
                                </select>
                            </div>
                        </div>

                        <div>
                            <label
                                className="block text-sm font-medium text-green-700 mb-1">{t("transplant_restrictions")}</label>
                            <textarea
                                name="transplantRestrictions"
                                value={formData.transplantRestrictions}
                                onChange={handleChange}
                                className="w-full px-4 py-2 border border-green-300 rounded-md focus:ring-2 focus:ring-green-400 min-h-24"
                                maxLength={500}
                            ></textarea>
                            <p className="text-xs text-gray-500 mt-1">{formData.transplantRestrictions.length}/{t('500_characters')}</p>
                        </div>

                        <div className="flex space-x-3">
                            <button
                                type="submit"
                                className="bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white px-5 py-2 rounded-lg shadow transition"
                            >
                                {editingDonor ? t("update") : t("create")}
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
                <table className="min-w-full divide-y divide-green-200 border border-green-300">
                    <thead className="bg-green-100">
                    <tr>
                        <th onClick={() => handleSort('name')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-green-700 uppercase border border-green-200">
                            {t("name")} {sortConfig.key === 'name' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('birthDate')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-green-700 uppercase border border-green-200">
                            {t("birth_date")} {sortConfig.key === 'birthDate' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('gender')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-green-700 uppercase border border-green-200">
                            {t("gender")} {sortConfig.key === 'gender' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('idNumber')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-green-700 uppercase border border-green-200">
                            {t("id_number")} {sortConfig.key === 'idNumber' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('bloodType')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-green-700 uppercase border border-green-200">
                            {t("blood_type")} {sortConfig.key === 'bloodType' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('transplantRestrictions')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-green-700 uppercase border border-green-200">
                            {t("about_me")} {sortConfig.key === 'transplantRestrictions' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th className="px-4 py-3 text-center text-xs font-bold text-green-700 uppercase border border-green-200">{t("actions")}</th>
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-green-100">
                    {loading ? (
                        <tr>
                            <td colSpan="7" className="text-center py-4 text-green-500">{t("loading")}</td>
                        </tr>
                    ) : donors.length === 0 ? (
                        <tr>
                            <td colSpan="7" className="text-center py-4 text-gray-500">{t("no_donors_found")}</td>
                        </tr>
                    ) : (
                        sortedDonors.map((donor) => (
                            <tr key={donor.donorID} className="hover:bg-green-50 transition">
                                <td className="px-4 py-3">{`${donor.firstName} ${donor.lastName}`}</td>
                                <td className="px-4 py-3">{formatDate(donor.birthDate)}</td>
                                <td className="px-4 py-3">{t(`gender_list.${donor.gender}`)}</td>
                                <td className="px-4 py-3">{donor.idNumber}</td>
                                <td className="px-4 py-3">
                                    <span className="flex items-center font-medium text-green-700">
                                        <HeartPulse className="w-4 h-4 mr-1"/>
                                        {getBloodTypeDisplay(donor.bloodType)}
                                    </span>
                                </td>
                                <td className="px-4 py-3">{donor.transplantRestrictions}</td>
                                <td className="px-4 py-3 text-center space-x-2">
                                    <button
                                        onClick={() => startEdit(donor.donorID)}
                                        className="text-green-600 hover:text-green-800 bg-green-50 hover:bg-green-100 rounded px-2 py-1"
                                    >
                                        <Edit className="w-5 h-5 inline"/>
                                    </button>
                                    <button
                                        onClick={() => deleteDonor(donor.donorID)}
                                        className="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 rounded px-2 py-1"
                                    >
                                        <Trash2 className="w-5 h-5 inline"/>
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