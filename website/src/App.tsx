import React from "react";
import { Routes, Route } from "react-router-dom";
//import Sidebar from "./components/Sidebar";
//import { Box } from "@mui/material";
import MapView from "./pages/MapView";
import Login from "./pages/Login";
import AdminUI from "./pages/AdminUI";
import MapViewAdmin from "./pages/MapViewAdmin";
import DefaultLayout from "./layouts/DefaultLayout";
import ProtectedLayout from "./layouts/ProtectedLayout";
import NotificationBar from "./components/notification/NotificationBar";
import Legal from "./pages/Legal";

function App() {
  const drawerWidth = 240;

  return (
    <>
    <NotificationBar />
    <Routes>
      <Route element={<DefaultLayout />}>
          <Route path="/" element={<MapView />} />
          <Route path="/login" element={<Login />} />
          <Route path="/legal" element={<Legal />} />
        </Route>
        <Route element={<ProtectedLayout />}>
          <Route path="/map" element={<MapView />} />
          <Route path="/admin" element={<AdminUI />} />
          <Route path="/adminmap" element={<MapViewAdmin />} />
        </Route>
    </Routes>
    </>
  );
}

export default App;
