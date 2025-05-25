import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { AlertCircle } from 'lucide-react';

export default function AuthPage() {
    const { t, i18n } = useTranslation();

    const [isRegisterMode, setIsRegisterMode] = useState(false);
    const [formData, setFormData] = useState({
        first_name: '',
        last_name: '',
        role: '',
        access_rights: 'FULL',
        login: '',
        password: '',
    });
    const [error, setError] = useState<string | null>(null);

    const handleLanguageToggle = () => {
        const newLang = i18n.language === 'uk' ? 'en' : 'uk';
        i18n.changeLanguage(newLang);
    };
    const API_BASE_URL = 'http://localhost:8080/api/user';

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        const endpoint = isRegisterMode ? '/admin/add' : '/login';

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData),
            });

            const data = await response.json();

            if (!response.ok) {
                const message = response.status === 500 && isRegisterMode
                    ? 'Email is already registered'
                    : data?.message || 'Authentication failed';
                throw new Error(message);
            }

            const userId = isRegisterMode ? data.userID : data;
            localStorage.setItem('userID', userId);

            const roleRes = await fetch(`${API_BASE_URL}/role/id/${userId}`);
            if (!roleRes.ok) throw new Error('Failed to fetch user role');

            const role = await roleRes.json();
            localStorage.setItem('role', role);

            if (role === 'READ_ONLY') {
                setError('access_denied');
                return;
            }
            localStorage.setItem('isAuthenticated', 'true');
            window.location.href = '/users';

        } catch (err: any) {
            console.error(err);
            setError(err.message || 'Unexpected error occurred');
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-100 to-green-100">
            <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full border border-blue-200">

                <div className="flex justify-end mb-4">
                    <button onClick={handleLanguageToggle} className="text-sm text-blue-700 hover:underline">
                        {i18n.language === 'uk' ? 'EN' : 'UK'}
                    </button>
                </div>

                <h2 className="text-2xl font-bold text-blue-800 mb-6 text-center">
                    {isRegisterMode ? t('sign_up') : t('sign_in')}
                </h2>

                {error && (
                    <div className="bg-red-100 border border-red-300 text-red-800 px-4 py-2 rounded flex items-center mb-4">
                        <AlertCircle className="w-5 h-5 mr-2" />
                        {t(error)}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    {isRegisterMode && (
                        <>
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('first_name')}</label>
                                <input type="text" name="first_name" value={formData.first_name} onChange={handleChange} required className="w-full border border-blue-300 px-4 py-2 rounded-md" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('last_name')}</label>
                                <input type="text" name="last_name" value={formData.last_name} onChange={handleChange} required className="w-full border border-blue-300 px-4 py-2 rounded-md" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('role')}</label>
                                <input type="text" name="role" value={formData.role} onChange={handleChange} required className="w-full border border-blue-300 px-4 py-2 rounded-md" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-blue-700 mb-1">{t('access_rights')}</label>
                                <select name="access_rights" value={formData.access_rights} onChange={handleChange} className="w-full border border-blue-300 px-4 py-2 rounded-md">
                                    <option value="FULL">{t('full_access')}</option>
                                    <option value="READ_ALL">{t('read_all')}</option>
                                </select>
                            </div>
                        </>
                    )}
                    <div>
                        <label className="block text-sm font-medium text-blue-700 mb-1">{t('login')}</label>
                        <input type="email" name="login" value={formData.login} onChange={handleChange} required className="w-full border border-blue-300 px-4 py-2 rounded-md" />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-blue-700 mb-1">{t('password')}</label>
                        <input type="password" name="password" value={formData.password} onChange={handleChange} required minLength={6} className="w-full border border-blue-300 px-4 py-2 rounded-md" />
                    </div>
                    <button type="submit" className="w-full bg-gradient-to-r from-blue-500 to-green-500 text-white py-2 px-4 rounded-md hover:from-blue-600 hover:to-green-600 transition">
                        {isRegisterMode ? t('register') : t('login_button')}
                    </button>
                </form>

                <div className="mt-4 text-center">
                    <button onClick={() => setIsRegisterMode(!isRegisterMode)} className="text-blue-700 hover:underline text-sm">
                        {isRegisterMode ? t('already_have_account') : t('no_account')}
                    </button>
                </div>
            </div>
        </div>
    );
}
