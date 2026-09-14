import { BrowserRouter, Routes, Route, useParams } from "react-router-dom";
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
import { LoginPage } from "@/pages/LoginPage";
import { DemandeEnCoursProvider } from "@/context/DemandeEnCoursContext";
import { AuthProvider } from "@/context/AuthContext";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";

function RapportPageRoute() {
  const { contratId } = useParams();
  return <RapportPage key={contratId} />;
}

function AppShell() {
  return (
    <div className="min-h-screen bg-slate-50">
      <TopBar />
      <AppBreadcrumb />
      <NouvelleDemandeTabs />
      <Routes>
        <Route path="/" element={<HomeMenu />} />
        <Route path="/demande/nouvelle/individu" element={<ClientForm />} />
        <Route path="/demande/nouvelle/contrat/:clientId" element={<ContratForm />} />
        <Route path="/demande/rapport/:contratId" element={<RapportPageRoute />} />
        <Route path="/recherche/individu" element={<RechercheIndividuPage />} />
        <Route path="/demande/modifier" element={<MiseAJourDemandePage />} />
        <Route path="/alertes" element={<AlertesPage />} />
        <Route path="/rapports" element={<HistoriqueRapportsPage />} />
      </Routes>
    </div>
  );
}

export default function App() {
  return (
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
  );
}