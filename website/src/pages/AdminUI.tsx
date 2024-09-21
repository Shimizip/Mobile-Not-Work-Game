import React, { useState } from "react";
import {
  Avatar,
  Button,
  Box,
  CssBaseline,
  Typography,
  TextField,
  Container,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper
} from "@mui/material";
import { useAppDispatch, useAppSelector } from "../hooks/redux-hooks";
import { getSpieler, logout } from "../slices/authSlice";
import { Link, useNavigate } from "react-router-dom";
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import { NotificationType, showNotification } from "../slices/notificationSlice";
import { blockiereSpieler, freischaltenSpieler, getScoreboard, getSpielerAdmin } from "../slices/adminSlice";

const AdminUI = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const basicUserInfo = useAppSelector((state) => state.auth.basicUserInfo);
  const userProfileInfo = useAppSelector((state) => state.auth.userProfileData);

  const [spielerData, setSpielerData] = useState<Array<{
    sid: number;
    benutzername: string;
    geraeteid: string;
    einwilligungagb: boolean;
    blockiert: boolean
  }>>([]);

  const [scoreboardData, setScoreboardData] = useState<Array<{
    scid: number;
    summepunkte: number;
    spid: number;
    spielerName: string;
    sid: number
  }>>([]);

  const token = localStorage.getItem("userInfo") ? JSON.parse(localStorage.getItem("userInfo")!).token : "";
  let userId = "0";

  const [userIdInput, setUserIdInput] = useState("");

  const [showEingabemaskeFreischalten, setShowEingabemaskeFreischalten] = useState(false);
  const handleActionFreischaltenClick = () => {
    setShowEingabemaskeFreischalten(true);
  };
  const [ShowEingabemaskeBlockieren, setShowEingabemaskeBlockieren] = useState(false);
  const handleActionBlockierenClick = () => {
    setShowEingabemaskeBlockieren(true);
  };
  const [ShowEingabemaskeGetSpieler, setShowEingabemaskeGetSpieler] = useState(false);
  const handleActionGetSpielerClick = () => {
    setShowEingabemaskeGetSpieler(true);
  };
  const [ShowEingabemaskeGetScoreboard, setShowEingabemaskeGetScoreboard] = useState(false);
  const handleActionGetScoreboardClick = () => {
    setShowEingabemaskeGetScoreboard(true);
  };

  const handleLogout = async () => {
    try {
      await dispatch(logout()).unwrap();
      navigate("/login");
    } catch (e) {
      console.error(e);
    }
  };

  const handleGetSpieler = async () => {
    let userId = userIdInput;
    try {
      const data = await dispatch(getSpielerAdmin({ userId, token })).unwrap();
      setSpielerData(data);
    } catch (e) {
      console.error(e);
      dispatch(
        showNotification({
          message: "handleGetSpieler: Ein Fehler ist aufgetreten!",
          type: NotificationType.Error,
        })
      );
    }
    setShowEingabemaskeGetSpieler(false);
    setUserIdInput("");
  };

  const handleSpielerFreischalten = async () => {
    userId = userIdInput;
    try {
      await dispatch(freischaltenSpieler({ userId, token })).unwrap();
    } catch (e) {
      console.error(e);
      dispatch(
        showNotification({
          message: "handleSpielerFreischalten: Ein Fehler ist aufgetreten!",
          type: NotificationType.Error,
        })
      );
    }
    setShowEingabemaskeFreischalten(false);
    setUserIdInput("");
  };

  const handleSpielerBlockieren = async () => {
    userId = userIdInput;
    try {
      await dispatch(blockiereSpieler({ userId, token })).unwrap();
    } catch (e) {
      console.error(e);
      dispatch(
        showNotification({
          message: "handleSpielerBlockieren: Ein Fehler ist aufgetreten!",
          type: NotificationType.Error,
        })
      );
    }
    setShowEingabemaskeBlockieren(false);
    setUserIdInput("");
  };

  const handleGetScoreboard = async () => {
    let userId = userIdInput.trim() !== "" ? userIdInput : "";
    try {
      const data = await dispatch(getScoreboard({ userId, token })).unwrap();
      setScoreboardData(data);
    } catch (e) {
      console.error(e);
      dispatch(
        showNotification({
          message: "handleGetScoreboard: Ein Fehler ist aufgetreten!",
          type: NotificationType.Error,
        })
      );
    }
    setShowEingabemaskeGetSpieler(false);
    setUserIdInput("");
  };

  return (
    <>
      <Container maxWidth="md">
        <CssBaseline />
        <Box
          sx={{
            mt: 20,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: "primary.light" }}>
            <AdminPanelSettingsIcon />
          </Avatar>
          <Typography variant="h5">Mobile NotWork Game</Typography>
          <Typography variant="h5">Verwaltung</Typography>
          <Box maxWidth={"98%"}>
            <Link to="/adminmap">
              <Button
                fullWidth
                variant="contained"
                sx={{ mt: 2, mb: 0 }}
                onClick={undefined}
              >
                Zur Admin Karte
              </Button>
            </Link>

            <Button
              fullWidth
              variant="contained"
              sx={{ mt: 2, mb: 10 }}
              onClick={handleLogout}
            >
              Abmelden
            </Button>

            <Button
              fullWidth
              variant="contained"
              sx={{ mt: 1, mb: 1 }}
              onClick={handleActionGetSpielerClick}
            >
              Spieler anzeigen
            </Button>
            {/* Eingabemaske anzeigen, wenn ShowEingabemaskeGetSpieler true ist */}
            {ShowEingabemaskeGetSpieler && (
              <Grid container spacing={2} alignItems="center" >
                <Grid item xs={12}>
                  <Typography variant="body1" color="textSecondary">
                    Lasse das Feld leer, um alle Spieler anzuzeigen.
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    label="UserID"
                    variant="outlined"
                    fullWidth
                    value={userIdInput}
                    onChange={(e) => setUserIdInput(e.target.value)}
                  />
                </Grid>
                <Grid item xs={6}>
                  <Button
                    fullWidth
                    variant="contained"
                    sx={{ mt: 1, mb: 1 }}
                    onClick={handleGetSpieler}
                  >
                    Bestätigen
                  </Button>
                </Grid>
              </Grid>
            )}

            <Button
              fullWidth
              variant="contained"
              sx={{ mt: 1, mb: 1 }}
              onClick={handleActionBlockierenClick}
            >
              Spieler blockieren
            </Button>
            {/* Eingabemaske anzeigen, wenn ShowEingabemaskeBlockieren true ist */}
            {ShowEingabemaskeBlockieren && (
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={6}>
                  <TextField
                    label="UserID"
                    variant="outlined"
                    fullWidth
                    value={userIdInput}
                    onChange={(e) => setUserIdInput(e.target.value)}
                  />
                </Grid>
                <Grid item xs={6}>
                  <Button
                    fullWidth
                    variant="contained"
                    sx={{ mt: 1, mb: 1 }}
                    onClick={handleSpielerBlockieren}
                  >
                    Bestätigen
                  </Button>
                </Grid>
              </Grid>
            )}

            <Button
              fullWidth
              variant="contained"
              sx={{ mt: 1, mb: 1 }}
              onClick={handleActionFreischaltenClick}
            >
              Spieler freischalten
            </Button>
            {/* Eingabemaske anzeigen, wenn showEingabemaskeFreischalten true ist */}
            {showEingabemaskeFreischalten && (
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={6}>
                  <TextField
                    label="UserID"
                    variant="outlined"
                    fullWidth
                    value={userIdInput}
                    onChange={(e) => setUserIdInput(e.target.value)}
                  />
                </Grid>
                <Grid item xs={6}>
                  <Button
                    fullWidth
                    variant="contained"
                    sx={{ mt: 1, mb: 1 }}
                    onClick={handleSpielerFreischalten}
                  >
                    Bestätigen
                  </Button>
                </Grid>
              </Grid>
            )}

            <Button
              fullWidth
              variant="contained"
              sx={{ mt: 1, mb: 1 }}
              onClick={handleActionGetScoreboardClick}
            >
              Scoreboard anzeigen
            </Button>
            {/* Eingabemaske anzeigen, wenn ShowEingabemaskeGetScoreboard true ist */}
            {ShowEingabemaskeGetScoreboard && (
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={6}>
                  <TextField
                    label="ID der Spielperiode"
                    variant="outlined"
                    fullWidth
                    value={userIdInput}
                    onChange={(e) => setUserIdInput(e.target.value)}
                  />
                </Grid>
                <Grid item xs={6}>
                  <Button
                    fullWidth
                    variant="contained"
                    sx={{ mt: 1, mb: 1 }}
                    onClick={handleGetScoreboard}
                  >
                    Bestätigen
                  </Button>
                </Grid>
              </Grid>
            )}

            {spielerData.length > 0 && (
              <TableContainer component={Paper} sx={{ mt: 2, overflowX: "auto", width: "100%" }}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Spieler ID</TableCell>
                      <TableCell>Benutzername</TableCell>
                      <TableCell>Geraete ID</TableCell>
                      <TableCell>Einwilligung AGB</TableCell>
                      <TableCell>Status</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {spielerData.map((spieler) => (
                      <TableRow key={spieler.sid}>
                        <TableCell>{spieler.sid}</TableCell>
                        <TableCell>{spieler.benutzername || ""}</TableCell>
                        <TableCell>{spieler.geraeteid || ""}</TableCell>
                        <TableCell>{spieler.einwilligungagb ? "Ja" : "Nein"}</TableCell>
                        <TableCell>{spieler.blockiert ? "Blockiert" : "Aktiv"}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

            {scoreboardData.length > 0 && (
              <TableContainer component={Paper} sx={{ mt: 2, overflowX: "auto", width: "100%" }}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>scid</TableCell>
                      <TableCell>Spieler ID</TableCell>
                      <TableCell>Spielperiode (spid)</TableCell>
                      <TableCell>Spielername</TableCell>
                      <TableCell>Summe der Punkte</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {scoreboardData.map((scoreboard) => (
                      <TableRow key={scoreboard.spid}>
                        <TableCell>{scoreboard.scid}</TableCell>
                        <TableCell>{scoreboard.sid || ""}</TableCell>
                        <TableCell>{scoreboard.spid || ""}</TableCell>
                        <TableCell>{scoreboard.spielerName || ""}</TableCell>
                        <TableCell>{scoreboard.summepunkte || ""}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

          </Box>
        </Box>
      </Container>
    </>

  );
};


export default AdminUI;