import { useState, useEffect } from 'react';
import { AlertCircle, Edit, Trash2, X, UserPlus, Shield } from 'lucide-react';
import {useTranslation} from "react-i18next";

export default function UsersManagementPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const {t} = useTranslation();
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
    const [isFormVisible, setIsFormVisible] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [formData, setFormData] = useState({
        first_name: '',
        last_name: '',
        role: '',
        access_rights: 'READ_ONLY',
        login: '',
        password: ''
    });

    const API_BASE_URL = 'http://localhost:8080/api/user';

    const userRole = localStorage.getItem('role');

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            setUsers(data);
            setError(null);
        } catch (err) {
            setError(`Failed to fetch users: ${err.message}`);
            console.error("Error fetching users:", err);
        } finally {
            setLoading(false);
        }
    };

    const fetchUserById = async (id) => {
        try {
            const response = await fetch(`${API_BASE_URL}/id/${id}`);
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (err) {
            setError(`Failed to fetch user: ${err.message}`);
            console.error(`Error fetching user ${id}:`, err);
            return null;
        }
    };

    const createUser = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/admin/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchUsers();
            resetForm();
        } catch (err) {
            setError(`Failed to create user: ${err.message}`);
            console.error("Error creating user:", err);
        }
    };

    const updateUser = async (id) => {
        try {
            const adminUserId = localStorage.getItem("userID");

            const response = await fetch(`${API_BASE_URL}/admin/${adminUserId}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData?.details || `Error: ${response.status}`);
            }

            await fetchUsers();
            resetForm();
        } catch (err) {
            setError(`Failed to update user: ${err.message}`);
            console.error(`Error updating user ${id}:`, err);
        }
    };

    const deleteUser = async (id) => {
        if (!window.confirm(t('delete_user_confirm'))) return;

        try {
            const adminUserId = localStorage.getItem("userID");

            const response = await fetch(`${API_BASE_URL}/admin/${adminUserId}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            await fetchUsers();
        } catch (err) {
            setError(`Failed to delete user: ${err.message}`);
            console.error(`Error deleting user ${id}:`, err);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingUser) {
            updateUser(editingUser.userID);
        } else {
            createUser();
        }
    };

    const startEdit = async (id) => {
        resetFormData();

        const user = await fetchUserById(id);
        if (user) {
            setEditingUser(user);
            setFormData({
                first_name: user.first_name || '',
                last_name: user.last_name || '',
                role: user.role || '',
                access_rights: user.access_rights || 'READ_ONLY',
                login: user.login || '',
                password: user.password
            });

            setIsFormVisible(true);
        }
    };

    const resetFormData = () => {
        setFormData({
            first_name: '',
            last_name: '',
            role: '',
            access_rights: 'READ_ONLY',
            login: '',
            password: ''
        });
        setEditingUser(null);
    };

    const resetForm = () => {
        resetFormData();
        setIsFormVisible(false);
    };

    const showAddUserForm = () => {
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

    const getAccessRightColor = (access) => {
        switch (access) {
            case 'FULL':
                return 'text-green-600';
            case 'READ_ALL':
                return 'text-orange-600';
            case 'READ_ONLY':
                return 'text-red-600';
            default:
                return 'text-grey-600';
        }
    };

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    const sortedUsers = [...users].sort((a, b) => {
        if (!sortConfig.key) return 0;

        let aValue, bValue;

        switch (sortConfig.key) {
            case 'name':
                aValue = `${a.first_name} ${a.last_name}`.toLowerCase();
                bValue = `${b.first_name} ${b.last_name}`.toLowerCase();
                break;
            case 'role':
                aValue = a.role?.toLowerCase() || '';
                bValue = b.role?.toLowerCase() || '';
                break;
            case 'access_rights':
                aValue = a.access_rights?.toLowerCase() || '';
                bValue = b.access_rights?.toLowerCase() || '';
                break;
            case 'login':
                aValue = a.login?.toLowerCase() || '';
                bValue = b.login?.toLowerCase() || '';
                break;
            default:
                return 0;
        }

        if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
        return 0;
    });

    return (
        <div className="max-w-7xl mx-auto p-6 bg-gradient-to-br from-cyan-50 to-green-50 min-h-screen rounded-lg shadow-md">
            <h3 className="text-3xl font-bold mb-8 text-cyan-800">{t("user_management")}</h3>

            {/* Error display */}
            {error && <ErrorAlert message={error} />}

            {/* Add button */}
            <div className="mb-6">
                <button
                    onClick={isFormVisible ? resetForm : showAddUserForm}
                    className="bg-gradient-to-r from-cyan-500 to-cyan-600 hover:from-cyan-600 hover:to-cyan-700 text-white px-5 py-2 rounded-lg shadow transition flex items-center"
                >
                    {isFormVisible ? (
                        <X className="w-5 h-5 mr-2" />
                    ) : (
                        <UserPlus className="w-5 h-5 mr-2" />
                    )}
                    {isFormVisible ? t('hide_form') : t('add_user')}
                </button>
            </div>

            {/* Form */}
            {isFormVisible && (
                <div className="bg-white p-6 rounded-xl shadow border border-cyan-200 mb-8">
                    <h2 className="text-xl font-semibold mb-4 text-cyan-700">
                        {editingUser ? t("edit_user") : t("add_new_user")}
                    </h2>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-cyan-700 mb-1">{t("first_name")}</label>
                                <input
                                    type="text"
                                    name="first_name"
                                    value={formData.first_name}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-cyan-300 rounded-md focus:ring-2 focus:ring-cyan-400"
                                    required
                                    minLength={2}
                                    maxLength={255}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-cyan-700 mb-1">{t("last_name")}</label>
                                <input
                                    type="text"
                                    name="last_name"
                                    value={formData.last_name}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-cyan-300 rounded-md focus:ring-2 focus:ring-cyan-400"
                                    required
                                    minLength={2}
                                    maxLength={255}
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-cyan-700 mb-1">{t("role")}</label>
                                <input
                                    type="text"
                                    name="role"
                                    value={formData.role}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-cyan-300 rounded-md focus:ring-2 focus:ring-cyan-400"
                                    required
                                    minLength={2}
                                    maxLength={255}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-cyan-700 mb-1">{t("access_rights")}</label>
                                <select
                                    name="access_rights"
                                    value={formData.access_rights}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-cyan-300 rounded-md focus:ring-2 focus:ring-cyan-400"
                                    required
                                >
                                    <option value="READ_ONLY">{t('read_only')}</option>
                                    <option value="READ_ALL">{t('read_all')}</option>
                                    <option value="FULL">{t('full_access')}</option>
                                </select>
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-cyan-700 mb-1">{t("login")}</label>
                            <input
                                type="email"
                                name="login"
                                value={formData.login}
                                onChange={handleChange}
                                className="w-full px-4 py-2 border border-cyan-300 rounded-md focus:ring-2 focus:ring-cyan-400"
                                required
                                minLength={2}
                                maxLength={100}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-cyan-700 mb-1">
                                {t("password")}
                            </label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                className="w-full px-4 py-2 border border-cyan-300 rounded-md focus:ring-2 focus:ring-cyan-400"
                                required={!editingUser}
                                minLength={6}
                            />
                        </div>

                        <div className="flex space-x-3">
                            <button
                                type="submit"
                                className="bg-gradient-to-r from-cyan-500 to-cyan-600 hover:from-cyan-600 hover:to-cyan-700 text-white px-5 py-2 rounded-lg shadow transition"
                            >
                                {editingUser ? t("update") : t("create")}
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
                <table className="min-w-full divide-y divide-cyan-200 border border-cyan-300">
                    <thead className="bg-cyan-100">
                    <tr>
                        <th onClick={() => handleSort('name')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-cyan-700 uppercase border border-cyan-200">
                            {t('name')} {sortConfig.key === 'name' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('role')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-cyan-700 uppercase border border-cyan-200">
                            {t('role')} {sortConfig.key === 'role' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('access_rights')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-cyan-700 uppercase border border-cyan-200">
                            {t('access_rights')} {sortConfig.key === 'access_rights' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        <th onClick={() => handleSort('login')} className="cursor-pointer px-4 py-3 text-left text-xs font-bold text-cyan-700 uppercase border border-cyan-200">
                            {t("login")} {sortConfig.key === 'login' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : ''}
                        </th>
                        {userRole === 'FULL' && (
                        <th className="px-4 py-3 text-center text-xs font-bold text-cyan-700 uppercase border border-cyan-200">{t('actions')}</th>
                            )}
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-cyan-100">
                    {loading ? (
                        <tr>
                            <td colSpan="6" className="text-center py-4 text-cyan-500">{t('loading')}</td>
                        </tr>
                    ) : users.length === 0 ? (
                        <tr>
                            <td colSpan="6" className="text-center py-4 text-gray-500">No users found</td>
                        </tr>
                    ) : (
                        sortedUsers.map((user) => (
                            <tr key={user.userID} className="hover:bg-cyan-50 transition">
                                <td className="px-4 py-3">{`${user.first_name} ${user.last_name}`}</td>
                                <td className="px-4 py-3">{user.role}</td>
                                <td className="px-4 py-3">
                                    <span className={`flex items-center ${getAccessRightColor(user.access_rights)}`}>
                                        <Shield className="w-4 h-4 mr-1" />
                                        {t(`access_right.${user.access_rights}`)}
                                    </span>
                                </td>
                                <td className="px-4 py-3">{user.login}</td>
                                {userRole === 'FULL' && (
                                <td className="px-4 py-3 text-center space-x-2">
                                    <button
                                        onClick={() => startEdit(user.userID)}
                                        className="text-cyan-600 hover:text-cyan-800 bg-cyan-50 hover:bg-cyan-100 rounded px-2 py-1"
                                    >
                                        <Edit className="w-5 h-5 inline" />
                                    </button>
                                    <button
                                        onClick={() => deleteUser(user.userID)}
                                        className="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 rounded px-2 py-1"
                                    >
                                        <Trash2 className="w-5 h-5 inline" />
                                    </button>
                                </td>)}
                            </tr>
                        ))
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}