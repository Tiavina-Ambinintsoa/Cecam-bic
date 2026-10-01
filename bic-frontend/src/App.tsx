import { BrowserRouter, Routes, Route, useParams, useLocation } from "react-router-dom";
import { AnimatePresence, motion, MotionConfig } from "motion/react";
import { TopBar } from "@/components/layout/TopBar";
import { AppBreadcrumb } from "@/components/layout/AppBreadcrumb";
import { NouvelleDemandeTabs } from "@/components/layout/NouvelleDemandeTabs";
import { HomeMenu } from "@/components/home/HomeMenu";
import { ClientForm } from "@/features/client/ClientForm";
import { ContratForm } from "@/features/contrat/ContratForm";
import { RapportPage } from "@/pages/RapportPage";
import { RechercheIndividuPage } from "@/pages/RechercheIndividuPage";
import { MiseAJourDemandePage } from "@/pages/MiseAJourDemandePage";
import { AlertesPage } from "@/pages/AlertesPage";
import { HistoriqueRapportsPage } from "@/pages/HistoriqueRapportsPage";
import { AuditPage } from "@/pages/AuditPage";
import { LoginPage } from "@/pages/LoginPage";
import { DemandeEnCoursProvider } from "@/context/DemandeEnCoursContext";
import { AuthProvider } from "@/context/AuthContext";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { ImportPage } from "@/pages/admin/ImportPage";
import { ToastProvider } from "./components/ui/toast";

function RapportPageRoute() {
  const { contratId } = useParams();
  return <RapportPage key={contratId} />;
}

const pageTransition = {
  initial: { opacity: 0, y: 8 },
  animate: { opacity: 1, y: 0 },
  exit: { opacity: 0, y: -8 },
};

function AppShell() {
  const location = useLocation();
  return (
    <div className="min-h-screen bg-slate-50">
      <TopBar />
      <AppBreadcrumb />
      <NouvelleDemandeTabs />
      <AnimatePresence mode="wait" initial={false}>
        <motion.div
          key={location.pathname}
          variants={pageTransition}
          initial="initial"
          animate="animate"
          exit="exit"
          transition={{ duration: 0.18, ease: "easeInOut" }}
        >
          <Routes location={location}>
            <Route path="/" element={<HomeMenu />} />
            <Route path="/demande/nouvelle/individu" element={<ClientForm />} />
            <Route path="/demande/nouvelle/contrat/:clientId" element={<ContratForm />} />
            <Route path="/demande/rapport/:contratId" element={<RapportPageRoute />} />
            <Route path="/recherche/individu" element={<RechercheIndividuPage />} />
            <Route path="/demande/modifier" element={<MiseAJourDemandePage />} />
            <Route path="/alertes" element={<AlertesPage />} />
            <Route path="/rapports" element={<HistoriqueRapportsPage />} />
            <Route path="/audit" element={<AuditPage />} />
            <Route path="/admin/import" element={<ImportPage />} />
          </Routes>
        </motion.div>
      </AnimatePresence>
    </div>
  );
}

export default function App() {
  return (
    <MotionConfig reducedMotion="user">
      <ToastProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/*"
              element={
                <ProtectedRoute>
                  <DemandeEnCoursProvider>
                    <AppShell />
                  </DemandeEnCoursProvider>
                </ProtectedRoute>
              }
            />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
      </ToastProvider>
    </MotionConfig>
  );
}