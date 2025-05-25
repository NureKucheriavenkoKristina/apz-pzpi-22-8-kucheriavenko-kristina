import {BrowserRouter as Router, Routes, Route} from 'react-router-dom'
import './App.css'
import AuthPage from "./pages/AuthPage.tsx";
import UsersManagementPage from './pages/UsersPage.tsx'
import DonorsManagementPage from './pages/DonorPage.tsx'
import Navigation from './components/Navigation';
import BiologicalMaterialsManagementPage from "./pages/BiologicalMaterialPage.tsx";
import EventLogManagementPage from './pages/EventLogPage.tsx';
import NotificationsManagementPage from "./pages/NotificationPage.tsx";
import StorageConditionsManagementPage from "./pages/StorageConditionPage.tsx";
import ProtectedRoute from './components/isAuthenticated.tsx';
import {useTranslation} from "react-i18next";



function App() {
    const { t } = useTranslation();
    return (
        <Router>
            <div className="app-container">
                <Navigation />
                <main>
                    <Routes>
                        <Route path="/" element={
                            <div className="home-page">
                                <div className="bg-white p-7 rounded-xl shadow border border-blue-200">
                                    <AuthPage />
                                </div>
                            </div>
                        }/>
                        <Route path="/users" element={
                            <ProtectedRoute>
                                <UsersManagementPage />
                            </ProtectedRoute>
                        }/>

                        <Route path="/donors" element={
                            <ProtectedRoute>
                                <DonorsManagementPage />
                            </ProtectedRoute>
                        }/>

                        <Route path="/biological-materials" element={
                            <ProtectedRoute>
                                <BiologicalMaterialsManagementPage />
                            </ProtectedRoute>
                        }/>

                        <Route path="/event-logs" element={
                            <ProtectedRoute>
                                <EventLogManagementPage />
                            </ProtectedRoute>
                        }/>

                        <Route path="/notifications" element={
                            <ProtectedRoute>
                                <NotificationsManagementPage />
                            </ProtectedRoute>
                        }/>

                        <Route path="/storage-conditions" element={
                            <ProtectedRoute>
                                <StorageConditionsManagementPage />
                            </ProtectedRoute>
                        }/>

                        <Route path="*" element={<div>{t('notfound')}</div>} />
                    </Routes>
                </main>
            </div>
        </Router>
    )
}

export default App
